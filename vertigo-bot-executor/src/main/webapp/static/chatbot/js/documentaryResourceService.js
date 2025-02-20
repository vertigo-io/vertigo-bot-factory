function initDocumentaryResources() {
    axios.get(chatbot.documentaryResourceConfig.documentaryResourceUrl + '/getDocumentaryResources').then(documentaryResourceResponse => {
        chatbot.documentaryResourceConfig.documentaryResourceList = documentaryResourceResponse.data;
    });
}
