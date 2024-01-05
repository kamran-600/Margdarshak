package com.margdarshakendra.margdarshak.api

import com.margdarshakendra.margdarshak.models.AttitudeQuestionsResponse
import com.margdarshakendra.margdarshak.models.AttitudeRatingQuesResponse
import com.margdarshakendra.margdarshak.models.CRMResponse
import com.margdarshakendra.margdarshak.models.CalenderSchedulesResponse
import com.margdarshakendra.margdarshak.models.CallRequest
import com.margdarshakendra.margdarshak.models.CallResponse
import com.margdarshakendra.margdarshak.models.ClientDataResponse
import com.margdarshakendra.margdarshak.models.CounsellingDataResponse
import com.margdarshakendra.margdarshak.models.DataRequest
import com.margdarshakendra.margdarshak.models.EditOrganisedStudyScheduleResponse
import com.margdarshakendra.margdarshak.models.GetInteractiveQuestionRequest
import com.margdarshakendra.margdarshak.models.GetOraniserLangaugesReponse
import com.margdarshakendra.margdarshak.models.GetOrganisedStudySchedulesResponse
import com.margdarshakendra.margdarshak.models.GetOrganiserCoursesResponse
import com.margdarshakendra.margdarshak.models.GetOrganiserLessonsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserLessonsResponse
import com.margdarshakendra.margdarshak.models.GetOrganiserStudyTimeRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserStudyTimeResponse
import com.margdarshakendra.margdarshak.models.GetOrganiserSubjectsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserSubjectsResponse
import com.margdarshakendra.margdarshak.models.GetOrganiserUtilRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserWeightagesResponse
import com.margdarshakendra.margdarshak.models.HiringDataResponse
import com.margdarshakendra.margdarshak.models.InteractiveCoursesResponse
import com.margdarshakendra.margdarshak.models.InteractiveSubjectsResponse
import com.margdarshakendra.margdarshak.models.InteractiveTeachersRequest
import com.margdarshakendra.margdarshak.models.InteractiveTeachersResponse
import com.margdarshakendra.margdarshak.models.InteractiveTestDetailsResponse
import com.margdarshakendra.margdarshak.models.LogoutRequest
import com.margdarshakendra.margdarshak.models.LogoutResponse
import com.margdarshakendra.margdarshak.models.McqResultsRequest
import com.margdarshakendra.margdarshak.models.McqResultsResponse
import com.margdarshakendra.margdarshak.models.OrganisedStudyScheduleRequest
import com.margdarshakendra.margdarshak.models.OrganisedStudyScheduleResponse
import com.margdarshakendra.margdarshak.models.PerformanceLessonWiseResponse
import com.margdarshakendra.margdarshak.models.PerformanceSubjectWiseResponse
import com.margdarshakendra.margdarshak.models.QuestionsResponse
import com.margdarshakendra.margdarshak.models.SaveAptitudeAnswerRequest
import com.margdarshakendra.margdarshak.models.SaveAptitudeAnswerResponse
import com.margdarshakendra.margdarshak.models.SaveAttitudeQuestionsRequest
import com.margdarshakendra.margdarshak.models.SaveAttitudeQuestionsResponse
import com.margdarshakendra.margdarshak.models.SaveAttitudeRatingQuesRequest
import com.margdarshakendra.margdarshak.models.SaveAttitudeRatingQuesResponse
import com.margdarshakendra.margdarshak.models.SaveFcmTokenRequest
import com.margdarshakendra.margdarshak.models.SaveFcmTokenResponse
import com.margdarshakendra.margdarshak.models.SaveInteractiveAnsResponse
import com.margdarshakendra.margdarshak.models.SaveInteractiveAnswerRequest
import com.margdarshakendra.margdarshak.models.SaveSkillTestAnswerRequest
import com.margdarshakendra.margdarshak.models.SaveSkillTestAnswerResponse
import com.margdarshakendra.margdarshak.models.SendEmailRequest
import com.margdarshakendra.margdarshak.models.SkillResponse
import com.margdarshakendra.margdarshak.models.SkillTestQuestionResponse
import com.margdarshakendra.margdarshak.models.SmsRequest
import com.margdarshakendra.margdarshak.models.SmsResponse
import com.margdarshakendra.margdarshak.models.StartAssessmentRequest
import com.margdarshakendra.margdarshak.models.StartAssessmentResponse
import com.margdarshakendra.margdarshak.models.StartAttitudeAssessmentResponse
import com.margdarshakendra.margdarshak.models.StartInteractiveTestRequest
import com.margdarshakendra.margdarshak.models.StartInteractiveTestResponse
import com.margdarshakendra.margdarshak.models.StartSkillTestRequest
import com.margdarshakendra.margdarshak.models.StartSkillTestResponse
import com.margdarshakendra.margdarshak.models.TemplateResponse
import com.margdarshakendra.margdarshak.models.UpdateTimerRequest
import com.margdarshakendra.margdarshak.models.UpdateTimerResponse
import com.margdarshakendra.margdarshak.models.UserAccessResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
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
    @POST("logout")
    suspend fun logout(@Body logoutRequest: LogoutRequest): Response<LogoutResponse>

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
    suspend fun saveAttitudeRatingQuesAndFinishTest(
        @Path("result_id") result_id: Int,
        @Body saveAttitudeRatingQuesRequest: SaveAttitudeRatingQuesRequest
    ): Response<SaveAttitudeRatingQuesResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("skill-test-crm")
    suspend fun getSkills(): Response<SkillResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("skill-test-crm")
    suspend fun getStartSkillTest(@Body startSkillTestRequest: StartSkillTestRequest): Response<StartSkillTestResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("get-skills-questions/{result_id}/{page_no}")
    suspend fun getSkillTestQuestions(
        @Path("result_id") result_id: Int,
        @Path("page_no") page_no: Int
    ): Response<SkillTestQuestionResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("update-timer")
    suspend fun updateTimer(@Body updateTimerRequest: UpdateTimerRequest): Response<UpdateTimerResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("save-skills-questions")
    suspend fun saveSkillTestAnswer(@Body saveSkillTestAnswerRequest: SaveSkillTestAnswerRequest): Response<SaveSkillTestAnswerResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("get-organiser-utils")
    suspend fun getOrganiserCoursesRequest(@Body getOrganiserUtilRequest: GetOrganiserUtilRequest): Response<GetOrganiserCoursesResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("get-organiser-utils")
    suspend fun getOrganiserLanguagesRequest(@Body getOrganiserUtilRequest: GetOrganiserUtilRequest): Response<GetOraniserLangaugesReponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("get-organiser-utils")
    suspend fun getOrganiserWeightagesRequest(@Body getOrganiserUtilRequest: GetOrganiserUtilRequest): Response<GetOrganiserWeightagesResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("get-organiser-utils")
    suspend fun getOrganiserSubjectsRequest(@Body getOrganiserSubjectsRequest: GetOrganiserSubjectsRequest): Response<GetOrganiserSubjectsResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("get-organiser-utils")
    suspend fun getOrganiserLessonsRequest(@Body getOrganiserLessonsRequest: GetOrganiserLessonsRequest): Response<GetOrganiserLessonsResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("get-organiser-utils")
    suspend fun getOrganiserStudyTime(@Body getOrganiserStudyTimeRequest: GetOrganiserStudyTimeRequest): Response<GetOrganiserStudyTimeResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("study-organiser")
    suspend fun getOrganisedStudySchedules(): Response<GetOrganisedStudySchedulesResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @PUT("study-organiser")
    suspend fun scheduleOrganisedStudy(@Body organisedStudyScheduleRequest: OrganisedStudyScheduleRequest): Response<OrganisedStudyScheduleResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("edit-study-organiser/{schedule_id}")
    suspend fun editOrganisedStudySchedules(@Path("schedule_id") scheduleID: Int): Response<EditOrganisedStudyScheduleResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("get-organiser-utils")
    suspend fun getInteractiveCoursesRequest(@Body getOrganiserUtilRequest: GetOrganiserUtilRequest): Response<InteractiveCoursesResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("get-organiser-utils")
    suspend fun getInteractiveSubjectsRequest(@Body getOrganiserSubjectsRequest: GetOrganiserSubjectsRequest): Response<InteractiveSubjectsResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("training-content")
    suspend fun getInteractiveTeachersRequest(@Body interactiveTeachersRequest: InteractiveTeachersRequest): Response<InteractiveTeachersResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("start-mcq-test/{lessonId}")
    suspend fun getInteractiveTestDetailsRequest(@Path("lessonId") lessonId: Int): Response<InteractiveTestDetailsResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("start-mcq-test/{lessonId}")
    suspend fun startInteractiveTestRequest(@Path("lessonId") lessonId: Int, @Body startInteractiveTestRequest: StartInteractiveTestRequest): Response<StartInteractiveTestResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("mcq_question/{resultId}/{quesNo}")
    suspend fun getInteractiveQuestions(@Path("resultId") resultId: Int, @Path("quesNo") quesNo: Int): Response<GetInteractiveQuestionRequest>



    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @PUT("update-mcq-question")
    suspend fun saveInteractiveAnswerRequest(@Body saveInteractiveQuestionRequest: SaveInteractiveAnswerRequest): Response<SaveInteractiveAnsResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("progress-tools")
    suspend fun getResultLessons(@Body getOrganiserLessonsRequest: GetOrganiserLessonsRequest): Response<GetOrganiserLessonsResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("progress-tools")
    suspend fun getMcqResults(@Body mcqResultsRequest: McqResultsRequest): Response<McqResultsResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("progress-tools")
    suspend fun getSubjectWisePerformance(@Body getOrganiserUtilRequest: GetOrganiserUtilRequest): Response<PerformanceSubjectWiseResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("progress-tools")
    suspend fun getLessonWisePerformance(@Body getOrganiserLessonsRequest: GetOrganiserLessonsRequest): Response<PerformanceLessonWiseResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("progress-tools")
    suspend fun getCalenderSchedules(@Body getOrganiserUtilRequest: GetOrganiserUtilRequest): Response<CalenderSchedulesResponse>



    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("set-firebase-token")
    suspend fun saveFcmTokenRequest(@Body saveFcmTokenRequest: SaveFcmTokenRequest): Response<SaveFcmTokenResponse>



}