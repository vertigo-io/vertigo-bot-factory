Vue.component('c-richtext', {
	
	props : {
		value:    { type: String,  required: true },
		name:     { type: String,  required: true },
		modeEdit: { type: Boolean, 'default': true },
		locale:   { type: String, 'default': 'en_US' },
		error : false
	},
	data: function () {
		return {
			imageUrl: null,
			linkUrl: null,
			newTab: true
		}
	},
	template : `
	<q-form>
		<div class="row wrap">
			<input v-if="name" class="hidden" type="text" :name="name" :value="value" />
			
			<q-editor v-bind:value="value" @input="val => $emit('input', val)"
				v-model="value"
				v-if="modeEdit"
				:style="error ? 'border: 2px solid;border-color: #C10015;border-radius:5px;':''"
				@keyup.enter.stop
				class="col-grow"
				ref="editor_ref"
			    @paste.native="evt => pasteCapture(evt, 'editor_ref')" 
				:definitions="{
					hr: {
						tip: 'Pause'
						},
					customimage: {
						tip: 'Insert image',
						label: 'Image',
						handler: addCustomImage
					},
					customlink: {
						tip: 'Insert link',
						label: 'Link',
						handler: addCustomLink
					},
					emoji: {
						tip: 'Add Emoji',
						label: 'Emoji',
						handler: addEmoji
					}
				}"
			    :toolbar="
			    [
			      [{
		            label: $q.lang.editor.fontSize,
	                fixedLabel: false,
		            fixedIcon: true,
		            list: 'no-icons',
		            options: [
		              'size-2',
		              'size-3',
		              'size-4',
		              'size-5'
		            ]
		          }],
				  ['bold', 'italic'],
			      ['unordered', 'ordered'],
			      ['hr'],
			      ['undo', 'redo'],
			      ['viewsource'],
			      ['customimage'],
			      ['customlink'],
			      ['emoji']
			    ]"
			   
				>
				
			     
			</q-editor>
			
			<div style="width:300px" class="q-px-md">
				<q-chat-message :sent="false" :text="getChatPreview()" text-color="black" bg-color="grey-4" ></q-chat-message>
			</div>
			 <q-dialog ref="newImage"  >
			 	<q-card style="width: 600px;">
					 <q-form @submit="handleCustomImage" class="q-gutter-md">
						<q-card-section>
							<div class="text-h6" >{{locale == 'fr_FR' ? 'Ajouter une image' : 'Add image'}}</div>
						</q-card-section>
						
						<q-card-section>
							<q-input 
								filled 
								type="text"
								v-model="imageUrl"
								label = "URL"	
								ref="imageUrlRef"
								autofocus
								required
	       					>
       					</q-card-section>
       					
						<q-card-actions align="around">
							<q-btn flat :label="locale == 'fr_FR' ? 'Annuler' : 'Cancel'" v-close-popup color="primary"/>
							<q-btn :label="locale == 'fr_FR' ? 'Ajouter' : 'Add'" type="submit" color="primary"/>
						</q-card-actions>
					</div>
				</q-card
			</q-dialog>
			<q-dialog ref="newLink"  >
			 	<q-card style="width: 600px;">
					 <q-form @submit="handleCustomLink" class="q-gutter-md">
						<q-card-section>
							<div class="text-h6" >{{locale == 'fr_FR' ? 'Ajouter un lien' : 'Add a link'}}</div>
						</q-card-section>
						
						<q-card-section>
							<q-input 
								filled 
								type="text"
								v-model="linkUrl"
								label = "URL"	
								ref="linkUrlRef"
								autofocus
								required
	       					>
       					</q-card-section>
       					<q-card-section>
       						<q-toggle left-label :label="locale === 'fr_FR' ? 'Nouvel onglet' : 'New tab'" ref="linkNewTabRef" v-model="newTab"></q-toggle>
						</q-card-section>
						<q-card-actions align="around">
							<q-btn flat :label="locale == 'fr_FR' ? 'Annuler' : 'Cancel'" v-close-popup color="primary"/>
							<q-btn :label="locale == 'fr_FR' ? 'Ajouter' : 'Add'" type="submit" color="primary"/>
						</q-card-actions>
					</q-form>
				</q-card>
			</q-dialog>
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
			/**
			 * Capture the <CTL-V> paste event, only allow plain-text, no images.
			 *
			 * see: https://stackoverflow.com/a/28213320
			 *
			 * @param {object} evt - array of files
			 * @author Daniel Thompson-Yvetot
			 * @license MIT
			 */
			pasteCapture: function (evt, ref) {
				if (this._isInToolbar(evt.target)) {
					return;
				}
				
				let text, onPasteStripFormattingIEPaste
				evt.preventDefault()
				if (evt.originalEvent && evt.originalEvent.clipboardData.getData) {
					text = evt.originalEvent.clipboardData.getData('text/plain')
					this.$refs[ref].runCmd('insertText', text)
				}
				else if (evt.clipboardData && evt.clipboardData.getData) {
					text = evt.clipboardData.getData('text/plain')
					this.$refs[ref].runCmd('insertText', text)
				}
				else if (window.clipboardData && window.clipboardData.getData) {
					if (!onPasteStripFormattingIEPaste) {
						onPasteStripFormattingIEPaste = true
						VUiPage.$refs[ref].runCmd('ms-pasteTextOnly', text)
					}
					onPasteStripFormattingIEPaste = false
				}
			},
			
			_isInToolbar: function(domElem) {
				if (domElem.className && domElem.className.match(/\bq-editor__toolbar\b/)) {
					return true;
				}
				if (!domElem.parentNode) {
					return false;
				}
				return this._isInToolbar(domElem.parentNode);
			},
			
			getChatPreview: function() {
				return !this.value ? [''] :
						this.value
						.replace("<a ", "<a target='_blank' rel='nofollow noopener noreferrer' ")
						.split(/<hr>|<hr \/>/);
			},
			checkMessage() {
		      if (this.value == "" || this.value == "<br />" || this.value == "<div><br></div>") {
		        this.styleWYSIWYG = "border: 2px solid;border-color: #C10015;border-radius:5px;";
		        this.showWarning = true;
		      } else {
		        this.styleWYSIWYG = "border: 1px solid;border-color: #D1CDC8;border-radius:5px;";
				this.showWarning = false;
			}
			},
			
			addCustomImage () {
		     this.$refs.newImage.show();
     		 this.imageUrl = null;
		    },

			addCustomLink () {
				this.$refs.newLink.show();
				this.linkUrl = null;
				this.newTab = true;
			},

			addEmoji() {
				this.$refs.newEmoji.show();
				this.$nextTick(() => {
					document.querySelector('emoji-picker').addEventListener('emoji-click', event => this.handleEmoji(event.detail.unicode));
				});
			},
		    
		    
			handleCustomImage () {
			  var url = this.$refs.imageUrlRef.value;
	 		  this.$refs.newImage.hide();
			  
		      const edit = this.$refs.editor_ref;
		      edit.caret.restore();
		      edit.runCmd('insertHTML', `<img class="imgClass" src="${url}"/>`);
		      edit.focus();
		    },

			handleCustomLink() {
				const url = this.$refs.linkUrlRef.value;
				let target = '_top';
				if (this.$refs.linkNewTabRef.value) {
					target = '_blank';
				}
				this.$refs.newLink.hide();
				const edit = this.$refs.editor_ref;
				edit.caret.restore();
				edit.runCmd('insertHTML', `<a href="${url}" target="${target}"/>${url}</a>`);
				edit.focus();
			},

			handleEmoji(emoji) {
				this.$refs.newEmoji.hide();

				const edit = this.$refs.editor_ref;
				edit.caret.restore();
				edit.runCmd('insertHTML', emoji);
				edit.focus();
			}
		}
		
});