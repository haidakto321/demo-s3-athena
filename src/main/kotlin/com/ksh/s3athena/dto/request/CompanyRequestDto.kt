package com.ksh.s3athena.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

class CompanyRequestDto {
    @NotBlank
    @JsonProperty("first_name")
    var firstName: String? = null

    @NotBlank
    @JsonProperty("last_name")
    var lastName: String? = null

    @Email
    @JsonProperty("email")
    var email: String? = null

    @NotBlank
    @JsonProperty("gender")
    var gender: String? = null

    @NotBlank
    @JsonProperty("destination_id")
    var destinationId: String? = null

    @NotBlank
    @JsonProperty("address")
    var address: String? = null
}
