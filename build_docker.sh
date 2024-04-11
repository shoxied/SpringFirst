#!/usr/bin/env bash
CI_REGISTRY_IMAGE="myfirstspring"
VERSION=$(grep "<version>" pom.xml | awk 'NR == 1' |  awk -F ">" {'print $2'} | awk -F "<" {'print $1'})
IMAGE=${CI_REGISTRY_IMAGE}:${VERSION}
mvn clean package
echo "Version is ${VERSION}, Image name is ${IMAGE}"
docker build --build-arg VERSION=${VERSION} -t ${IMAGE} .
docker tag ${IMAGE} ${CI_REGISTRY_IMAGE}:latest