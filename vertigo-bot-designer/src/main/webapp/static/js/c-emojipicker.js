Vue.component('c-emojipicker', {
    props : {
        modeEdit:		{ type: Boolean, 'default': true },
        nameVarEmoji:			{ type: String,  'default':null},
        idBlock:			{ type: String,  'default':null },
        locale:   		{ type: String, 'default': 'en_US' },
    },
    data: function () {
        return {
            inputValue: ''
        }
    },
    template :
        `	<div>
				<q-btn v-if="modeEdit" label="Emoji" color="primary" @click="addEmoji()"/>
				<q-dialog ref="newEmoji" id="newEmoji" >
					<q-card style="text-align: center">
						<q-card-section>
							<div class="text-h6" >{{locale == 'fr_FR' ? 'Ajouter un emoji' : 'Add emoji'}}</div>
						</q-card-section>
						<q-card-section>
							<emoji-picker></emoji-picker>
						</q-card-section>
						<q-card-section>
						    <input id="input_field_emojipicker" v-model="inputValue" type="text" style="width: 100%"/>
						</q-card-section>
						<q-card-section>
						    <q-btn label="Valider" @click="validateLabel()" />
						</q-card-section>
					</q-card>
				</q-dialog>
			</div>
		`
    ,
    methods: {
        doGetCaretPosition (oField) {
            var iCaretPos = oField.value.length;
            if (document.selection) {
                oField.focus();
                var oSel = document.selection.createRange();
                oSel.moveStart('character', -oField.value.length);
                iCaretPos = oSel.text.length;
            }
            else if (oField.selectionStart || oField.selectionStart == '0')
                iCaretPos = oField.selectionDirection=='backward' ? oField.selectionStart : oField.selectionEnd;
            return iCaretPos;
        },
        addCharacterAtPosition(originalString, character, position) {
            if (position >= 0 && position <= originalString.length) {
                return originalString.slice(0, position) + character + originalString.slice(position);
            } else {
                return originalString;
            }
        },
        addEmoji() {
            try{
                // recover the string concerned
                var stringEmoji = document.getElementsByClassName('blocklyEditableText editing')[0].getElementsByClassName('blocklyText')[0].innerHTML.replace(/&nbsp;/g,' ');
                this.idBlock = document.getElementsByClassName('blocklyEditableText editing')[0].parentNode.getAttribute("data-id").toString();
                if(this.idBlock!=null){
                    var block = workspace.getBlockById(this.idBlock)
                    // find the name of the field of the input of the block to change
                    block.inputList.forEach(input =>{
                        input.fieldRow.forEach(field =>{
                            if(field.getValue()==stringEmoji){
                                this.nameVarEmoji = field.name
                            }
                        })
                    })
                    this.inputValue = stringEmoji
                    this.$refs.newEmoji.show();
                    document.getElementsByClassName('blocklyWidgetDiv')[0].style.display = "none";
                    this.$nextTick(() => {
                        document.querySelector('emoji-picker').addEventListener('emoji-click', event => this.handleEmoji(event.detail.unicode));
                    });
                }
                else{
                    console.log((this.locale == 'fr_FR' ? 'Block non trouvé' : 'Block not found'))
                }
            }catch (e) {
                alert((this.locale == 'fr_FR' ? 'Aucun champ de texte du diagramme n\'est selectionné' : 'No diagram text field is selected'))
            }

        },
        handleEmoji(emoji) {
            let pos = this.doGetCaretPosition(document.getElementById('input_field_emojipicker'))
            this.inputValue = this.addCharacterAtPosition(this.inputValue, emoji, pos)
        },
        validateLabel(){
            workspace.getBlockById(this.idBlock).setFieldValue(this.inputValue,this.nameVarEmoji)
            workspace.getBlockById(this.idBlock).unselect()
            if( workspace.getBlockById(this.idBlock).getNextBlock())workspace.getBlockById(this.idBlock).getNextBlock().select()
            this.$emit('emojipicker-event');
            this.$refs.newEmoji.hide();
        },
    }
});