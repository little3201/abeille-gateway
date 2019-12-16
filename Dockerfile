# Start with a base image containing Java runtime
FROM openjdk:8-jdk-alpine

# Add Maintainer Info
LABEL maintainer="little3201@gmail.com"

# Add param to use in anywhere
ARG JAR_NAME
ARG ACTIVE=dev

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8763 available to the world outside this container
EXPOSE 8763

# The application's jar file
ARG JAR_FILE=target/${JAR_NAME}-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} ${JAR_NAME}.jar

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/${JAR_NAME}.jar --spring.profiles.active=${ACTIVE}"]
