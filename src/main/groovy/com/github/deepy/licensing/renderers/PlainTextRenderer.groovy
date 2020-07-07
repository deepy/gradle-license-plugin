package com.github.deepy.licensing.renderers

import com.github.deepy.licensing.data.LicenseResult
import com.github.deepy.licensing.data.MavenPomLicense
import com.github.deepy.licensing.data.MavenPomMissingLicense
import com.github.deepy.licensing.data.PomCommentLicense
import org.gradle.api.artifacts.component.ComponentIdentifier

class PlainTextRenderer implements Renderer {
    @Override
    String render(List<Map<String, Object>> maps) {
        return maps.collect {
            parse(it['artifact'] as ComponentIdentifier,
            it['licenses'] as List<LicenseResult>)
        }.join("\n")
    }

    String parse(LicenseResult license) {
        return "Not handled: ${license.class}"
    }

    String parse(MavenPomMissingLicense license) {
        return "License not found!"
    }

    String parse(PomCommentLicense license) {
        return license.result
    }

    String parse(MavenPomLicense license) {
        StringWriter sw = new StringWriter()
        if (license.name != null) {
            sw.write("Licensed under: ${license.name}\n")
        }
        if (license.url != null) {
            sw.write("Available at: ${license.url}\n")
        }
        if (license.distribution != null && !license.distribution.empty) {
            if (license.distribution == "repo") {
                sw.write("Distribution: repo, may be downloaded from a manual repository\n")
            } else if (license.distribution == "manual") {
                sw.write("Distribution: manual, *MUST* be manually installed\n")
            } else {
                sw.write("Distribution: ${license.distribution}\n")
            }
        }
        if (license.comment != null && !license.comment.empty) {
            sw.write("Comment: ${license.comment}\n")
        }

        return sw.toString()
    }

    String parse(ComponentIdentifier componentIdentifier, List<LicenseResult> licenseResults) {
        return "\n\n${componentIdentifier.displayName}\n${'='*componentIdentifier.displayName.length()}\n" + licenseResults.collect { parse(it) }.join("\n")
    }
}
