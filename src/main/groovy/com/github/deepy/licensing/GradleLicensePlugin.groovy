package com.github.deepy.licensing

import org.gradle.api.Project
import org.gradle.api.Plugin

class GradleLicensePlugin implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.extraProperties.set(LicenseReportTask.class.getSimpleName(), LicenseReportTask.class)
        //project.tasks.register('licenseReport', LicenseReportTask, "runtimeClasspath")
    }
}
