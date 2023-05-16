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
  - `phpmyadmin` (optional, can be stopped)
- To tail running container logs: `docker logs --tail=100 -f <container name>`
  - e.g.: `docker logs --tail=100 -f judgels-server`

### Judgels client

- Judgels client serves a static React app as the contestant web interface.
- `/judgels/client/var/` is a mounted volume:
  - Config file is located at `/judgels/client/var/conf/judgels-client.js`.
    - Values are populated from `vars.yml` during deployment.

### Judgels server

- Judgels server serves the admin web interface and the API server endpoints.
- `/judgels/server/var/` is a mounted volume:
  - Config file is located at `/judgels/server/var/conf/judgels-server.yml`.
    - Values are populated from `vars.yml` during deployment.
  - Data files are located at `/judgels/server/var/data/`.
  - Log files are located at `/judgels/server/var/log/`.

### Nginx

- The site config files are located at:
  * `/etc/nginx/sites-available/judgels-client`
  * `/etc/nginx/sites-available/judgels-server-admin`
  * `/etc/nginx/sites-available/judgels-server-api`
  * `/etc/nginx/sites-available/letsencrypt`

### MySQL

- Database name: `judgels`
- Database username: `vars.yml` -> `db_username`
- Database password: `vars.yml` -> `db_password`
- phpMyAdmin is available at `http://<core VM IP>:8080`
- To check MySQL status: `service mysql status`

### RabbitMQ

- The management web interface is available at `http://<core VM IP>:15672`.
- To check the current grading queue, log in to the management web interface above using the credentials from `vars.yml` -> {`rabbitmq_username`, `rabbitmq_password`}, then click the Queues tab.

### Grader VM

- `judgels-grader` Docker container must be running (check via `docker ps`).
- To tail running grader logs: `docker logs --tail=100 -f judgels-grader`.

### Judgels grader 

- `/judgels/grader/var/` is a mounted volume:
  - Config file is located at `/judgels/grader/var/conf/judgels-grader.yml`.
    - Values are populated from `vars.yml` during deployment.
  - Log files are located at `/judgels/grader/var/log/`.
- Judgels grader uses [Isolate](https://www.ucw.cz/moe/isolate.1.html) as the sandbox for grading submissions:
  - The Isolate binaries are located at `/judgels/isolate/` in the Docker container.
  - The Isolate boxes are located at `/var/local/lib/isolate/<box id>` in the Docker container.
  - For example, to check active boxes:
    ```
    docker exec -it judgels-grader bash
    cd /var/local/lib/isolate
    ls
    ```
