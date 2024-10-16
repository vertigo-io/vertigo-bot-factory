function initDocumentaryResources() {
    chatbot.$http.get(chatbot.documentaryResourceConfig.documentaryResourceUrl + '/getDocumentaryResources').then(documentaryResourceResponse => {
        chatbot.documentaryResourceConfig.documentaryResourceList = documentaryResourceResponse.data;
    });
}
