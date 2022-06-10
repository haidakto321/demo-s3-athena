package com.ksh.s3athena.service.impl

import com.ksh.s3athena.config.AthenaClientConfig
import com.ksh.s3athena.service.AthenaService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.athena.AthenaClient
import software.amazon.awssdk.services.athena.model.AthenaException
import software.amazon.awssdk.services.athena.model.ColumnInfo
import software.amazon.awssdk.services.athena.model.Datum
import software.amazon.awssdk.services.athena.model.GetQueryExecutionRequest
import software.amazon.awssdk.services.athena.model.GetQueryExecutionResponse
import software.amazon.awssdk.services.athena.model.GetQueryResultsRequest
import software.amazon.awssdk.services.athena.model.QueryExecutionContext
import software.amazon.awssdk.services.athena.model.QueryExecutionState
import software.amazon.awssdk.services.athena.model.ResultConfiguration
import software.amazon.awssdk.services.athena.model.Row
import software.amazon.awssdk.services.athena.model.StartQueryExecutionRequest
import software.amazon.awssdk.services.athena.model.StartQueryExecutionResponse
import software.amazon.awssdk.services.athena.paginators.GetQueryResultsIterable

@Service
class AthenaServiceImpl: AthenaService {

    private val athenaClient = AthenaClientConfig().createClient()

    @Value("\${aws.athena.output-bucket-1}")
    val outputBucket: String? = null

    @Value("\${aws.athena.default-database-1}")
    val defaultDatabase: String? = null

    override fun get() {
        val queryExecutionId = submitAthenaQuery(athenaClient)
        waitForQueryToComplete(athenaClient, queryExecutionId)
        processResultRows(athenaClient, queryExecutionId)
        athenaClient.close()
    }

    fun submitAthenaQuery(athenaClient: AthenaClient): String? {
        try {
            val queryExecutionContext: QueryExecutionContext = QueryExecutionContext.builder()
                .database(defaultDatabase).build()
            val resultConfiguration: ResultConfiguration = ResultConfiguration.builder()
                .outputLocation(outputBucket)
                .build()
            val startQueryExecutionRequest: StartQueryExecutionRequest =
                StartQueryExecutionRequest.builder().queryString(
                    "select * from athena_test where company_id is not null limit 5;"
                )
                    .queryExecutionContext(queryExecutionContext)
                    .resultConfiguration(resultConfiguration)
                    .build()
            val startQueryExecutionResponse: StartQueryExecutionResponse =
                athenaClient.startQueryExecution(startQueryExecutionRequest)
            return startQueryExecutionResponse.queryExecutionId()
        } catch (e: AthenaException) {
            e.printStackTrace()
            System.exit(1)
        }
        return ""
    }

    @Throws(InterruptedException::class)
    fun waitForQueryToComplete(
        athenaClient: AthenaClient, queryExecutionId: String?
    ) {
        val getQueryExecutionRequest: GetQueryExecutionRequest = GetQueryExecutionRequest.builder()
            .queryExecutionId(queryExecutionId).build()
        var getQueryExecutionResponse: GetQueryExecutionResponse
        var isQueryStillRunning = true
        while (isQueryStillRunning) {
            getQueryExecutionResponse = athenaClient.getQueryExecution(getQueryExecutionRequest)
            val queryState = getQueryExecutionResponse.queryExecution().status().state().toString()
            if (queryState == QueryExecutionState.FAILED.toString()) {
                throw RuntimeException(
                    "Error message: " + getQueryExecutionResponse
                        .queryExecution().status().stateChangeReason()
                )
            } else if (queryState == QueryExecutionState.CANCELLED.toString()) {
                throw RuntimeException("The Amazon Athena query was cancelled.")
            } else if (queryState == QueryExecutionState.SUCCEEDED.toString()) {
                isQueryStillRunning = false
            } else {
                Thread.sleep(1000)
            }
            println("The current status is: $queryState")
        }
    }

    fun processResultRows(athenaClient: AthenaClient, queryExecutionId: String?) {
        try {
            val getQueryResultsRequest: GetQueryResultsRequest = GetQueryResultsRequest.builder()
                .queryExecutionId(queryExecutionId)
                .build()
            val getQueryResultsResults: GetQueryResultsIterable =
                athenaClient.getQueryResultsPaginator(getQueryResultsRequest)
            for (result in getQueryResultsResults) {
                val columnInfoList: List<ColumnInfo> = result.resultSet().resultSetMetadata().columnInfo()
                val results: List<Row> = result.resultSet().rows()
                processRow(results, columnInfoList)
            }
        } catch (e: AthenaException) {
            e.printStackTrace()
            System.exit(1)
        }
    }

    private fun processRow(row: List<Row>, columnInfoList: List<ColumnInfo>) {
        for (myRow in row) {
            val allData: List<Datum> = myRow.data()
            for (data in allData) {
                System.out.println("The value of the column is " + data.varCharValue())
            }
        }
    }

}
