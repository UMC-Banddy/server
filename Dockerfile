FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} banddy.jar
ENTRYPOINT ["java", "-jar", "/banddy.jar"]
