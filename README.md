Spring boot, Kotlin, Hibernate, MariaDB

### Architecture

We are using MVC from this [reference]().

### Pull requests template

Please check out [here]().

### Requirements

- Docker >= 19.x.x, docker-compose >= 1.25.x

### Run the application
- Clone project from repository `git clone -b master git@github.com:trongtd-1688/kotlint.git`
- Build gradle in `build.gradle.kts`

- Spring boot configuration:

> Edit configuration > Environment variables section add: SPRING_PROFILES_ACTIVE=local
> Add src/main/resources/application-local.yml to ~/.gitignore_global

```shell
cp src/main/resources/application-local.yml.example src/main/resources/application-local.yml
```

- Start server
> - __Make sure stop mysql server on your machine!__

```shell
# Start docker container
docker-compose up

# Stop docker container
docker-compose down
```

### Klint

- Configuration file `.editorconfig` [reference](https://github.com/pinterest/ktlint#editorconfig)

- Command check:

```shell
./gradlew ktlintCheck  # Check convention
./gradlew ktlintFormat # Auto format
```

### Swagger API Document

- Available at http://localhost:8080/api/swagger-ui/
