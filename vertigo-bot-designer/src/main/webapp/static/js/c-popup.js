Vue.component('c-popup', {
	props : {
		color:			{ type: String,  required: true, 'default' : 'red' },
		model:    	   	{ type: String,  required: true },
		object:			{ type: String,  required: true },
		confirm:		{ type: String,  required: true, 'default' : 'delete' },
		functionToCall: { type: String,  required: true },
	},
	data: function () {
		return {
		}
	},
	template : `
			<q-dialog v-model="VertigoUi.vueData[model]" persistent>
				<q-card>
						<q-card-section class="row items-center">
							<span class="q-ml-sm">Do you want to delete this {{ object }}</span>
						</q-card-section>
						<q-card-actions align="right">
						<q-form method="post" :action="functionToCall">
							<input type="hidden" name="CTX" v-bind:value="VertigoUi.vueData['CTX']">
							<q-btn flat label="Cancel" color="primary" v-close-popup ></q-btn>
							<q-btn type="submit" title="confirm" label="confirm" aria-label="confirm" :color="color"></q-btn> 
						</q-form>
						</q-card-actions>
				</q-card>
			</q-dialog>
				
		</div>
	`
});