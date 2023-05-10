FROM nginx:1.15.6-alpine

WORKDIR /judgels/client

ARG VCS_REF

LABEL org.opencontainers.image.title judgels/client
LABEL org.opencontainers.image.source https://github.com/ia-toki/judgels
LABEL org.opencontainers.image.revision $VCS_REF

COPY judgels-client.conf /etc/nginx/conf.d/default.conf
COPY build .
