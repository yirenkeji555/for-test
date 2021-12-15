FROM pig4cloud/java:8-jre

MAINTAINER excel.lin

ENV TZ=Asia/Shanghai
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Djava.security.egd=file:/dev/./urandom"

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN mkdir -p /scfunc

WORKDIR /scfunc

EXPOSE 4000

ADD ./target/scfunc-0.0.1-SNAPSHOT.jar ./

CMD sleep 60;java $JAVA_OPTS -jar scfunc-0.0.1-SNAPSHOT.jar
