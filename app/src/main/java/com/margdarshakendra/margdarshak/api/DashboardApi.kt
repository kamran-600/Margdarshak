package com.margdarshakendra.margdarshak.api

import com.margdarshakendra.margdarshak.models.AttitudeQuestionsResponse
import com.margdarshakendra.margdarshak.models.AttitudeRatingQuesResponse
import com.margdarshakendra.margdarshak.models.CRMResponse
import com.margdarshakendra.margdarshak.models.CallRequest
import com.margdarshakendra.margdarshak.models.CallResponse
import com.margdarshakendra.margdarshak.models.ClientDataResponse
import com.margdarshakendra.margdarshak.models.CounsellingDataResponse
import com.margdarshakendra.margdarshak.models.DataRequest
import com.margdarshakendra.margdarshak.models.HiringDataResponse
import com.margdarshakendra.margdarshak.models.QuestionsResponse
import com.margdarshakendra.margdarshak.models.SaveAptitudeAnswerRequest
import com.margdarshakendra.margdarshak.models.SaveAptitudeAnswerResponse
import com.margdarshakendra.margdarshak.models.SaveAttitudeQuestionsRequest
import com.margdarshakendra.margdarshak.models.SaveAttitudeQuestionsResponse
import com.margdarshakendra.margdarshak.models.SaveAttitudeRatingQuesRequest
import com.margdarshakendra.margdarshak.models.SaveAttitudeRatingQuesResponse
import com.margdarshakendra.margdarshak.models.SendEmailRequest
import com.margdarshakendra.margdarshak.models.SmsRequest
import com.margdarshakendra.margdarshak.models.SmsResponse
import com.margdarshakendra.margdarshak.models.StartAssessmentRequest
import com.margdarshakendra.margdarshak.models.StartAssessmentResponse
import com.margdarshakendra.margdarshak.models.StartAttitudeAssessmentResponse
import com.margdarshakendra.margdarshak.models.TemplateResponse
import com.margdarshakendra.margdarshak.models.UserAccessResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DashboardApi {

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("user-access")
    suspend fun getUserAccess(): Response<UserAccessResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("user-dashboard")
    suspend fun getHiringData(@Body dataRequest: DataRequest): Response<HiringDataResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("click-to-call")
    suspend fun makeCall(@Body callRequest: CallRequest): Response<CallResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("crm-contact")
    suspend fun getCrmContact(): Response<CRMResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("crm-contact")
    suspend fun sendMessage(@Body smsRequest: SmsRequest): Response<SmsResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("crm-contact")
    suspend fun sendEmailMessage(@Body sendEmailRequest: SendEmailRequest): Response<SmsResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("get-template")
    suspend fun getTemplateContent(
        @Query("tempID") tempID: Int,
        @Query("uid") uid: Int
    ): Response<TemplateResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("user-dashboard")
    suspend fun getCounsellingData(@Body dataRequest: DataRequest): Response<CounsellingDataResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("user-dashboard")
    suspend fun getClientData(@Body dataRequest: DataRequest): Response<ClientDataResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("aptitute-assessment")
    suspend fun getStartAptitudeAssessment(@Body startAssessmentRequest: StartAssessmentRequest): Response<StartAssessmentResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("get-aptitude-questions/{result_id}/{page_no}")
    suspend fun getAptitudeQuestions(
        @Path("result_id") result_id: Int,
        @Path("page_no") page_no: Int
    ): Response<QuestionsResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("save-aptitude-response")
    suspend fun saveAptitudeAnswer(@Body saveAptitudeAnswerRequest: SaveAptitudeAnswerRequest): Response<SaveAptitudeAnswerResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("attitute-assessment")
    suspend fun getStartAttitudeAssessment(@Body startAssessmentRequest: StartAssessmentRequest): Response<StartAttitudeAssessmentResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("get-attitude-questions/{page_no}")
    suspend fun getAttitudeQuestions(@Path("page_no") page_no: Int): Response<AttitudeQuestionsResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("save-attitude-questions")
    suspend fun saveAttitudeQuestions(@Body saveAttitudeQuestionsRequest: SaveAttitudeQuestionsRequest): Response<SaveAttitudeQuestionsResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("attitude-rating/{result_id}")
    suspend fun getAttitudeRatingQuestions(@Path("result_id") result_id: Int): Response<AttitudeRatingQuesResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("attitude-rating/{result_id}")
    suspend fun saveAttitudeRatingQuesAndFinishTest(@Path("result_id") result_id: Int, @Body saveAttitudeRatingQuesRequest: SaveAttitudeRatingQuesRequest): Response<SaveAttitudeRatingQuesResponse>




}