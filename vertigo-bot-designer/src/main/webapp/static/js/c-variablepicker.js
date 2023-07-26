Vue.component('c-variablepicker', {
    props : {
        modeEdit:		    { type: Boolean, 'default': true },
        nameVarVariable:    { type: String,  'default':null},
        idBlock:			{ type: String,  'default':null },
        locale:   		    { type: String, 'default': 'en_US' },
        object:			    { type: String,  required: true },
    },
    data: function () {
        return {
            inputValue: '',
            typeVar: 'local',
            nameVar: ''
        }
    },
    template :
        `	<div>
				<q-btn v-if="modeEdit" label="Variable" color="primary" @click="addVariableValue()"/>
				<q-dialog ref="newVariableDialog" id="newVariableDialog" >
					<q-card style="text-align: center">
						<q-card-section>
							<div class="text-h6" >{{locale == 'fr_FR' ? "Ajouter la valeur d'une variable au texte" : 'Add the value of a variable into the text'}}</div>
						</q-card-section>
						<q-card-section style="display: flex;">
                            <q-btn-group>
                                <q-btn id="q-btn-type-local" :label="(locale == 'fr_FR' ? 'Locale':'Local')" @click="setTypeVar('/user/local/', 'q-btn-type-local')"></q-btn>
                                <q-btn id="q-btn-type-global" :label="(locale == 'fr_FR' ? 'Globale':'Global')" @click="setTypeVar('/user/global/', 'q-btn-type-global')"></q-btn>
                                <q-btn-dropdown id="q-btn-type-context" :label="(locale == 'fr_FR' ? 'Contexte':'Context')">
                                    <q-list>
                                        <q-item v-for="contextValue in VertigoUi.vueData[object]" clickable v-close-popup @click="setTypeVar('/user/global/context/'+contextValue.label, 'q-btn-type-context')">
                                            <q-item-section>
                                                   <q-item-label>{{contextValue.label}}</q-item-label>
                                            </q-item-section>
                                        </q-item>
                                    </q-list>
                                </q-btn-dropdown>
                            </q-btn-group>
						    <div  id="variablepicker">
						        <label for="input_variableNamePicker">{{locale == 'fr_FR' ? "Nom variable":"Variable name"}}:</label>
                                <input id="input_variableNamePicker" v-model="nameVar" type="text"/>
                            </div>                  
                        </q-card-section>
						<q-card-section style="display: flex;">
						    <input id="input_variableValuePicker" v-model="inputValue" type="text" style="width: 70%;"/>
						    <q-btn @click="handleVariablevalue()" :label="(locale == 'fr_FR' ? 'Générer valeur':'Generate value')" color="primary" style="width:30%; margin: 0 10px"></q-btn>
						</q-card-section>
						<q-card-section>
						    <q-btn :label="(locale == 'fr_FR' ? 'Valider':'Validate')" @click="validateVariableValue()" color="primary"/>
						</q-card-section>
					</q-card>
				</q-dialog>
			</div>
		`
    ,
    methods: {
        setTypeVar(type, id){
            this.typeVar = type
            if(this.typeVar.startsWith('/user/global/context/')){
                document.getElementById('input_variableNamePicker').setAttribute("placeholder", (this.locale == 'fr_FR' ? "Pas de nom de variable":"Not name variable"))
                document.getElementById('input_variableNamePicker').disabled = true;
            }else{
                document.getElementById('input_variableNamePicker').disabled = false;
                document.getElementById('input_variableNamePicker').setAttribute("placeholder", '')
            }
            // reinitialize the style of each button type variable
            document.getElementById('q-btn-type-local').setAttribute("style", "color: black; background-color: white;")
            document.getElementById('q-btn-type-global').setAttribute("style", "color: black; background-color: white;")
            document.getElementById('q-btn-type-context').setAttribute("style", "color: black; background-color: white;")

            document.getElementById(id).setAttribute("style", "color: white; background-color: #027be3;")
        },
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
        addVariableValue() {
            try{
                // recover the string concerned reduce to max length of a input text blockly to avoid side effects
                var stringVariable = document.getElementsByClassName('blocklyEditableText editing')[0].getElementsByClassName('blocklyText')[0].innerHTML.replace(/&nbsp;/g,' ').substring(0,40);
                this.idBlock = document.getElementsByClassName('blocklyEditableText editing')[0].parentNode.getAttribute("data-id").toString();
                if(this.idBlock!=null){
                    var block = workspace.getBlockById(this.idBlock)
                    // find the name of the field of the input of the block to change
                    block.inputList.forEach(input =>{
                        input.fieldRow.forEach(field =>{
                            if(field.getValue().startsWith(stringVariable)){
                                this.nameVarVariable = field.name
                                stringVariable = field.getValue()
                            }
                        })
                    })
                    this.inputValue = stringVariable
                    this.$refs.newVariableDialog.show();
                    document.getElementsByClassName('blocklyWidgetDiv')[0].style.display = "none";
                }
            }catch (e) {
                alert((this.locale == 'fr_FR' ? 'Aucun champ de texte du diagramme n\'est selectionné' : 'No diagram text field is selected'))
            } finally {
            }

        },
        handleVariablevalue() {
            let pos = this.doGetCaretPosition(document.getElementById('input_variableValuePicker'))
            let stringToAdd;
            if(this.typeVar.substr(-1)==='/')stringToAdd = '{{' + this.typeVar + this.nameVar.replace(/ /g,'').toLowerCase() +'}}'
            else stringToAdd = '{{' + this.typeVar + '}}'
            this.inputValue = this.addCharacterAtPosition(this.inputValue, stringToAdd, pos)
        },
        validateVariableValue(){
            workspace.getBlockById(this.idBlock).setFieldValue(this.inputValue,this.nameVarVariable)
            workspace.getBlockById(this.idBlock).unselect()
            if( workspace.getBlockById(this.idBlock).getNextBlock())workspace.getBlockById(this.idBlock).getNextBlock().select()
            this.$emit('variablepicker-event');
            this.$refs.newVariableDialog.hide();
        },
    }
});