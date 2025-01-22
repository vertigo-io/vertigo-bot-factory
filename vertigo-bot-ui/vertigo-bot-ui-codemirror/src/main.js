import VueCodemirror from 'vue-codemirror'
import ChatbotCodemirror from "@/components/ChatbotCodemirror.vue";

var chatbotCodemirror = {
    install: function (vueApp, options) {
        vueApp.use(VueCodemirror);
        vueApp.component('c-codemirror', ChatbotCodemirror);
    }
};

if (window) {
    window.chatbotCodemirror = chatbotCodemirror;
}