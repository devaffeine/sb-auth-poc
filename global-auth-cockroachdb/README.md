# global-auth-cockroachdb

Spring Boot Auth PoC project

## Setup

- Install [JDK 17](https://openjdk.org/projects/jdk/17/) or superior
- Install [Docker compose](https://docs.docker.com/compose/)

### Config

- Copy *.env-sample* to *.env* and edit it.

### Run local

Using docker compose:

```bash
docker compose up -d
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

- Check [OpenAPI json](http://localhost:8080/api-docs)
- Check [OpenAPI UI](http://localhost:8080/api-ui.html)

## Monitoring

- Check [health status](http://localhost:8080/actuator/health)
- Check [metrics](http://localhost:8080/actuator/metrics)

### Testing

### Unit tests

```bash
./gradlew clean test
```

### K6 Tests

- Install [k6](https://k6.io/docs/getting-started/installation/)

#### Configuration

- *BASE_URL*: API base URL. Optional, defaults to *http://localhost:8080*

#### Smoke test

```bash
k6 run k6/smoke-test.js --env BASE_URL=https://todo.url
```

#### Load test

```bash
k6 run k6/load-test.js --env BASE_URL=https://todo.url
```

## TODO

- add docker logging for splunk
- checkout how to add application metrics to prometheus
- checkout how to measure latency of requests
- newman test with github actions
