#  Recruitment task

## Requirements
- Java 21
- Building the app from sources requires Maven, suggested version 3.9+

## Building, testing and running the app

To test the app: `mvn test` \
To build and run: `mvn spring-boot:run`, alternatively build with `mvn package` and run with `java -jar {path/to/generated/jar/in/taget/directory}`

Once started, service will be available at `localhost:8080` by default. Endpoint listing repositories is available at `/{useranme}`
