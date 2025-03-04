FROM ubuntu:24.04

RUN apt-get update \
    && apt-get install -y git make gcc libcap-dev

ENV ISOLATE_VERSION v1.10.1
RUN git clone --branch $ISOLATE_VERSION --depth 1 https://github.com/ioi/isolate; \
    cd isolate; \
    make install
RUN git clone --depth 1 git://git.ucw.cz/moe.git; \
    cd moe/eval; \
    gcc -o iwrapper iwrapper.c


FROM ubuntu:24.04

ENV CARGO_HOME /usr
ENV RUSTUP_HOME /usr
ENV XDG_CACHE_HOME /tmp
ENV JAVA_HOME /usr/lib/jvm/java-17-openjdk-amd64
ENV PATH $JAVA_HOME/bin:$PATH

RUN apt-get update \
    && apt-get -y --no-install-recommends install software-properties-common gpg-agent \
    && add-apt-repository -y ppa:pypy/ppa \
    && add-apt-repository -y ppa:ubuntu-toolchain-r/test \
    && apt-get -y --no-install-recommends install \
       curl \
       openssh-client \
       rsync \
    && apt-get -y --no-install-recommends install \
       fp-compiler \
       g++-13 \
       golang \
       libcap-dev \
       openjdk-17-jdk-headless \
       openjdk-17-jre \
       pypy3 \
       python3 \
    && ln -s /usr/bin/gcc-13 /usr/bin/gcc \
    && ln -s /usr/bin/g++-13 /usr/bin/g++ \
    && curl -sSf https://sh.rustup.rs/ | bash -s -- -y -q --default-toolchain=1.57.0 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /judgels/grader

ARG VCS_REF

LABEL org.opencontainers.image.title judgels/grader
LABEL org.opencontainers.image.source https://github.com/ia-toki/judgels
LABEL org.opencontainers.image.revision $VCS_REF

COPY --from=0 /isolate/isolate /judgels/isolate/bin/
COPY --from=0 /usr/local/etc/isolate /usr/local/etc/
COPY --from=0 /moe/eval/iwrapper /judgels/isolate/bin/
COPY build/distributions .

ENTRYPOINT ["./init.sh"]
CMD ["server"]
