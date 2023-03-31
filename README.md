# sb-auth-poc

Spring Boot Auth PoC project

## Setup

- Install [JDK 17](https://openjdk.org/projects/jdk/17/) or superior
- Install [Docker compose](https://docs.docker.com/compose/)

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

- Check [OpenAPI json](http://localhost:8080/api-docs)
- Check [OpenAPI UI](http://localhost:8080/api-ui.html)

## Monitoring

- Check [health status](http://localhost:8080/actuator/health)
- Check [metrics](http://localhost:8080/actuator/metrics)

## K6 Testing

- Install [k6](https://k6.io/docs/getting-started/installation/)

### Smoke test

- *--vus*: Virtual users. Optional, defaults to 5
- *--duration*: Duration of tests. Optional, defaults to 1m (a minute)
- *BASE_URL*: API base URL. Optional, defaults to *http://localhost:8080*

```bash
k6 run --vus 5 --duration 1m k6/smoke-test.js --env BASE_URL=https://todo.url
```

## TODO

- checkout how to add application metrics to prometheus
- checkout how to measure latency of requests
- build with github actions
- add integration tests with newman
- add logging for splunk
