FROM openjdk:13-alpine

ADD ./build/libs/CulturalFootprint-1.0-SNAPSHOT.jar ./app.jar

RUN mkdir data

CMD ["java", "-Xmx50m", "-Xms15m", "-jar", "app.jar"]
