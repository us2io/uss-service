FROM openjdk:8-jre

ARG APP_VER
ENV APP_VER ${APP_VER:-latest}

ADD package.tar.gz /app
WORKDIR /app
CMD /app/bin/svc-seed -Dorg.aspectj.tracing.factory=default -J-javaagent:lib/org.aspectj.aspectjweaver-1.8.9.jar \
     -J-Xms512m -J-Xmx512m -J-server \
     -J-XX:MaxMetaspaceSize=128m \
     -J-XX:+UseConcMarkSweepGC -J-XX:+CMSParallelRemarkEnabled \
     -J-XX:+UseCMSInitiatingOccupancyOnly -J-XX:CMSInitiatingOccupancyFraction=68 \
     -J-XX:+ScavengeBeforeFullGC -J-XX:+CMSScavengeBeforeRemark \
     -J-XX:+PrintGCDateStamps -J-verbose:gc -J-XX:+PrintGCDetails -J-Xloggc:"/app/log/" \
     -J-XX:+UseGCLogFileRotation -J-XX:NumberOfGCLogFiles=10 -J-XX:GCLogFileSize=10M \
     -J-Dcom.sun.management.jmxremote.port=7199 \
     -J-Dcom.sun.management.jmxremote.rmi.port=7199 \
     -J-Dcom.sun.management.jmxremote.authenticate=false \
     -J-Dcom.sun.management.jmxremote.local.only=false \
     -J-Djava.rmi.server.hostname=127.0.0.1 \
     -J-Dcom.sun.management.jmxremote.ssl=false
