package com.qiblaarfinder.domain.model

enum class LocationSource(val label: String) {
    AUTO("GPS"),
    MANUAL("Manual"),
    CACHE("Offline Cache"),
}

