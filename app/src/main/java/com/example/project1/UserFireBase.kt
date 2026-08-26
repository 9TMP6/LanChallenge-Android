package com.example.project1

import android.widget.Toast
import com.example.project1.model.User
import com.google.firebase.database.FirebaseDatabase

class UserFireBase {
    private val dbrf = FirebaseDatabase.getInstance().getReference("User")
    fun capNhatUser(user: User, result: (Boolean) -> Unit) {
        if (user.tenDangNhap.isBlank() || user.tenDangNhap == "N/A")
            result(false)
        else {
            dbrf.child(user.tenDangNhap).setValue(user)
                .addOnSuccessListener { result(true) }
                .addOnFailureListener { result(false) }
        }
    }

    fun xoaUser(user: User, result: (Boolean) -> Unit) {
        if (user.tenDangNhap.isBlank() || user.tenDangNhap == "N/A")
            result(false)
        else {
            dbrf.child(user.tenDangNhap).removeValue()
                .addOnSuccessListener { result(true) }
                .addOnFailureListener { result(false) }
        }
    }

    fun timUser(nameUser: String, passWord: String, result: (User?) -> Unit) {
        dbrf.child(nameUser).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null && user.matKhau == passWord) {
                        result(user)
                    } else {
                        result(null)
                    }
            } else {
                result(null)
            }
        }.addOnFailureListener {
            result(null)
        }
    }

    fun checkNameUser(nameUser: String, result: (Boolean) -> Unit) {
        dbrf.child(nameUser).get().addOnSuccessListener { snapshot ->
            result(!snapshot.exists())
        }.addOnFailureListener { result(false) }
    }
}