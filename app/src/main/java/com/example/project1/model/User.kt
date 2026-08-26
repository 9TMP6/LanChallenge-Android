package com.example.project1.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class User(
    var tenUser: String = "N/A",
    var ngaySinh: String = "N/A",
    var gioiTinh: String = "N/A",
    var streak: Int = 0,
    var ngayCuoi: String = "N/A",
    var avatar: String = "avatar.jpg",
    var tenDangNhap: String = "N/A",
    var matKhau: String = "N/A",
    var listDocument: MutableList<DocumentLearn> = mutableListOf(),
    var documentSelectedId: Int = 0
) : Serializable {

    @RequiresApi(Build.VERSION_CODES.O)
    fun capnhatNgayCuoi() {
        val format = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
        ngayCuoi = LocalDate.now().format(format)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun capNhatStreak() {
        val format = DateTimeFormatter.ofPattern(
            "dd/MM/yyyy",
            Locale.getDefault()
        )

        if (ngayCuoi == "N/A" || ngayCuoi.isBlank()) {
            ngayCuoi = LocalDate.now().format(format)
        }

        val beforeDay = LocalDate.parse(ngayCuoi, format)

        val range = ChronoUnit.DAYS.between(beforeDay,LocalDate.now()
        ).toInt()

        streak = when (range) {
            0 -> streak
            1 -> streak + 1
            else -> 0
        }

    }
}