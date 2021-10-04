Vue.component('v-chatbot-dev', {
		template : `
	<div class="bot">
		<q-scroll-area class="bg-grey-2 col-grow row q-pa-sm" ref="scroller">
			<div class="q-pr-md">
				<div v-for="(msg, index) in messages">
					<q-chat-message v-if="msg.rating" class= "animate-fade" :key="'msgRating-'+index" :sent="msg.sent" :bg-color= "msg.bgColor" :avatar = "msg.avatar" >
						<q-rating v-model="msg.rating" style="font-size: 2rem;" readonly ></q-rating>
					</q-chat-message>
					<q-chat-message v-if="msg.text" class="animate-fade" :key="'msg-'+index" :label="msg.label" :text-sanitize="msg.sent" :sent="msg.sent" :text-color="msg.textColor"
						:bg-color="msg.bgColor" :name="msg.name" :avatar="msg.avatar" :text="msg.text" :stamp="msg.stamp" ></q-chat-message>
				</div>
				<div class="sys-chat">
					<q-chat-message v-if="error" class="animate-fade" bg-color="orange-4" text-color="black" size="12" >
						<div class="q-pb-sm">
							{{$q.lang.vui.chatbot.errorMessage}}
						</div>
						<q-btn class="full-width" @click="postAnswerBtn(lastPayload)" :label="$q.lang.vui.chatbot.tryAgain" color="white" text-color="black" ></q-btn>
					</q-chat-message>
				</div>
				<div class="sys-chat non-selectable">
					<q-chat-message v-if="inputConfig.buttons.length > 0" class="animate-fade" bg-color="primary" size="12">
						<div class="text-blue-2 q-caption">
							{{$q.lang.vui.suggestedAnswers}}
						</div>
						<div class="row docs-btn">
							<q-btn v-for="(btn, index) in inputConfig.buttons" class="full-width" :key="'repChatBtn-'+index" @click="postAnswerBtn(btn)" :label="btn.label" color="white" text-color="black" ></q-btn>
						</div>
					</q-chat-message>
				</div>
				<div class="message-processing sys-chat non-selectable" >
					<q-chat-message v-if="processing" class="animate-fade" bg-color="grey-4">
						<q-spinner-dots size="2em"></q-spinner-dots>
					</q-chat-message>
				</div>
				<div class="non-selectable">
					<q-chat-message v-if="inputConfig.showRating" class="animate-fade" bg-color="primary" sent >
						<q-rating v-model="rating" style="font-size: 2rem;" 
							icon="star_border"
       						icon-selected="star"
        					icon-half="star_half" 
        					@input="rateBot"></q-rating>
					</q-chat-message>
				</div>
			</div>
		</q-scroll-area>
		<div class="message-response row docs-btn q-pl-sm non-selectable">
			<q-input :type="inputConfig.modeTextarea ? 'textarea' : 'text'"
					 ref="input" dense
					 @keydown.enter.prevent="inputConfig.modeTextarea ? false : (inputConfig.responseText.trim() === '' && inputConfig.rating === 0) ? false : postAnswerText()"
					 :max-height="100"
					 class="col-grow"
					 v-model="inputConfig.responseText"
					 :placeholder="placeholder ? placeholder : $q.lang.vui.chatbot.inputPlaceHolder"
					 :disable="processing || error"
					 :loading="processing"></q-input>
		
			<q-btn round color="primary" icon="send" @click="postAnswerText()" :disable="processing || (inputConfig.responseText.trim() === '' && inputConfig.rating === 0)"></q-btn>
		</div>
		<div class="message-response row docs-btn q-pl-sm non-selectable justify-center" v-if="devMode === true">
			<q-btn round color="black" icon="arrow_back" @click="restart(false)"><q-tooltip>Back one step</q-tooltip></q-btn>
			<q-btn round color="red" icon="refresh" @click="restart(false)"><q-tooltip>Restart conversation</q-tooltip></q-btn>
		</div>
	</div>
	`
		,
		props : {
			botUrl : { type: String, required:true },
			devMode: { type: Boolean, 'default': false },
			minTimeBetweenMessages: { type: Number, 'default': 1000 },
			botAvatar: { type: String, required:true },
			botName: { type: String, required:true },
			placeholder: { type: String },
			startCall: {type:String, 'default' : '_start'},
			rateCall: {type:String, 'default' : '_rate'},
			rating: {type:Number}
		},
		data: function () {
			return {
				// config
				convId: null,
				// technique
				inputConfig: {
					modeTextarea : false, // TODO, il exste d'autres modes, par ex email
					responseText: "",
					responsePattern : "",
					showRating : false,
					rating: 0,
					buttons: [],
				},
				prevInputConfig: {},
				lastPayload: null,
				processing: false,
				error: false,
				messages: [],
				keepAction: false,
				menu: false,
				lastUserInteraction: 0,
				watingMessagesStack: [],
				rating: 0,
			}
		},
		created : function (){
			this.startConversation();
		},
		methods: {
			startConversation: function (){
			this.lastUserInteraction = Date.now();
			urlPage =  window.location.href
			context = {}
			context['url'] =  urlPage
			context['user'] = 'toto'
 			this.$http.post(this.startCall, {message: null, metadatas: {'context' : context}})
					.then(httpResponse => {
							this.convId = httpResponse.data.metadatas.sessionId
							this._handleResponse(httpResponse, false)
						}).catch(error => {
						// error
						this.error = true;
						this.processing = false;
						this._scrollToBottom();
					});
			},
			postAnswerBtn: function (btn) {
				this.messages.push({
					text: [btn.label],
					sent: true,
					bgColor: "primary",
					textColor: "white"
				});

				this._scrollToBottom();
				context = {}
				context['url'] =  urlPage
				context['user'] = 'toto'
				let botInput = {sender: this.convId, message: null, metadatas:{payload : btn.payload, 'context' : context}}
				this.askBot(botInput);
			},
			postAnswerText: function () {
				var sanitizedString = this.inputConfig.responseText.trim().replace(/(?:\r\n|\r|\n)/g, '<br>');
				
				this.messages.push({
					text: sanitizedString !== '' ? [sanitizedString] : null,
					rating: this.inputConfig.rating > 0 ? this.inputConfig.rating : null,
					sent: true,
					bgColor: "primary",
					textColor: "white"
				});

				this._scrollToBottom();
				
				var response = this.inputConfig.responsePattern === "" ? sanitizedString.replace(/(")/g, "\"")
															  : this.inputConfig.responsePattern.replace("#", sanitizedString.replace(/(")/g, "\\\""));
	  			context = {}
				context['url'] =  urlPage
				context['user'] = 'toto'
				let botInput = {sender: this.convId, message: response, metadatas:{'context' : context}}
				this.askBot(botInput);
			},
			_scrollToBottom: function () {
				if (this.$refs.scroller) {
					this.$refs.scroller.setScrollPosition(this.$refs.scroller.scrollSize, 400);
				}
			},
			askBot: function (value) {
				this.prevInputConfig = JSON.parse(JSON.stringify(this.inputConfig));
				this.reinitInput();
				this.lastPayload = value;
				this.processing = true;
				
				this.lastUserInteraction = Date.now();
				
				this.$http.post(this.botUrl, value)
					.then(httpResponse => {
						// success
						this._handleResponse(httpResponse, false);
					}).catch(error => {
						// error
						this.error = true;
						
						this.processing = false;
						this._scrollToBottom();
					});
					
			},
			_handleResponse: function(httpResponse, isRating) {
				// success
				var responses = httpResponse.data.htmlTexts;
				var buttons = httpResponse.data.choices;
				var isEnded = httpResponse.data.status  === 'Ended'
				isEnded &= !isRating
								
				for (var i = 0; i < responses.length - 1; i++){
					this.watingMessagesStack.push({text: responses[i]})
				}
				this.watingMessagesStack.push({text: responses[responses.length -1], buttons : buttons})
				
				this._displayMessages(isEnded);
			},
			_displayMessages: function (isEnded) {
				if (this.watingMessagesStack.length > 0) {
					var currentMessage = this.watingMessagesStack.shift();
					var watingTime = this.lastUserInteraction - Date.now() + this.minTimeBetweenMessages;
					
					this.sleep(watingTime).then(() => {
						this._processResponse(currentMessage);
						this.lastUserInteraction = Date.now();
						this._displayMessages(isEnded);
					});
				} else {
					this.processing = false;
					if (this.keepAction) {
						this.inputConfig = this.prevInputConfig;
						this.inputConfig.responseText = "";
						this.keepAction = false;
					}
					
					this.sleep(1).then(() => { // en différé le temps que la vue soit mise à jour
						this.inputConfig.showRating = isEnded;
						this.$refs.input.focus();
					});
					
				}
			},
			_processResponse: function (response) {
			var lastMsg = this.messages[this.messages.length-1];
				if (lastMsg && !lastMsg.sent && !lastMsg.sys) {
					// ajoute un message à un précédent message du bot
					lastMsg.text.push(response.text);
				} else {
					// première réponse du bot
					this.messages.push({
						avatar: this.botAvatar,
						text: [response.text],
						bgColor: "grey-4"
					});
				}
				
				if (response.buttons) {
					response.buttons.forEach(function(value, key) {
						this.inputConfig.buttons.push(value);
					}, this);
				}
				
				this._scrollToBottom();
			},
			restart: function (silent) {
				if (!silent) {
					this.systemMessage(this.$q.lang.vui.chatbot.restartMessage);
				}
				this.reinitInput();
				this.startConversation(); // lancement de la phrase d'accueil
				
			},
			reinitInput: function () {
				this.inputConfig.modeTextarea = false;
				this.inputConfig.responsePattern = "";
				this.inputConfig.responseText = "";
				this.inputConfig.showRating = false;
				this.inputConfig.rating = 0;
				this.inputConfig.buttons = [];
				this.error = false;
			},
			sleep: function(milliseconds) {
			  return new Promise(function(resolve) {setTimeout(resolve, milliseconds)});
			},
			systemMessage: function(msg) {
				this.messages.push({
					text: [msg],
					bgColor: "orange",
					sys: true
				});
				this._scrollToBottom();
			},
			rateBot: function(value){
				this.$http.post(this.rateCall, {sender: this.convId, note: value})
					.then(httpResponse => {
						httpResponse.data = {htmlTexts : ["Merci de m'avoir noté"]}
						this._handleResponse(httpResponse, true);
						
					})
			}
		}
});
