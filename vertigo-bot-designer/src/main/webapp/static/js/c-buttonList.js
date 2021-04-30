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
        			v-model="VertigoUi.vueData[buttonList]">
				<div v-for="(button, index) in VertigoUi.vueData[buttonList]">
					<template v-if="modeEdit">
						<input class="hidden" type="text" :name="'vContext['+buttonList+']['+index+'][text]'" :value="button.text" />
						<input class="hidden" type="text" :name="'vContext['+buttonList+']['+index+'][topIdResponse]'" :value="button.topIdResponse" />
					</template>
					
					<div>
						<q-chip
							:label="button.text" color="primary" text-color="white"
							:clickable="modeEdit" @click="editIndex = index; popupContent = Vue.util.extend({}, button); $refs.popupEdit.show();"
							:removable="modeEdit" @remove="VertigoUi.vueData[buttonList].splice(index, 1);"
							>
						</q-chip>
						<q-tooltip>{{getChoiceTitleById(button.topIdResponse)}}</q-tooltip>
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
							 v-model="popupContent.topIdResponse"
					         :options='transformListForSelection(choiceList, "topId", "title")'
					         label="Topic"
				         ></q-select>
					</q-card-section>
					
					<q-card-actions align="around">
						<q-btn v-if="editIndex === -1" :disable="!popupContent.text || !popupContent.topIdResponse" label="Add" color="primary" v-close-popup @click="VertigoUi.vueData[buttonList].push(popupContent)" ></q-btn>
						<q-btn v-if="editIndex !== -1" :disable="!popupContent.text || !popupContent.topIdResponse" label="Save" color="primary" v-close-popup @click="Vue.set(VertigoUi.vueData[buttonList], editIndex, popupContent)" ></q-btn>
					</q-card-actions>
			</q-dialog>
				
		</div>
	`
		,
		methods: {
			getChoiceTitleById: function(id) {
				if (id == null) return null;
				
				let result = VertigoUi.vueData[this.choiceList].filter(t => t.topId === id);
				return result.length === 0 ? null : result[0].title;
			},
			transformListForSelection: function (list, valueField, labelField) {
				return VertigoUi.vueData[list].map(function (object) {
					return { value: object[valueField], label: object[labelField].toString()} // a label is always a string
				});
			},
		}
});