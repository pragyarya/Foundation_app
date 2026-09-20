package com.example.data

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class MemberRegisterResponse(
    val message: String? = null,
    val member: RegisteredMember? = null,
    val error: String? = null
)

data class RegisteredMember(
    val id: Long? = null,
    val member_no: String? = null,
    val name: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val designation: String? = null,
    val department: String? = null,
    val joining_date: String? = null,
    val status: String? = null
)

data class MemberLoginResponse(
    val message: String? = null,
    val member: LoginMember? = null,
    val error: String? = null
)

data class LoginMember(
    val id: Long? = null,
    val member_no: String? = null,
    val name: String? = null,
    val designation: String? = null,
    val department: String? = null
)

data class MemberMeResponse(
    val member: ServerMember? = null,
    val error: String? = null
)

data class ServerMember(
    val id: Long? = null,
    val member_no: String? = null,
    val name: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val photo_path: String? = null,
    val designation: String? = null,
    val department: String? = null,
    val joining_date: String? = null,
    val status: String? = null,
    val is_active: Boolean? = null,
    val created_at: String? = null
)

interface MemberApi {

    @Multipart
    @POST("api/member/register")
    suspend fun registerMember(
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("designation") designation: RequestBody,
        @Part("department") department: RequestBody,
        @Part("joining_date") joiningDate: RequestBody?,
        @Part photo: MultipartBody.Part?
    ): Response<MemberRegisterResponse>

    @POST("api/member/login")
    suspend fun login(
        @retrofit2.http.Body request: LoginRequest
    ): Response<MemberLoginResponse>

    @POST("api/member/logout")
    suspend fun logout(): Response<Unit>

    @GET("api/member/me")
    suspend fun getCurrentMember(): Response<MemberMeResponse>
}

data class LoginRequest(
    val email: String,
    val password: String
)
