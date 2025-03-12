window.addEventListener('vui-before-plugins', function (event) {
    let vuiChatbotTextExample = Vue.defineComponent({

        props: {
            modelValue: {type: String, required: true},
            modeEdit: {type: Boolean, 'default': true},
            locale: {type: String, 'default': 'en_US'},
            error: false
        },

        template: `
        <div style="width:300px" class="q-px-md">
            <q-chat-message :sent="false" :text="getChatPreview()" text-color="black" bg-color="grey-4" :text-html="${true}"></q-chat-message>
        </div>
        `
        ,
        methods: {
            getChatPreview: function () {
                return !this.modelValue ? [''] :
                    DOMPurify.sanitize(this.modelValue)
                        .replace("<a ", "<a target='_blank' rel='nofollow noopener noreferrer' ")
                        .split(/<hr>|<hr \/>/);
            },
        }

    });

    event.detail.vuiAppInstance.component('c-chatbottextexample', vuiChatbotTextExample);
});