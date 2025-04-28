package com.example.pickandcook


import android.util.Log
import com.example.pickandcook.api.RecipeItem
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

/**
 * MySQL 데이터베이스에 연결하여
 * - 카테고리 조건 목록 조회
 * - 사용자 선택 조건에 맞는 레시피 이름 목록 조회
 * 를 수행하는 헬퍼 객체 (새로 추가)
 */
object MySQLHelper {
    // MySQL 연결 정보 (10.0.2.2: 에뮬레이터에서 로컬호스트 접속)
    private const val URL = "jdbc:mysql://192.168.72.108:3306/pickandcookdb?serverTimezone=UTC"
    private const val USER = "root"
    private const val PASSWORD = "1234"

    /**
     * 객체 초기화 시점에 JDBC 드라이버 로드 시도
     */
    init {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")   // MySQL 드라이버 로드
            Log.d("MySQL", "✅ 드라이버 로드 성공")
        } catch (e: ClassNotFoundException) {
            Log.e("MySQL", "❌ 드라이버 로드 실패: ${e.message}")
        }
    }

    /**
     * 특정 컬럼(종류/상황/방법 등)에서 중복 없는 항목 리스트 조회
     * @param column 조회 대상 컬럼명 (예: CKG_KND_ACTO_NM)
     * @return 해당 컬럼에서 중복 제거된 값 리스트
     */
    fun getCategoryValues(column: String): List<String> {
        val result = mutableListOf<String>()
        val query = "SELECT DISTINCT $column FROM recipe WHERE $column IS NOT NULL AND $column != ''"

        try {
            // DB 연결 및 쿼리 실행
            DriverManager.getConnection(URL, USER, PASSWORD).use { conn ->
                conn.prepareStatement(query).use { stmt ->
                    stmt.executeQuery().use { rs ->
                        while (rs.next()) {
                            result.add(rs.getString(1)) // 첫 번째 컬럼값 추가
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            Log.e("MySQL", "❌ 카테고리 조회 실패: ${e.message}")
        }
        return result
    }

    /**
     * 카테고리(종류/상황/방법)와 식재료 리스트를 기반으로 필터링된 레시피명 리스트 반환
     *
     * @param kind 종류 조건 (nullable)
     * @param situation 상황 조건 (nullable)
     * @param method 조리 방법 조건 (nullable)
     * @param ingredients 사용자가 선택한 식재료들
     * @return 조건을 모두 만족하는 레시피 이름 리스트
     */
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
            val recipeNo = rs.getInt("recipe_no")               // db변경으로 recipe_no가져오기
            val name = rs.getString("CKG_NM")
            val dbIngredients = rs.getString("CKG_MTRL_CN")

            val isMatch = ingredients.all { dbIngredients.contains(it) }
            if (isMatch) {
                result.add(RecipeItem(recipeNo, name))           // RecipeItem으로 저장
            }
        }

        rs.close()
        pstmt.close()
        conn.close()
        return result
    }


    // 레시피 이미지 가져오기
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
}
