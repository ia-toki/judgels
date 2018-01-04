FROM openjdk:8-jre-alpine

RUN apk add --no-cache bash

ENV APP_HOME /judgels/sealtiel
WORKDIR $APP_HOME

EXPOSE 9003 9103

COPY build/distributions/*.tgz /tmp
RUN mkdir -p $APP_HOME \
    && tar -xf /tmp/*.tgz -C $APP_HOME --strip-components=1 \
    && rm /tmp/*.tgz

RUN addgroup -S judgels && adduser -S -g judgels judgels && chown -R judgels var
USER judgels

CMD ["service/bin/init.sh", "console"]
