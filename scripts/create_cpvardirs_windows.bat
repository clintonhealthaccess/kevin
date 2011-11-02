@echo off
REM javadocs cannot use classpath variables in Eclipse. Provide workaround so that a team can share common .classpath and .project files.
REM change the default location with grails create-eclipse-files --cpvars-dir=/some/other/dir
REM Get linkd.exe from Microsoft rktools: http://www.microsoft.com/downloads/details.aspx?FamilyID=9D467A69-57FF-4AE7-96EE-B18C4790CFFD
REM linkd.exe is only for NTFS partitions (creates NTFS junction points)mkdir "C:\Users\user\CHAI\DHSST\dhsst-Conf\grails-2.0.0.RC1\grails-2.0.0.RC1\eclipse-cpvars"
cd "C:\Users\user\CHAI\DHSST\dhsst-Conf\grails-2.0.0.RC1\grails-2.0.0.RC1\eclipse-cpvars"
linkd "GRAILS_IVYCACHE" "C:\Users\user\.grails\ivy-cache"
linkd "GRAILS_WORKDIR" "C:\Users\user\.grails\2.0.0.RC1"
echo Links installed to C:\Users\user\CHAI\DHSST\dhsst-Conf\grails-2.0.0.RC1\grails-2.0.0.RC1\eclipse-cpvars. Delete these with linkd directory /d or delrp directory command.
echo Run grails create-eclipse-files again to create common paths to javadocs in .classpath (team members can share .classpath files and they can be in versioned in the source code repository)
