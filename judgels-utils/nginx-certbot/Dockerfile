FROM nginx:1.17.3-alpine

COPY nginx.conf /etc/nginx/nginx.conf

RUN apk add --no-cache bash

RUN apk add --no-cache --virtual .build-deps \
    gcc \
    python-dev \
    musl-dev \
    libffi-dev \
    openssl-dev \
 && apk add --no-cache py-pip certbot \
 && pip install --no-cache-dir certbot-nginx \
 && apk del .build-deps

COPY docker-entrypoint.sh /

ENTRYPOINT ["/docker-entrypoint.sh"]
