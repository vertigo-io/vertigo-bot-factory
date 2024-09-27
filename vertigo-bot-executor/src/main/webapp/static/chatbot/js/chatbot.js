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
const _platformRunnerUrl = urlVars['runnerUrl'];
const _platformName = decodeURI(urlVars['botName']);
const _avatar = _platformRunnerUrl + '/static/chatbot/images/avatar/avatar.png';

const _platformBaseUrl = _platformRunnerUrl + '/api/platform-config';
const _botBaseUrl = _platformRunnerUrl + '/api/chatbot';
const _qAndABaseUrl = _platformRunnerUrl + '/api/qanda';
const _documentaryResourceBaseUrl = _platformRunnerUrl + '/api/docres';

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
    async function (event) {
        if (event.data === 'start') {
            await chatbot.initPlatform();
        }
        if (event.data.sendTopic) {
            const button = chatbot.inputConfig.buttons.find((button) => button.payload === event.data.sendTopic.topic)
            if (chatbot.botConfig.convId !== undefined && button !== undefined) {
                postAnswerBtn(button)
            }
        }
        if (event.data === 'clearSessionStorage') {
            sessionStorage.clear();
        } else if (event.data === 'refresh') {
            chatbot.refreshPlatform()
            console.log('refresh event')
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
            if (chatbot.$refs.input && !chatbot.$refs.input.disable) {
                this.focusInput()
            }
        },
        data: {
            // config

            footerMenu: '',
            platformUrl: _platformBaseUrl,
            context: {},
            contextMap: {},
            platformName: _platformName,

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
                disableNlu: false,
                chatbotDisplay: false,
                qandaDisplay: false,
                documentaryResourceDisplay: false
            },
            botConfig: {
                convId: null,
                botUrl: _botBaseUrl,
                devMode: false, // ##param.devMode##
                minTimeBetweenMessages: 1000,
                botAvatar: _avatar,
                isEnded: false,
                prevInputConfig: {},
                lastPayload: null,
                processing: false,
                acceptNlu: true,
                rating: false,
                ratingType: 'SIMPLE',
                error: false,
                messages: [],
                keepAction: false,
                lastUserInteraction: 0,
                watingMessagesStack: [],
            },
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

            qAndAConfig: {
                qAndAUrl: _qAndABaseUrl,
                questionAnswerList: [],
                filteredQuestionAnswerList: [],
                filterInput: '',
                selectedQuestion: null
            },
            documentaryResourceConfig: {
                documentaryResourceUrl: _documentaryResourceBaseUrl,
                documentaryResourceList: [],
                documentaryResourceFileBaseUrl: _documentaryResourceBaseUrl + '/getDocumentaryResourceFile?label='
            }
        },
        methods: {

            async initPlatform(){
                // waiting for the custom config to set the display params
                await chatbot.initPlatformConfig()
                if(chatbot.customConfig.chatbotDisplay) {startConversation()}
                if(chatbot.customConfig.qandaDisplay) {initQAndA()}
                if(chatbot.customConfig.documentaryResourceDisplay) {initDocumentaryResources()}
                chatbot.initLayout()
            },

            async initPlatformConfig() {
                document.getElementById('page').style.visibility = 'visible';
                document.getElementById('loading').style.display = 'none';

                try {
                    await this.$http.get(chatbot.platformUrl + '/getCustomConfig').then(httpResponse => {
                        chatbot.customConfig.reinitializationButton = httpResponse.body.reinitializationButton;
                        chatbot.customConfig.backgroundColor = httpResponse.body.backgroundColor;
                        chatbot.customConfig.fontColor = httpResponse.body.fontColor;
                        chatbot.customConfig.botMessageBackgroundColor = httpResponse.body.botMessageBackgroundColor;
                        chatbot.customConfig.botMessageFontColor = httpResponse.body.botMessageFontColor;
                        chatbot.customConfig.userMessageBackgroundColor = httpResponse.body.userMessageBackgroundColor;
                        chatbot.customConfig.userMessageFontColor = httpResponse.body.userMessageFontColor;
                        chatbot.customConfig.fontFamily = httpResponse.body.fontFamily;
                        chatbot.customConfig.displayAvatar = httpResponse.body.displayAvatar;
                        chatbot.customConfig.disableNlu = httpResponse.body.disableNlu;
                        chatbot.customConfig.chatbotDisplay = httpResponse.body.chatbotDisplay;
                        chatbot.customConfig.qandaDisplay = httpResponse.body.qandaDisplay;
                        chatbot.customConfig.documentaryResourceDisplay = httpResponse.body.documentaryResourceDisplay;
                        sessionStorage.setItem('customConfig', JSON.stringify(chatbot.customConfig));
                    });
                } catch (error) {
                console.error('Erreur lors de la récupération de la configuration du chatbot', error);
            }
                if (sessionStorage.contextMap) {
                    chatbot.contextMap = JSON.parse(sessionStorage.contextMap);
                } else {
                    this.$http.post(chatbot.botConfig.botUrl + '/context').then(contextResponse => {
                        chatbot.contextMap = contextResponse.data;
                        sessionStorage.setItem('contextMap', JSON.stringify(chatbot.contextMap));
                    });
                }
            },
            minimize() {
                parent.postMessage('Chatbot.minimize', '*');
            },

            refreshPlatform(){
                chatbot.initPlatformConfig()
                if (chatbot.footerMenu === 'bot') {
                    refreshBot()
                } else if (chatbot.footerMenu === 'qAndA') {
                    initQAndA();
                }
                if (chatbot.customConfig.documentaryResourceDisplay) {
                    initDocumentaryResources();
                }
            },
            // Function to complete to switch to another bot
            changeLocal(locale) {
                //todo : change url bot to simulate language change
            },

            initLayout(){
                // On start, if there is only the Q&A on, we show it. Otherwise, we open on the chatbot by default
                if (chatbot.customConfig.qandaDisplay && !chatbot.customConfig.chatbotDisplay){
                    chatbot.footerMenu = 'qAndA'
                } else {
                    chatbot.footerMenu = 'bot'
                }
            },


            footerMenuChange(menuValue) {
                chatbot.footerMenu = menuValue
                if(menuValue === 'bot'){
                    this.$nextTick(() => {
                        // instant scroll to bottom of the bot layout when clicking on 'bot' menu
                        const scrollHeight = chatbot.$refs.scroller.$el.children[0].children[0].scrollHeight; // workaround
                        chatbot.$refs.scroller.setScrollPosition(scrollHeight);
                        // if a question/answer is open when clicking on 'bot' menu, it closes it
                        chatbot.qAndAConfig.selectedQuestion = null;
                    });
                }
            },

            focusInput() {
                chatbot.$refs.input.focus();
            }
        }
    })
;
