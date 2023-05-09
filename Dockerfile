FROM amazoncorretto:17-alpine AS build

WORKDIR /app

COPY . .

RUN ./gradlew build --no-daemon

FROM amazoncorretto:17-alpine AS service

WORKDIR /app

COPY --from=build /app/build/libs/agendaplus-0.0.1-SNAPSHOT.jar /app/application.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/application.jar"]
