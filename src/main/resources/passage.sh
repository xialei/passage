#!/bin/sh

CLASSPATH="."

# travel all jars and add to CLASSPATH
for jarfile in `ls lib/.`
do
    CLASSPATH="${CLASSPATH}:lib/$jarfile"
done

echo $CLASSPATH
java -cp ${CLASSPATH} com.aug3.storage.passage.server.PassageServer