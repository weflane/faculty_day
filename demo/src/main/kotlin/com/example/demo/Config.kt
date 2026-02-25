package com.example.demo

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "app.service")
class ServiceConfig {
    var maxRecords: Int = 50
    var forbiddenNames: List<String> = listOf()
}