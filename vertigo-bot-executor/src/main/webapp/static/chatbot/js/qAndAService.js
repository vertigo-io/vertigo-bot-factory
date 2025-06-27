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