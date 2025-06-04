package com.example.pickandcook.api

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.*
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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


}
