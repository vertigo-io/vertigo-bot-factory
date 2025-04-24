function initQAndA(){
    axios.get(chatbot.qAndAConfig.qAndAUrl + '/getQuestionsAnswers').then(questionAnswerResponse => {
        chatbot.qAndAConfig.questionAnswerList = questionAnswerResponse.data;
        chatbot.qAndAConfig.filteredQuestionAnswerList = questionAnswerResponse.data;
        chatbot.qAndAConfig.filterInput = '';
    });
}