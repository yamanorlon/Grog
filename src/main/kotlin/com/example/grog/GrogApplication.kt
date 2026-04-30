package com.example.grog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GrogApplication

fun main(args: Array<String>) {
	runApplication<GrogApplication>(*args)
}
