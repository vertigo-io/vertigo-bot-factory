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
const _useRating = urlVars['useRating'];
const _avatar = _botRunnerUrl + "/static/chatbot/images/avatar/avatar.png"

const _botBaseUrl = _botRunnerUrl + "/api/chatbot";

const scanContextKeys = () => new Promise((res, rej) => {
    const channel = new MessageChannel();
    channel.port1.onmessage = ({data}) => {
        channel.port1.close();
        if (data.error) {
            rej(data.error);
        }else {
            res(data.result);
        }
    };

    parent.postMessage("scanContext", "*", [channel.port2]);
});

const chatbot = new Vue({
        el: "#q-app",
        updated: function() {
            const images = document.getElementsByClassName('imgClass');
            for(let i=0; i<images.length; i++){
                images[i].addEventListener('click', function(e){
                    parent.postMessage({pictureModal: this.src}, "*");
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
                modeTextarea: false, // TODO, il exste d'autres modes, par ex email
                responseText: "",
                responsePattern: "",
                useRating: _useRating === "true",
                showRating: false,
                rating: 0,
                buttons: []
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
            context: {}
        },
        methods: {

            initBot: function() {
                document.getElementById("page").style.visibility = "visible";
                document.getElementById("loading").style.display = "none";

                chatbot.startConversation(); // lancement de la phrase d'accueil
            },
            updateSessionStorage: function () {
                sessionStorage.convId = chatbot.convId;
                sessionStorage.inputConfig = chatbot.inputConfig;
                sessionStorage.setItem('inputConfig', JSON.stringify(chatbot.inputConfig));
                sessionStorage.setItem('messages', JSON.stringify(chatbot.messages));
            },
            restoreFromSessionStorage: function () {
                chatbot.convId = sessionStorage.convId;
                chatbot.inputConfig = JSON.parse(sessionStorage.inputConfig);
                chatbot.messages = JSON.parse(sessionStorage.messages);
            },
            startConversation: function() {
                chatbot.lastUserInteraction = Date.now();
                const urlPage =  window.location.href
                scanContextKeys().then(value => {
                    chatbot.context = value;
                    if(chatbot.context['url'] === undefined) {
                        chatbot.context['url'] =  urlPage
                    }
                    if (sessionStorage.convId) {
                        chatbot.restoreFromSessionStorage();
                        chatbot._scrollToBottom();
                    } else {

                        this.$http.post(chatbot.botUrl + "/start", {message: null, metadatas: {'context': chatbot.context}})
                            .then(httpResponse => {
                                chatbot.convId = httpResponse.data.metadatas.sessionId;
                                chatbot.updateSessionStorage()
                                chatbot._handleResponse(httpResponse, false);
                            }).catch(error => {
                            // error
                            chatbot.error = true;
                            chatbot.processing = false;
                            chatbot._scrollToBottom();
                        });
                    }
                });
            },
            postAnswerBtn: function(btn) {
                chatbot.messages.push({
                    text: [btn.label],
                    sent: true,
                    bgColor: "primary",
                    textColor: "white"
                });

                chatbot.updateSessionStorage()

                chatbot._scrollToBottom();

                chatbot.askBot(btn.payload, true);
            }
            ,
            postAnswerText: function() {
                const sanitizedString = chatbot.inputConfig.responseText.trim().replace(/(?:\r\n|\r|\n)/g, "<br>");

                chatbot.messages.push({
                    text: sanitizedString !== "" ? [sanitizedString] : null,
                    rating: chatbot.inputConfig.rating > 0 ? chatbot.inputConfig.rating : null,
                    sent: true,
                    bgColor: "primary",
                    textColor: "white"
                });
                chatbot.updateSessionStorage()

                chatbot._scrollToBottom();

                const response = chatbot.inputConfig.responsePattern === "" ? sanitizedString.replace(/(")/g, "\"")
                    : chatbot.inputConfig.responsePattern.replace("#", sanitizedString.replace(/(")/g, "\\\""));

                chatbot.askBot(response, false);
            }
            ,
            _scrollToBottom: function() {
                // var scrollHeight = this.$refs.scroller.scrollHeight; // don't work on mobile
                const scrollHeight = this.$refs.scroller.$el.children[0].children[0].scrollHeight; // workaround
                this.$refs.scroller.setScrollPosition(scrollHeight, 400);
            }
            ,
            askBot: function(value, isButton) {
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
                return Vue.http.post(chatbot.botUrl + "/talk/" + chatbot.convId, botInput)
                    .then(httpResponse => {
                        chatbot._handleResponse(httpResponse, false);
                    }).catch(
                        error => {
                            // error
                            chatbot.error = true;

                            chatbot.processing = false;
                            chatbot._scrollToBottom();
                        });

            }
            ,
            _handleResponse: function(httpResponse, isRating) {
                // success
                const responses = httpResponse.data.htmlTexts;
                const buttons = httpResponse.data.choices;
                chatbot.isEnded = httpResponse.data.status === "Ended" && !isRating;


                if (httpResponse.data.metadatas && httpResponse.data.metadatas.jsevent) {
                    parent.postMessage({jsevent: httpResponse.data.metadatas.jsevent}, "*");
                }

                for (let i = 0; i < responses.length - 1; i++) {
                    chatbot.watingMessagesStack.push({ text: responses[i] });
                }
                chatbot.watingMessagesStack.push({ text: responses[responses.length - 1], buttons: buttons });

                chatbot._displayMessages();
            }
            ,
            _displayMessages: function() {
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
                        chatbot.inputConfig.responseText = "";
                        chatbot.keepAction = false;
                        chatbot.updateSessionStorage();
                    }

                    sleep(1).then(function() { // en différé le temps que la vue soit mise à jour
                        if (chatbot.inputConfig.useRating) { // si les dialogues sont fermés
                            chatbot.inputConfig.showRating = chatbot.isEnded;
                            chatbot.updateSessionStorage();
                            chatbot.focusInput();
                        }
                    });
                }
            }
            ,
            focusInput: function() {
                chatbot.$refs.input.focus();
            }
            ,
            _processResponse: function(response) {
                const lastMsg = chatbot.messages[chatbot.messages.length - 1];
                if (response.text !== "") {
                    if (lastMsg && !lastMsg.sent) {
                        // ajoute un message à un précédent message du bot
                        lastMsg.text.push(response.text);
                    } else {
                        // première réponse du bot
                        chatbot.messages.push({
                            avatar: chatbot.botAvatar,
                            text: [response.text],
                            bgColor: "grey-grdf"
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

                chatbot._scrollToBottom();
            },
            reinitInput: function() {
                chatbot.inputConfig.modeTextarea = false;
                chatbot.inputConfig.responsePattern = "";
                chatbot.inputConfig.responseText = "";
                // chatbot.evalDialog = false;
                chatbot.inputConfig.rating = 0;
                chatbot.inputConfig.buttons = [];
                chatbot.error = false;
                chatbot.updateSessionStorage();
            },
            minimize: function() {
                parent.postMessage("Chatbot.minimize", "*");
            },
            rateBot: function(value){
                this.$http.post(chatbot.botUrl + "/rating", {sender: chatbot.convId, note: value})
                    .then(httpResponse => {
                        httpResponse.data = {htmlTexts : ["Merci de m'avoir noté"]}
                        chatbot._handleResponse(httpResponse, true);

                    })
            },
            close: function() {
                parent.postMessage("Chatbot.close", "*");
            }
        }
    })
;

chatbot.initBot();