Vue.component('c-popup', {
	props : {
		color:			{ type: String,  required: true, 'default' : 'red' },
		model:    	   	{ type: String,  required: true },
		object:			{ type: String,  required: true },
		confirm:		{ type: String,  required: true, 'default' : 'delete' },
		action: 		{ type: String,  required: true },
	},
	template : `
			<q-dialog v-model="VertigoUi.vueData[model]" persistent>
				<q-card>
						<q-card-section class="row items-center">
							<span class="q-ml-sm">Do you want to delete this {{ object }}</span>
						</q-card-section>
						<q-card-actions align="right">
						<q-form method="post" :action="action">
							<input type="hidden" name="CTX" :value="VertigoUi.vueData['CTX']">
							<q-btn flat label="Cancel" color="primary" v-close-popup ></q-btn>
							<q-btn type="submit" :title="confirm" :label="confirm" :aria-label="confirm" :color="color"></q-btn> 
						</q-form>
						</q-card-actions>
				</q-card>
			</q-dialog>
				
		</div>
	`
});