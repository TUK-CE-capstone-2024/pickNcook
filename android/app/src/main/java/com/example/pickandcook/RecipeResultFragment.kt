package com.example.pickandcook

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pickandcook.api.Recipe
import com.example.pickandcook.api.RecipeFilterRequest
import com.example.pickandcook.api.RecipeItem
import com.example.pickandcook.api.RetrofitClient
import com.example.pickandcook.databinding.FragmentRecipeResultBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeResultFragment : Fragment() {

    private var _binding: FragmentRecipeResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack("CategoryFragment", 0)
        }

        val recipeList = arguments?.getParcelableArrayList<Recipe>("recipes") ?: arrayListOf()

        binding.recipeContainer.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recipeContainer.adapter = RecipeResultAdapter(recipeList)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
