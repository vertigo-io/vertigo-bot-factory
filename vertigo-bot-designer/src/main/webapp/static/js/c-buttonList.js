Vue.component('c-buttonlist', {
	props : {
		buttonList:    { type: String,  required: true },
		choiceList:    { type: String,  required: true },
		modeEdit:      { type: Boolean, 'default': true },
	},
	data: function () {
		return {
			popupContent: {},
			editIndex: -1
		}
	},
	template : `
		<div class="row wrap items-center">
			<draggable class="row wrap" :disabled="!modeEdit"
        			v-model="vueData[buttonList]">
				<div v-for="(button, index) in vueData[buttonList]">
					<template v-if="modeEdit">
						<input class="hidden" type="text" :name="'vContext['+buttonList+']['+index+'][text]'" :value="button.text" />
						<input class="hidden" type="text" :name="'vContext['+buttonList+']['+index+'][smtIdResponse]'" :value="button.smtIdResponse" />
					</template>
					
					<div>
						<q-chip
							:label="button.text" color="primary" text-color="white"
							:clickable="modeEdit" @click="editIndex = index; popupContent = Vue.util.extend({}, button); $refs.popupEdit.show();"
							:removable="modeEdit" @remove="vueData[buttonList].splice(index, 1);"
							>
						</q-chip>
						<q-tooltip>{{getChoiceTitleById(button.smtIdResponse)}}</q-tooltip>
					</div>
				</div>
			</draggable>
			
			<q-btn v-if="modeEdit"class="fab-block" round color="primary" icon="add" aria-label="Add button" title="Add button" size="sm"
					       @click="editIndex = -1; popupContent = {}; $refs.popupEdit.show();"></q-btn>
					       
			<q-dialog ref="popupEdit">
				<q-card>
					<q-card-section>
						<q-input
							v-model="popupContent.text" 
							placeholder="Enter button label" dense
							autofocus
							>
							</q-input>
						<q-select
							 dense
					         map-options
					         emit-value
							 v-model="popupContent.smtIdResponse"
					         :options='transformListForSelection(choiceList, "smtId", "title")'
					         label="Small talk"
				         ></q-select>
					</q-card-section>
					
					<q-card-actions align="around">
						<q-btn v-if="editIndex === -1" :disable="!popupContent.text || !popupContent.smtIdResponse" label="Add" color="primary" v-close-popup @click="vueData[buttonList].push(popupContent)" ></q-btn>
						<q-btn v-if="editIndex !== -1" :disable="!popupContent.text || !popupContent.smtIdResponse" label="Save" color="primary" v-close-popup @click="Vue.set(vueData[buttonList], editIndex, popupContent)" ></q-btn>
					</q-card-actions>
			</q-dialog>
				
		</div>
	`
		,
		methods: {
			getChoiceTitleById: function(id) {
				if (id == null) return null;
				
				let result = vueData[this.choiceList].filter(s => s.smtId === id);
				return result.length === 0 ? null : result[0].title;
			},
			transformListForSelection: function (list, valueField, labelField) {
				return vueData[list].map(function (object) {
					return { value: object[valueField], label: object[labelField].toString()} // a label is always a string
				});
			},
		}
});