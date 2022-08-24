FROM tomcat:9-jdk8

RUN mkdir /usr/local/tomcat/webapps2

ADD server.xml /usr/local/tomcat/conf/server.xml
ADD cas.war /usr/local/tomcat/webapps2/cas.war

CMD ["catalina.sh", "run"]
