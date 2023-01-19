# Build Tracking Plugin

Gradle plugin for collecting and sending some build data
to [build-traker-store service](https://github.com/dvkrasnov/build-tracker-store)

## Project structure

src folder contains next:

* **main** folder contains main source code of the plugin
* **test** folder contains unit tests, which can be executed during the full build of the project or by running
  task ***:test***
* **integrationTest** folder contains integration tests, which can be executed during the full build of the project or
  by running task ***:integrationTest***
* **functionalTest** folder contains functional tests, which can be executed with ***:functionalTest*** task

## Build and push to .m2

From root project folder run:

```bash
./gradlew publish
```

## Usage

**NOTE: you must build and push plugin to local .m2 repository ("BUILD" section of this doc)**

Now this plugin is available only from local .m2 repository. So you need to add next at the top of the settings.gradle
file in your project:

```groovy
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
```

and then apply plugin in your build.gradle.kts file:

```kotlin
plugins {
    id("ru.krasnov.jetbrains.tracking-plugin") version "1.0.0"
}
```

or in your build.gradle:

```groovy
plugins {
    id 'ru.krasnov.jetbrains.tracking-plugin' version '1.0.0'
}
```

**Additionally**, you need start **[build-traker-store service](https://github.com/dvkrasnov/build-tracker-store)**
service and set up plugin properties. Check relevant sections of this doc.

## Supported properties

This plugin has one extension ***trackingPlugin*** for custom setting properties. This extension contains the following
properties:

Name | Type   | Description | Default value
--- |--------| --- | ---
resultsCollectorService | Object | This is base object for [build-traker-store service](https://github.com/dvkrasnov/build-tracker-store) settings | - 
resultsCollectorService.url | String | Url for [build-traker-store service](https://github.com/dvkrasnov/build-tracker-store)'s endpoint for saving build results | http://localhost:8080/build

**Example:**

In build.gradle.kts

```kotlin
trackingPlugin {
    resultsCollectorService {
        url.set("http://localhost:8080/build")
    }
}
```

In build.gradle

```groovy
trackingPlugin {
    resultsCollectorService {
        url = 'http://localhost:8080/build'
    }
}
```

## Run [build-traker-store service](https://github.com/dvkrasnov/build-tracker-store)

**Prerequisite**

* docker for linux 20.10.0+
* docker for Mac and Windows 18.03+

To start [build-traker-store service](https://github.com/dvkrasnov/build-tracker-store) from this project root folder
you need to run:

```bash
docker-compose up -d
```

by default will be created two docker containers:

* **build-tracker-store** - service for processing build results from this plugin
* **postgres-db** - PostgreSQL db for storing data

**build-tracker-store** by default is available on port 8080. If you want to change this port just set **STORE_PUBLIC_PORT** environment variable before running docker-compose:

```bash
export STORE_PUBLIC_PORT=8081
```

After all steps your UI with executed builds will be available on:
http://localhost:8080/build

Now you can run any task in your project and **Build Tracking Plugin** will send collected data
to [build-traker-store service](https://github.com/dvkrasnov/build-tracker-store).
For example in your project you can run:

```bash
./gradlew clean tasks
```