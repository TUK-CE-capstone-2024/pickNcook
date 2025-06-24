package com.example.pickandcook.api

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.*
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import okhttp3.ResponseBody
import retrofit2.Response

data class User(
    val id: String,
    val password: String,
    val userName: String
)
data class RegisterResponse(
    val message: String? = null,
    val error: String? = null
)
data class LoginResponse(
    val message: String? = null,
    val error: String? = null,
    val userName: String? = null,
    val userId: String? = null
)


// 바코드 응답
data class BarcodeResponse(
    val barcodeNum: String,
    val ingredientName: String,
    val price: Int
)

data class FridgeItem(
    val fridgeIngredientNo: Int,
    val userId: String,
    val fridgeIngredient: String,
    val imageResId: Int
)


// 장바구니 추가 요청 DTO
data class CartRequest(
    val userId: String,
    val barcode: String
)
data class CartResponse(
    val message: String?,  // 성공 메시지
    val error: String?     // 오류 메시지
)
data class ShoppingList(
    val shoppingListNo: Int,
    val userId: String,
    val listName: String,
    val regDate: String
)
data class ShoppingListDetail(
    val listDetailNo: Int,           // 상세 항목 번호 (PK)
    val listNo: Int,                 // 쇼핑리스트 번호 (FK)
    @SerializedName("ingredient")
    val ingredientName: String       // 재료명
)
data class SaveRecipeRequest(
    val recipeNo: Int,
    val userId: String
)
data class SaveRecipeResponse(
    val message: String?
)

@Parcelize
data class RecipeItem(
    val recipeNo: Int,
    val recipeName: String
) : Parcelable

data class SavedRecipeResponse(
    val recipeNo: Int,
    val ckgNm: String
)

data class FavoriteRequest(
    val userId: String,
    val preferIngredient: String
)
data class Notification(
    val notificationNo: Int,
    val userId: String,
    val notificationMsg: String,
    val regDate: String
)

data class RemovedFavoriteCheckRequest(
    val userId: String,
    val previousFridgeItems: List<String>
)

@Parcelize
data class Recipe(
    val recipeNo: Int,
    val rcpTtl: String,
    val ckgMtrlCn: String,
    val rcpImgUrl: String,
    val ckgNm: String,            // 음식 이름
    val ckgInbunNm: String,       // 인분
    val ckgDodfNm: String,        // 난이도
    val ckgTimeNm: String,        // 소요 시간
    val ckgKndActoNm: String,     // 종류
    val ckgStaActoNm: String,     // 상황
    val ckgMthActoNm: String      // 방법
) : Parcelable

data class PreferIngredient(
    val preferIngredientNo: Int,
    val userId: String,
    val preferIngredient: String
)


data class RecipeDetailResponse(
    val recipeNo: Int,
    val rcpImgUrl: String,
    val ckgNm: String,
    val ckgInbunNm: String,
    val ckgDodfNm: String,
    val ckgTimeNm: String,
    val ckgKndActoNm: String,
    val ckgStaActoNm: String,
    val ckgMthActoNm: String
)

data class RecipeFilterRequest(
    val kind: String?,
    val situation: String?,
    val method: String?,
    val ingredients: List<String>?
)


interface ApiService {
    @POST("/main/register")
    fun registerUser(@Body user: User): Call<RegisterResponse>

    @POST("/main/login")
    fun loginUser(@Body loginRequest: Map<String, String>): Call<LoginResponse>

    @GET("/api/barcode/{barcodeNum}")
    fun getProduct(@Path("barcodeNum") barcodeNum: String): Call<BarcodeResponse>

    @GET("/api/fridge/{userId}")
    fun getFridgeItems(@Path("userId") userId: String): Call<List<FridgeItem>>


    // 장바구니에 추가
    @POST("/api/cart/add")
    fun addToCart(@Body cartRequest: CartRequest): Call<CartResponse>

    @GET("/api/cart/{userId}")
    fun getCartItems(@Path("userId") userId: String): Call<List<BarcodeResponse>>

    @DELETE("/api/cart/delete")
    fun deleteCartItem(
        @Query("userId") userId: String,
        @Query("barcode") barcode: String
    ): Call<Map<String, String>>

    // 이거 안씀. 그냥 재료들 가격 더해서 씀
    @GET("/api/cart/{userId}/totalPrice")
    fun getTotalCartPrice(@Path("userId") userId: String): Call<Map<String, Int>>


