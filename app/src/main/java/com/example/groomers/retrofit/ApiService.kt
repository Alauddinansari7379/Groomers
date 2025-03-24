package com.example.groomers.retrofit


import com.example.groomers.model.modeladdresslist.ModelAddressList
import com.example.groomers.model.modelbooking.ModelBooking
import com.example.groomers.model.modelbookinglist.ModelBookingList
import com.example.groomers.model.modelcategory.ModelCategory
import com.example.groomers.model.modelcreateaddress.ModelCreateAddress
import com.example.groomers.model.modellogin.ModelLogin
import com.example.groomers.model.modelregister.ModelRegesters
import com.example.groomers.model.modelservice.ModelService
import com.example.groomers.model.modulcountry.ModelCountry
import com.groomers.groomersvendor.model.modelcity.ModelCity
import com.groomers.groomersvendor.model.modelstate.ModelState
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("login")
    suspend fun login(
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("username") username: String,
        @Query("role") role: String,
    ): Response<ModelLogin>
    @Multipart
    @POST("register")
   suspend fun registerUser(
        @Query("username") username: String,
        @Query("name") name: String,
        @Query("mobile") mobile: String,
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("password_confirmation") passwordConfirmation: String,
        @Query("role") role: String,
        @Query("language") language: String,
        @Query("user_type") user_type: String,
        @Query("address") address: String,
        @Query("country") country: String,
        @Query("state") state: String,
        @Query("city") city: String,
        @Query("zipcode") zipcode: String,
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("gender") gender: String,
        @Query("birthdate") birthdate: String,
        @Part UserImage: MultipartBody.Part  // For file upload
    ): Response<ModelRegesters>

    @GET("getCustomerBookings")
    suspend fun getBookingList(
        @Header("Authorization") authorization: String
    ): Response<ModelBookingList>


    @POST("createBooking")
    suspend fun createBooking(
        @Header("Authorization") authorization: String,
        @Query("customer_id") customerId: Int,
        @Query("vendor_id") vendorId: Int,
        @Query("total") total: Int,
        @Query("payment_mode") paymentMode: Int,
        @Query("slot_id") slotId: Int,
        @Query("service_id") serviceId: Int,
        @Query("date") date: String,
        @Query("time") time: String,
        @Query("notes") notes: String? = null,
        @Query("quantity") quantity: String
    ): Response<ModelBooking>

    @POST("suggestServices")
    suspend fun getServiceList(
        @Header("Authorization") authorization: String,
        @Query("user_type") user_type: String,
    ): Response<ModelService>

    @GET("getCategory")
    suspend fun getCategory(): Response<ModelCategory>
    @POST("city")
    suspend fun getCity(): Response<ModelCity>

    @POST("state")
    suspend fun getState(): Response<ModelState>

    @POST("state")
    suspend fun getCountry(): Response<ModelCountry>
    @POST("customerCreateAddress")
    suspend fun customerCreateAddress(
        @Header("Authorization") authorization: String,
        @Query("address") address: String,
        @Query("zip_code") zipCode: String,
        @Query("country") country: Int,
        @Query("state") state: Int,
        @Query("city") city: Int
    ): Response<ModelCreateAddress>

    @GET("getCustomerAddress")
    suspend fun getCustomerAddress(@Header("Authorization") token: String): Response<ModelAddressList>
}