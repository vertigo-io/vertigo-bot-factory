function updateSessionStorage() {
    sessionStorage.convId = chatbot.botConfig.convId;
    sessionStorage.setItem('inputConfig', JSON.stringify(chatbot.inputConfig));
    sessionStorage.setItem('messages', JSON.stringify(chatbot.botConfig.messages));
}

function restoreFromSessionStorage() {
    chatbot.botConfig.convId = sessionStorage.convId;
    chatbot.inputConfig = JSON.parse(sessionStorage.inputConfig);
    chatbot.botConfig.messages = JSON.parse(sessionStorage.messages);
}

function clearSessionStorage() {
    sessionStorage.removeItem('convId');
    sessionStorage.removeItem('inputConfig');
    sessionStorage.removeItem('messages');
}

function setParametersFromHttpResponse(httpResponse) {
    chatbot.botConfig.convId = httpResponse.data.metadatas.sessionId;
    updateSessionStorage();
    _handleResponse(httpResponse, false);
}

function startConversation() {
    chatbot.botConfig.lastUserInteraction = Date.now();
    const urlPage = window.location.href;
    scanContextKeys(new Map(Object.entries(chatbot.contextMap))).then(value => {
        chatbot.context = value;
        if (chatbot.context['url'] === undefined) {
            chatbot.context['url'] = urlPage;
        }
        if (sessionStorage.convId) {
            restoreFromSessionStorage();
            _scrollToBottom();
        } else {
            axios.post(chatbot.botConfig.botUrl + '/start', {message: null, metadatas: {'context': chatbot.ontext}})
                .then(setParametersFromHttpResponse)
                .catch(() => {
                    // error
                    chatbot.botConfig.error = true;
                    chatbot.botConfig.processing = false;
                    _scrollToBottom();
                });
        }
    });
}

function _handleResponse(httpResponse, isRating) {
    // success
    if (httpResponse.data.metadatas.sessionId && chatbot.botConfig.convId !== httpResponse.data.metadatas.sessionId) {
        refreshBot(true);
        setParametersFromHttpResponse(httpResponse);

    } else {
        const responses = httpResponse.data.htmlTexts;
        const buttons = httpResponse.data.choices;
        const cards = httpResponse.data.cards;
        const files = httpResponse.data.files;
        chatbot.botConfig.acceptNlu = httpResponse.data.acceptNlu !== undefined ? httpResponse.data.acceptNlu : true;
        chatbot.botConfig.rating = httpResponse.data.rating.enabled;
        chatbot.botConfig.ratingType = httpResponse.data.rating.type;
        chatbot.botConfig.isEnded = httpResponse.data.status === 'Ended' && !isRating;
        if (httpResponse.data.metadatas && httpResponse.data.metadatas.avatar) {
            chatbot.botConfig.botAvatar = 'data:image/png;base64,' + httpResponse.data.metadatas.avatar;
        }

        if (httpResponse.data.metadatas && httpResponse.data.metadatas.jsevent) {
            parent.postMessage({jsevent: httpResponse.data.metadatas.jsevent}, '*');
        }

        if (httpResponse.data.metadatas && httpResponse.data.metadatas.welcometour) {
            parent.postMessage({welcomeTour: httpResponse.data.metadatas.welcometour}, '*');
        }

        for (let i = 0; i < responses.length - 1; i++) {
            chatbot.botConfig.watingMessagesStack.push({text: responses[i]});
        }
        chatbot.botConfig.watingMessagesStack.push({
            text: responses[responses.length - 1],
            buttons: buttons,
            cards: cards,
            files: files
        });

        _displayMessages();
    }
}

function _displayMessages() {
    if (chatbot.botConfig.watingMessagesStack.length > 0) {
        const currentMessage = chatbot.botConfig.watingMessagesStack.shift();
        let watingTime = chatbot.botConfig.lastUserInteraction - Date.now() + chatbot.botConfig.minTimeBetweenMessages;

        if (chatbot.botConfig.devMode === true) {
            watingTime = 0;
        }

        sleep(watingTime).then(function () {
            _processResponse(currentMessage);
            chatbot.botConfig.lastUserInteraction = Date.now();
            _displayMessages();
        });
    } else {
        chatbot.inputConfig.showRating = chatbot.botConfig.rating && chatbot.botConfig.ratingType === 'SIMPLE' && chatbot.inputConfig.rating === 0;
        chatbot.botConfig.processing = false;
        if (chatbot.botConfig.keepAction) {
            chatbot.inputConfig = chatbot.botConfig.prevInputConfig;
            chatbot.inputConfig.responseText = '';
            chatbot.botConfig.keepAction = false;
            updateSessionStorage();
        }
    }
}

function _processResponse(response) {
    const lastMsg = chatbot.botConfig.messages[chatbot.botConfig.messages.length - 1];
    if (response.text !== undefined && response.text !== '') {
        if (lastMsg && !lastMsg.sent) {
            // ajoute un message à un précédent message du bot
            lastMsg.text.push(response.text);
        } else {
            // première réponse du bot
            if (chatbot.customConfig.displayAvatar) {
                chatbot.botConfig.messages.push({
                    avatar: chatbot.botConfig.botAvatar,
                    text: [response.text],
                    bgColor: 'grey-grdf'
                });
            } else {
                chatbot.botConfig.messages.push({
                    text: [response.text],
                    bgColor: 'grey-grdf'
                });
            }
        }
        updateSessionStorage();

    }

    if (response.buttons) {
        response.buttons.forEach(function (value, key) {
            chatbot.inputConfig.buttons.push(value);
            updateSessionStorage();
        }, chatbot);
    }

    if (response.cards) {
        response.cards.forEach(function (value, key) {
            chatbot.inputConfig.cards.push(value);
            updateSessionStorage();
        }, chatbot);
    }

    if (response.files) {
        response.files.forEach(function (value, key) {
            chatbot.inputConfig.files.push(value);
            updateSessionStorage();
        }, chatbot);
    }

    _scrollToBottom();
}

function refreshBot(isAnotherConversation) {
    chatbot.inputConfig = {
        modeTextarea: false,
        responseText: '',
        responsePattern: '',
        showRating: false,
        rating: 0,
        buttons: [],
        files: [],
        cards: [],
        cardIndex: 0
    };
    chatbot.botConfig.error = false;
    chatbot.botConfig.convId = null;
    chatbot.botConfig.messages = [];
    chatbot.botConfig.rating = false;

    clearSessionStorage();

    if (!isAnotherConversation) {
        startConversation();
    }
}

function reinitInput() {
    chatbot.inputConfig.modeTextarea = false;
    chatbot.inputConfig.responsePattern = '';
    chatbot.inputConfig.responseText = '';
    chatbot.inputConfig.buttons = [];
    chatbot.inputConfig.cards = [];
    chatbot.inputConfig.files = [];
    chatbot.botConfig.error = false;
    updateSessionStorage();
}
