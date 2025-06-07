FROM eclipse-temurin:22-jdk as runtime

WORKDIR /artifacts

ENV JAVA_OPTS=""

ENTRYPOINT ["/bin/sh", "-c", "\
  JAR_TO_RUN=$(ls -1 *.jar 2>/dev/null | sort -t '-' -k2,3 -r | head -n 1); \
  if [ -z \"$JAR_TO_RUN\" ]; then \
    echo 'ERROR: No .jar files found in /artifacts'; \
    exit 1; \
  fi; \
  echo \"Running: java $JAVA_OPTS -jar $JAR_TO_RUN\"; \
  exec java $JAVA_OPTS -jar \"$JAR_TO_RUN\" \
"]
