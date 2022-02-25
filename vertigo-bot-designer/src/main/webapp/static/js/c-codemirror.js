Vue.component('c-codemirror', {
	props : {
		modeEdit:		{ type: Boolean, 'default': true },
		object:			{ type: String,  required: true },
		field:			{ type: String,  required: true },
		compositeNode: 	{ type: String,  'default': 'Noeud composite'},
		simpleNode: 	{ type: String,  'default': 'Noeud simple'},
		locale:   		{ type: String, 'default': 'en_US' },
		error : false
	},
	data: function () {
		return {
			options :{
		 				indentWithTabs:true,
		 				indentUnit: 4,
		 				tabSize: 4,
		 				lineNumbers : true,
		 				mode: 'chatbot',
		 				extraKeys: {"Ctrl-Space": "autocomplete"},
		 				readOnly : !this.modeEdit
		 			}
		}
	},
	template : 
		`	<div>
				<q-btn-dropdown class="q-ma-sm" v-if="modeEdit"  color="primary" :label=compositeNode>
					<q-list>
						<q-item clickable v-close-popup @click="addSequence">
							<q-item-section>
								<q-item-label>Sequence</q-item-label>
							</q-item-section>
						</q-item>
				
						<q-item clickable v-close-popup @click="addChooseButton">
							<q-item-section>
								<q-item-label>choose Button</q-item-label>
							</q-item-section>
						</q-item>
				
						<q-item clickable v-close-popup @click="addChooseButtonNlu">
							<q-item-section>
								<q-item-label>chooseButtonNlu</q-item-label>
							</q-item-section>
						</q-item>
						
						<q-item clickable v-close-popup @click="addSwitch">
							<q-item-section>
								<q-item-label>switch</q-item-label>
							</q-item-section>
						</q-item>
						
						<q-item clickable v-close-popup @click="addCase">
							<q-item-section>
								<q-item-label>case</q-item-label>
							</q-item-section>
						</q-item>
					</q-list>
				</q-btn-dropdown>
				
				<q-btn-dropdown class="q-ma-sm" v-if="modeEdit" color="primary" :label=simpleNode>
					<q-list>
						<q-item clickable v-close-popup @click="addButton">
							<q-item-section>
								<q-item-label>Button</q-item-label>
							</q-item-section>
						</q-item>
						
						<q-item clickable v-close-popup @click="addSay">
							<q-item-section>
								<q-item-label>Say</q-item-label>
							</q-item-section>
						</q-item>
						
						<q-item clickable v-close-popup @click="addInputString">
							<q-item-section>
								<q-item-label>inputString</q-item-label>
							</q-item-section>
						</q-item>
					</q-list>
				</q-btn-dropdown>
				<q-btn v-if="modeEdit" label="Emoji" color="primary" @click="addEmoji()"/>
				<div :style="error ? 'border: 2px solid;border-color: #C10015;border-radius:5px;':''">
					<codemirror ref="cm" v-model='VertigoUi.vueData[object][field]' :options="options"></codemirror>
				</div>
				<input type="hidden" :name="'vContext['+object+']['+field+']'" :value="VertigoUi.vueData[object][field]"/>
				<q-dialog ref="newEmoji" id="newEmoji" >
					<q-card>
						<q-card-section>
							<div class="text-h6" >{{locale == 'fr_FR' ? 'Ajouter une emoji' : 'Add emoji'}}</div>
						</q-card-section>
						<q-card-section>
							<emoji-picker></emoji-picker>
						</q-card-section>
					</q-card
				</q-dialog>
			</div>
		`
		,
		methods: {
			//getCursor position and line
		 	 getCursor : function(){
		 		const cursor = this.$refs.cm.codemirror.getCursor();
		 		return {line : cursor.line, ch: cursor.ch};
		 	},
		 	
		 	//Create the string to add
		 	//Get only the first string if it's simple node i.e. say
		 	//Get tabulation for end composite node
		 	createStringToAdd : function (beginString, endString, currentLine){
		 		var stringToAdd = beginString + '\n';
		 		if (endString == null){
		 			return stringToAdd;
		 		}
		 		//matchs the tabulation pattern
		 		var count = (currentLine.match(/\t/g) || []).length;
		 		for (let i = 0; i < count; i++){
		 			endString =  '\t'.concat(endString);
		 		}
		 		return stringToAdd + endString
		 	},
		 	
		 	//Add string of node into the editor value
		 	//addEndString must be null for simple node
		 	modifyValue : function (addBeginString, addEndString){
		 		var position = this.getCursor();
		 		const value = this.$refs.cm.codemirror.getValue();
		 		const lines = value.split('\n');
		 		chs = lines[position.line];
		 		stringToAdd = this.createStringToAdd(addBeginString, addEndString, chs);
		 		lines[position.line] = chs.substring(0,position.ch) + stringToAdd + chs.substring(position.ch, chs.length);
		 		this.$refs.cm.codemirror.setValue(lines.join('\n'));
		 	},
		 	
		 	addSequence : function (){
		 		this.modifyValue('begin sequence','end sequence');
		 	},
		 	
		 	addChooseButton : function(){
		 		this.modifyValue('begin choose:button /user/local ""','end choose:button');
		 	},
		 	
		 	addChooseButtonNlu : function(){
		 		this.modifyValue('begin choose:button:nlu /user/local ""','end choose:button:nlu');
		 	},
		 	
		 	addSwitch : function(){
		 		this.modifyValue('begin switch /user/local ','end switch');
		 	},
		 	
		 	addCase : function(){
		 		this.modifyValue('begin case ""','end case');
		 	},
		 	
		 	addButton : function(){
		 		this.modifyValue('button "" value');
		 	},
		 	
		 	addSay : function(){
		 		this.modifyValue('say ""');
		 	},
		 	
		 	addInputString : function(){
		 		this.modifyValue('inputString /user/local ""');
		 	},
			clearContent: function () {
				this.$refs.cm.codemirror.setValue('');
			},
			addEmoji() {
				this.$refs.newEmoji.show();
				this.$nextTick(() => {
					document.querySelector('emoji-picker').addEventListener('emoji-click', event => this.handleEmoji(event.detail.unicode));
				});
			},
			handleEmoji(emoji) {
				this.$refs.newEmoji.hide();
				const position = this.getCursor();
				const value = this.$refs.cm.codemirror.getValue();
				const lines = value.split('\n');
				chs = lines[position.line];
				lines[position.line] = chs.substring(0,position.ch) + emoji + chs.substring(position.ch, chs.length);
				this.$refs.cm.codemirror.setValue(lines.join('\n'));
			}
		}
});