package com.example.pickandcook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pickandcook.api.RecipeDetailResponse
import com.example.pickandcook.api.RetrofitClient
import com.example.pickandcook.api.SavedRecipeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SavedRecipeAdapter(
    private val items: List<SavedRecipeResponse>,
    private val onClick: (SavedRecipeResponse) -> Unit
) : RecyclerView.Adapter<SavedRecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryText: TextView = view.findViewById(R.id.categoryText)
        val recipeImage: ImageView = view.findViewById(R.id.recipeImage)
        val recipeTitle: TextView = view.findViewById(R.id.recipeTitle)
        val portionText: TextView = view.findViewById(R.id.portionText)
        val timeText: TextView = view.findViewById(R.id.timeText)
        val difficultyText: TextView = view.findViewById(R.id.difficultyText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    /*
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val item = items[position]
        holder.recipeTitle.text = item.ckgNm
        holder.categoryText.text = "종류별 / 상황별 / 방법별"
        holder.portionText.text = "1인분"
        holder.timeText.text = "5분이내"
        holder.difficultyText.text = "아무나"
        holder.recipeImage.setImageResource(R.drawable.ic_placeholder) // Glide 사용 가능

        holder.itemView.setOnClickListener { onClick(item) }
    }

     */


    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val item = items[position]

        // 서버에서 recipeNo로 상세 정보 요청
        RetrofitClient.instance.getRecipeDetail(item.recipeNo)
            .enqueue(object : Callback<RecipeDetailResponse> {
                override fun onResponse(
                    call: Call<RecipeDetailResponse>,
                    response: Response<RecipeDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body() ?: return

                        holder.recipeTitle.text = data.ckgNm
                        holder.portionText.text = data.ckgInbunNm
                        holder.timeText.text = data.ckgTimeNm
                        holder.difficultyText.text = data.ckgDodfNm
                        holder.categoryText.text =
                            "${data.ckgKndActoNm} / ${data.ckgStaActoNm} / ${data.ckgMthActoNm}"

                        Glide.with(holder.itemView.context)
                            .load(data.rcpImgUrl)
                            .placeholder(R.drawable.ic_placeholder)
                            .into(holder.recipeImage)
                    }
                }

                override fun onFailure(call: Call<RecipeDetailResponse>, t: Throwable) {
                    // 실패 시 기본값 유지 또는 로그
                }
            })

        holder.itemView.setOnClickListener { onClick(item) }
    }


    override fun getItemCount(): Int = items.size
}