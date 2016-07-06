FROM java:8-jre

ADD package.tar.gz /app
WORKDIR /app
CMD /app/bin/svc-seed -Dorg.aspectj.tracing.factory=default -J-javaagent:lib/org.aspectj.aspectjweaver-1.8.9.jar

