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
const _qAndABaseUrl = _botRunnerUrl + '/api/qanda';

const scanContextKeys = (context) => new Promise((res, rej) => {
    const channel = new MessageChannel();
    channel.port1.onmessage = ({data}) => {
        channel.port1.close();
        if (data.error) {
            rej(data.error);
        } else {
            res(data.result);
        }
    };

    parent.postMessage({context: context}, '*', [channel.port2]);
});

window.addEventListener(
    'message',
    function (event) {
        if (event.data === 'start') {
            chatbot.initBot();
            chatbot.initQAndA()
        }
        if (event.data.sendTopic) {
            const button = chatbot.inputConfig.buttons.find((button) => button.payload === event.data.sendTopic.topic)
            if (chatbot.convId !== undefined && button !== undefined) {
                chatbot.postAnswerBtn(button)
            }
        }
        if (event.data === 'clearSessionStorage') {
            sessionStorage.clear();
        } else if (event.data === 'refresh') {
            chatbot.refreshBot()
        } else if (event.data === 'conversationExist') {
            parent.postMessage({conversationExist: sessionStorage.convId !== undefined}, '*');
        }
    });

const chatbot = new Vue({
        el: '#q-app',
        updated() {
            const images = document.getElementsByClassName('imgClass');
            const htmls = document.getElementsByClassName('htmlClass');
            for (let i = 0; i < images.length; i++) {
                images[i].addEventListener('click', function (e) {
                    parent.postMessage({pictureModal: this.src}, '*');
                }, false);
            }
            for (let j = 0; j < htmls.length; j++) {
                htmls[j].addEventListener('click', function (e) {
                    parent.postMessage({htmlModal: this.getAttribute('data-html')}, '*');
                }, false);
            }
            if (!chatbot.$refs.input.disable) {
                this.focusInput()
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
                cards: [],
                files: []
            },
            customConfig: {
                reinitializationButton: false,
                backgroundColor: 'grey',
                fontColor: 'black',
                botMessageBackgroundColor: 'grey',
                botMessageFontColor: 'black',
                userMessageBackgroundColor: 'grey',
                userMessageFontColor: 'black',
                fontFamily: 'Arial, Helvetica, sans-serif',
                displayAvatar: true,
                disableNlu: false
            },
            isEnded: false,

            tab: 'bot',
            prevInputConfig: {},
            lastPayload: null,
            processing: false,
            acceptNlu: true,
            rating: false,
            ratingType: 'SIMPLE',
            error: false,
            messages: [],
            keepAction: false,
            menu: false,
            lastUserInteraction: 0,
            watingMessagesStack: [],
            context: {},
            contextMap: {},

            qAndAConfig: {
                qAndAUrl : _qAndABaseUrl,
                questionAnswerList: [],
                filteredQuestionAnswerList:[],
                filterInput : ''
            }
        },
        methods: {

            initBot: function () {
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
            clearSessionStorage() {
                sessionStorage.clear();
            },

            setParametersFromHttpResponse(httpResponse) {
                chatbot.convId = httpResponse.data.metadatas.sessionId;
                chatbot.customConfig.reinitializationButton = httpResponse.data.metadatas.customConfig.reinitializationButton;
                chatbot.customConfig.backgroundColor = httpResponse.data.metadatas.customConfig.backgroundColor;
                chatbot.customConfig.fontColor = httpResponse.data.metadatas.customConfig.fontColor;
                chatbot.customConfig.botMessageBackgroundColor = httpResponse.data.metadatas.customConfig.botMessageBackgroundColor;
                chatbot.customConfig.botMessageFontColor = httpResponse.data.metadatas.customConfig.botMessageFontColor;
                chatbot.customConfig.userMessageBackgroundColor = httpResponse.data.metadatas.customConfig.userMessageBackgroundColor;
                chatbot.customConfig.userMessageFontColor = httpResponse.data.metadatas.customConfig.userMessageFontColor;
                chatbot.customConfig.fontFamily = httpResponse.data.metadatas.customConfig.fontFamily;
                chatbot.customConfig.displayAvatar = httpResponse.data.metadatas.customConfig.displayAvatar;
                chatbot.customConfig.disableNlu = httpResponse.data.metadatas.customConfig.disableNlu;
                chatbot.updateSessionStorage();
                chatbot._handleResponse(httpResponse, false);
            },

            startConversation() {
                chatbot.lastUserInteraction = Date.now();
                const urlPage = window.location.href;
                scanContextKeys(new Map(Object.entries(chatbot.contextMap))).then(value => {
                    chatbot.context = value;
                    if (chatbot.context['url'] === undefined) {
                        chatbot.context['url'] = urlPage;
                    }
                    if (sessionStorage.convId) {
                        chatbot.restoreFromSessionStorage();
                        chatbot._scrollToBottom();
                    } else {
                        this.$http.post(chatbot.botUrl + '/start', {message: null, metadatas: {'context': chatbot.context}})
                            .then(chatbot.setParametersFromHttpResponse)
                            .catch(() => {
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
                    text: [DOMPurify.sanitize(btn.label)],
                    sent: true,
                    bgColor: 'primary',
                    textColor: 'white'
                });

                chatbot.updateSessionStorage();

                chatbot._scrollToBottom();

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
                chatbot.askBot(btn.payload, btn.label, true, null, null, chatbot.rating);
            },
            fileUpload(btn, index) {
                const file = document.getElementById('file_' + index).files[0];
                const reader = new FileReader();
                reader.readAsDataURL(file);
                reader.onload = function (evt) {
                    chatbot.askBot(btn.payload, null, false, evt.target.result, file.name, false);
                };
            }
            ,
            postAnswerText(isRating) {
                let sanitizedString = '';
                if (isRating) {
                    sanitizedString = chatbot.inputConfig.rating.toString();
                    chatbot.inputConfig.showRating = false;
                    chatbot.messages.push({
                        text: null,
                        rating: true,
                        sent: true,
                        bgColor: 'primary',
                        textColor: 'white'
                    });

                } else {
                    sanitizedString = DOMPurify.sanitize(chatbot.inputConfig.responseText.trim()
                        .replace(/(?:\r\n|\r|\n)/g, '<br>'));
                    chatbot.messages.push({
                        text: sanitizedString !== '' ? [sanitizedString] : null,
                        rating: isRating,
                        sent: true,
                        bgColor: 'primary',
                        textColor: 'white'
                    });
                }
                chatbot.updateSessionStorage();

                chatbot._scrollToBottom();

                const response = chatbot.inputConfig.responsePattern === '' ? sanitizedString.replace(/(")/g, '"')
                    : chatbot.inputConfig.responsePattern.replace('#', sanitizedString.replace(/(")/g, '\\"'));
                chatbot.askBot(response, null, false, null, null, isRating);
            },
            _scrollToBottom() {
                const scrollHeight = this.$refs.scroller.$el.children[0].children[0].scrollHeight; // workaround
                this.$refs.scroller.setScrollPosition(scrollHeight, 400);
            }
            ,
            askBot(value, label, isButton, fileContent, fileName, rating) {
                chatbot.prevInputConfig = JSON.parse(JSON.stringify(chatbot.inputConfig));
                chatbot.reinitInput();
                chatbot.lastPayload = value;
                chatbot.processing = true;

                chatbot.lastUserInteraction = Date.now();
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
                    if (chatbot.ratingType === 'SIMPLE') {
                        botInput.message = null
                    }
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
                if (httpResponse.data.metadatas.sessionId && chatbot.convId !== httpResponse.data.metadatas.sessionId) {
                    chatbot.refreshBot(true);
                    chatbot.setParametersFromHttpResponse(httpResponse);

                } else {
                    const responses = httpResponse.data.htmlTexts;
                    const buttons = httpResponse.data.choices;
                    const cards = httpResponse.data.cards;
                    const files = httpResponse.data.files;
                    chatbot.acceptNlu = httpResponse.data.acceptNlu !== undefined ? httpResponse.data.acceptNlu : true;
                    chatbot.rating = httpResponse.data.rating.enabled;
                    chatbot.ratingType = httpResponse.data.rating.type;
                    chatbot.isEnded = httpResponse.data.status === 'Ended' && !isRating;
                    if (httpResponse.data.metadatas && httpResponse.data.metadatas.avatar) {
                        chatbot.botAvatar = 'data:image/png;base64,' + httpResponse.data.metadatas.avatar;
                    }

                    if (httpResponse.data.metadatas && httpResponse.data.metadatas.jsevent) {
                        parent.postMessage({jsevent: httpResponse.data.metadatas.jsevent}, '*');
                    }

                    if (httpResponse.data.metadatas && httpResponse.data.metadatas.welcometour) {
                        parent.postMessage({welcomeTour: httpResponse.data.metadatas.welcometour}, '*');
                    }

                    for (let i = 0; i < responses.length - 1; i++) {
                        chatbot.watingMessagesStack.push({text: responses[i]});
                    }
                    chatbot.watingMessagesStack.push({
                        text: responses[responses.length - 1],
                        buttons: buttons,
                        cards: cards,
                        files: files
                    });

                    chatbot._displayMessages();
                }
            }
            ,
            _displayMessages() {
                if (chatbot.watingMessagesStack.length > 0) {
                    const currentMessage = chatbot.watingMessagesStack.shift();
                    let watingTime = chatbot.lastUserInteraction - Date.now() + chatbot.minTimeBetweenMessages;

                    if (chatbot.devMode === true) {
                        watingTime = 0;
                    }

                    sleep(watingTime).then(function () {
                        chatbot._processResponse(currentMessage);
                        chatbot.lastUserInteraction = Date.now();
                        chatbot._displayMessages();
                    });
                } else {
                    chatbot.inputConfig.showRating = chatbot.rating && chatbot.ratingType === 'SIMPLE' && chatbot.inputConfig.rating === 0;
                    chatbot.processing = false;
                    if (chatbot.keepAction) {
                        chatbot.inputConfig = chatbot.prevInputConfig;
                        chatbot.inputConfig.responseText = '';
                        chatbot.keepAction = false;
                        chatbot.updateSessionStorage();
                    }
                }
            }
            ,
            focusInput() {
                chatbot.$refs.input.focus();
            }
            ,
            _processResponse(response) {
                const lastMsg = chatbot.messages[chatbot.messages.length - 1];
                if (response.text !== undefined && response.text !== '') {
                    if (lastMsg && !lastMsg.sent) {
                        // ajoute un message à un précédent message du bot
                        lastMsg.text.push(response.text);
                    } else {
                        // première réponse du bot
                        if (chatbot.customConfig.displayAvatar) {
                            chatbot.messages.push({
                                avatar: chatbot.botAvatar,
                                text: [response.text],
                                bgColor: 'grey-grdf'
                            });
                        } else {
                            chatbot.messages.push({
                                text: [response.text],
                                bgColor: 'grey-grdf'
                            });
                        }
                    }
                    chatbot.updateSessionStorage();

                }

                if (response.buttons) {
                    response.buttons.forEach(function (value, key) {
                        chatbot.inputConfig.buttons.push(value);
                        chatbot.updateSessionStorage();
                    }, chatbot);
                }

                if (response.cards) {
                    response.cards.forEach(function (value, key) {
                        chatbot.inputConfig.cards.push(value);
                        chatbot.updateSessionStorage();
                    }, chatbot);
                }

                if (response.files) {
                    response.files.forEach(function (value, key) {
                        chatbot.inputConfig.files.push(value);
                        chatbot.updateSessionStorage();
                    }, chatbot);
                }

                chatbot._scrollToBottom();
            },
            reinitInput() {
                chatbot.inputConfig.modeTextarea = false;
                chatbot.inputConfig.responsePattern = '';
                chatbot.inputConfig.responseText = '';
                chatbot.inputConfig.buttons = [];
                chatbot.inputConfig.cards = [];
                chatbot.inputConfig.files = [];
                chatbot.error = false;
                chatbot.updateSessionStorage();
            },
            minimize() {
                parent.postMessage('Chatbot.minimize', '*');
            },
            refreshBot(isAnotherConversation) {
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
                chatbot.customConfig = {};
                chatbot.error = false;
                chatbot.convId = null;
                chatbot.contextMap = {};
                chatbot.messages = [];
                chatbot.rating = false;

                chatbot.clearSessionStorage();

                if (!isAnotherConversation) {
                    chatbot.initBot();
                }
            },

            refresh(){
                if(chatbot.tab === 'qAndA'){
                    chatbot.initQAndA()
                }else{
                    chatbot.refreshBot()

                }
            },
            // Function to complete to switch to another bot
            changeLocal(locale) {
                //todo : change url bot to simulate language change
            },

            initQAndA(){
                this.$http.get(chatbot.qAndAConfig.qAndAUrl + '/getQuestionsAnswers').then(questionAnswerResponse => {
                    chatbot.qAndAConfig.questionAnswerList = questionAnswerResponse.data;
                    chatbot.qAndAConfig.filteredQuestionAnswerList = questionAnswerResponse.data;
                    chatbot.qAndAConfig.filterInput = '';
                });
            },

            filterQuestionsAnswers(){
                if (chatbot.qAndAConfig.filterInput !== ''){
                    chatbot.qAndAConfig.filteredQuestionAnswerList = chatbot.qAndAConfig.questionAnswerList.filter(questionAnswer => questionAnswer.question.toLowerCase().includes(chatbot.qAndAConfig.filterInput) || questionAnswer.answer.toLowerCase().includes(chatbot.qAndAConfig.filterInput))
                }else{
                    chatbot.qAndAConfig.filteredQuestionAnswerList = chatbot.qAndAConfig.questionAnswerList
                }
            }
        }
    })
;
