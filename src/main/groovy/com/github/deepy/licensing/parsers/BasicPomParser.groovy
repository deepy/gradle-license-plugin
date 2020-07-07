package com.github.deepy.licensing.parsers

import com.github.deepy.licensing.data.LicenseResult
import com.github.deepy.licensing.data.MavenPomLicense
import com.github.deepy.licensing.data.MavenPomMissingLicense
import com.github.deepy.licensing.data.MavenProject
import com.github.deepy.licensing.data.PomCommentLicense
import groovy.util.slurpersupport.GPathResult
import groovy.util.slurpersupport.NodeChild
import org.gradle.maven.MavenPomArtifact

class BasicPomParser {
    static List<LicenseResult> checkMavenLicenseTag(GPathResult xml) {
        if (xml.licenses.size() > 0) {
            return xml.licenses.collect { NodeChild it ->
                new MavenPomLicense(
                        it.children()['name'].text(),
                        it.children()['url'].text(),
                        it.children()['distribution'].text(),
                        it.children()['comment'].text())
            }
        } else if (xml.'parent'.size() > 0) {
            return [new MavenPomMissingLicense(
                    new MavenProject(
                            xml.'parent'.'groupId'.text(),
                            xml.'parent'.'artifactId'.text(),
                            xml.'parent'.'version'.text()
                    ))]
        }

        return null
    }

    static List<LicenseResult> checkPomForLicense(File file) {
        def startOfXml = file.text.indexOf("<project")
        def result = file.text.substring(0, startOfXml).trim()
        def start = result.indexOf("<!--")
        def end = result.indexOf("-->")
        if (start > 0 && end > 0) {
            result = result.substring(start+4, end)
            if (result.length() > 0) {
                return [new PomCommentLicense(result)]
            }
        }

        return null
    }

    static List<LicenseResult> getLicenses(File file) {
        def xml = new XmlSlurper().parseText(file.text)
        def result = checkMavenLicenseTag(xml)
        if (result != null) {
            if (result.size() != 1 && !result[0] instanceof MavenPomMissingLicense) {
                return result
            } else {
                def pomLicense = checkPomForLicense(file)
                if (pomLicense != null) {
                    return pomLicense
                } else {
                    return result
                }
            }
        }

        return checkPomForLicense(file)
    }

    static List<LicenseResult> parseArtifact(Collection<MavenPomArtifact> artifacts) {
        return artifacts.collectMany { getLicenses(it.file) }
    }
}
