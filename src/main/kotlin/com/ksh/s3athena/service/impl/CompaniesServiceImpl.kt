package com.ksh.s3athena.service.impl

import com.ksh.s3athena.dto.request.CompanyRequestDto
import com.ksh.s3athena.service.AthenaService
import com.ksh.s3athena.service.CompaniesService
import org.springframework.stereotype.Service

@Service
class CompaniesServiceImpl(private val athenaService: AthenaService) : CompaniesService {
    override fun addCompany(dto: CompanyRequestDto) {
        athenaService.get()
    }

}
