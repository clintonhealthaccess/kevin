#!/bin/sh
# javadocs cannot use classpath variables in Eclipse. Provide workaround so that a team can share common .classpath and .project files.
# change the default location with grails create-eclipse-files --cpvars-dir=/some/other/dir
mkdir -p "/Users/fterrier/Projects/grails-2.0.0.M2/eclipse-cpvars"
cd "/Users/fterrier/Projects/grails-2.0.0.M2/eclipse-cpvars"
ln -s "/Users/fterrier/.grails/ivy-cache" "GRAILS_IVYCACHE"
ln -s "/Users/fterrier/.grails/2.0.0.M2" "GRAILS_WORKDIR"
echo "Run grails create-eclipse-files again to create common paths to javadocs in .classpath (team members can share .classpath files and they can be in versioned in the source code repository)"
