function initQAndA(){
    scanContextKeys(new Map(Object.entries(chatbot.contextMap))).then(value => {
        chatbot.context = value;
        if (chatbot.context['url'] === undefined) {
            chatbot.context['url'] = window.location.href;
        }
        axios.post(chatbot.qAndAConfig.qAndAUrl + '/getQuestionsAnswers', chatbot.context).then(questionAnswerResponse => {
            chatbot.qAndAConfig.questionAnswerList = questionAnswerResponse.data;
            chatbot.qAndAConfig.filteredQuestionAnswerList = questionAnswerResponse.data;
            chatbot.qAndAConfig.filterInput = '';
        });
    });
}

/**
 * Track a question/answer click for analytics purposes
 * Sends click information to the analytics endpoint if a session exists
 * 
 * @param {number} qaId - Question/Answer ID
 * @param {string} question - Question text
 * @param {string} catLabel - Category label
 */
function trackQuestionAnswerClick(qaId, question, catLabel) {
    const sessionId = sessionStorage.getItem('convId');
    if (sessionId) {
        axios.post(chatbot.qAndAConfig.qAndAUrl + '/stats/' + sessionId, {
            qaId,
            question,
            catLabel
        }).catch(error => console.error('Error tracking Q&A click:', error));
    }
}