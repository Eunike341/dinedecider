FROM alpine/java:21-jdk
ARG JAR_FILE=dinedecider-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]