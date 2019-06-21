FROM openjdk:8u181-jre-alpine3.8

RUN addgroup -S judgels && adduser -S -g judgels judgels
RUN apk add --no-cache bash procps

USER judgels

WORKDIR /judgels/jophiel

EXPOSE 9001 9101

COPY build/distributions .

ENTRYPOINT ["./init.sh"]
CMD ["console"]
