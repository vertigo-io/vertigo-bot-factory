Vue.component('c-richtext', {
	
	props : {
		value:    { type: String,  required: true },
		name:     { type: String,  required: true },
		modeEdit: { type: Boolean, 'default': true },
		locale:   { type: String, 'default': 'en_US' },
	},
	data: function () {
		return {
			imageUrl: null,			
		}
	},
	template : `
		<div class="row wrap">
			<input v-if="name" class="hidden" type="text" :name="name" :value="value" />
			
			<q-editor v-bind:value="value" @input="val => $emit('input', val)"
				v-if="modeEdit"
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
			      ['link', 'removeFormat'],
			      ['hr'],
			      ['undo', 'redo'],
			      ['viewsource'],
			      ['customimage']
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
			
			addCustomImage () {
		     this.$refs.newImage.show();
     		 this.imageUrl = null
		    },
		    
			handleCustomImage () {
			  var url = this.$refs.imageUrlRef.value			  
	 		  this.$refs.newImage.hide()
			  
		      const edit = this.$refs.editor_ref
		      edit.caret.restore()
		      edit.runCmd('insertHTML', `<div class="imgUrl"><img class="imgUrl" src="${url}"/></div>`)
		      edit.focus()      
		    }		
		}
});