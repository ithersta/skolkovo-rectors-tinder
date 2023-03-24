FROM ghcr.io/graalvm/graalvm-ce:22.3.1 AS cache
USER root
WORKDIR /project
ENV GRADLE_USER_HOME /cache
COPY build.gradle.kts gradle.properties settings.gradle.kts gradlew /project/
COPY gradle /project/gradle/
RUN ./gradlew --no-daemon build
USER 1000

FROM ghcr.io/graalvm/graalvm-ce:22.3.1 AS build-kotlin
USER root
WORKDIR /project
COPY --from=cache /cache /root/.gradle
COPY . /project/
RUN ./gradlew --no-daemon installDist
USER 1000

FROM ghcr.io/graalvm/graalvm-ce:22.3.1
ENV APP_NAME="skolkovo-rectors-tinder"
ENTRYPOINT /$APP_NAME/bin/$APP_NAME
USER root
COPY --from=build-kotlin /project/build/install/$APP_NAME /$APP_NAME
RUN mkdir /db && chown -R 1000 /db /$APP_NAME
USER 1000
WORKDIR /db
