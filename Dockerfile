FROM openjdk:8-jre

COPY docker/entrypoint.sh /

EXPOSE 8080

COPY api/target/reference-api-*.jar /app.jar

ENTRYPOINT [ "/entrypoint.sh" ]
