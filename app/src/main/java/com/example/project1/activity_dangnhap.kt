package com.example.project1

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project1.databinding.ActivityDangnhapBinding
import com.example.project1.model.DocumentLearn
import com.example.project1.model.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class activity_dangnhap : AppCompatActivity() {
    private lateinit var binding: ActivityDangnhapBinding
    private lateinit var userFireBase: UserFireBase
    private lateinit var shp: SharedPreferences
    private lateinit var toast: Toast
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDangnhapBinding.inflate(layoutInflater)
        userFireBase = UserFireBase()
        shp = this.getSharedPreferences("USER_DATA", MODE_PRIVATE)

        setContentView(binding.root)
        toast = Toast.makeText(this, "", Toast.LENGTH_LONG)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, 0)
            insets
        }

        val user = getUserFromShp()
        if (user == null) {
            onClick()
            onDangNhap()
        } else {
            user.capNhatStreak()
            user.capnhatNgayCuoi()
            userFireBase.capNhatUser(user){ans ->
                if(ans){
                    setUserToShp(user)
                }else{
                    showToast("Cập nhật streak lên firebase thất bại!")
                }
            }
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("USER", user)
            startActivity(intent)
            finish()
        }
    }

    fun getUserFromShp(): User? {
        val tenDangNhap = shp.getString("USERNAMEACCOUNT", null)
        if (tenDangNhap.isNullOrBlank()) return null

        val tenUser = shp.getString("USERNAME", "N/A") ?: "N/A"
        val ngaySinh = shp.getString("BIRTHDAY", "30/04/1975") ?: "30/04/1975"
        val gioiTinh = shp.getString("GENDER", "N/A") ?: "N/A"
        val streak = shp.getInt("STREAK", 0)
        val ngayCuoi = shp.getString("LASTDAY", "30/04/1975") ?: "30/04/1975"
        val matKhau = shp.getString("USERPASSWORD", "") ?: ""
        val docselectedid = shp.getInt("DOCUMENTSELECTEDID", 0)
        val avatar = shp.getString("AVATAR", "avatar.jpg") ?: "avatar.jpg"
        val listDoc = shp.getString("LISTDOCUMENT", null)

        val listDocument = mutableListOf<DocumentLearn>()
        if (!listDoc.isNullOrBlank()) {
            runCatching {
                val jsonArray = JSONArray(listDoc)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    listDocument.add(
                        DocumentLearn(
                            jsonObject.optString("NAMEDOCUMENT", ""),
                            jsonObject.optString("LINKFILE", "")
                        )
                    )
                }
            }.onFailure {
                showToast("Lỗi chuyển đọc json!")
            }
        }
        return User(
            tenUser,
            ngaySinh,
            gioiTinh,
            streak,
            ngayCuoi,
            avatar,
            tenDangNhap,
            matKhau,
            listDocument,
            docselectedid
        )
    }

    fun setUserToShp(user: User) {
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun xacThucTaiKhoan(userName: String, userPass: String) {
        binding.btnDangnhap.isEnabled = false
        userFireBase.timUser(userName, userPass) { user ->
            if (user == null) {
                binding.btnDangnhap.isEnabled = true
                MaterialAlertDialogBuilder(this).apply {
                    setTitle("Xác thực thất bại")
                    setMessage("Thông tin không chính xác!\nVui lòng kiểm tra lại!")
                    setPositiveButton("Đồng ý", null)
                }.show()
            } else {
                user.capNhatStreak()
                user.capnhatNgayCuoi()
                userFireBase.capNhatUser(user){ans ->
                    if(ans){
                        setUserToShp(user)
                    }else{
                        showToast("Cập nhật streak lên firebase thất bại!")
                    }
                }
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER", user)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun onClick() {
        val onFocus = object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (hasFocus) {
                    val view = v as? EditText
                    view?.setTextColor("#FFFFFF".toColorInt())
                }
            }
        }

        binding.edtUsernameDangnhap.onFocusChangeListener = onFocus
        binding.edtUserpasswordDangnhap.onFocusChangeListener = onFocus
        binding.edtAccountCreate.onFocusChangeListener = onFocus
    }

    fun showToast(mess: String) {
        toast.apply {
            cancel()
            setText(mess)
            show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onDangKy() {
        binding.edtFullname.text.clear()
        binding.edtNgaySinh.text.clear()
        binding.edtAccountCreate.text.clear()
        binding.edtPasswordCreate.text?.clear()

        binding.lDangNhap.visibility = View.GONE
        binding.lDangKy.visibility = View.VISIBLE
        binding.btnSelectDate.setOnClickListener {
            val now = LocalDate.now()
            val date = DatePickerDialog(this, { _, year, month, day ->
                val birthday = String.format("%02d/%02d/%d", day, month + 1, year)
                binding.edtNgaySinh.setText(birthday)
            }, now.year, now.monthValue - 1, now.dayOfMonth)
            date.datePicker.maxDate = System.currentTimeMillis()
            date.show()
        }
        binding.btnQuaylaidangnhap.setOnClickListener { view -> view.postDelayed({onDangNhap()},100) }
        binding.btnDangky.setOnClickListener { view -> view.postDelayed({ onClickDangKy() }, 100) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onClickDangKy() {
        val hoten = binding.edtFullname.text.toString()
        val ngaySinh = binding.edtNgaySinh.text.toString()
        val gioiTinh = if (binding.rdFemale.isChecked) "Female" else "Male"
        val tenDangNhap = binding.edtAccountCreate.text.toString().trim()
        val matKhau = binding.edtPasswordCreate.text.toString().trim()

        for (x in listOf(hoten, ngaySinh, gioiTinh, tenDangNhap, matKhau)) {
            if (x.isBlank()) {
                showToast("Hãy nhập đầy đủ thông tin!")
                return
            }
        }

        userFireBase.checkNameUser(tenDangNhap) { ans ->
            if (ans) {
                val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val user = User(
                    hoten,
                    ngaySinh,
                    gioiTinh,
                    0,
                    LocalDate.now().format(format),
                    "N/A",
                    tenDangNhap,
                    matKhau
                )
                user.listDocument.add(DocumentLearn("English","DataUserLanguage.txt"))
                userFireBase.capNhatUser(user) { ans ->
                    if (ans) {
                        MaterialAlertDialogBuilder(this).apply {
                            setTitle("Tạo tài khoản thành công!")
                            setMessage("Tạo tài khoản $tenDangNhap thành công!\nVui lòng quay lại trang Đăng nhập!")
                            setPositiveButton("Yes") { _, _ ->
                                onDangNhap()
                            }
                            setCancelable(false)
                        }.show()
                    } else {
                        showToast("Ghi lên Firebase bị lỗi!")
                    }

                }
            } else {
                showToast("Tên tài khoản đã được sử dụng!")
                binding.edtAccountCreate.setTextColor(Color.RED)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onDangNhap() {
        binding.lDangKy.visibility = View.GONE
        binding.lDangNhap.visibility = View.VISIBLE
        binding.btnDangnhap.setOnClickListener { view ->
            view.postDelayed({
                if (binding.edtUsernameDangnhap.text.isNullOrBlank() || binding.edtUserpasswordDangnhap.text.isNullOrBlank()) {
                    showToast("Hãy nhập đủ thông tin!")
                    return@postDelayed
                }
                val userName = binding.edtUsernameDangnhap.text.toString().trim()
                val userPass = binding.edtUserpasswordDangnhap.text.toString().trim()
                xacThucTaiKhoan(
                    userName,
                    userPass
                )
            }, 100)

        }
        binding.btnTaotaikhoan.setOnClickListener { view -> view.postDelayed({ onDangKy() }, 100) }
    }
}