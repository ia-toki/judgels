FROM nginx:1.13.9-alpine

COPY nginx.conf /etc/nginx/nginx.conf

RUN apk add --no-cache bash

RUN apk add --no-cache certbot py-pip \
 && pip install certbot-nginx

COPY docker-entrypoint.sh /

ENTRYPOINT ["/docker-entrypoint.sh"]
