package com.example.demo.repository;

import com.example.demo.model.Recipe;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
	
	List<Recipe> findByCkgKndActoNm(String kind);
    List<Recipe> findByCkgStaActoNm(String situation);
    List<Recipe> findByCkgMthActoNm(String method);

    // 이름으로 이미지 URL 조회
    Recipe findByCkgNm(String name);

    // 조합 필터를 위한 조건 조회용
    List<Recipe> findByCkgKndActoNmAndCkgStaActoNmAndCkgMthActoNm(String kind, String situation, String method);
    
    
    @Query("SELECT r FROM Recipe r WHERE " +
            "(:kind IS NULL OR r.ckgKndActoNm = :kind) AND " +
            "(:situation IS NULL OR r.ckgStaActoNm = :situation) AND " +
            "(:method IS NULL OR r.ckgMthActoNm = :method)")
    List<Recipe> filterRecipes(
            @Param("kind") String kind,
            @Param("situation") String situation,
            @Param("method") String method
    );
}
