package com.github.deepy.licensing

import org.gradle.api.Project
import org.gradle.api.Plugin

class GradleLicensePlugin implements Plugin<Project> {
    void apply(Project project) {
        project.tasks.register("greeting") {
            doLast {
                println("Hello from plugin 'com.github.deepy.licensing.greeting'")
            }
        }

        project.tasks.register('licenseReport', LicenseReportTask, "runtimeClasspath")
    }
}
