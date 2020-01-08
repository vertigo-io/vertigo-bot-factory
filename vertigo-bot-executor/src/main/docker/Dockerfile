################
# Rasa as base #
################
FROM rasa/rasa:1.2.3

###########
# Prepare #
###########
RUN apt-get update


################
# Add RabbitMq #
################
RUN apt-get install -y rabbitmq-server \
 && printf '\n\nNODENAME=rabbit-rasa\n' >> /etc/rabbitmq/rabbitmq-env.conf
 
 
##############
# Add Tomcat #
##############
# JRE
RUN mkdir -p /usr/share/man/man1 \
 && apt-get install -y default-jre-headless
 
# Tomcat
ADD apache-tomcat-8.5.50 /opt/tomcat/

	# user home is used for berkeley database
RUN useradd -m tomcat \ 
 && chown -R tomcat. /opt/tomcat

EXPOSE 8080


##########################################################
# Add rasa working directory with rabbitmq configuration #
##########################################################
ADD bot /opt/bot/

RUN chown -R tomcat. /opt/bot
 
#######################
# start configuration #
#######################
ADD start.sh /
RUN chmod +x /start.sh

ENTRYPOINT [] # clear entrypoint from base image
CMD ["/start.sh"] # override CMD from base image