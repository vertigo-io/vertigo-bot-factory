Vue.component('c-variablepicker', {
    props : {
        modeEdit:		    { type: Boolean, 'default': true },
        nameVarVariable:    { type: String,  'default':null},
        idBlock:			{ type: String,  'default':null },
        locale:   		    { type: String, 'default': 'en_US' },
        // listTypeVar is an array of types of variable. Each variable contains [0] the id of the type (that's also the label name), [1] the french label, [2] the path variable
        listTypeVar:   { type: Array, 'default': [['local', 'locale', '/user/local/'],['global', 'globale','/user/global/'],['context','contexte','/user/global/context/'],['url', 'url','/user/global/context/url']]}
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
                                <q-btn v-for="type in listTypeVar" size="md" :id="'q-btn-type-'+type[0]" @click="setTypeVar(type[0])" :label="(locale == 'fr_FR' ? type[1]:type[0])"></q-btn>
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
        setTypeVar(type){
            this.typeVar = type
            if(this.typeVar==='url'){
                document.getElementById('input_variableNamePicker').setAttribute("placeholder", (this.locale == 'fr_FR' ? "Pas de nom de variable":"Not name variable"))
            }else{
                document.getElementById('input_variableNamePicker').setAttribute("placeholder", '')
            }
            // reinitialize the style of each button type variable
            for (let i = 0; i < this.listTypeVar.length; i++) {
                document.getElementById('q-btn-type-'.concat(this.listTypeVar[i][0])).setAttribute("style", "color: black; background-color: white;")
            }
            document.getElementById('q-btn-type-'.concat(this.typeVar)).setAttribute("style", "color: white; background-color: #027be3;")
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
                // recover the string concerned
                var stringVariable = document.getElementsByClassName('blocklyEditableText editing')[0].getElementsByClassName('blocklyText')[0].innerHTML.replace(/&nbsp;/g,' ');
                this.idBlock = document.getElementsByClassName('blocklyEditableText editing')[0].parentNode.getAttribute("data-id").toString();
                if(this.idBlock!=null){
                    var block = workspace.getBlockById(this.idBlock)
                    // find the name of the field of the input of the block to change
                    block.inputList.forEach(input =>{
                        input.fieldRow.forEach(field =>{
                            if(field.getValue()==stringVariable){
                                this.nameVarVariable = field.name
                            }
                        })
                    })
                    this.inputValue = stringVariable
                    this.$refs.newVariableDialog.show();
                    document.getElementsByClassName('blocklyWidgetDiv')[0].style.display = "none";
                }
                else{
                    console.log((this.locale == 'fr_FR' ? 'Block non trouvé' : 'Block not found'))
                }
            }catch (e) {
                alert((this.locale == 'fr_FR' ? 'Aucun champ de texte du diagramme n\'est selectionné' : 'No diagram text field is selected'))
            } finally {
            }

        },
        handleVariablevalue() {
            let pos = this.doGetCaretPosition(document.getElementById('input_variableValuePicker'))
            let stringToAdd;
            for (let i = 0; i < this.listTypeVar.length; i++) {
                if(this.listTypeVar[i][0]===this.typeVar){
                    if(this.listTypeVar[i][2].substr(-1)==='/') stringToAdd = '{{' +this.listTypeVar[i][2]+this.nameVar.replace(/ /g,'').toLowerCase()+'}}'
                    else stringToAdd = '{{' +this.listTypeVar[i][2]+'}}'
                    break;
                }
            }
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