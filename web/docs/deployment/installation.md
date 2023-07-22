---
sidebar_position: 2
---

# Installation

This page explains how to install Judgels, step-by-step. These steps will install Judgels using our Ansible scripts, which will deploy the Docker images of the apps.

### A. Preparing local workstation

We will install Judgels by running an Ansible playbook from a local workstation, which could be your own laptop or another VM. Make sure that the local workstation has Ansible installed.

### B. Setting up deployment env directory

1. On the local workstation, clone the Judgels repository (https://github.com/ia-toki/judgels).
   - We'll assume that we clone to `~/judgels`.
1. Copy the directory `deployment/ansible/env-example` from the cloned Judgels repository. We'll assume that we copy it and rename as `~/judgels-env`:
   ```
   cp -R ~/judgels/deployment/ansible/env-example ~/judgels-env
   ```
1. Inside `deployment/ansible`, create a symbolic link to the env directory as `env`:
   ```
   cd ~/judgels/deployment/ansible
   ln -s ~/judgels-env env
   ```

This directory consists of two files: `hosts.ini` and `vars.yml`. They describe the parameters of a Judgels deployment.

- `hosts.ini` is an [Ansible inventory](https://docs.ansible.com/ansible/latest/user_guide/intro_inventory.html) describing the VMs to which Judgels is to be deployed.
- `vars.yml` is a file containing environment variables, which will be used in for Ansible playbook [roles](https://github.com/ia-toki/judgels/tree/master/deployment/ansible/roles) during deployment.

It is recommended to track this env directory in a version control as the source of truth of our Judgels deployment. We will fill the values in both files in the next steps.

### C. Spinning up VMs

As mentioned in the [Concepts](/docs/deployment/concepts) page, we need to spin up 1 core VM and 1 or more grader VMs. The VMs have the following requirements:

- Linux. In particular, we have tested that Judgels works on Ubuntu 20.04 LTS and 22.04 LTS.
- Can be accessed from the local workstation to a root user via SSH.

Now, open `hosts.ini`:

1. Fill the public IP of the core VM under the `[core]` section.
1. Fill the public IP(s) of grader VM(s) under the `[grader]` section. Note that we can add multiple lines, one for each IP.

### D. Generating passwords

In `vars.yml`, generate different random strings for these values:

- `db_root_password`
   * The root password of the MySQL installation.
- `db_password`
   * The password of the `judgels` database.
- `jophiel_superadmin_initialPassword`:
   * The password for the auto-generated initial `superadmin` user.
- `rabbitmq_password`
   * The password for the server and grader apps to connect to RabbitMQ.

If you generate a different ssh key filename, edit the following variable in `vars.yml` to allow access to root:

- `ansible_ssh_private_key_file`
   * Fill with the location of the custom ssh private key file

### E. Setting up domains

1. Purchase a domain (for example: `mycontest.org`). Then, set up the following (sub)domains.
   - `mycontest.org`
   - `api.mycontest.org`
   - `admin.mycontest.org`
1. For each of the subdomains above, set up an A record pointing to the core VM's public IP.
1. Open `vars.yml`, and modify the following config:
   - `nginx_domain_judgels_client`: `mycontest.org`
   - `nginx_domain_judgels_server_api`: `api.mycontest.org`
   - `nginx_domain_judgels_server_admin`: `admin.mycontest.org`
   - `nginx_certbot_email`: your email, used for obtaining SSL certificates from Let's Encrypt.

### F. Running Ansible playbooks

1. Go to `deployment/ansible`.
1. Edit the app version value in `env/vars.yml`:
   ```
   app_version: '2.0'
   ```
   You can get the latest version from https://github.com/ia-toki/judgels/releases. Enter the version without the `v` prefix.
1. Run the provision playbook:
   ```
   ansible-playbook -e @env/vars.yml playbooks/provision.yml
   ```
   This will install the prerequisites packages in the VMs. We only need to do the above provision playbook once.
1. Run the deploy playbook:
   ```
   ansible-playbook -e @env/vars.yml playbooks/deploy.yml
   ```
   This will actually install Judgels. We can rerun this playbook e.g. if we want to deploy new version.
1. You will notice that there are two new files in your `env/` directory: `judgels-grader` and `judgels-grader.pub`. These are the keypair used for the graders to retrieve test cases from the server when grading submissions. Please commit these files to your version control as well.

Wait until everything is done. After the playbooks finished, do these verifications:

1. Verify that we can access the admin web interface at `https://admin.mycontest.org`.
1. Verify that we can log in as `superadmin`, with the password that we just set above.
1. Verify that we can access the contestant web interface at `https://mycontest.org`.
1. Verify that the contestant web interface does not show any error (which means it can successfully access the API server `https://api.mycontest.org`).

Congratulations, you have just deployed Judgels successfully!

### G. Deploying more graders

If you want to add more graders:

1. Add/update the IP of the graders under the `[grader]` section in the `hosts.ini` file.
   - Let's say that the new IPs are `1.2.3.4` and `5.6.7.8`.
1. Run the provision playbook, only for the new grader VMs:
   ```
   ansible-playbook -e @env/vars.yml playbooks/provision.yml --limit=1.2.3.4,5.6.7.8
   ```
1. Run the deploy playbook, only for the new grader VMs:
   ```
   ansible-playbook -e @env/vars.yml playbooks/deploy.yml --limit=1.2.3.4,5.6.7.8
   ```
