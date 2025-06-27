function initDocumentaryResources() {
    scanContextKeys(new Map(Object.entries(chatbot.contextMap))).then(value => {
        chatbot.context = value;
        if (chatbot.context['url'] === undefined) {
            chatbot.context['url'] = window.location.href;
        }
        axios.post(chatbot.documentaryResourceConfig.documentaryResourceUrl + '/getDocumentaryResources', chatbot.context).then(documentaryResourceResponse => {
            chatbot.documentaryResourceConfig.documentaryResourceList = documentaryResourceResponse.data;
        });
    });
}
