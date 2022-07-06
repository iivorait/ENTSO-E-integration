#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app

#running the tests require the securityToken
RUN mvn -f /home/app/pom.xml clean package -DskipTests

#
# Package stage
#
FROM ibm-semeru-runtimes:open-11-jdk-focal

RUN mkdir /opt/app
COPY --from=build /home/app/target/fingrid2-1.0.0.jar /opt/app

CMD ["java", "-jar", "/opt/app/fingrid2-1.0.0.jar"]