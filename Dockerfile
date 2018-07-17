FROM nginx:1.13.8-alpine

WORKDIR /judgels/raphael

COPY build .
COPY nginx-site.conf /etc/nginx/conf.d/default.conf

VOLUME var/conf
