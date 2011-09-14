#!/bin/sh
# javadocs cannot use classpath variables in Eclipse. Provide workaround so that a team can share common .classpath and .project files.
# change the default location with grails create-eclipse-files --cpvars-dir=/some/other/dir
mkdir -p "/usr/local/Cellar/grails/1.3.7/libexec/eclipse-cpvars"
cd "/usr/local/Cellar/grails/1.3.7/libexec/eclipse-cpvars"
ln -s "/Users/fterrier/.ivy2/cache" "GRAILS_IVYCACHE"
ln -s "/Users/fterrier/.grails/1.3.7" "GRAILS_WORKDIR"
echo "Run grails create-eclipse-files again to create common paths to javadocs in .classpath (team members can share .classpath files and they can be in versioned in the source code repository)"
