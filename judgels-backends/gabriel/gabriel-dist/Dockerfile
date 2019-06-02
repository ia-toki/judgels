FROM ubuntu:18.04

RUN apt-get update \
    && apt-get install -y git make gcc libcap-dev

ENV ISOLATE_VERSION v1.8.1
RUN git clone --branch $ISOLATE_VERSION --depth 1 https://github.com/ioi/isolate; \
    cd isolate; \
    make install
RUN git clone --depth 1 git://git.ucw.cz/moe.git; \
    cd moe/eval; \
    gcc -o iwrapper iwrapper.c


FROM ubuntu:18.04

RUN apt-get update \
    && apt-get -y install openjdk-8-jre libcap-dev g++ \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV PATH $JAVA_HOME/bin:$PATH

WORKDIR /judgels/gabriel

COPY --from=0 /isolate/isolate /judgels/moe/
COPY --from=0 /isolate/isolate-check-environment /judgels/moe/
COPY --from=0 /usr/local/etc/isolate /usr/local/etc/
COPY --from=0 /moe/eval/iwrapper /judgels/moe/
COPY build/distributions .

ENTRYPOINT ["./service/bin/init.sh"]
CMD ["console"]
