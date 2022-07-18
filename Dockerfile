FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package install -DskipTests


FROM openjdk:8
#ADD target/ZinkWorksATMApplication.jar ZinkWorksATMApplication.jar
COPY --from=build /home/app/target/ZinkWorksATMApplication.jar /usr/local/lib/ZinkWorksATMApplication.jar
#ADD target/site/jacoco/index.html index.html
ADD zinkworks.db zinkworks.db
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "/usr/local/lib/ZinkWorksATMApplication.jar"]