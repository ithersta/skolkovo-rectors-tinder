FROM ghcr.io/graalvm/graalvm-ce:22.3.1 AS build-kotlin
USER root
WORKDIR /project
COPY . /project
RUN chmod +x ./gradlew && ./gradlew distTar

FROM ghcr.io/graalvm/graalvm-ce:22.3.1
ENV APP_NAME="skolkovo-rectors-tinder"
ENV WITH_VERSION="$APP_NAME-1.0-SNAPSHOT"
ENTRYPOINT /$WITH_VERSION/bin/$APP_NAME
USER root
COPY --from=build-kotlin /project/build/distributions/$WITH_VERSION.tar dist.tar
RUN tar -xf dist.tar -C / \
    && rm dist.tar \
    && mkdir /db \
    && chown -R 1000 /db /$WITH_VERSION
USER 1000
WORKDIR /db
