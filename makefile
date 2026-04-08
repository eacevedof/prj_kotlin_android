#!/bin/bash
TODAY := $(shell date +'%Y-%m-%d')
CURRENT_BRANCH := $(shell git rev-parse --abbrev-ref HEAD)

# ============================================================================
# HELP
# ============================================================================
help: ## Show this help message
	@echo "usage: make [target]"
	@echo
	@echo "targets:"
	@egrep "^(.+)\:\ ##\ (.+)" ${MAKEFILE_LIST} | column -t -c 2 -s ":#"

# ============================================================================
# GIT
# ============================================================================
gitpush: ## git push m="any message"
	clear;
	git add .; git commit -m "$(m)"; git push;

update-branch: ## update main branches
	git fetch --all;
	git checkout main; git reset --hard origin/main;
	git checkout $(CURRENT_BRANCH);

# ============================================================================
# POS-MODULES
# ============================================================================
modules-build: ## Build pos-modules
	cd pos-modules && JAVA_HOME="C:/programs/jdk/jdk-24" ./gradlew build

modules-clean: ## Clean pos-modules
	cd pos-modules && JAVA_HOME="C:/programs/jdk/jdk-24" ./gradlew clean

modules-test: ## Test pos-modules
	cd pos-modules && JAVA_HOME="C:/programs/jdk/jdk-24" ./gradlew test

# ============================================================================
# POS-API
# ============================================================================
api-run: ## Run pos-api server (port 9090)
	cd pos-api && JAVA_HOME="C:/programs/jdk/jdk-24" ./gradlew run

api-build: ## Build pos-api
	cd pos-api && JAVA_HOME="C:/programs/jdk/jdk-24" ./gradlew build

api-clean: ## Clean pos-api
	cd pos-api && JAVA_HOME="C:/programs/jdk/jdk-24" ./gradlew clean

api-test: ## Test pos-api
	cd pos-api && JAVA_HOME="C:/programs/jdk/jdk-24" ./gradlew test

api-jar: ## Build pos-api fat JAR
	cd pos-api && JAVA_HOME="C:/programs/jdk/jdk-24" ./gradlew buildFatJar

# ============================================================================
# POS-CLI (placeholder)
# ============================================================================
cli-run: ## Run pos-cli
	cd pos-cli && ./gradlew run

cli-build: ## Build pos-cli
	cd pos-cli && ./gradlew build

# ============================================================================
# POS-CLIENT (placeholder)
# ============================================================================
client-android: ## Run pos-client Android
	cd pos-client && ./gradlew :composeApp:installDebug

client-desktop: ## Run pos-client Desktop
	cd pos-client && ./gradlew :composeApp:run

client-build: ## Build pos-client
	cd pos-client && ./gradlew build

# ============================================================================
# ALL
# ============================================================================
build-all: ## Build all projects
	make modules-build
	make api-build

clean-all: ## Clean all projects
	make modules-clean
	make api-clean

test-all: ## Test all projects
	make modules-test
	make api-test

# ============================================================================
# DOCKER (placeholder)
# ============================================================================
docker-build: ## Build Docker image for api
	docker build -t pos-api:latest ./pos-api

docker-run: ## Run Docker container
	docker run -p 8080:8080 pos-api:latest

# ============================================================================
# UTILS
# ============================================================================
setup: ## Download gradle wrapper JARs
	@echo "Downloading gradle wrapper for pos-modules..."
	cd pos-modules && ./gradlew wrapper --gradle-version=8.10
	@echo "Downloading gradle wrapper for pos-api..."
	cd pos-api && ./gradlew wrapper --gradle-version=8.10
	@echo "Setup complete!"
