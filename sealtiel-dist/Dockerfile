FROM openjdk:8u151-jre-alpine3.7

RUN apk add --no-cache bash

WORKDIR /judgels/sealtiel

EXPOSE 9003 9103

COPY build/distributions .

RUN addgroup -S judgels && adduser -S -g judgels judgels && chown -R judgels var
USER judgels

VOLUME var

CMD ["service/bin/init.sh", "console"]
