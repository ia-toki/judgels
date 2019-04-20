#!/bin/bash

set -e

nginx

grep server_name /etc/nginx/conf.d/default.conf | awk '{ print $2 }' | sed 's/.$//' | xargs -I {} certbot \
    --domains {} \
    --email $CERTBOT_EMAIL \
    --authenticator webroot \
    --installer nginx \
    --agree-tos \
    --webroot-path=/usr/share/nginx/html \
    --redirect \
    --keep-until-expiring \
    --quiet

nginx -s stop
nginx -g "daemon off;"
