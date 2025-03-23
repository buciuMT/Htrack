FROM golang:1.24 AS builder

WORKDIR /build

COPY ./ ./

RUN go mod download

RUN go build -ldflags="-w -s" -o bin


FROM scratch

COPY --from=builder /build/bin /bin

ENTRYPOINT ["/bin"]
