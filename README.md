# Klog

![Dutch Clogs](./Dutch-Clogs.png)

A [bliki](https://martinfowler.com/bliki/WhatIsaBliki.html) engine 
written in [Kotlin](https://kotlinlang.org/), 
using [Spring Boot](https://spring.io/), 
[Jetbrains Exposed](https://github.com/JetBrains/Exposed), 
and a [Postgres](https://www.postgresql.org/) database.

----

## Toolchain

These tools are known to work. You can use others if you wish, but YMMV.
- Azul JDK 17
- Kotlin 1.8.0
- IntelliJ 2022.3.x or newer
- Postgres 15.1

## Development Standards

We stick to a few simple rules to keep this codebase in good shape.

1. Formatting and linting is handled by [detekt](https://detekt.dev/) with some help from [spotless](https://github.com/diffplug/spotless)
   1. Builds will fail if your code does not pass these checks. 
   2. To lint your code: `./gradlew check`
2. Test your code.
   1. `domain` code should have almost always have unit tests.
   2. `frameworks` code should include some amount of integration tests, unless implementing them is difficult/impossible. Use your best judgement here.
3. Document your code.
    1. Try to imagine what a new developer with no context will need to know about your code. If it's not obvious, document it.
    2. Include links to reference materials you use.
    3. Use [KDoc](https://kotlinlang.org/docs/kotlin-doc.html#kdoc-syntax) syntax.

## Run the web app in your dev environment

Spin up Postgres 15

```shell
# From the project root, cd into compose
cd ./compose
# and start the postgres container
docker-compose up -d postgres
```

The `klog` database should be created automagically for you. Then run migrations ...

```shell
# From project root directory
./gradlew flywayMigrate
```

You can now run the application from IntelliJ using the included run configuration or from the command line with ...

```shell
./gradlew bootRun
```

### Shutting down the local services

```shell
# From the project root, cd into compose
cd ./compose
# and tear down the containers
docker-compose down
```
