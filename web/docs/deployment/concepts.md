---
sidebar_position: 1
---

# Concepts

This page explains the required concepts for deploying a Judgels instance.

### A. Judgels services

Judgels consists of 3 apps (services):
- **Judgels client app**: a React app serving the contestant web interface.
  * Contestants access the contestant web interface to do the contest.
- **Judgels server app**: a Java app serving the admin web interface and the API server endpoints.
  * Admins access the admin web interface to set up the problems and other administrative tasks.
  * Contestant web browsers access the API server endpoints (via AJAX requests).
- **Judgels grader app**: a Java app grading contestant submissions.

In a Judgels deployment, we will run one client app, one server app, and multiple grader apps.

### B. Data storage

The server app stores persistent data in the following places:

- Local disk, for user avatar and submission files.
- A MySQL database, for everything else.

### C. Grading queue

The server and grader apps communicate with each other via a RabbitMQ queue. The simplified flow is as follows:
  * The server app sends a grading request to a RabbitMQ queue.
     * One grader app retrieves the grading request from the RabbitMQ queue.
       * The grader app evaluates the submission in the grading request.
     * The grader app sends the grading result to the RabbitMQ queue.
  * The server app retrieves the grading result from the RabbitMQ queue.

### D. Domains

To access the web interfaces, we typically need 3 domains:
- ***domain*** (e.g. `mycontest.org`)
  * For the contestants to access the contestant web interface. This is the URL that we publish to the contestants.
- **api.*domain*** (e.g. `api.mycontest.org`)
  * For the contestant web browsers to connect to the API server (via AJAX requests).
- **admin.*domain***(e.g. `admin.mycontest.org`)
  * For the admins to access the admin web interface.

Requests from contestants and admins are routed by Nginx.

### E. (Virtual) machines

The apps need to be deployed in one or more VMs (virtual machines). We find that the following setup is sufficient for running a contest with ~ 100 contestants:

- 1 "core" VM (at least 4 GB RAM, 2 CPUs), running:
  * MySQL database.
  * Both Judgels client and server apps.
  * Nginx, routing the requests from contestants and admins.
  * RabbitMQ, for the server and grader apps to communicate with each other for grading requests/results.
- 1 or more "grader" VMs  (at least 2 GB RAM, 2 CPUs), each running only a Judgels grader app.


### F. Diagram

To sum up, a summary about the deployment architecture and the dependencies can be seen in the following diagram:

<img src={require("./img/judgels-deployment.png").default} width="600" />
