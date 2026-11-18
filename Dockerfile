# Etapa 1: Compilar el .war con Maven y Java 25
FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests -Dcheckstyle.skip=true -Dpmd.skip=true -Dcpd.skip=true -Dprettier.skip=true

# Etapa 2: Servir el .war en Tomcat 10.1 (compatible con Jakarta EE 10)
FROM tomcat:10.1-jdk25
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/spring.war
EXPOSE 8080
ENV JAVA_OPTS="-Xms128m -Xmx320m -XX:+UseSerialGC"
CMD ["catalina.sh", "run"]