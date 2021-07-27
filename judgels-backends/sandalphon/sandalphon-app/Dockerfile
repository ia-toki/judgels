FROM openjdk:8u181-jre-alpine3.8

RUN addgroup -S judgels && adduser -S -g judgels judgels
RUN apk add --no-cache bash procps

USER judgels

WORKDIR /judgels/sandalphon

EXPOSE 9002

ARG VCS_REF

LABEL org.opencontainers.image.title judgels/sandalphon
LABEL org.opencontainers.image.source https://github.com/ia-toki/judgels
LABEL org.opencontainers.image.revision $VCS_REF

COPY build/stage/main .

ENTRYPOINT ["./bin/main"]
