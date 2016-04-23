#!/bin/sh

#RSS Server Home
RSS_HOME=.

#Java Home, must be the Java SDK 1.8 or later
#JAVA_HOME=

#JAVA Environment
JAVA_OPTIONS="-Djava.util.logging.config.file=${RSS_HOME}/config/logging.properties -Djsse.enableSNIExtension=false"
JAVA_CLASSPATH="${RSS_HOME}/config:${RSS_HOME}/lib"

#Build Classpath
for JARFILE in ${RSS_HOME}/lib/*.jar ; do
  JAVA_CLASSPATH="${JAVA_CLASSPATH}:${JARFILE}"
done

#Start the Web Renderer Server
${JAVA_HOME}/bin/java -server ${JAVA_OPTIONS} -cp "${JAVA_CLASSPATH}" org.apretaste.rss.Main >rss.log 2>rss.err

