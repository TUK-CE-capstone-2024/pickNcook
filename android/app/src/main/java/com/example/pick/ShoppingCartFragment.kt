package com.example.pick

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pick.databinding.FragmentShoppingCartBinding
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class ShoppingCartFragment : Fragment() {

    private var _binding: FragmentShoppingCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ShoppingAdapter

    // 중복 기준 식재료 리스트 ( ***** )
    private val duplicatedItems = listOf("우유", "양배추")

    // 쇼핑카트 아이템 리스트 ( ***** )
    private val itemNames = listOf("우유", "양배추", "사과", "당근")
    private val itemList = itemNames.map { name ->
        ShoppingItem(name, duplicatedItems.contains(name))
    }.toMutableList()

    // 카메라
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val photo = result.data?.extras?.get("data") as? Bitmap
            photo?.let {
                Toast.makeText(requireContext(), "사진 촬영 완료", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 권한 요청 런처
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 카메라 권한 요청 함수
    private fun requestCameraPermission() {
        val cameraPermission = Manifest.permission.CAMERA

        when {
            ContextCompat.checkSelfPermission(requireContext(), cameraPermission) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }

            shouldShowRequestPermissionRationale(cameraPermission) -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("카메라 권한 필요")
                    .setMessage("사진을 촬영하려면 카메라 권한이 필요합니다.")
                    .setPositiveButton("확인") { _, _ ->
                        requestPermissionLauncher.launch(cameraPermission)
                    }
                    .setNegativeButton("취소", null)
                    .show()
            }

            else -> {
                requestPermissionLauncher.launch(cameraPermission)
            }
        }
    }

    // 카메라 인텐트 실행 함수
    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 식재료 삭제
        adapter = ShoppingAdapter(itemList, onDelete = { itemToRemove ->
            AlertDialog.Builder(requireContext())
                .setTitle("삭제하시겠습니까?")
                .setMessage("\"${itemToRemove.name}\"(이)가 삭제됩니다.")
                .setPositiveButton("확인") { _, _ ->
                    adapter.removeItem(itemToRemove)
                }
                .setNegativeButton("취소", null)
                .show()
        }, enableSelection = false, enableDelete = true)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // 뒤로 가기 버튼 클릭 시 Fragment 스택에서 제거
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 카메라 클릭 시
        binding.cameraButton.setOnClickListener {
            requestCameraPermission()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
