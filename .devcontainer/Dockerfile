FROM ubuntu:22.04

ARG USER_UID=1000
ARG USER_GID=$USER_UID

RUN apt-get update \
    && apt-get install -y sudo git \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends tzdata \
    && addgroup --gid "${USER_GID}" tokidev \
    && adduser --uid "${USER_UID}" --gid "${USER_GID}" --disabled-password --gecos '' tokidev \
    && echo 'tokidev ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

EXPOSE 9101
EXPOSE 3000
