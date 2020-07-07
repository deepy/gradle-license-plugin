package com.github.deepy.licensing.data

import groovy.transform.Immutable

@Immutable
class MavenPomLicense implements LicenseResult {
    String name
    String url
    String distribution
    String comment
}
