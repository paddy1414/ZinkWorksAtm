FROM openjdk:8
ADD target/ZinkWorksATMApplication.jar ZinkWorksATMApplication.jar
ADD target/site/jacoco/index.html index.html
ADD zinkworks.db zinkworks.db
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "ZinkWorksATMApplication.jar"]