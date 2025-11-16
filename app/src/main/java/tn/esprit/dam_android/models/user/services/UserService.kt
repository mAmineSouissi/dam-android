package tn.esprit.dam_android.models.user.services

import retrofit2.Response
import retrofit2.http.*
import tn.esprit.dam_android.models.user.UpdateProfileRequest
import tn.esprit.dam_android.models.user.User

interface UserService {

    @GET("/api/users/me")
    suspend fun getCurrentUser(
        @Header("Authorization") authorization: String
    ): Response<User>

    @PATCH("/api/users/profile/{id}")
    suspend fun updateProfile(
        @Path("id") userId: String,
        @Header("Authorization") authorization: String,
        @Body request: UpdateProfileRequest
    ): Response<User>
}

