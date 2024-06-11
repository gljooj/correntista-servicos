FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
CMD ["java", "-jar", "app.jar"]
# CMD ["mvn", "spring-boot:run"]
