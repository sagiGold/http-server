# Use openjdk:17 as the base image
FROM openjdk:17

# Create a new app directory for app files
RUN mkdir /app

# Copy the class files and any other required files of your program into the container
COPY target/ /app/

# Set the working directory
WORKDIR /app/

# Run the HttpServerApplication class
CMD java -jar http-server-0.0.1-SNAPSHOT.jar
