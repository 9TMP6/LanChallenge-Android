package com.example.project1

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentManager
import com.example.project1.databinding.ActivityMainBinding
import com.example.project1.model.User

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var user: User
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.layoutMain) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }
        user = getUser()!!
        setHeaderProfile(user)
        onClick()
        val bundle = Bundle()
        bundle.putSerializable("USER",user)
        supportFragmentManager.beginTransaction()
            .replace(R.id.layoutMain, fragment1::class.java,bundle)
            .commit()
        Toast.makeText(this,"Đăng nhập thành công!", Toast.LENGTH_LONG).show()
    }

    fun onClick() {
        binding.btnProfile.setOnClickListener {
            binding.main.openDrawer(GravityCompat.START)
            val degree = binding.btnProfile.rotation
            ObjectAnimator.ofFloat(binding.btnProfile, "rotation", degree, degree + 120)
                .start()
        }

        binding.nvProfile.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.miHome -> {
                    if (supportFragmentManager.backStackEntryCount > 0)
                        supportFragmentManager.popBackStack(
                            null,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        )
                }

                R.id.miList -> {
                    val bundle = Bundle()
                    bundle.putString("LINKFILEUSER",user.listDocument[user.documentSelectedId].linkFile)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.layoutMain, frag_menu_listword()::class.java,bundle)
                        .addToBackStack(null)
                        .commit()
                }

                R.id.miLookup -> {
                    val bundle = Bundle()
                    bundle.putString("LINKFILEUSER",user.listDocument[user.documentSelectedId].linkFile)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.layoutMain, frag_lookup()::class.java,bundle)
                        .addToBackStack(null)
                        .commit()
                }

                R.id.miAdd ->{
                    val bundle = Bundle()
                    bundle.putSerializable("USER",user)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.layoutMain, frag_adddocument::class.java,bundle)
                        .addToBackStack(null)
                        .commit()
                }

                R.id.miLogOut -> {
                    Toast.makeText(this,"Đã đăng xuất!", Toast.LENGTH_LONG).show()
                    val shp = this.getSharedPreferences("USER_DATA",MODE_PRIVATE)
                    shp.edit().clear().apply()
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    startActivity(Intent(this, activity_dangnhap::class.java))
                    finish()
                }
            }
            binding.main.closeDrawer(GravityCompat.START)
            true
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getUser(): User? {
        val user = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("USER", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("USER") as? User
        }
        if (user == null) {
            startActivity(Intent(this, activity_dangnhap::class.java))
            finish()
            return null
        }
        return user
    }

    fun setHeaderProfile(user: User) {
        val header = binding.nvProfile.getHeaderView(0)
        header.findViewById<TextView>(R.id.txtHoten).text = user.tenUser
        header.findViewById<ImageView>(R.id.imgAvatar).setImageResource(R.drawable.avatar)
        header.findViewById<TextView>(R.id.txtStreak).text = user.streak.toString()
        header.findViewById<Button>(R.id.btnDetail).setOnClickListener { view ->
            val bundle = Bundle()
            bundle.putSerializable("USER", user)
            view.postDelayed({
                supportFragmentManager.beginTransaction()
                    .replace(R.id.layoutMain, frag_detailuser::class.java, bundle)
                    .addToBackStack(null)
                    .commit()
                binding.main.closeDrawer(GravityCompat.START)
            }, 100)
        }
    }
}