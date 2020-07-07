package com.github.deepy.licensing

import com.github.deepy.licensing.data.MavenPomMissingLicense
import com.github.deepy.licensing.data.MavenProject
import com.github.deepy.licensing.parsers.BasicPomParser
import com.github.deepy.licensing.renderers.PlainTextRenderer
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.maven.MavenModule
import org.gradle.maven.MavenPomArtifact

import javax.inject.Inject

class LicenseReportTask extends DefaultTask {
    @Input
    String configuration

    @OutputDirectory
    File outDirectory

    @OutputFile
    File outFile

    @Inject
    LicenseReportTask(String configuration) {
        this.configuration = configuration

        outDirectory = project.file("$project.buildDir/licensing/$configuration")
        outDirectory.mkdirs()
        outFile = project.file("build/reports/${configuration}LicenseReport.txt")
        outFile.parentFile.mkdirs()
        outputs.dir(outDirectory)
            .withPropertyName("${configuration}Poms")
    }

    @TaskAction
    void action() {
        def conf = project.configurations.getByName(configuration)
        def results = project.dependencies.createArtifactResolutionQuery()
                .forComponents(conf
                                .incoming
                                .resolutionResult
                                .allDependencies
                                .collect { it.selected.id })
                .withArtifacts(MavenModule, MavenPomArtifact)
                .execute()

        results = results.resolvedComponents.collect {
            [artifact: it.id, licenses: BasicPomParser.parseArtifact(it.getArtifacts(MavenPomArtifact))]
        }

        def parents = results.collectMany { it['licenses'] }
                .find { it instanceof MavenPomMissingLicense }
                .collect { it.parent }
                .collect { MavenProject it -> project.dependencies.create([
                        group: it.groupId,
                        name: it.artifactId,
                        version: it.version
                ]) }

        def parentDeps = project.configurations
                .detachedConfiguration(*parents)
                    .incoming
                    .resolutionResult
                    .allDependencies
                    .collect { it.selected.id }


        def parentQuery = project.dependencies.createArtifactResolutionQuery()
                .forComponents(parentDeps)
                .withArtifacts(MavenModule, MavenPomArtifact)
                .execute()
                .resolvedComponents.each {
                    cit ->
                    def license = BasicPomParser.parseArtifact(cit.getArtifacts(MavenPomArtifact))
                    if (license != null && license.size() > 0 && !(license[0] instanceof MavenPomMissingLicense)) {
                        results
                        results.findAll { it.licenses.any { it instanceof MavenPomMissingLicense } }
                                .each {
                                    it['licenses'] = license
                                }
                    }
                }

        outFile.write("License report for: ${configuration}" + new PlainTextRenderer().render(results))
    }
}
