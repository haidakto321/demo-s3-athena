package com.ksh.s3athena.service

import com.ksh.s3athena.dto.request.CompanyRequestDto

interface CompaniesService {
    fun addCompany(dto: CompanyRequestDto)
}