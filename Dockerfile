FROM openjdk:8-jdk-slim

RUN apt-get update && apt-get install -y maven

ADD src /stats/src
ADD pom.xml /stats/

WORKDIR /stats

RUN mvn clean install 

CMD mvn spring-boot:run 
