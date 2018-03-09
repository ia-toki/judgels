FROM nginx:1.13.9-alpine

WORKDIR /judgels/raphael

COPY build .
COPY nginx-site.conf /etc/nginx/conf.d/default.conf

VOLUME var
