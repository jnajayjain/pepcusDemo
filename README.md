## Installation


Prerequisites:
- mysql, maven, jdk 8, postman, nginx
- good understanding of springboot, jwt, oauth, springdata, springrest

Build
- tweek any properties in the resources/application.properties files. Add database credentials. 
- Create a single table using "client.sql" under resources folder 
 
 Run as
- mvn clean compile install -DskipTests

Instructions to run as spring boot project
1.	Import project in eclipse as existing maven project
2.	Run as maven project with command  "mvn clean compile install -DskipTests"
3.  Modify application.properties to refer your configuration
3.	Locate executable JAR file (japi-0.1.jar) inside /target folder 
4.	Run command "Java -jar japi-0.1.jar (This will launch inbuilt tomcat container and execute the application)

