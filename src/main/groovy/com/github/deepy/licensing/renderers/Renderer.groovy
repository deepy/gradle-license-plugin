package com.github.deepy.licensing.renderers

interface Renderer {
    String render(List<Map<String, Object>> linkedHashMaps)
}