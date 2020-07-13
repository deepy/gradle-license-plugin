# Dependency license report

This plugin lets you create a report of licenses used in your dependencies.

The plugin is published to the [Gradle plugin portal](https://plugins.gradle.org/plugin/com.github.deepy.licensing-report)

## Usage
This will create a `licenseReport` task that scans the `runtimeClasspath` dependencies and creates a report in `build/reports/runtimeClasspathLicenseReport.txt`

```groovy
plugins { id 'com.github.deepy.licensing-report' version '0.2.0' }
tasks.register('licenseReport', LicenseReportTask, "runtimeClasspath")
```
