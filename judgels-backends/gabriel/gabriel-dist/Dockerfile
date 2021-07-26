FROM ubuntu:20.04

RUN apt-get update \
    && apt-get install -y git make gcc libcap-dev

ENV ISOLATE_VERSION v1.8.1
RUN git clone --branch $ISOLATE_VERSION --depth 1 https://github.com/ioi/isolate; \
    cd isolate; \
    make install
RUN git clone --depth 1 git://git.ucw.cz/moe.git; \
    cd moe/eval; \
    gcc -o iwrapper iwrapper.c


FROM ubuntu:20.04

RUN apt-get update \
    && apt-get -y --no-install-recommends install software-properties-common \
    && add-apt-repository -y ppa:pypy/ppa \
    && apt-get -y --no-install-recommends install \
       fp-compiler \
       g++ \
       golang \
       libcap-dev \
       openjdk-8-jdk-headless \
       openjdk-8-jre \
       pypy3 \
       python3 \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV PATH $JAVA_HOME/bin:$PATH

WORKDIR /judgels/gabriel

ARG VCS_REF

LABEL org.label-schema.schema-version="1.0"
LABEL org.label-schema.name="judgels/gabriel"
LABEL org.label-schema.vcs-url="https://github.com/ia-toki/judgels"
LABEL org.label-schema.vcs-ref=$VCS_REF

COPY --from=0 /isolate/isolate /judgels/moe/
COPY --from=0 /isolate/isolate-check-environment /judgels/moe/
COPY --from=0 /usr/local/etc/isolate /usr/local/etc/
COPY --from=0 /moe/eval/iwrapper /judgels/moe/
COPY build/distributions .

ENTRYPOINT ["./service/bin/init.sh"]
CMD ["console"]
