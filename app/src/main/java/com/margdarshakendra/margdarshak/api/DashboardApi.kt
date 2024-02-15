package com.margdarshakendra.margdarshak.api

import com.margdarshakendra.margdarshak.models.AttitudeQuestionsResponse
import com.margdarshakendra.margdarshak.models.AttitudeRatingQuesResponse
import com.margdarshakendra.margdarshak.models.CRMResponse
import com.margdarshakendra.margdarshak.models.CalenderSchedulesResponse
import com.margdarshakendra.margdarshak.models.CallRequest
import com.margdarshakendra.margdarshak.models.CallResponse
import com.margdarshakendra.margdarshak.models.ClientDataResponse
import com.margdarshakendra.margdarshak.models.CommunicationTestsResponse
import com.margdarshakendra.margdarshak.models.ComparisonResponse
import com.margdarshakendra.margdarshak.models.EditOrganisedStudyScheduleResponse
import com.margdarshakendra.margdarshak.models.EmailSearchResponse
import com.margdarshakendra.margdarshak.models.GetCommunicatiionTestIDResponse
import com.margdarshakendra.margdarshak.models.GetComparisonDataRequest
import com.margdarshakendra.margdarshak.models.GetFilterPostsResponse
import com.margdarshakendra.margdarshak.models.GetForCounsellingRequest
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
import com.margdarshakendra.margdarshak.models.GiveCommunicationTestLinkRequest
import com.margdarshakendra.margdarshak.models.GiveDocsUploadLinkRequest
import com.margdarshakendra.margdarshak.models.GiveSkillTestLinkRequest
import com.margdarshakendra.margdarshak.models.HiringDataResponse
import com.margdarshakendra.margdarshak.models.HiringFilteredDataResponse
import com.margdarshakendra.margdarshak.models.HiringSkillsResponse
import com.margdarshakendra.margdarshak.models.HrInterviewQuesResponse
import com.margdarshakendra.margdarshak.models.HrInterviewQuesUtilRequest
import com.margdarshakendra.margdarshak.models.HrInterviewQuesUtilResponse
import com.margdarshakendra.margdarshak.models.HrIntervieweeQuestionResponse
import com.margdarshakendra.margdarshak.models.InductionRequest
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
import com.margdarshakendra.margdarshak.models.ProgressMarksAndTimeResponse
import com.margdarshakendra.margdarshak.models.ProgressMeterDataResponse
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
import com.margdarshakendra.margdarshak.models.ScheduleNotificationRequest
import com.margdarshakendra.margdarshak.models.SendEmailRequest
import com.margdarshakendra.margdarshak.models.ShortListUserRequest
import com.margdarshakendra.margdarshak.models.SkillResponse
import com.margdarshakendra.margdarshak.models.SkillTestQuestionResponse
import com.margdarshakendra.margdarshak.models.SmsRequest
import com.margdarshakendra.margdarshak.models.SmsResponse
import com.margdarshakendra.margdarshak.models.StartAssessmentRequest
import com.margdarshakendra.margdarshak.models.StartAssessmentResponse
import com.margdarshakendra.margdarshak.models.StartAttitudeAssessmentResponse
import com.margdarshakendra.margdarshak.models.StartHrInterviewRequest
import com.margdarshakendra.margdarshak.models.StartHrInterviewResponse
import com.margdarshakendra.margdarshak.models.StartInteractiveTestRequest
import com.margdarshakendra.margdarshak.models.StartInteractiveTestResponse
import com.margdarshakendra.margdarshak.models.StartSkillTestRequest
import com.margdarshakendra.margdarshak.models.StartSkillTestResponse
import com.margdarshakendra.margdarshak.models.TemplateResponse
import com.margdarshakendra.margdarshak.models.UpdateCommunicationTimerRequest
import com.margdarshakendra.margdarshak.models.UpdateCommunicationTimerResponse
import com.margdarshakendra.margdarshak.models.UpdateTimerRequest
import com.margdarshakendra.margdarshak.models.UpdateTimerResponse
import com.margdarshakendra.margdarshak.models.UserAccessResponse
import com.margdarshakendra.margdarshak.models.WebEmailsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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
    @GET("user-dashboard/{mode}")
    suspend fun getHiringData(@Path("mode") mode: String, @Query("page") pageNo: Int): Response<HiringDataResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("user-dashboard/filterHiring")
    suspend fun getHiringFilteredData(
        @Query("employer") employerId: Int, @Query("post") postId: Int, @Query("page") pageNo: Int
    ): Response<HiringDataResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("get-posts/{employerId}")
    suspend fun getFilterPostsData(@Path("employerId") employerId: Int): Response<GetFilterPostsResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("get-link-utils/skills")
    suspend fun getHiringSkills(): Response<HiringSkillsResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("skill-test-link")
    suspend fun giveHiringSkillTestLink(@Body giveSkillTestLinkRequest: GiveSkillTestLinkRequest): Response<SmsResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("give-interview-link")
    suspend fun giveHiringInterviewLink(@Body giveSkillTestLinkRequest: GiveSkillTestLinkRequest): Response<SmsResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("induction")
    suspend fun inductionRequest(@Body inductionRequest: InductionRequest): Response<SmsResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("give-hire-test-link")
    suspend fun getCommunicationTests(): Response<CommunicationTestsResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("give-hire-test-link")
    suspend fun giveCommunicationTestLink(@Body giveCommunicationTestLinkRequest: GiveCommunicationTestLinkRequest): Response<SmsResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("docs-upload-link")
    suspend fun giveDocsUploadLink(@Body giveDocsUploadLinkRequest: GiveDocsUploadLinkRequest): Response<SmsResponse>


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
        @Query("tempID") tempID: Int, @Query("uid") uid: Int
    ): Response<TemplateResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("user-dashboard/{mode}")
    suspend fun getCounsellingData(@Path("mode") mode: String, @Query("page") position: Int): Response<HiringDataResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("shortlist-user")
    suspend fun shortListUser(@Body shortListUserRequest: ShortListUserRequest): Response<SmsResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("user-dashboard/{mode}")
    suspend fun getClientData(@Path("mode") mode: String): Response<ClientDataResponse>



    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("get-lead")
    suspend fun getForCounselling(@Body getForCounsellingRequest: GetForCounsellingRequest ): Response<SmsResponse>


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
        @Path("result_id") result_id: Int, @Path("page_no") page_no: Int
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
        @Path("result_id") result_id: Int, @Path("page_no") page_no: Int
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
    suspend fun startInteractiveTestRequest(
        @Path("lessonId") lessonId: Int,
        @Body startInteractiveTestRequest: StartInteractiveTestRequest
    ): Response<StartInteractiveTestResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("mcq_question/{resultId}/{quesNo}")
    suspend fun getInteractiveQuestions(
        @Path("resultId") resultId: Int, @Path("quesNo") quesNo: Int
    ): Response<GetInteractiveQuestionRequest>


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


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("progress-tools")
    suspend fun getProgressMeterData(@Body getOrganiserSubject: GetOrganiserSubjectsRequest): Response<ProgressMeterDataResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("progress-tools")
    suspend fun getProgressMarksAndTime(@Body getOrganiserLessonsRequest: GetOrganiserLessonsRequest): Response<ProgressMarksAndTimeResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("progress-tools")
    suspend fun getComparisonDataRequest(@Body getComparisonDataRequest: GetComparisonDataRequest): Response<ComparisonResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("email-search/{email}")
    suspend fun searchEmail(@Path("email") email: String): Response<EmailSearchResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("start-interview")
    suspend fun startHrInterviewRequest(@Body startHrInterviewRequest: StartHrInterviewRequest): Response<StartHrInterviewResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("hr-interview/{resultId}")
    suspend fun getHrInterviewQuestions(@Path("resultId") resultId: Int): Response<HrInterviewQuesResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("set-interview-data")
    suspend fun setHrInterviewQuesData(@Body hrInterviewQuesUtilRequest: HrInterviewQuesUtilRequest): Response<HrInterviewQuesUtilResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("interview-data")
    suspend fun getHrIntervieweeQues(): Response<HrIntervieweeQuestionResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("user-emails/{category}")
    suspend fun getWebEmails(@Path("category") category : String): Response<WebEmailsResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("gmail-export")
    suspend fun getImportedWebEmails(): Response<SmsResponse>


    @Headers(
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @GET("communication-test")
    suspend fun getHireTestId(): Response<GetCommunicatiionTestIDResponse>


    @Headers(
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("update-hire-test-time")
    suspend fun updateCommunicationTestTimer(@Body updateCommunicationTimerRequest: UpdateCommunicationTimerRequest): Response<UpdateCommunicationTimerResponse>



    @Multipart
    @Headers(
//        "Content-Type:multipart/form-data",  dont add this line in file upload
        "Accept: application/json",
        "Access-Key: 5516b2db6d31669314fdf03b66556dabcc52593651917d771c"
    )
    @POST("communication-test")
    suspend fun submitCommunicationTest(@Part("hiretestID") hiretestID: RequestBody,
                                        @Part("answer") answer: RequestBody,
                                        @Part fileupload: MultipartBody.Part ): Response<SmsResponse>



    @Multipart
    @Headers(
//        "Content-Type:multipart/form-data",  dont add this line in file upload
        "Accept: application/json",
        "Access-Key: 5516b2db6d31669314fdf03b66556dabcc52593651917d771c"
    )
    @POST("docs-upload")
    suspend fun submitDocsUpload(@Part photoId: MultipartBody.Part,
                                 @Part panCard: MultipartBody.Part?= null,
                                 @Part hac: MultipartBody.Part?= null,
                                 @Part pc: MultipartBody.Part?= null,
                                 @Part lac: MultipartBody.Part?=null): Response<SmsResponse>

    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @POST("user/notifications")
    suspend fun scheduleNotification(@Body scheduleNotificationRequest: ScheduleNotificationRequest): Response<SmsResponse>


    @Headers(
        "Content-Type:application/json",
        "access-key:5516b2db6d31669314fdf03b66556dabcc52593651917d771c",
        "Accept:application/json"
    )
    @DELETE("delete-user/notifications/{notifyId}")
    suspend fun deleteNotification(@Path("notifyId") notifyId : Int): Response<SmsResponse>



}