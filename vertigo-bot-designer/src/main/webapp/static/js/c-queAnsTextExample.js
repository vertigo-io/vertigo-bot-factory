Vue.component('c-queanstextexample', {

    props : {
        value:    { type: String,  required: true },
        modeEdit: { type: Boolean, 'default': true },
        locale:   { type: String, 'default': 'en_US' },
        error : false
    },

    template : `
	<div style="width:300px" class="q-px-md">
		<q-chat-message :sent="false" :text="getChatPreview()" text-color="black" bg-color="grey-4" ></q-chat-message>
	</div>
	`
    ,
    methods: {
        getChatPreview: function() {
            return !this.value ? [''] :
                DOMPurify.sanitize(this.value)
                    .replace("<a ", "<a target='_blank' rel='nofollow noopener noreferrer' ")
                    .split(/<hr>|<hr \/>/);
        },
    }

});