package com.example.demo
//import com.example.demo.Config
import org.springframework.stereotype.Service
import java.util.ArrayList

@Service
class Service(
    private val config: ServiceConfig
) {
    private val people = mutableListOf<Person>()
    private var counter = 0
    fun add(name: String): Person? {
        if (people.size >= config.maxRecords) return null
        if (config.forbiddenNames.contains(name)) return null
        val person = Person(counter ++, name, 0, "")
        people.add(person)
        return person
    }

    fun getAll() = people.toList()

    fun get(id: Int) = people.find { it.id == id }

    fun remove(id: Int): Boolean {
        return people.removeIf { it.id == id }
    }
}