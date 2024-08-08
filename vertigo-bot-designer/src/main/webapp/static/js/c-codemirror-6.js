window.addEventListener('vui-before-plugins', function (event) {
        let vuiCodeMirror6 = Vue.defineComponent({
            props: {
                modelValue: {type: String, required: true},
            },
            data: function () {
                return {
                    options: {
                        indentWithTabs: true,
                        indentUnit: 4,
                        tabSize: 4,
                        lineNumbers: true,
                        mode: 'chatbot',
                        extraKeys: {"Ctrl-Space": "autocomplete"},
                        readOnly: !this.modeEdit
                    }
                }
            },
            emits: ["update:modelValue"],
            created: function () {
                if (this.$props.modelValue) {
                    this.$data.modelValue = this.$props.modelValue
                } else {
                    this.$data.modelValue = {}
                }
            },
            watch: {
                modelValue: function (newVal) {
                    this.$data.modelValue = newVal;
                },
                modelValue: {
                    handler: function (newVal) {
                        this.$emit('update:modelValue', this.$data.modelValue);
                    },
                    deep: true
                },
            },
            template:
                `<div>
                    {{ modelValue }}
                </div>
        `
            ,
            methods: {}
        });
        event.detail.vuiAppInstance.component('c-codemirror-6', vuiCodeMirror6);
});
