package com.example.project1

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.project1.databinding.FragmentFragment1Binding
import com.example.project1.model.Game_Item
import com.example.project1.model.User
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class fragment1 : Fragment(R.layout.fragment_fragment1) {
    private var _binding: FragmentFragment1Binding? = null
    private val binding get() = _binding!!
    private lateinit var linkFileUser: String
    private var _user: User? = null
    private val user get() = _user!!
    private var streak = 0
    private val userFireBase = UserFireBase()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            _user = it.getSerializable("USER", User::class.java)
            _user?.let {
                linkFileUser = it.listDocument[it.documentSelectedId].linkFile
                streak = it.streak
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFragment1Binding.bind(view)
        linkFileUser = user.listDocument[user.documentSelectedId].linkFile
        binding.txtLengthList.text = "/${getLengthLineFile()}"

        val listSoLuoc = listOf(R.drawable.ic_so_luoc_game_1, R.drawable.ic_so_luoc_game_2)
        val onClick: (Int) -> Unit = { pos ->
            val bundle = Bundle().apply {
                putString("LINKFILEUSER", linkFileUser)
                putInt("STREAK", streak)
                putInt("START", getStartPoint())
            }
            val target = when (pos) {
                0 -> frag_game1().apply { arguments = bundle }
                1 -> frag_game2().apply { arguments = bundle }
                else -> frag_game2().apply { arguments = bundle }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.layoutMain, target)
                .addToBackStack(null)
                .commit()
        }
        binding.vpSoLuoc.adapter = Adapter_SoLuoc(listSoLuoc, onClick)
        TabLayoutMediator(binding.tabSoLuoc, binding.vpSoLuoc) { tab, pos -> }.attach()

        val listGame = listOf(
            Game_Item("Word Quiz", R.drawable.game1, "#81ff8a"),
            Game_Item("Spell Master", R.drawable.game2, "#4284DB")
        )
        binding.rcGames.adapter = Adapter_Games(listGame, onClick)

        binding.btnBatDau.setOnClickListener { view ->
            view.postDelayed({ onClick(listGame.size - 1) }, 120)
        }

        binding.imgReport.setOnClickListener { view ->
            view.postDelayed({
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://forms.gle/Nkpyf5RrT4aHSuB48")
                    )
                )
            }, 100)
        }
        onAdapterSpiner()
    }

    private fun getLengthLineFile(): Int {
        val file =
            File(requireContext().filesDir, user.listDocument[user.documentSelectedId].linkFile)
        return if (file.exists()) {
            file.useLines { lines ->
                lines.count()
            }
        } else 0
    }

    //start [0;size-1]
    private fun getStartPoint(): Int {
        val start = binding.edtStartpoint.text.toString().toIntOrNull()
        if (start == null || start < 1 || start > getLengthLineFile()) return 0
        return start - 1
    }

    fun onAdapterSpiner() {
        val listDocName = mutableListOf<String>()
        var checkFirstSp = true
        for (x in user.listDocument)
            listDocName.add(x.tenDocument)
        binding.spChangeDoc.adapter =
            Adapter_ChangeDoc(requireContext(), R.layout.item_spchangedoc, listDocName)
        binding.spChangeDoc.setSelection(user.documentSelectedId)

        binding.spChangeDoc.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (checkFirstSp) {
                    checkFirstSp = false
                    return
                }
                if (position == user.documentSelectedId) return

                user.documentSelectedId = position
                linkFileUser = user.listDocument[position].linkFile
                binding.txtLengthList.text = "/${getLengthLineFile()}"
                userFireBase.capNhatUser(user) { ans ->
                    runCatching {
                        if (ans) {
                            setUserToShp(user)
                        } else
                            Toast.makeText(
                                requireContext(),
                                "Cập nhật dữ liệu Firebase thất bại!",
                                Toast.LENGTH_SHORT
                            ).show()
                    }.onFailure {
                        Toast.makeText(
                            requireContext(),
                            "Cập nhật dữ liệu Shp thất bại!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }


    fun setUserToShp(user: User) {
        val editor = requireContext().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE).edit()
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}