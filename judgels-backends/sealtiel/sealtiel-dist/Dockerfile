FROM ubuntu:18.04

RUN apt-get update \
    && apt-get -y install openjdk-8-jre \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV PATH $JAVA_HOME/bin:$PATH

RUN useradd -ms /bin/bash judgels
USER judgels

WORKDIR /judgels/sealtiel

EXPOSE 9003 9103

COPY build/distributions .

ENTRYPOINT ["./service/bin/init.sh"]
CMD ["console"]
