const sleep = function (milliseconds) {
    return new Promise(function (resolve) {
        setTimeout(resolve, milliseconds);
    });
};

const getUrlVars = function () {
    const vars = {};
    window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function (m, key, value) {
        vars[key] = value;
    });
    return vars;
};

const urlVars = getUrlVars();
const _botRunnerUrl = urlVars['runnerUrl'];
const _botName = decodeURI(urlVars['botName']);
const _avatar = _botRunnerUrl + '/static/chatbot/images/avatar/avatar.png';

const _botBaseUrl = _botRunnerUrl + '/api/chatbot';

const scanContextKeys = (context) => new Promise((res, rej) => {
    const channel = new MessageChannel();
    channel.port1.onmessage = ({data}) => {
        channel.port1.close();
        if (data.error) {
            rej(data.error);
        }else {
            res(data.result);
        }
    };

    parent.postMessage({context: context}, '*', [channel.port2]);
});

const chatbot = new Vue({
        el: '#q-app',
        updated() {
            const images = document.getElementsByClassName('imgClass');
            for(let i=0; i<images.length; i++){
                images[i].addEventListener('click', function(e){
                    parent.postMessage({pictureModal: this.src}, '*');
                },false);
            }
        },
        data: {
            // config
            convId: null,
            botName: _botName,
            botUrl: _botBaseUrl,
            devMode: false, // ##param.devMode##
            minTimeBetweenMessages: 1000,
            botAvatar: _avatar,
            // technique
            inputConfig: {
                // TODO, il exste d'autres modes, par ex email
                modeTextarea: false,
                responseText: '',
                responsePattern: '',
                showRating: false,
                rating: 0,
                buttons: [],
                cards: []
            },
            customConfig: {
              useRating: false,
              ratingMessage: 'Merci !'
            },
            isEnded: false,


            prevInputConfig: {},
            lastPayload: null,
            processing: false,
            error: false,
            messages: [],
            keepAction: false,
            menu: false,
            lastUserInteraction: 0,
            watingMessagesStack: [],
            context: {},
            contextMap: {}
        },
        methods: {

            initBot: function() {
                document.getElementById('page').style.visibility = 'visible';
                document.getElementById('loading').style.display = 'none';
                if (sessionStorage.contextMap) {
                    chatbot.contextMap = JSON.parse(sessionStorage.contextMap);
                    this.startConversation();
                } else {
                    this.$http.post(chatbot.botUrl + '/context').then(contextResponse => {
                        chatbot.contextMap = contextResponse.data;
                        sessionStorage.setItem('contextMap', JSON.stringify(chatbot.contextMap));
                        chatbot.startConversation();
                    });
                }

            },
            updateSessionStorage() {
                sessionStorage.convId = chatbot.convId;
                sessionStorage.setItem('inputConfig', JSON.stringify(chatbot.inputConfig));
                sessionStorage.setItem('messages', JSON.stringify(chatbot.messages));
                sessionStorage.setItem('customConfig', JSON.stringify(chatbot.customConfig));
            },
            restoreFromSessionStorage() {
                chatbot.convId = sessionStorage.convId;
                chatbot.inputConfig = JSON.parse(sessionStorage.inputConfig);
                chatbot.messages = JSON.parse(sessionStorage.messages);
                chatbot.customConfig = JSON.parse(sessionStorage.customConfig);
            },
            startConversation() {
                chatbot.lastUserInteraction = Date.now();
                const urlPage =  window.location.href;
                scanContextKeys(new Map(Object.entries(chatbot.contextMap))).then(value => {
                    chatbot.context = value;
                    if(chatbot.context['url'] === undefined) {
                        chatbot.context['url'] =  urlPage;
                    }
                    if (sessionStorage.convId) {
                        chatbot.restoreFromSessionStorage();
                        chatbot._scrollToBottom();
                    } else {

                        this.$http.post(chatbot.botUrl + '/start', {message: null, metadatas: {'context': chatbot.context}})
                            .then(httpResponse => {
                                chatbot.convId = httpResponse.data.metadatas.sessionId;
                                chatbot.customConfig.useRating = httpResponse.data.metadatas.customConfig.rating;
                                chatbot.customConfig.ratingMessage = httpResponse.data.metadatas.customConfig.ratingMessage;
                                chatbot.updateSessionStorage();
                                chatbot._handleResponse(httpResponse, false);
                            }).catch(() => {
                            // error
                            chatbot.error = true;
                            chatbot.processing = false;
                            chatbot._scrollToBottom();
                        });
                    }
                });
            },
            postAnswerBtn(btn) {
                chatbot.messages.push({
                    text: [btn.label],
                    sent: true,
                    bgColor: 'primary',
                    textColor: 'white'
                });

                chatbot.updateSessionStorage();

                chatbot._scrollToBottom();

                chatbot.askBot(btn.payload, true);
            }
            ,
            postAnswerText() {
                const sanitizedString = chatbot.inputConfig.responseText.trim().replace(/(?:\r\n|\r|\n)/g, '<br>');

                chatbot.messages.push({
                    text: sanitizedString !== '' ? [sanitizedString] : null,
                    rating: chatbot.inputConfig.rating > 0 ? chatbot.inputConfig.rating : null,
                    sent: true,
                    bgColor: 'primary',
                    textColor: 'white'
                });
                chatbot.updateSessionStorage();

                chatbot._scrollToBottom();

                const response = chatbot.inputConfig.responsePattern === '' ? sanitizedString.replace(/(")/g, '"')
                    : chatbot.inputConfig.responsePattern.replace('#', sanitizedString.replace(/(")/g, '\\"'));

                chatbot.askBot(response, false);
            }
            ,
            _scrollToBottom() {
                const scrollHeight = this.$refs.scroller.$el.children[0].children[0].scrollHeight; // workaround
                this.$refs.scroller.setScrollPosition(scrollHeight, 400);
            }
            ,
            askBot(value, isButton) {
                chatbot.prevInputConfig = JSON.parse(JSON.stringify(chatbot.inputConfig));
                chatbot.reinitInput();
                chatbot.lastPayload = value;
                chatbot.processing = true;

                chatbot.lastUserInteraction = Date.now();
                let botInput;
                if (isButton) {
                    botInput = { message: null, metadatas: { context: chatbot.context, payload: value } };
                } else {
                    botInput = { message: value, metadatas: { context: chatbot.context } };
                }
                return Vue.http.post(chatbot.botUrl + '/talk/' + chatbot.convId, botInput)
                    .then(httpResponse => {
                        chatbot._handleResponse(httpResponse, false);
                    }).catch(
                        () => {
                            // error
                            chatbot.error = true;

                            chatbot.processing = false;
                            chatbot._scrollToBottom();
                        });

            }
            ,
            _handleResponse(httpResponse, isRating) {
                // success
                const responses = httpResponse.data.htmlTexts;
                const buttons = httpResponse.data.choices;
                const cards = httpResponse.data.cards;
                chatbot.isEnded = httpResponse.data.status === 'Ended' && !isRating;
                if (httpResponse.data.metadatas && httpResponse.data.metadatas.avatar) {
                    chatbot.botAvatar = 'data:image/png;base64,' + httpResponse.data.metadatas.avatar;
                }

                if (httpResponse.data.metadatas && httpResponse.data.metadatas.jsevent) {
                    parent.postMessage({jsevent: httpResponse.data.metadatas.jsevent}, '*');
                }

                for (let i = 0; i < responses.length - 1; i++) {
                    chatbot.watingMessagesStack.push({ text: responses[i] });
                }
                chatbot.watingMessagesStack.push({ text: responses[responses.length - 1], buttons: buttons, cards: cards });

                chatbot._displayMessages();
            }
            ,
            _displayMessages() {
                if (chatbot.watingMessagesStack.length > 0) {
                    const currentMessage = chatbot.watingMessagesStack.shift();
                    let watingTime = chatbot.lastUserInteraction - Date.now() + chatbot.minTimeBetweenMessages;

                    if (chatbot.devMode === true) {
                        watingTime = 0;
                    }

                    sleep(watingTime).then(function() {
                        chatbot._processResponse(currentMessage);
                        chatbot.lastUserInteraction = Date.now();
                        chatbot._displayMessages();
                    });
                } else {
                    chatbot.processing = false;
                    if (chatbot.keepAction) {
                        chatbot.inputConfig = chatbot.prevInputConfig;
                        chatbot.inputConfig.responseText = '';
                        chatbot.keepAction = false;
                        chatbot.updateSessionStorage();
                    }

                    // en différé le temps que la vue soit mise à jour
                    sleep(1).then(function() {
                        // si les dialogues sont fermés
                        if (chatbot.customConfig.useRating) {
                            chatbot.inputConfig.showRating = chatbot.isEnded;
                            chatbot.updateSessionStorage();
                            chatbot.focusInput();
                        }
                    });
                }
            }
            ,
            focusInput() {
                chatbot.$refs.input.focus();
            }
            ,
            _processResponse(response) {
                const lastMsg = chatbot.messages[chatbot.messages.length - 1];
                if (response.text !== '') {
                    if (lastMsg && !lastMsg.sent) {
                        // ajoute un message à un précédent message du bot
                        lastMsg.text.push(response.text);
                    } else {
                        // première réponse du bot
                        chatbot.messages.push({
                            avatar: chatbot.botAvatar,
                            text: [response.text],
                            bgColor: 'grey-grdf'
                        });
                    }
                    chatbot.updateSessionStorage();

                }

                if (response.buttons) {
                    response.buttons.forEach(function(value, key) {
                        chatbot.inputConfig.buttons.push(value);
                        chatbot.updateSessionStorage();
                    }, chatbot);
                }

                if (response.cards) {
                    response.cards.forEach(function(value, key) {
                        chatbot.inputConfig.cards.push(value);
                        chatbot.updateSessionStorage();
                    }, chatbot);
                }

                chatbot._scrollToBottom();
            },
            reinitInput() {
                chatbot.inputConfig.modeTextarea = false;
                chatbot.inputConfig.responsePattern = '';
                chatbot.inputConfig.responseText = '';
                chatbot.inputConfig.rating = 0;
                chatbot.inputConfig.buttons = [];
                chatbot.inputConfig.cards = [];
                chatbot.error = false;
                chatbot.updateSessionStorage();
            },
            minimize() {
                parent.postMessage('Chatbot.minimize', '*');
            },
            rateBot(value){
                this.$http.post(chatbot.botUrl + '/rating', {sender: chatbot.convId, note: value})
                    .then(httpResponse => {
                        httpResponse.data = {htmlTexts : [chatbot.customConfig.ratingMessage]};
                        chatbot._handleResponse(httpResponse, true);

                    });
            },
            close() {
                parent.postMessage('Chatbot.close', '*');
            }
        }
    })
;

chatbot.initBot();
