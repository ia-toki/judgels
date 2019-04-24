FROM golang:1.11.9-alpine3.8 as builder

WORKDIR /build
COPY main.go .
RUN CGO_ENABLED=0 GOOS=linux go build -installsuffix 'static' -o scoreboard-receiver .

FROM scratch
COPY --from=builder /build/scoreboard-receiver /judgels/scoreboard-receiver/service/bin/
WORKDIR /judgels/scoreboard-receiver
EXPOSE 9144
CMD ["./service/bin/scoreboard-receiver"]
