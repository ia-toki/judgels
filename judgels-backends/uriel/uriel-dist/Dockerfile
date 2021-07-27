FROM openjdk:8u181-jre-alpine3.8

RUN addgroup -S judgels && adduser -S -g judgels judgels
RUN apk add --no-cache bash procps ttf-dejavu

USER judgels

WORKDIR /judgels/uriel

EXPOSE 9004 9104

ARG VCS_REF

LABEL org.opencontainers.image.title judgels/uriel
LABEL org.opencontainers.image.source https://github.com/ia-toki/judgels
LABEL org.opencontainers.image.revision $VCS_REF

COPY build/distributions .

ENTRYPOINT ["./init.sh"]
CMD ["console"]
