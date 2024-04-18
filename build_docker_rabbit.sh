#!/usr/bin/env bash
CI_REGISTRY_IMAGE="rabbitmq"
VERSION=3.9.8
IMAGE=${CI_REGISTRY_IMAGE}:${VERSION}
docker build -t ${IMAGE} .