import grails.util.Metadata;

import java.text.SimpleDateFormat;

eventCreateWarStart = { warName, stagingDir ->
 
	def formatter = new SimpleDateFormat("ddMMyyyy-HHmmss")
	def buildDate = formatter.format(new Date(System.currentTimeMillis()))
	buildDate += '-'+Calendar.getInstance().getTimeZone().getDisplayName(false, TimeZone.SHORT)
	
	def gitCommit = ''
	try {
		// TODO make it platform independant
		def proc = "git rev-parse HEAD".execute()
		gitCommit = proc.text
	} catch (Exception e) {}
	
	Metadata build = Metadata.getInstance(new File("${stagingDir}/WEB-INF/classes/build.info"));
	build.'app.buildDate' = buildDate
	build.'app.gitCommit' = gitCommit
	build.'app.systemName' = InetAddress.getLocalHost().getHostName()
	build.'app.timezone' = Calendar.getInstance().getTimeZone().getID()
	build.persist()
 
	println "Compile Starting on build #${buildDate}, git commit: ${gitCommit}"
}