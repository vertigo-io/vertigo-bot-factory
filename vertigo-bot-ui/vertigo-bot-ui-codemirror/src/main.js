import VueCodemirror from 'vue-codemirror'
import ChatbotCodemirror from "@/components/ChatbotCodemirror.vue";
import {lintGutter} from "@codemirror/lint";
import {basicSetup} from "codemirror";

var chatbotCodemirror = {
    install: function (vueApp, options) {
        // No need for foldGutter, defined at index 4 of basicSetup.
        basicSetup.splice(4, 1);
        vueApp.use(VueCodemirror, {
            extensions: [basicSetup, lintGutter()]
        });
        vueApp.component('c-codemirror', ChatbotCodemirror);
    }
};

if (window) {
    window.chatbotCodemirror = chatbotCodemirror;
}