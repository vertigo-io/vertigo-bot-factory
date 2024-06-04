window.addEventListener('vui-before-plugins', function (event) {
	let vuiButtonList = Vue.defineComponent({
		props: {
			buttonList: {type: String, required: true},
			buttonUrlList: {type: String, required: true},
			choiceList: {type: String, required: true},
			modeEdit: {type: Boolean, 'default': true},
			locale: {type: String, 'default': 'en_US'}
		},
		data: function () {
			return {
				popupContent: {},
				editIndex: -1,
				type: 'BUTTON'
			}
		},
		template: `
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
							:clickable="modeEdit" @click="editIndex = index; type = 'BUTTON'; popupContent = Vue.util.extend({}, button); $refs.popupEdit.show();"
							:removable="modeEdit" @remove="VertigoUi.vueData[buttonList].splice(index, 1);"
							>
						</q-chip>
						<q-tooltip>{{getChoiceTitleById(button.topIdResponse)}}</q-tooltip>
					</div>
				</div>
			</draggable>
			<draggable class="row wrap" :disabled="!modeEdit"
        			v-model="VertigoUi.vueData[buttonUrlList]">
				<div v-for="(button, index) in VertigoUi.vueData[buttonUrlList]">
					<template v-if="modeEdit">
						<input class="hidden" type="text" :name="'vContext['+buttonUrlList+']['+index+'][text]'" :value="button.text" />
						<input class="hidden" type="text" :name="'vContext['+buttonUrlList+']['+index+'][url]'" :value="button.url" />
						<input class="hidden" type="text" :name="'vContext['+buttonUrlList+']['+index+'][newTab]'" :value="button.newTab" />
					</template>
					
					<div>
						<q-chip
							:label="button.text" color="primary" text-color="white"
							:clickable="modeEdit" @click="editIndex = index; type = 'BUTTON_URL' ; popupContent = Vue.util.extend({}, button); $refs.popupEdit.show();"
							:removable="modeEdit" @remove="VertigoUi.vueData[buttonUrlList].splice(index, 1);"
							>
						</q-chip>
					</div>
				</div>
			</draggable>
			
			<q-btn v-if="modeEdit"class="fab-block" round color="primary" icon="add" aria-label="Add button" title="Add button" size="sm"
					       @click="editIndex = -1; type = 'BUTTON'; popupContent = {}; $refs.popupEdit.show();"></q-btn>
					       
			<q-dialog ref="popupEdit">
				<q-card>
					<q-card-section>
						<q-btn-toggle
							v-if="modeEdit"
							v-model="type"
							toggle-color="primary"
							:options="locale  === 'fr_FR' ? [{label: 'Bouton', value: 'BUTTON'}, {label: 'Bouton URL', value: 'BUTTON_URL'}] : [{label: 'Button', value: 'BUTTON'}, {label: 'Button URL', value: 'BUTTON_URL'}]"
						/>
					</q-card-section>
					<div v-if="type === 'BUTTON'">
						<q-card-section>
							<q-input
								v-model="popupContent.text" 
								:placeholder="locale === 'fr_FR' ? 'Label du bouton' : 'Enter button label'" dense
								autofocus
								>
								</q-input>
							<q-select
								 dense
								 map-options
								 emit-value
								 v-model="popupContent.topIdResponse"
								 :options='transformListForSelection(choiceList, "topId", "title")'
								 :label="locale === 'fr_FR' ? 'Intention' : 'Topic'"
							 ></q-select>
						</q-card-section>
						
						<q-card-actions align="around">
							<q-btn v-if="editIndex === -1" :disable="!popupContent.text || !popupContent.topIdResponse" :label="locale === 'fr_FR' ? 'Ajouter' : 'Add'" color="primary" 
							v-close-popup @click="VertigoUi.vueData[buttonList].push(popupContent)" ></q-btn>
							<q-btn v-if="editIndex !== -1" :disable="!popupContent.text || !popupContent.topIdResponse" :label="locale === 'fr_FR' ? 'Sauvegarder' : 'Save'" color="primary" 
							v-close-popup @click="Vue.set(VertigoUi.vueData[buttonList], editIndex, popupContent)" ></q-btn>
						</q-card-actions>
					</div>
					<div v-if="type === 'BUTTON_URL'">
						<q-card-section>
							<q-input
								v-model="popupContent.text" 
								:placeholder="locale === 'fr_FR' ? 'Label du bouton' : 'Enter button label'" dense
								autofocus
								>
								</q-input>
							<q-input
								v-model="popupContent.url" 
								:placeholder="locale === 'fr_FR' ? 'URL du bouton' : 'Enter button URL'" dense
								autofocus
								>
							</q-input>
							<q-toggle left-label :label="locale === 'fr_FR' ? 'Nouvel onglet' : 'New tab'" v-model="popupContent.newTab"></q-toggle>
							<input type="hidden" name="popupContent.newTab" :value="popupContent.newTab"/>
						</q-card-section>
						
						<q-card-actions align="around">
							<q-btn v-if="editIndex === -1" :disable="!popupContent.text || !popupContent.url"
							:label="locale === 'fr_FR' ? 'Ajouter' : 'Add'" color="primary" v-close-popup @click="VertigoUi.vueData[buttonUrlList].push(popupContent)" ></q-btn>
							<q-btn v-if="editIndex !== -1" :disable="!popupContent.text || !popupContent.url" 
							:label="locale === 'fr_FR' ? 'Sauvegarder' : 'Save'" color="primary" v-close-popup @click="Vue.set(VertigoUi.vueData[buttonUrlList], editIndex, popupContent)" ></q-btn>
						</q-card-actions>
					</div>
				</q-card>
			</q-dialog>
				
		</div>
	`
		,
		methods: {
			getChoiceTitleById: function (id) {
				if (id == null) return null;

				let result = VertigoUi.vueData[this.choiceList].filter(t => t.topId === id);
				return result.length === 0 ? null : result[0].title;
			},
			transformListForSelection: function (list, valueField, labelField) {
				return VertigoUi.vueData[list].map(function (object) {
					return {value: object[valueField], label: object[labelField].toString()} // a label is always a string
				});
			},
		}
	});
	event.detail.vuiAppInstance.component('c-buttonlist', vuiButtonList);
});