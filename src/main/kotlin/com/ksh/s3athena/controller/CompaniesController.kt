package com.ksh.s3athena.controller

import com.ksh.s3athena.dto.request.CompanyRequestDto
import com.ksh.s3athena.service.CompaniesService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/companies")
@Validated
class CompaniesController(private val companiesService: CompaniesService) {
    @PostMapping
    fun addCompany(dto: CompanyRequestDto): ResponseEntity<*> {
        return ResponseEntity(companiesService.addCompany(dto), HttpStatus.CREATED)
    }
}
