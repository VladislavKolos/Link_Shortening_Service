FROM amazoncorretto:21.0.2-alpine3.18
WORKDIR /app

COPY .env ./
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

RUN ./gradlew dependencies

COPY src ./src

RUN ./gradlew clean bootJar && \
    JAR_FILE=$(ls build/libs/*.jar | grep -v plain) && \
    cp $JAR_FILE app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]