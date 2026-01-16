/**
 * Initialize and load documentary resources based on current context
 * Fetches documentary resources from the server using context information
 */
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

/**
 * Track a documentary resource click for analytics purposes
 * Sends click information to the analytics endpoint if a session exists
 * 
 * @param {number} dreId - Documentary resource ID
 * @param {string} title - Documentary resource title
 * @param {string} dreTypeCd - Documentary resource type code (URL or FILE)
 */
function trackDocumentaryResourceClick(dreId, title, dreTypeCd) {
    const sessionId = sessionStorage.getItem('convId');
    if (sessionId) {
        axios.post(chatbot.documentaryResourceConfig.documentaryResourceUrl + '/stats/' + sessionId, {
            dreId,
            title,
            dreTypeCd
        }).catch(error => console.error('Error tracking documentary resource click:', error));
    }
}
