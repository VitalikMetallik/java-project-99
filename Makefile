check-deps:
	./gradlew dependencyUpdates -Drevision=release

dev:
	./gradlew run

setup:
	gradle wrapper --gradle-version 8.5

clean:
	./gradlew clean

build:
	make clean
	make lint
	make test

start: dev

install:
	./gradlew installDist

lint:
	./gradlew checkstyleMain
	./gradlew checkstyleTest

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

.PHONY: build