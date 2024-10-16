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
            chatbot.$http.post(chatbot.botConfig.botUrl + '/start', {message: null, metadatas: {'context': chatbot.context}})
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

function postAnswerBtn(btn) {
    chatbot.botConfig.messages.push({
        text: [DOMPurify.sanitize(btn.label)],
        sent: true,
        bgColor: 'primary',
        textColor: 'white'
    });

    updateSessionStorage();

    _scrollToBottom();

    if (btn.url !== undefined) {
        const link = document.createElement('a');
        link.href = btn.url;
        if (btn.newTab) {
            link.target = '_blank';
        } else {
            link.target = '_top';
        }
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }
    askBot(btn.payload, btn.label, true, null, null, chatbot.botConfig.rating);
}

function fileUpload(btn, index) {
    const file = document.getElementById('file_' + index).files[0];
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = function (evt) {
        askBot(btn.payload, null, false, evt.target.result, file.name, false);
    };
}

function postAnswerText(isRating) {
    let sanitizedString = '';
    if (isRating) {
        sanitizedString = chatbot.inputConfig.rating.toString();
        chatbot.inputConfig.showRating = false;
        chatbot.botConfig.messages.push({
            text: null,
            rating: true,
            sent: true,
            bgColor: 'primary',
            textColor: 'white'
        });

    } else {
        sanitizedString = DOMPurify.sanitize(chatbot.inputConfig.responseText.trim()
            .replace(/(?:\r\n|\r|\n)/g, '<br>'));
        chatbot.botConfig.messages.push({
            text: sanitizedString !== '' ? [sanitizedString] : null,
            rating: isRating,
            sent: true,
            bgColor: 'primary',
            textColor: 'white'
        });
    }
    updateSessionStorage();

    _scrollToBottom();

    const response = chatbot.inputConfig.responsePattern === '' ? sanitizedString.replace(/(")/g, '"')
        : chatbot.inputConfig.responsePattern.replace('#', sanitizedString.replace(/(")/g, '\\"'));
    askBot(response, null, false, null, null, isRating);
}

function askBot(value, label, isButton, fileContent, fileName, rating) {
    chatbot.botConfig.prevInputConfig = JSON.parse(JSON.stringify(chatbot.inputConfig));
    reinitInput();
    chatbot.botConfig.lastPayload = value;
    chatbot.botConfig.processing = true;

    chatbot.botConfig.lastUserInteraction = Date.now();
    let botInput;
    if (fileContent) {
        botInput = {
            message: null,
            metadatas: {context: chatbot.context, payload: value, filecontent: fileContent, filename: fileName}
        };
    } else if (isButton) {
        botInput = {message: null, metadatas: {context: chatbot.context, payload: value, text: label}};
    } else {
        botInput = {message: value, metadatas: {context: chatbot.context}};
    }
    if (rating) {
        botInput.metadatas.rating = value
        if (chatbot.botConfig.ratingType === 'SIMPLE') {
            botInput.message = null
        }
    }
    return Vue.http.post(chatbot.botConfig.botUrl + '/talk/' + chatbot.botConfig.convId, botInput)
        .then(httpResponse => {
            _handleResponse(httpResponse, false);
        }).catch(
            () => {
                // error
                chatbot.botConfig.error = true;

                chatbot.botConfig.processing = false;
                _scrollToBottom();
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
        cards: []
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

function _scrollToBottom() {
    const scrollHeight = chatbot.$refs.scroller.$el.children[0].children[0].scrollHeight; // workaround
    chatbot.$refs.scroller.setScrollPosition(scrollHeight, 400);
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
