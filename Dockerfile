FROM gradle:jdk17 AS build

WORKDIR /home/Agenda+

COPY . .

RUN gradle build --no-daemon

FROM openjdk:17-alpine AS service

COPY --from=build /home/Agenda+/build/libs/*.jar /app/spring-boot-application.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/spring-boot-application.jar"]
