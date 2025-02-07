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
                chatbot.postAnswerBtn(button)
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


const chatbotComponent = {
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
        if (this.$refs.input && !this.$refs.input.disable) {
            this.focusInput()
        }
    },
    data() {
        return {
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
                documentaryResourceFileBaseUrl: _documentaryResourceBaseUrl + '/getDocumentaryResourceFile?attId='
            }
        }
    },
    methods: {
        async initPlatform(){
            // waiting for the custom config to set the display params
            await this.initPlatformConfig()
            if(this.customConfig.chatbotDisplay) {startConversation()}
            if(this.customConfig.qandaDisplay) {initQAndA()}
            if(this.customConfig.documentaryResourceDisplay) {initDocumentaryResources()}
            this.initLayout()
        },

        async initPlatformConfig() {
            document.getElementById('page').style.visibility = 'visible';
            document.getElementById('loading').style.display = 'none';

            try {
                await axios.get(this.platformUrl + '/getCustomConfig').then(httpResponse => {
                    this.customConfig.reinitializationButton = httpResponse.data.reinitializationButton;
                    this.customConfig.backgroundColor = httpResponse.data.backgroundColor;
                    this.customConfig.fontColor = httpResponse.data.fontColor;
                    this.customConfig.botMessageBackgroundColor = httpResponse.data.botMessageBackgroundColor;
                    this.customConfig.botMessageFontColor = httpResponse.data.botMessageFontColor;
                    this.customConfig.userMessageBackgroundColor = httpResponse.data.userMessageBackgroundColor;
                    this.customConfig.userMessageFontColor = httpResponse.data.userMessageFontColor;
                    this.customConfig.fontFamily = httpResponse.data.fontFamily;
                    this.customConfig.displayAvatar = httpResponse.data.displayAvatar;
                    this.customConfig.disableNlu = httpResponse.data.disableNlu;
                    this.customConfig.chatbotDisplay = httpResponse.data.chatbotDisplay;
                    this.customConfig.qandaDisplay = httpResponse.data.qandaDisplay;
                    this.customConfig.documentaryResourceDisplay = httpResponse.data.documentaryResourceDisplay;
                    sessionStorage.setItem('customConfig', JSON.stringify(this.customConfig));
                });
            } catch (error) {
                console.error('Erreur lors de la récupération de la configuration du chatbot', error);
            }
            if (sessionStorage.contextMap) {
                this.contextMap = JSON.parse(sessionStorage.contextMap);
            } else {
                axios.post(this.botConfig.botUrl + '/context').then(contextResponse => {
                    this.contextMap = contextResponse.data;
                    sessionStorage.setItem('contextMap', JSON.stringify(this.contextMap));
                });
            }
        },
        minimize() {
            parent.postMessage('Chatbot.minimize', '*');
        },

        refreshPlatform(){
            this.initPlatformConfig()
            if (this.footerMenu === 'bot') {
                refreshBot()
            } else if (this.footerMenu === 'qAndA') {
                initQAndA();
            }
            if (this.customConfig.documentaryResourceDisplay) {
                initDocumentaryResources();
            }
        },
        // Function to complete to switch to another bot
        changeLocal(locale) {
            //todo : change url bot to simulate language change
        },

        initLayout(){
            // On start, if there is only the Q&A on, we show it. Otherwise, we open on the chatbot by default
            if (this.customConfig.qandaDisplay && !this.customConfig.chatbotDisplay){
                this.footerMenu = 'qAndA'
            } else {
                this.footerMenu = 'bot'
            }
        },

        footerMenuChange(menuValue) {
            this.footerMenu = menuValue
            if(menuValue === 'qAndA'){
                initQAndA();
            }
            if(menuValue === 'bot'){
                this.$nextTick(() => {
                    // instant scroll to bottom of the bot layout when clicking on 'bot' menu
                    _scrollToBottom();
                    // if a question/answer is open when clicking on 'bot' menu, it closes it
                    this.qAndAConfig.selectedQuestion = null;
                });
            }
            // always refresh documentaryResources on tab change if visible
            if (this.customConfig.documentaryResourceDisplay) {
                initDocumentaryResources();
            }
        },

        focusInput() {
            this.$refs.input.focus();
        },

        askBot(value, label, isButton, fileContent, fileName, rating) {
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
            return axios.post(chatbot.botConfig.botUrl + '/talk/' + chatbot.botConfig.convId, botInput)
                .then(httpResponse => {
                    _handleResponse(httpResponse, false);
                }).catch(
                    () => {
                        // error
                        chatbot.botConfig.error = true;

                        chatbot.botConfig.processing = false;
                        _scrollToBottom();
                    });

        },

        postAnswerText(isRating) {
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
            this.askBot(response, null, false, null, null, isRating);
        },

        postAnswerBtn(btn) {
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
            this.askBot(btn.payload, btn.label, true, null, null, chatbot.botConfig.rating);
        },

        fileUpload(btn, index) {
            const file = document.getElementById('file_' + index).files[0];
            const reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onload = function (evt) {
                this.askBot(btn.payload, null, false, evt.target.result, file.name, false);
            };
        },

        filterQuestionsAnswers(){
            if (chatbot.qAndAConfig.filterInput !== ''){
                chatbot.qAndAConfig.filteredQuestionAnswerList = chatbot.qAndAConfig.questionAnswerList.filter(questionAnswer => questionAnswer.question.toLowerCase().includes(chatbot.qAndAConfig.filterInput.toLowerCase()) || questionAnswer.answer.toLowerCase().includes(chatbot.qAndAConfig.filterInput.toLowerCase()))
            }else{
                chatbot.qAndAConfig.filteredQuestionAnswerList = chatbot.qAndAConfig.questionAnswerList
            }
        },

        selectQuestion(selected){
            chatbot.qAndAConfig.selectedQuestion = selected;
        }
    }
};

const { createApp } = Vue;
const chatbotApp = createApp(chatbotComponent);

chatbotApp.use(Quasar);

const chatbot = chatbotApp.mount("#q-app");


function _scrollToBottom() {
    const scrollHeight = chatbot.$refs.scroller.$el.children[0].children[0].scrollHeight; // workaround
    chatbot.$refs.scroller.setScrollPosition('vertical', scrollHeight, 400);
}
