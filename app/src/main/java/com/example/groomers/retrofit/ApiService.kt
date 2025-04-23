package com.example.groomers.retrofit


import com.example.groomers.model.modeladdresslist.ModelAddressList
import com.example.groomers.model.modelallvendors.ModelAllVendors
import com.example.groomers.model.modelbooking.ModelBooking
import com.example.groomers.model.modelbookinglist.ModelBookingList
import com.example.groomers.model.modelcategory.ModelCategory
import com.example.groomers.model.modelcreateaddress.ModelCreateAddress
import com.example.groomers.model.modeldeleteaddress.ModelDeleteAddress
import com.example.groomers.model.modeleditprofile.ModelEditProfile
import com.example.groomers.model.modelgetallvendorbyid.ModelAllPostByVendorId
import com.example.groomers.model.modellogin.ModelLogin
import com.example.groomers.model.modelprofilepicupload.ModelUploadProfPhoto
import com.example.groomers.model.modelregister.ModelRegesters
import com.example.groomers.model.modelservice.ModelService
import com.example.groomers.model.modelslotbooking.ModelSlotBooking
import com.example.groomers.model.modelupdateaddress.ModelUpdateAddress
import com.example.groomers.model.modeluserdetails.ModelUserDetails
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

    @POST("getUser")
    suspend fun getUserDetails(
        @Header("Authorization") authorization: String,
    ): Response<ModelUserDetails>

    @POST("getSlotBooking")
    suspend fun getSlotBooking(
        @Header("Authorization") authorization: String,
        @Query("vendor_id") vendorId: String,
        @Query("categoryid") categoryId: String,
        @Query("day") day: String,
        @Query("service_id") serviceId: String
    ): Response<ModelSlotBooking>

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

    @POST("updateCustomerProfile")
    suspend fun updateUserDetails(
        @Header("Authorization") authorization: String,
        @Query("name") name: String,
        @Query("mobile") mobile: String,
        @Query("email") email: String,
        @Query("address") address: String,
        @Query("gender") gender: String,
        @Query("birthdate") birthdate: String
    ): Response<ModelEditProfile>

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

    @POST("country")
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

    @Multipart
    @POST("updateCustomerPictures")
    suspend fun uploadProfilePicture(
        @Header("Authorization") authorization: String,
        @Part image: MultipartBody.Part
    ): Response<ModelUploadProfPhoto>

    @POST("customerDeleteAddress")
    suspend fun deleteAddress(
        @Header("Authorization") authorization: String,
        @Query("id") id: Int
    ): Response<ModelDeleteAddress>

    @POST("getAllPostByVendorId")
    suspend fun getAllPostByVendorId(
        @Header("Authorization") authorization: String,
        @Query("vendor_id") vendorId: Int
    ): Response<ModelService>

    @GET("getAllVendors")
    suspend fun getAllVendors(
        @Header("Authorization") authorization: String,
    ): Response<ModelAllVendors>

    @POST("customerUpdateAddress")
    suspend fun updateAddress(
        @Header("Authorization") authorization: String,
        @Query("address") address: String,
        @Query("zip_code") zipCode: String,
        @Query("country") country: String,
        @Query("state") state: String,
        @Query("city") city: String,
        @Query("id") id: Int
    ): Response<ModelUpdateAddress>

}