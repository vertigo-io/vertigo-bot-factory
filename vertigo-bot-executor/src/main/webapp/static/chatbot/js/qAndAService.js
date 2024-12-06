function initQAndA(){
    chatbot.$http.get(chatbot.qAndAConfig.qAndAUrl + '/getQuestionsAnswers').then(questionAnswerResponse => {
        chatbot.qAndAConfig.questionAnswerList = questionAnswerResponse.data;
        chatbot.qAndAConfig.filteredQuestionAnswerList = questionAnswerResponse.data;
        chatbot.qAndAConfig.filterInput = '';
    });
}

function filterQuestionsAnswers(){
    if (chatbot.qAndAConfig.filterInput !== ''){
        chatbot.qAndAConfig.filteredQuestionAnswerList = chatbot.qAndAConfig.questionAnswerList.filter(questionAnswer => questionAnswer.question.toLowerCase().includes(chatbot.qAndAConfig.filterInput.toLowerCase()) || questionAnswer.answer.toLowerCase().includes(chatbot.qAndAConfig.filterInput.toLowerCase()))
    }else{
        chatbot.qAndAConfig.filteredQuestionAnswerList = chatbot.qAndAConfig.questionAnswerList
    }
}

function selectQuestion(selected){
    chatbot.qAndAConfig.selectedQuestion = selected;
}