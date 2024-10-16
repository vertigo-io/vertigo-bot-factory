Vue.component('c-queanstextexample', {

    props : {
        value:    { type: String,  required: true },
        modeEdit: { type: Boolean, 'default': true },
        locale:   { type: String, 'default': 'en_US' },
        textColor: {type: String, 'default': '#000'},
        error : false
    },

    template : `
	<div style="width:340px;" class="q-px-md">
		<q-chat-message :sent="false" :text="getChatPreview()" :text-color="textColor" ></q-chat-message>
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
