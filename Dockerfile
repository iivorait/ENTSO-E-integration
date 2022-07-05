FROM ibm-semeru-runtimes:open-11-jdk-focal

RUN mkdir /opt/app

COPY target/fingrid2-1.0.0.jar /opt/app

CMD ["java", "-jar", "/opt/app/fingrid2-1.0.0.jar"]