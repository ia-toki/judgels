FROM nginx:1.15.6-alpine

WORKDIR /judgels/raphael

ARG VCS_REF

LABEL org.opencontainers.image.title judgels/raphael
LABEL org.opencontainers.image.source https://github.com/ia-toki/judgels
LABEL org.opencontainers.image.revision $VCS_REF

COPY raphael.conf /etc/nginx/conf.d/default.conf
COPY build .
