FROM openjdk:8u181-jre-alpine3.8

RUN addgroup -S judgels && adduser -S -g judgels judgels
RUN apk add --no-cache bash procps ttf-dejavu

USER judgels

WORKDIR /judgels/jerahmeel

EXPOSE 9005 9105

ARG VCS_REF

LABEL org.label-schema.schema-version="1.0"
LABEL org.label-schema.name="judgels/jerahmeel"
LABEL org.label-schema.vcs-url="https://github.com/ia-toki/judgels"
LABEL org.label-schema.vcs-ref=$VCS_REF

COPY build/distributions .

ENTRYPOINT ["./init.sh"]
CMD ["console"]
