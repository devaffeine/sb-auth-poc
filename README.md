# sb-auth-poc

Spring Boot Auth PoC project

## Setup

- [JDK 17](https://openjdk.org/projects/jdk/17/) or superior
- [Docker compose](https://docs.docker.com/compose/)

### Environment variables:

```
JWT_SECRET_KEY=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970;
DB_HOSTNAME=localhost; DB_PORT=3306; DB_USERNAME=auth_poc; DB_PASSWORD=auth_poc_pass; DB_NAME=auth_poc
```

### Run local

Start database:

```
docker compose up
```

Run app:

```
./gradlew bootRun
```

## OpenAPI

- Check OpenAPI json at http://localhost:8080/api-docs
- Check OpenAPI UI at http://localhost:8080/api-ui.html

## Monitoring

- Check health status at http://localhost:8080/actuator/health
- Check metrics at http://localhost:8080/actuator/metrics


## TODO

 - checkout how to add application metrics to prometheus
 - checkout how to measure latency of requests
 - build with github actions
 - add integration tests with jmeter
 - add integration tests with newman
 - add logging for splunk
