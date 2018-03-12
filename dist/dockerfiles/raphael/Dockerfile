FROM nginx:1.13.9-alpine

WORKDIR /judgels/raphael

VOLUME var

COPY raphael.conf /etc/nginx/conf.d/default.conf
COPY build .
