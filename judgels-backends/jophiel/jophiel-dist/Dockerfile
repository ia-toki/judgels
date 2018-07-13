FROM openjdk:8u151-jre-alpine3.7

RUN addgroup -S judgels && adduser -S -g judgels judgels
RUN apk add --no-cache bash procps

USER judgels

WORKDIR /judgels/jophiel

EXPOSE 9001 9101

COPY build/distributions .

ENTRYPOINT ["./service/bin/init.sh"]
CMD ["console"]
