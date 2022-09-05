FROM httpd:2.4

ENV RUNNER_URL_ENV http://localhost:8080/vertigo-bot-executor
ENV BOT_IHM_BASE_URL_ENV http://localhost:8080/vertigo-bot-executor/static/chatbot/index.html

COPY ./public-html/ /usr/local/tmp/html/
COPY start/start.sh /
RUN chmod +x /start.sh

ENTRYPOINT []
CMD ["/start.sh"]