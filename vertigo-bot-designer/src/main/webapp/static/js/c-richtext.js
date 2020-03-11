Vue.component('c-richtext', {
	props : {
		value:    { type: String,  required: true },
		name:     { type: String,  required: true },
		modeEdit: { type: Boolean, 'default': true },
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
			      ['viewsource']
			    ]"
			    :definitions="{hr: {tip: 'Pause'}}"
				>
			</q-editor>
			
			<div style="width:300px" class="q-px-md">
				<q-chat-message :sent="false" :text="getChatPreview()" text-color="black" bg-color="grey-4" ></q-chat-message>
			</div>
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
			
			getChatPreview: function() {
				return !this.value ? [''] :
						this.value
						.replace("<a ", "<a target='_blank' rel='nofollow noopener noreferrer' ")
						.split(/<hr>|<hr \/>/);
			}
		}
});