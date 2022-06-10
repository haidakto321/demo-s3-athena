package com.ksh.s3athena.config

import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.athena.AthenaClient
import software.amazon.awssdk.services.athena.AthenaClientBuilder

@Configuration
class AthenaClientConfig {
    private val builder: AthenaClientBuilder = AthenaClient.builder()
        .region(Region.AP_NORTHEAST_1)
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())

    fun createClient(): AthenaClient {
        return builder.build()
    }
}
