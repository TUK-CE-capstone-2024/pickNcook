package com.example.pickandcook

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pickandcook.api.RetrofitClient
import com.example.pickandcook.api.ShoppingListDetail
import com.example.pickandcook.databinding.FragmentShoppingListDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShoppingListDetailFragment : Fragment() {

    private var _binding: FragmentShoppingListDetailBinding? = null
    private val binding get() = _binding!!

    private var listNo: Int = -1
    private var listName: String? = null
    private var regDate: String? = null
    private lateinit var ingredientAdapter: IngredientAdapter
    private var ingredients: MutableList<ShoppingListDetail> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            listNo = it.getInt("listNo")
            listName = it.getString("listName")
            regDate = it.getString("regDate")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingListDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.tvListTitle.text = "$listName"
        binding.tvDate.text = "$regDate"
        binding.etEditTitle.setText(listName)

        // 텍스트 클릭 시 EditText로 전환
        binding.tvListTitle.setOnClickListener {
            binding.tvListTitle.visibility = View.GONE
            binding.etEditTitle.visibility = View.VISIBLE
            binding.etEditTitle.requestFocus()
            binding.etEditTitle.setSelection(binding.etEditTitle.text.length)
        }

        // Enter 누르면 제목 저장
        binding.etEditTitle.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val newTitle = binding.etEditTitle.text.toString().trim()
                if (newTitle.isNotEmpty() && newTitle != listName) {
                    updateTitle(newTitle)
                }
                binding.etEditTitle.visibility = View.GONE
                binding.tvListTitle.visibility = View.VISIBLE
                true
            } else {
                false
            }
        }

        // 어댑터 연결 (삭제 기능 포함)
        ingredientAdapter = IngredientAdapter(ingredients) { item ->
            AlertDialog.Builder(requireContext())
                .setTitle("삭제 확인")
                .setMessage("${item.ingredientName}을(를) 삭제할까요?")
                .setPositiveButton("삭제") { _, _ ->
                    RetrofitClient.instance.deleteIngredient(item.listDetailNo)
                        .enqueue(object : Callback<Map<String, String>> {
                            override fun onResponse(
                                call: Call<Map<String, String>>,
                                response: Response<Map<String, String>>
                            ) {
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "삭제 완료", Toast.LENGTH_SHORT).show()
                                    loadIngredients()
                                } else {
                                    Toast.makeText(context, "삭제 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                                Toast.makeText(context, "삭제 실패", Toast.LENGTH_SHORT).show()
                            }
                        })
                }
                .setNegativeButton("취소", null)
                .show()
        }

        binding.rvIngredients.layoutManager = LinearLayoutManager(requireContext())
        binding.rvIngredients.adapter = ingredientAdapter

        // 재료 추가
        binding.btnAddIngredient.setOnClickListener {
            val ingredient = binding.etNewIngredient.text.toString().trim()
            if (ingredient.isNotEmpty()) {
                addIngredient(ingredient)
                binding.etNewIngredient.text.clear()
            }
        }

        loadIngredients()
    }

    private fun loadIngredients() {
        Log.d("ShoppingDetail", "불러오는 listNo: $listNo")

        RetrofitClient.instance.getShoppingListDetails(listNo)
            .enqueue(object : Callback<List<ShoppingListDetail>> {
                override fun onResponse(
                    call: Call<List<ShoppingListDetail>>,
                    response: Response<List<ShoppingListDetail>>
                ) {
                    if (response.isSuccessful) {
                        ingredients.clear()
                        response.body()?.let {
                            ingredients.addAll(it)
                        }
                        ingredientAdapter.notifyDataSetChanged()
                    } else {
                        Log.e("ShoppingDetail", "불러오기 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<ShoppingListDetail>>, t: Throwable) {
                    Log.e("ShoppingDetail", "불러오기 실패: ${t.message}")
                }
            })
    }

    private fun addIngredient(name: String) {
        RetrofitClient.instance.addIngredientToList(listNo, name)
            .enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(
                    call: Call<Map<String, String>>,
                    response: Response<Map<String, String>>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "추가됨", Toast.LENGTH_SHORT).show()
                        loadIngredients()
                    }
                }

                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "추가 실패", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateTitle(newTitle: String) {
        val request = mapOf(
            "listId" to listNo.toString(),
            "newName" to newTitle
        )

        RetrofitClient.instance.updateShoppingListTitle(request)
            .enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(
                    call: Call<Map<String, String>>,
                    response: Response<Map<String, String>>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "제목이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                        listName = newTitle

                        view?.let {
                            binding.tvListTitle.text = "$listName"
                            binding.tvDate.text = "$regDate"
                            binding.etEditTitle.setText(listName)
                        }
                    }
                }

                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "수정 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    companion object {
        fun newInstance(listNo: Int, listName: String, regDate: String) =
            ShoppingListDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt("listNo", listNo)
                    putString("listName", listName)
                    putString("regDate", regDate)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
