package com.example.pickandcook

import android.util.Log
import com.example.pickandcook.api.RecipeItem
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

// 레시피 제목과 이미지 정보를 담는 데이터 클래스
data class RecipeData(
    val rcpTtl: String,
    val rcpImgUrl: String?
)

/**
 * MySQL 데이터베이스에 연결하여
 * - 카테고리 조건 목록 조회
 * - 필터링된 레시피 목록 조회
 * - 레시피 제목 및 이미지 조회
 */
object MySQLHelper {
    private const val URL = "jdbc:mysql://192.168.160.68:3306/pickandcookdb?serverTimezone=UTC"
    private const val USER = "root"
    private const val PASSWORD = "1234"

    init {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
            Log.d("MySQL", "✅ 드라이버 로드 성공")
        } catch (e: ClassNotFoundException) {
            Log.e("MySQL", "❌ 드라이버 로드 실패: ${e.message}")
        }
    }

    // 1. 카테고리 항목 리스트 조회
    fun getCategoryValues(column: String): List<String> {
        val result = mutableListOf<String>()
        val query = "SELECT DISTINCT $column FROM recipe WHERE $column IS NOT NULL AND $column != ''"

        try {
            DriverManager.getConnection(URL, USER, PASSWORD).use { conn ->
                conn.prepareStatement(query).use { stmt ->
                    stmt.executeQuery().use { rs ->
                        while (rs.next()) {
                            result.add(rs.getString(1))
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            Log.e("MySQL", "❌ 카테고리 조회 실패: ${e.message}")
        }
        return result
    }

    // 2. 조건에 따라 레시피 필터링
    fun getFilteredRecipes(
        kind: String?, situation: String?, method: String?, ingredients: List<String>
    ): List<RecipeItem> {
        val result = mutableListOf<RecipeItem>()

        val conn = try {
            DriverManager.getConnection(URL, USER, PASSWORD).also {
                Log.d("MySQL", "✅ DB 연결 성공")
            }
        } catch (e: SQLException) {
            Log.e("MySQL", "❌ DB 연결 실패: ${e.message}")
            return result
        }

        val query = StringBuilder("SELECT recipe_no, CKG_NM, CKG_MTRL_CN FROM recipe WHERE 1=1")
        val params = mutableListOf<String>()

        if (!kind.isNullOrEmpty()) {
            query.append(" AND CKG_KND_ACTO_NM = ?")
            params.add(kind)
        }
        if (!situation.isNullOrEmpty()) {
            query.append(" AND CKG_STA_ACTO_NM = ?")
            params.add(situation)
        }
        if (!method.isNullOrEmpty()) {
            query.append(" AND CKG_MTH_ACTO_NM = ?")
            params.add(method)
        }

        val pstmt = conn.prepareStatement(query.toString())
        for ((i, param) in params.withIndex()) {
            pstmt.setString(i + 1, param)
        }

        val rs = pstmt.executeQuery()
        while (rs.next()) {
            val recipeNo = rs.getInt("recipe_no")
            val name = rs.getString("CKG_NM")
            val dbIngredients = rs.getString("CKG_MTRL_CN")

            val isMatch = ingredients.all { dbIngredients.contains(it) }
            if (isMatch) {
                result.add(RecipeItem(recipeNo, name))
            }
        }

        rs.close()
        pstmt.close()
        conn.close()
        return result
    }

    // 3. 이미지 URL만 가져오기 (이전 방식 그대로 유지)
    fun getRecipeImageUrl(recipeName: String): String? {
        var imageUrl: String? = null
        val query = "SELECT RCP_IMG_URL FROM recipe WHERE CKG_NM = ?"

        try {
            DriverManager.getConnection(URL, USER, PASSWORD).use { conn ->
                conn.prepareStatement(query).use { stmt ->
                    stmt.setString(1, recipeName)
                    stmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            imageUrl = rs.getString("RCP_IMG_URL")
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            Log.e("MySQL", "❌ 이미지 URL 조회 실패: ${e.message}")
        }

        return imageUrl
    }

    // 4. 레시피 번호로 제목(RCP_TTL)과 이미지 URL을 가져오는 함수 (크롤링용)
    fun getRecipeByNo(recipeNo: Int): RecipeData? {
        var result: RecipeData? = null
        val query = "SELECT RCP_TTL, RCP_IMG_URL FROM recipe WHERE recipe_no = ?"

        try {
            DriverManager.getConnection(URL, USER, PASSWORD).use { conn ->
                conn.prepareStatement(query).use { stmt ->
                    stmt.setInt(1, recipeNo)
                    stmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            val title = rs.getString("RCP_TTL")
                            val imgUrl = rs.getString("RCP_IMG_URL")
                            result = RecipeData(title, imgUrl)
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            Log.e("MySQL", "❌ 레시피 제목 및 이미지 조회 실패: ${e.message}")
        }

        return result
    }
}
