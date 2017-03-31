FROM alpine:3.3

RUN apk --update add postgresql-client && rm -rf /var/cache/apk/*
RUN apk --update add curl && rm -rf /var/cache/apk/*
RUN apk --update add bash && rm -rf /var/cache/apk/*

RUN curl -L https://raw.githubusercontent.com/ettore26/wait-for-command/master/wait-for-command.sh -o /usr/local/bin/wait-for-command.sh \
    && chmod +x /usr/local/bin/wait-for-command.sh

COPY entrypoint.sh /

ENTRYPOINT [ "/entrypoint.sh" ]