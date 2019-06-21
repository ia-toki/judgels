FROM openjdk:8u181-jre-alpine3.8

RUN addgroup -S judgels && adduser -S -g judgels judgels
RUN apk add --no-cache bash procps

USER judgels

WORKDIR /judgels/jerahmeel

EXPOSE 9006

COPY build/stage/playBinary .

ENTRYPOINT ["./bin/playBinary"]
