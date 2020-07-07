package com.github.deepy.licensing.data

import groovy.transform.Immutable

@Immutable
class MavenProject {
    String groupId
    String artifactId
    String version
}
