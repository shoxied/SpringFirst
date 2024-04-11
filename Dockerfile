FROM openjdk:18.0.2.1

ARG VERSION=$VERSION
ENV jarFile=SpringFirst-$VERSION.jar

RUN mkdir -p /opt/spring-first
COPY target/${jarFile} /opt/spring-first
EXPOSE 8080
CMD  cd /opt/spring-first; java -XshowSettings:vm $JAVA_OPTS -Dspring.profiles.active=$PROFILE -jar ${jarFile} $ARGS