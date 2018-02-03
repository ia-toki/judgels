FROM nginx

RUN apt-get update && \
    apt-get install --no-install-recommends --no-install-suggests -y python-certbot-nginx

COPY jophiel /etc/nginx/conf.d/default.conf

COPY docker-entrypoint.sh /

RUN chmod +x /docker-entrypoint.sh && mkdir -p /var/www/html

VOLUME /etc/letsencrypt

CMD ["/docker-entrypoint.sh"]
