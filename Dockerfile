FROM eclipse-temurin:18 AS build-kotlin
USER 1000
WORKDIR /project
COPY --chown=1000 . /project
RUN chmod +x ./gradlew && ./gradlew distTar

FROM eclipse-temurin:19
ENTRYPOINT ["skolkovo-rectors-tinder-1.0-SNAPSHOT/bin/skolkovo-rectors-tinder"]
USER root
COPY --from=build-kotlin /project/build/distributions/skolkovo-rectors-tinder-1.0-SNAPSHOT.tar dist.tar
RUN tar -xf dist.tar \
    && rm dist.tar \
    && mkdir /bot \
    && chown -R 1000 /bot skolkovo-rectors-tinder-1.0-SNAPSHOT
USER 1000
WORKDIR /bot
