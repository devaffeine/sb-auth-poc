# sb-auth-poc

Spring Boot Auth PoC project

## Setup

- [JDK 17](https://openjdk.org/projects/jdk/17/) or superior
- [Docker compose](https://docs.docker.com/compose/)

### Environment variables:

```
JWT_SECRET_KEY=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
DB_HOSTNAME=localhost
DB_PORT=3306
DB_USERNAME=auth_poc
DB_PASSWORD=auth_poc_pass
DB_NAME=auth_poc
```

### Run local

Using docker compose:

```bash
docker compose up
```

Running standalone:

```bash
./gradlew bootRun
```

### Containerizing

Using buildpacks:
```bash
./gradlew bootBuildImage
```

Using jib:
```bash
./gradlew jibDockerBuild
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
- add integration tests with newman
- add logging for splunk

## K6 Testing

- Check https://k6.io/docs/getting-started/installation/ for installation

```bash
k6 run --vus 100 --duration 60s k6/basic-load.js
```
 