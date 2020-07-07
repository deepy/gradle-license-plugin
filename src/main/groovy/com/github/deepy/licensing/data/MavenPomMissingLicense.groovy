package com.github.deepy.licensing.data

import groovy.transform.Immutable

@Immutable
class MavenPomMissingLicense implements LicenseResult {
    MavenProject parent
}
