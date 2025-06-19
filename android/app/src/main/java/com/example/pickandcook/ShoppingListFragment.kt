package com.example.pickandcook

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pickandcook.api.RetrofitClient
import com.example.pickandcook.api.ShoppingList
import com.example.pickandcook.databinding.FragmentShoppingListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShoppingListFragment : Fragment() {

    private var _binding: FragmentShoppingListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ShoppingListAdapter
    private var shoppingLists: MutableList<ShoppingList> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.shoppingListRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ShoppingListAdapter(
            shoppingLists,
            onItemClick = { selectedItem ->
                val fragment = ShoppingListDetailFragment.newInstance(
                    selectedItem.shoppingListNo,
                    selectedItem.listName,
                    selectedItem.regDate
                )
                parentFragmentManager.beginTransaction()
                    .replace(R.id.mainFragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onDeleteClick = { selectedItem ->
                AlertDialog.Builder(requireContext())
                    .setTitle("리스트 삭제")
                    .setMessage("정말 삭제하시겠습니까?")
                    .setPositiveButton("삭제") { _, _ ->
                        RetrofitClient.instance.deleteShoppingList(selectedItem.shoppingListNo)
                            .enqueue(object : Callback<Map<String, String>> {
                                override fun onResponse(
                                    call: Call<Map<String, String>>,
                                    response: Response<Map<String, String>>
                                ) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "삭제 완료", Toast.LENGTH_SHORT).show()
                                        loadShoppingLists()
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
        )



        binding.shoppingListRecyclerView.adapter = adapter

        binding.btnAddList.setOnClickListener { addNewShoppingList() }

        loadShoppingLists()
    }

    private fun loadShoppingLists() {
        val userId = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            .getString("userId", null) ?: return

        Log.d("ShoppingList", "불러올 userId: $userId") // ✅ 유저 ID 확인

        RetrofitClient.instance.getShoppingLists(userId)
            .enqueue(object : Callback<List<ShoppingList>> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<List<ShoppingList>>,
                    response: Response<List<ShoppingList>>
                ) {
                    if (response.isSuccessful) {
                        val lists = response.body()
                        Log.d("ShoppingList", "받은 쇼핑리스트 수: ${lists?.size}") // ✅ 받은 리스트 수 확인
                        lists?.forEach {
                            Log.d("ShoppingList", "제목: ${it.listName}, 날짜: ${it.regDate}")
                        }

                        shoppingLists.clear()
                        lists?.let { shoppingLists.addAll(it.reversed()) } // 최신순으로 정렬
                        adapter.notifyDataSetChanged()
                    } else {
                        Log.e("ShoppingList", "서버 응답 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<ShoppingList>>, t: Throwable) {
                    Log.e("ShoppingList", "통신 실패: ${t.message}")
                }
            })
    }


    private fun addNewShoppingList() {
        val userId = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            .getString("userId", null) ?: return

        val request = mapOf(
            "userId" to userId,
            "listName" to "새 쇼핑리스트"
        )

        RetrofitClient.instance.addShoppingList(request)
            .enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(
                    call: Call<Map<String, String>>,
                    response: Response<Map<String, String>>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "리스트가 추가되었습니다", Toast.LENGTH_SHORT).show()
                        loadShoppingLists()
                    }
                }

                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "추가 실패", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}