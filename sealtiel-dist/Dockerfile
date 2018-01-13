FROM openjdk:8u151-jre-alpine3.7

RUN apk add --no-cache bash

WORKDIR /judgels/sealtiel

EXPOSE 9003 9103

COPY build/distributions/*.tgz /tmp
RUN tar -xf /tmp/*.tgz -C . --strip-components=1 && rm /tmp/*.tgz

RUN addgroup -S judgels && adduser -S -g judgels judgels && chown -R judgels var
USER judgels

CMD ["service/bin/init.sh", "console"]
