---
sidebar_position: 3
---

# Cheatsheet

This page lists some technical details that Judgels admins should know in order to be able to debug issues arising from a Judgels deployment.

### Core VM

- The following Docker containers must be running (check via `docker ps`):
  - `judgels-client`
  - `judgels-server`
  - `rabbitmq`
  - `phpmyadmin` (optional)
- To check for running container logs: `docker logs --tail=100 -f <container name>`
  - e.g.: `docker logs --tail=100 -f judgels-server`

### Judgels client

- Judgels client serves the static React app as the contestant web interface.
- `/judgels/client/var` is a mounted volume:
  - Config file is located at `/judgels/client/var/conf/raphael-v9.js`
    - Values were populated from `vars.yml` during deployment.
  - Data files are located at `/judgels/client/var/data`
  - Log files are located at `/judgels/client/var/log`

### Judgels server

- Judgels server serves the admin web interface and the API server endpoints.
- `/judgels/server/var` is a mounted volume:
  - Config file is located at `/judgels/server/var/conf/judgels-server.yml`
    - Values were populated from `vars.yml` during deployment.
  - Data files are located at `/judgels/server/var/data`
  - Log files are located at `/judgels/server/var/log`

### Nginx

- The config files are located at:
  * `/etc/nginx/sites-available/judgels-client`
  * `/etc/nginx/sites-available/judgels-server-admin`
  * `/etc/nginx/sites-available/judgels-server-api`
  * `/etc/nginx/sites-available/letsencrypt`

### MySQL

- Database name: `judgels`
- Database username: `vars.yml` -> `db_username`
- Database password: `vars.yml` -> `db_password`
- phpMyAdmin is available at `http://<core VM IP>:8080`
- To check for MySQL status: `service mysql status`

### RabbitMQ

- Management web interface is available at `http://<core VM IP>:15672`
