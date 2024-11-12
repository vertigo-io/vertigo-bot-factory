window.addEventListener('vui-before-plugins', function (event) {
    let vuiQueAnsTextExample = Vue.defineComponent({

        props: {
            modelValue: {type: String, required: true},
            modeEdit: {type: Boolean, 'default': true},
            locale: {type: String, 'default': 'en_US'},
            textColor: {type: String, 'default': '#000'},
            error: false
        },

        template: `
        <div style="width:300px;" class="q-px-md">
            <q-chat-message :sent="false" :text="getChatPreview()" :text-color="textColor" bg-color="grey-4" ></q-chat-message>
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

    event.detail.vuiAppInstance.component('c-queanstextexample', vuiQueAnsTextExample);
});
