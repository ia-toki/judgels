FROM openjdk:8u212-jre-alpine3.9

RUN addgroup -S judgels && adduser -S -g judgels judgels
RUN apk add --no-cache bash procps

USER judgels

WORKDIR /judgels/sandalphon

EXPOSE 9002

COPY build/stage/playBinary .

ENTRYPOINT ["./bin/playBinary"]
