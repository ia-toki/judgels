FROM nginx:1.15.6-alpine

WORKDIR /judgels/raphael

ARG VCS_REF

LABEL org.label-schema.schema-version="1.0"
LABEL org.label-schema.name="judgels/raphael"
LABEL org.label-schema.vcs-url="https://github.com/ia-toki/judgels"
LABEL org.label-schema.vcs-ref=$VCS_REF

COPY raphael.conf /etc/nginx/conf.d/default.conf
COPY build .
