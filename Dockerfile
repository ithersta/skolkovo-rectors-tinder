FROM ghcr.io/graalvm/graalvm-ce:22.3.1 AS build-kotlin
USER root
WORKDIR /project
COPY . /project
RUN ./gradlew installDist
USER 1000

FROM ghcr.io/graalvm/graalvm-ce:22.3.1
ENV APP_NAME="skolkovo-rectors-tinder"
ENTRYPOINT /$APP_NAME/bin/$APP_NAME
USER root
COPY --from=build-kotlin /project/build/install/$APP_NAME /$APP_NAME
RUN mkdir /db && chown -R 1000 /db /$APP_NAME
USER 1000
WORKDIR /db
