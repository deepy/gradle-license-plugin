package com.github.deepy.licensing

import spock.lang.Specification
import org.gradle.testkit.runner.GradleRunner

public class GradleLicensePluginFunctionalTest extends Specification {
    def "can run task"() {
        given:
        def projectDir = new File("build/functionalTest")
        projectDir.mkdirs()
        new File(projectDir, "settings.gradle").write ""
        new File(projectDir, "build.gradle").write """
            plugins {
                id('com.github.deepy.licensing-report')
                id('java')
            }

	    project.tasks.register('licenseReport', LicenseReportTask, "runtimeClasspath")
        """

        when:
        def runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("licenseReport")
        runner.withProjectDir(projectDir)
        def result = runner.build()

        then:
        new File('build/functionalTest/build/reports/runtimeClasspathLicenseReport.txt')
                .text == "License report for: runtimeClasspath"
    }
}