    // 쇼핑리스트 목록 조회
    @GET("/api/shopping-list/{userId}")
    fun getShoppingLists(@Path("userId") userId: String): Call<List<ShoppingList>>

    // 쇼핑리스트 추가
    @POST("/api/shopping-list/add")
    fun addShoppingList(@Body request: Map<String, String>): Call<Map<String, String>>

    // 쇼핑리스트 제목 수정
    @PUT("/api/shopping-list/update")
    fun updateShoppingListTitle(@Body request: Map<String, String>): Call<Map<String, String>>


    @GET("/api/shopping-list/detail/{listNo}")
    fun getShoppingListDetails(@Path("listNo") listNo: Int): Call<List<ShoppingListDetail>>

    @POST("/api/shopping-list/detail/add")
    fun addIngredientToList(
        @Query("listNo") listNo: Int,
        @Query("ingredientName") ingredientName: String
    ): Call<Map<String, String>>


    @DELETE("/api/shopping-list/delete/{listId}")
    fun deleteShoppingList(@Path("listId") listId: Int): Call<Map<String, String>>


    @DELETE("/api/shopping-list/detail/delete/{detailNo}")
    fun deleteIngredient(@Path("detailNo") detailNo: Int): Call<Map<String, String>>

    @POST("/api/recipe-storage/add")
    fun saveRecipe(@Body request: SaveRecipeRequest): Call<SaveRecipeResponse>

    //@GET("/api/recipe-storage/{userId}")
    //fun getSavedRecipes(@Path("userId") userId: String): Call<List<String>>
    @GET("/api/recipe-storage/{userId}")
    fun getSavedRecipes(@Path("userId") userId: String): Call<List<SavedRecipeResponse>>


    @GET("api/recipe-storage/exists")
    fun isRecipeSaved(
        @Query("userId") userId: String,
        @Query("recipeNo") recipeNo: Int
    ): Call<Boolean>

    @DELETE("api/recipe-storage/{userId}/{recipeNo}")
    fun deleteRecipe(
        @Path("userId") userId: String,
        @Path("recipeNo") recipeNo: Int
    ): Call<Void>




    @POST("/api/prefer-ingredient/add")
    fun addFavorite(@Body request: FavoriteRequest): Call<Map<String, String>>




    @DELETE("/api/prefer-ingredient/delete")
    fun deleteFavorite(
        @Query("userId") userId: String,
        @Query("ingredient") ingredient: String
    ): Call<Map<String, String>>

    @GET("/api/prefer-ingredient/exists")
    suspend fun isFavorite(
        @Query("userId") userId: String,
        @Query("ingredient") ingredient: String
    ): Response<Boolean>

    @GET("/api/prefer-ingredient/all")
    fun getPreferIngredients(
        @Query("userId") userId: String
    ): Call<List<PreferIngredient>>



    @GET("/api/notifications/{userId}")
    fun getUserNotifications(@Path("userId") userId: String): Call<List<Notification>>

    @DELETE("/api/notifications/{notificationNo}")
    fun deleteNotification(@Path("notificationNo") notificationNo: Int): Call<Void>



    @POST("/api/notifications/check-removed-favorites")
    fun checkRemovedFavorites(@Body request: RemovedFavoriteCheckRequest): Call<Void>



    @GET("/api/recipes/detail/{recipeNo}")
    fun getRecipeEntity(@Path("recipeNo") recipeNo: Int): Call<Recipe>




    @GET("/api/recipes/info/{recipeNo}")
    fun getRecipeDetail(@Path("recipeNo") recipeNo: Int): Call<RecipeDetailResponse>




    @GET("/api/recipes")
    fun getAllRecipes(): Call<List<Recipe>>

    @POST("/api/recipes/filter")
    fun filterRecipes(@Body request: RecipeFilterRequest): Call<List<Recipe>>

    @GET("/api/recipes/categories/{column}")
    fun getCategoryValues(@Path("column") column: String): Call<List<String>>

    @GET("/api/recipes/image-url")
    fun getImageUrl(@Query("name") name: String): Call<String>

    @GET("/api/recipes/{recipeNo}")
    fun getRecipeByNo(@Path("recipeNo") recipeNo: Int): Call<Recipe>


    @GET("/api/fridge/photo")
    fun getFridgePhoto(
        @Query("userId") userId: String,
        @Query("ingredientName") ingredientName: String
    ): Call<ResponseBody>


    @GET("/main/user/{userId}")
    fun getUserInfo(@Path("userId") userId: String): Call<User>



}
