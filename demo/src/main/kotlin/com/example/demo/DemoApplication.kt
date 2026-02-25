package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(ServiceConfig::class)
class Lesson2SeminarApplication

fun main(args: Array<String>) {
    runApplication<Lesson2SeminarApplication>(*args)
}