FROM openjdk:8u181-jre-alpine3.8

RUN addgroup -S judgels && adduser -S -g judgels judgels
RUN apk add --no-cache bash procps

USER judgels

WORKDIR /judgels/uriel

EXPOSE 9004 9104

COPY build/distributions .

ENTRYPOINT ["./init.sh"]
CMD ["console"]
