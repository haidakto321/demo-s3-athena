package com.ksh.s3athena

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class S3AthenaApplication

fun main(args: Array<String>) {
	runApplication<S3AthenaApplication>(*args)
}
