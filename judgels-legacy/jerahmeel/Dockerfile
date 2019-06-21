FROM ubuntu:18.04

RUN apt-get update \
    && apt-get -y install openjdk-8-jre \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV PATH $JAVA_HOME/bin:$PATH

RUN useradd -ms /bin/bash judgels
USER judgels

WORKDIR /judgels/jerahmeel

EXPOSE 9006

COPY build/stage/playBinary .

ENTRYPOINT ["./bin/playBinary"]
