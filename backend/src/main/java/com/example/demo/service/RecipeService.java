package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Recipe;
import com.example.demo.model.RecipeDetailDTO;
import com.example.demo.repository.RecipeRepository;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    public Recipe getRecipeByNo(int recipeNo) {
        return recipeRepository.findById(recipeNo).orElse(null);
    }
    
    //레시피 결과 화면에 나올 것들 불러오는 함수
    public RecipeDetailDTO getRecipeDetailDTO(int recipeNo) {
        Optional<Recipe> optionalRecipe = recipeRepository.findById(recipeNo);
        return optionalRecipe.map(RecipeDetailDTO::new).orElse(null);
    }
    
    
    
    
    // 1. 모든 레시피 조회
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    // 2. 조건별 레시피 필터링
    /*
    public List<Recipe> filterRecipes(String kind, String situation, String method, List<String> ingredients) {
        List<Recipe> filtered = recipeRepository.findByCkgKndActoNmAndCkgStaActoNmAndCkgMthActoNm(kind, situation, method);

        return filtered.stream()
                .filter(recipe -> ingredients.stream().allMatch(ing -> recipe.getCkgMtrlCn() != null && recipe.getCkgMtrlCn().contains(ing)))
                .collect(Collectors.toList());
    }
*/

    public List<Recipe> filterRecipes(String kind, String situation, String method) {
        // 빈 문자열이면 null로 변경 (쿼리에서 IS NULL 체크 가능하도록)
        kind = kind == null || kind.isEmpty() ? null : kind;
        situation = situation == null || situation.isEmpty() ? null : situation;
        method = method == null || method.isEmpty() ? null : method;

        return recipeRepository.filterRecipes(kind, situation, method);
    }
    
    
    // 3. 카테고리 조회
    public List<String> getCategoryValues(String column, List<Recipe> all) {
        return switch (column) {
            case "ckg_knd_acto_nm" -> all.stream().map(Recipe::getCkgKndActoNm).distinct().collect(Collectors.toList());
            case "ckg_sta_acto_nm" -> all.stream().map(Recipe::getCkgStaActoNm).distinct().collect(Collectors.toList());
            case "ckg_mth_acto_nm" -> all.stream().map(Recipe::getCkgMthActoNm).distinct().collect(Collectors.toList());
            default -> List.of();
        };
    }

    // 4. 이미지 URL
    public String getImageUrlByName(String name) {
        Recipe recipe = recipeRepository.findByCkgNm(name);
        return recipe != null ? recipe.getRcpImgUrl() : null;
    }

    // 5. 레시피 번호로 제목 + 이미지
    public Recipe getRecipeByNo(Integer recipeNo) {
        return recipeRepository.findById(recipeNo).orElse(null);
    }
}
