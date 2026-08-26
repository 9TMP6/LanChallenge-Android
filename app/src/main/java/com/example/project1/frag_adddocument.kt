package com.example.project1

import android.content.Context
import android.net.Uri
import androidx.core.widget.doOnTextChanged
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.example.project1.databinding.FragmentFragAdddocumentBinding
import com.example.project1.model.DocumentLearn
import com.example.project1.model.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class frag_adddocument : Fragment(R.layout.fragment_frag_adddocument) {
    private var _user: User? = null
    private val user get() = _user!!
    private var _binding: FragmentFragAdddocumentBinding? = null
    private val binding get() = _binding!!
    private lateinit var pickTxtFileLauncher: ActivityResultLauncher<String>
    private var fileContentOther: String? = ""

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            _user = it.getSerializable("USER", User::class.java)
        }

        pickTxtFileLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    val nameFile = it.lastPathSegment?.substringAfterLast("/")?.let {
                        if (it.endsWith(".txt")) it else "$it.txt"
                    }
                    binding.txtLinkFile.text = nameFile
                    fileContentOther =
                        requireContext().contentResolver.openInputStream(uri)?.bufferedReader()
                            ?.readText()
                }
            }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFragAdddocumentBinding.bind(view)
        onAnimation(binding.txtNewAddDoc, binding.txtOtherAddDoc)
        onClickButton()
    }

    private fun onAnimation(viewSelected: View, viewUnSelected: View) {
        viewSelected.isSelected = true
        viewUnSelected.isSelected = false

        viewSelected.animate()
            .scaleX(1.4f)
            .scaleY(1.4f)
            .setDuration(300)
            .start()

        viewUnSelected.animate()
            .scaleX(1.0f)
            .scaleY(1.0f)
            .setDuration(300)
            .start()
    }

    fun setUserToShp(user: User) {
        val shp = requireContext().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        val editor = shp.edit()
        editor.putString("USERNAME", user.tenUser)
        editor.putString("BIRTHDAY", user.ngaySinh)
        editor.putString("GENDER", user.gioiTinh)
        editor.putInt("STREAK", user.streak)
        editor.putString("LASTDAY", user.ngayCuoi)
        editor.putString("USERNAMEACCOUNT", user.tenDangNhap)
        editor.putString("USERPASSWORD", user.matKhau)
        editor.putInt("DOCUMENTSELECTEDID", user.documentSelectedId)
        editor.putString("AVATAR", user.avatar)

        val listDocment = JSONArray()
        for (x in user.listDocument) {
            val jsonObject = JSONObject().apply {
                put("NAMEDOCUMENT", x.tenDocument)
                put("LINKFILE", x.linkFile)
            }
            listDocment.put(jsonObject)
        }
        editor.putString("LISTDOCUMENT", listDocment.toString())
        editor.apply()
    }

    fun onRepairText(text: String): String {
        if (text.isBlank()) return ""
        var ans = ""
        for (x in text) {
            if (x.isLetterOrDigit())
                ans += x
        }
        return ans
    }

    fun onClickButton() {
        binding.txtNewAddDoc.setOnClickListener {
            binding.txtLuuy.setTextColor("#FFFFFF".toColorInt())
            binding.txtLuuy.text =
                "Tạo một từ điển rỗng để bạn chủ động thêm từ mới trong quá trình ôn tập. Cài đặt có thể thay đổi bất kỳ lúc nào. Vui lòng kết nối mạng để có trải nghiệm tốt nhất."
            onAnimation(it, binding.txtOtherAddDoc)
            binding.lNew.visibility = View.VISIBLE
            binding.lOther.visibility = View.GONE
            binding.imgLuuy.visibility = View.GONE

        }.also { binding.txtNewAddDoc.isSelected = true }

        binding.txtOtherAddDoc.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle("Xác nhận nhập dữ liệu từ tệp (.txt)")
                setMessage(
                    """
                    Tính năng này cho phép bạn tải tệp .txt lên ứng dụng để tạo từ điển tự động. Lưu ý quan trọng về định dạng:
                    1. Mỗi từ vựng nằm trên một dòng duy nhất.
                    2. Định dạng bắt buộc mỗi dòng:
                    [Từ mới] . /[Mô tả]/ | [Nghĩa]
                    Đây là tính năng nâng cao. Nếu tệp không đúng cấu trúc, ứng dụng có thể gặp lỗi hiển thị không mong muốn. Để an toàn, bạn nên dùng tính năng "New" để thêm từng từ thủ công.
                    Hãy chắc chắn bạn đã hiểu rõ để tiếp tục?
                """.trimIndent()
                )
                setPositiveButton("Đã hiểu") { _, _ ->
                    binding.txtLuuy.text =
                        "Cho phép nạp từ điển từ tệp .txt. Yêu cầu mỗi từ nằm trên một dòng theo đúng cấu trúc: [Từ] . /[Mô tả]/ | [Nghĩa]. Sai định dạng có thể dẫn đến lỗi dữ liệu.\nVí dụ:\nHello . /hə'ləʊ/ | Xin chào\nBạn cũng có thể dùng tab New để nhập từ thủ công an toàn hơn."
                    binding.txtLuuy.setTextColor("#CEAB3D".toColorInt())
                    onAnimation(it, binding.txtNewAddDoc)
                    binding.imgLuuy.visibility = View.VISIBLE
                    binding.lOther.visibility = View.VISIBLE
                    binding.lNew.visibility = View.GONE
                }
                setNegativeButton("Huỷ") { _, _ ->
                    binding.txtNewAddDoc.performClick()
                }
                setCancelable(false)
            }.show()
        }

        binding.edtTenDoc.doOnTextChanged { text, start, before, count ->
            val ans = onRepairText(text.toString()).trim()
            binding.edtTenFile.setText("$ans.txt")
        }

        binding.btnClose.setOnClickListener { view ->
            view.postDelayed({ parentFragmentManager.popBackStack() }, 100)
        }

        binding.btnThemDoc.setOnClickListener {
            if (binding.edtTenDoc.text.isNullOrBlank()) {
                Toast.makeText(context, "Hãy nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val listChar = setOf('/', ':', '*', '?', '"', '<', '>', '|', '.')
            if (binding.edtTenDoc.text.toString()
                    .any { it in listChar } || binding.edtTenFile.text.length <= 4 || binding.edtTenDoc.text.toString() == "DataUserLanguage"
            ) {
                Toast.makeText(context, "Tên file không hợp lệ!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userFireBase = UserFireBase()
            val newDoc = DocumentLearn(
                binding.edtTenDoc.text.toString().trim(),
                binding.edtTenFile.text.toString().trim()
            )
            user.listDocument.add(newDoc)

            userFireBase.capNhatUser(user) { ans ->
                if (ans) {
                    runCatching {
                        setUserToShp(user)
                        binding.edtTenDoc.text.clear()
                        binding.edtTenFile.setText(".txt")
                        MaterialAlertDialogBuilder(requireContext()).apply {
                            setIcon(R.drawable.ic_edit_menu_listword)
                            setTitle("Thêm tài liệu thành công")
                            setMessage("Thêm tài liệu ${newDoc.tenDocument} thành công!")
                            setPositiveButton("Đồng ý", null)
                        }.show()
                    }
                        .onFailure {
                            Toast.makeText(context, "Cập nhật SHP thất bại!", Toast.LENGTH_LONG)
                                .show()
                        }
                } else {
                    Toast.makeText(context, "Không có kết nối Internet! ", Toast.LENGTH_LONG).show()
                }
            }

        }

        binding.txtLinkFile.setOnClickListener { view ->
            view.postDelayed({ pickTxtFileLauncher.launch("text/plain") }, 100)
        }

        binding.btnThemFile.setOnClickListener {
            val tenFile = binding.edtTenDocFile.text.toString().trim()
            val linkFile = binding.txtLinkFile.text.toString().trim()
            if (tenFile.isBlank() || linkFile.isBlank() || linkFile.length < 4) {
                Toast.makeText(context, "Hãy nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val listChar = setOf('/', ':', '*', '?', '"', '<', '>', '|', '.')
            if (tenFile.any { it in listChar } || linkFile == "DataUserLanguage.txt") {
                Toast.makeText(context, "Tên file không hợp lệ!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newDoc = DocumentLearn(tenFile, linkFile)
            user.listDocument.add(newDoc)
            val userFireBase = UserFireBase()
            userFireBase.capNhatUser(user) { ans ->
                if (ans) {
                    runCatching {
                        setUserToShp(user)
                        File(requireContext().filesDir, linkFile).writeText(
                            fileContentOther ?: ""
                        )
                        binding.edtTenDocFile.text.clear()
                        binding.txtLinkFile.text = ""
                        MaterialAlertDialogBuilder(requireContext()).apply {
                            setIcon(R.drawable.ic_edit_menu_listword)
                            setTitle("Thêm tài liệu thành công")
                            setMessage("Thêm tài liệu ${newDoc.tenDocument} thành công!")
                            setPositiveButton("Đồng ý", null)
                        }.show()
                    }.onFailure {
                        Toast.makeText(context, "Cập nhật SHP thất bại!", Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    Toast.makeText(context, "Không có kết nối Internet! ", Toast.LENGTH_LONG).show()
                }
            }

        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}
