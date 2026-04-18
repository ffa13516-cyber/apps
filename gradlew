#!/bin/sh
APP_HOME="$(dirname "$0")"
exec java -Xmx512m \
    -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" \
    org.gradle.wrapper.GradleWrapperMain "$@"
