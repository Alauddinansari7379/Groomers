package com.example.groomers.retrofit

import com.groomers.groomersvendor.model.modelregister.ModelRegister
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
//    @POST("login")
//    suspend fun login(
//        @Query("email") email: String,
//        @Query("password") password: String
//    ): retrofit2.Response<ModelLogin>
    @Multipart
    @POST("register")
    suspend fun registerUser(
        @Query("name") name: String,
        @Query("mobile") mobile: String,
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("password_confirmation") passwordConfirmation: String,
        @Query("role") role: String,
        @Query("businessName") businessName: String,
        @Query("businessCategory") businessCategory: String,
        @Query("aboutBusiness") aboutBusiness: String,
        @Query("teamSize") teamSize: String,
        @Query("address") address: String,
        @Query("city") city: String,
        @Query("zipcode") zipcode: String,
        @Query("idproofType") idproofType: String,
        @Query("services") services: String,
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("country") country: String,  // New field added
        @Query("state") state: String,      // New field added
        @Query("slot_interval") slot_interval: String,      // New field added
        @Query("username") username: String,      // New field added
        @Part shopAgreement: MultipartBody.Part  // For file upload
    ): Response<ModelRegister>
//
//    @POST("city")
//    suspend fun getCity(): Response<ModelCity>
//
//    @POST("state")
//    suspend fun getState(): Response<ModelState>
//    @GET("getCategory")
//    suspend fun getCategory(): Response<ModelCategory>
//
//    @POST("getPostAll")
//    suspend fun getServiceList(
//        @Header("Authorization") authorization: String
//    ): Response<ModelService>
//
//
//
//    @Multipart
//    @POST("CreateServicePost")
//    suspend fun createServicePost(
//        @Header("Authorization") authorization: String,
//        @Query("serviceName") serviceName: String,
//        @Query("description") description: String,
//        @Query("price") price: String,
//        @Query("time") time: String,
//        @Query("serviceType") serviceType: String,
//        @Query("date") date: String,
//        @Query("category") category: String,
//        @Query("slot_time") slot_time: String,
//        @Query("address") address: String,
//        @Query("user_type") user_type: String,
//        @Part image: MultipartBody.Part
//    ): Response<ModelCreateServiceX>

}