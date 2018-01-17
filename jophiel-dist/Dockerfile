FROM openjdk:8u151-jre-alpine3.7

RUN apk add --no-cache bash

WORKDIR /judgels/jophiel

EXPOSE 9001 9101

COPY build/distributions/jophiel-* .

RUN addgroup -S judgels && adduser -S -g judgels judgels && chown -R judgels var
USER judgels

VOLUME var/conf

CMD ["service/bin/init.sh", "console"]
