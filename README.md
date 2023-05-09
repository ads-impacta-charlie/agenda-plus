
# Agenda+

## Requisitos

- [OpenJDK 17][openjdk-archive]

## Executar os testes

    Linux/Mac:
    ./gradlew test

    Windows:
    gradlew.bat test

## Build

Build é executado pelo Gradle:

    Linux/Mac: 
    ./gradlew build

    Windows:
    gradlew.bat build

## Executar a aplicação

Pelo Gradle:

    Linux/Mac:
    ./gradlew bootRun

    Windows
    gradlew.bat bootRun

OU, após realizar o build:

    java -jar build/libs/agendaplus-0.0.1-SNAPSHOT.jar

[openjdk-archive]: https://jdk.java.net/archive/ 
