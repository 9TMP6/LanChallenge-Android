package com.example.project1

import android.app.DatePickerDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.project1.databinding.FragmentFragDetailuserBinding
import com.example.project1.model.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

class frag_detailuser : Fragment(R.layout.fragment_frag_detailuser) {
    private var user: User? = null
    private var _binding: FragmentFragDetailuserBinding? = null
    private val binding get() = _binding!!

    private val userFireBase = UserFireBase()
    private var changeDeleteDoc = false

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getSerializable("USER", User::class.java)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFragDetailuserBinding.bind(view)
        onSetData(user)
        onClickButton()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onClickButton() {
        binding.btnDeleteDocDetailuser.setOnClickListener {
            if (user?.documentSelectedId == 0) {
                Toast.makeText(
                    requireContext(),
                    "Không thể xoá từ điển gốc!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle("Xác nhận xoá từ điển")
                setMessage("Xác nhận xoá từ điển ${binding.edtTudienUser.text}!\nHành động này không thể hoàn tác!")
                setCancelable(false)
                setNegativeButton("Huỷ", null)
                setPositiveButton("Xoá") { _, _ ->
                    user?.let {
                        it.listDocument.removeAt(it.documentSelectedId)
                        it.documentSelectedId = 0
                        binding.edtTudienUser.setText(it.listDocument[it.documentSelectedId].tenDocument)
                        changeDeleteDoc = true
                    }
                    Toast.makeText(
                        requireContext(),
                        "Xoá thành công, vui lòng bấm Lưu để hoàn thành!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.show()
        }

        binding.btnClose.setOnClickListener { view ->
            view.postDelayed({
                parentFragmentManager.popBackStack()
            }, 100)
        }
        binding.edtBirthdayUser.setOnClickListener {
            val now = LocalDate.now()
            DatePickerDialog(requireContext(), { _, year, month, day ->
                binding.edtBirthdayUser.text = String.format("%02d/%02d/%d", day, month + 1, year)
            }, now.year, now.monthValue - 1, now.dayOfMonth).also {
                it.datePicker.maxDate = System.currentTimeMillis()
            }.show()
        }

        binding.btnEditUser.setOnClickListener { onEdit() }
        binding.btnRemoveUser.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle("Xác nhận xoá")
                    .setMessage("Xác nhận xoá tài khoản của bạn!\nHành động này không thể hoàn tác và sẽ xoá toàn bộ dữ liệu tài liệu của bạn vĩnh viễn!")
                    .setPositiveButton("Xác nhận") { _, _ ->
                        userFireBase.xoaUser(user!!) { ans ->
                            if (ans) {
                                Toast.makeText(
                                    requireContext(),
                                    "Xoá thành công!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val shp = requireContext().getSharedPreferences(
                                    "USER_DATA",
                                    Context.MODE_PRIVATE
                                )
                                shp.edit().clear().apply()
                                parentFragmentManager.popBackStack(
                                    null,
                                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                                )
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        activity_dangnhap::class.java
                                    )
                                )
                                requireActivity().finish()
                            } else
                                Toast.makeText(
                                    requireContext(),
                                    "Xoá thất bại!",
                                    Toast.LENGTH_SHORT
                                ).show()
                        }
                    }
                    .setNegativeButton("Huỷ", null)
            }.show()
        }
    }

    fun setUserToShp(user: User) {
        val editor = requireContext().getSharedPreferences("USER_DATA", MODE_PRIVATE).edit()
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

    fun onEdit() {
        if (binding.btnEditUser.tag == "Edit") {
            binding.edtNameUser.isEnabled = true
            binding.edtBirthdayUser.isEnabled = true
            binding.edtPasswordUser.isEnabled = true
            binding.btnChangeAvatarUser.visibility = View.VISIBLE
            binding.btnDeleteDocDetailuser.visibility = View.VISIBLE
            binding.rdFemale.isClickable = true
            binding.rdMale.isClickable = true
            binding.btnEditUser.tag = "Save"
            binding.btnEditUser.text = "Lưu"
            binding.edtNameUser.requestFocus()
        } else {
            if (checkEmptyEdt()) {
                Toast.makeText(
                    requireContext(),
                    "Vui lòng điền đầy đủ thông tin!",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (checkChange(user!!)) {
                onSave(user!!)
                userFireBase.capNhatUser(user!!) { ans ->
                    if (ans) {
                        setUserToShp(user!!)
                        Toast.makeText(
                            requireContext(),
                            "Cập nhật thành công! ",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Cập nhật thất bại! ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            onSetData(user)
            binding.btnEditUser.tag = "Edit"
            binding.btnEditUser.text = "Chỉnh sửa"

        }
    }

    private fun onSetData(user: User?) {
        binding.edtNameUser.isEnabled = false
        binding.edtBirthdayUser.isEnabled = false
        binding.edtPasswordUser.isEnabled = false
        binding.rdMale.isClickable = false
        binding.rdFemale.isClickable = false
        binding.imgAvatarUser.setImageResource(R.drawable.avatar)
        binding.btnChangeAvatarUser.visibility = View.INVISIBLE
        binding.btnDeleteDocDetailuser.visibility = View.GONE

        if (user != null) {
            binding.txtHotenUser.text = user.tenUser
            binding.edtNameUser.setText(user.tenUser)
            binding.edtBirthdayUser.text = user.ngaySinh
            binding.edtAccountUser.setText(user.tenDangNhap)
            binding.edtPasswordUser.setText(user.matKhau)
            binding.edtTudienUser.setText(user.listDocument[user.documentSelectedId].tenDocument)
            binding.edtStreakUser.setText(user.streak.toString())
            if (user.gioiTinh == "Female")
                binding.rdFemale.isChecked = true
            else
                binding.rdMale.isChecked = true
        }
    }

    fun onSave(user: User) {
        user.tenUser = binding.edtNameUser.text.toString().trim()
        user.ngaySinh = binding.edtBirthdayUser.text.toString().trim()
        user.matKhau = binding.edtPasswordUser.text.toString().trim()
        user.gioiTinh = if (binding.rdFemale.isChecked) "Female" else "Male"
    }

    fun checkChange(user: User): Boolean {
        val ten = user.tenUser != binding.edtNameUser.text.toString().trim()
        val ngaySinh = user.ngaySinh != binding.edtBirthdayUser.text.toString().trim()
        val mk = user.matKhau != binding.edtPasswordUser.text.toString().trim()
        val gioiTinh = user.gioiTinh != (if (binding.rdFemale.isChecked) "Female" else "Male")
        return ten || ngaySinh || mk || gioiTinh || changeDeleteDoc
    }

    fun checkEmptyEdt(): Boolean {
        return binding.edtNameUser.text.isNullOrBlank() || binding.edtBirthdayUser.text.isNullOrBlank() || binding.edtPasswordUser.text.isNullOrBlank()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}