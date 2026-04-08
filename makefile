#!/bin/bash
TODAY := $(shell date +'%Y-%m-%d')
CURRENT_BRANCH := $(shell git rev-parse --abbrev-ref HEAD)

help: ## Show this help message
	@echo "usage: make [target]"
	@echo
	@echo "targets:"
	@egrep "^(.+)\:\ ##\ (.+)" ${MAKEFILE_LIST} | column -t -c 2 -s ":#"

gitpush: ## git push m=any message
	clear;
	git add .; git commit -m "$(m)"; git push;

ssh-be: ## cont-php-fpm-7.4
	clear;
	winpty docker exec -it --user root cont-php-fpm-7.4 bash

CURRENT_BRANCH := $(shell git rev-parse --abbrev-ref HEAD)
update-branch:  ## update main branches
	git fetch --all;

	git checkout pos-main && git reset --hard origin/pos-main;
	git checkout main; git reset --hard origin/main;

	git checkout $(CURRENT_BRANCH);
	git diff --name-only pos-main main