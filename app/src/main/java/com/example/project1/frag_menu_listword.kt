package com.example.project1


import android.animation.ValueAnimator
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project1.databinding.FragmentFragMenuListwordBinding
import com.example.project1.model.DictionaryModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class frag_menu_listword : Fragment(R.layout.fragment_frag_menu_listword) {
    private var _binding: FragmentFragMenuListwordBinding? = null
    private var _model: DictionaryModel? = null
    private val binding get() = _binding!!
    private val model get() = _model!!
    private var adapter: Adapter_ListWord? = null
    private val list = mutableListOf<Triple<String, String, String>>()
    private var toast: Toast? = null
    private var linkFileUser = "DataUserLanguage.txt"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFragMenuListwordBinding.bind(view)
        linkFileUser = arguments?.getString("LINKFILEUSER") ?: "DataUserLanguage.txt"
        _model = DictionaryModel(requireContext(), linkFileUser)
        model.addData()
        getListWord()
        addAdapter(list)
        setOnClickButton()
    }

    fun getListWord(dic: MutableMap<String, String> = model.dictionary) {
        list.clear()
        for (x in dic)
            list.add(
                Triple(
                    x.key.substringBefore(".", "").trim(),
                    x.key.substringAfter(".", "").trim(),
                    x.value
                )
            )
    }

    fun animationChangeViewWidth(view: View, start: Int, end: Int, duration: Long = 200) {
        val density = resources.displayMetrics.density
        val startPx = (start * density).toInt()
        val endPx = (end * density).toInt()

        val animator = ValueAnimator.ofInt(startPx, endPx)
        animator.setDuration(duration)
        animator.addUpdateListener { animate ->
            val value = animate.getAnimatedValue()
            val layout = view.layoutParams
            layout.width = value as Int
            view.layoutParams = layout
        }
        animator.start()

    }

    fun setOnClickButton() {
        binding.btnClose.setOnClickListener { view ->
            view.postDelayed({ parentFragmentManager.popBackStack() }, 100)
        }

        binding.svSearchListword.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                onSearch(newText)
                sortListWord(binding.btnSortListword.selectedItemPosition)
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                onSearch(query)
                sortListWord(binding.btnSortListword.selectedItemPosition)
                return true
            }
        })

        binding.svSearchListword.setOnSearchClickListener { view ->
            animationChangeViewWidth(binding.btnSortListword, 50, 40)
            animationChangeViewWidth(binding.btnAddListword, 50, 40)
        }

        binding.svSearchListword.setOnCloseListener {
            animationChangeViewWidth(binding.btnSortListword, 40, 50)
            animationChangeViewWidth(binding.btnAddListword, 40, 50)
            false
        }

        binding.btnAddListword.setOnClickListener { view ->
            view.isEnabled = false
            val viewalert = layoutInflater.inflate(R.layout.addword_dialog, null)

            val alert = MaterialAlertDialogBuilder(view.context).apply {
                setView(viewalert)
                setCancelable(false)
            }.create()

            viewalert.findViewById<Button>(R.id.btn_addword_cancel).setOnClickListener {
                alert.dismiss()
            }

            viewalert.findViewById<Button>(R.id.btn_addword_add).setOnClickListener {
                val newWord = checkDialogWord(
                    viewalert,
                    R.id.edt_addword_newword,
                    R.id.edt_addword_meanword,
                    R.id.edt_addword_desc
                )
                if (newWord != null) {
                    if (model.addWord(newWord.first, newWord.second)) {
                        getListWord()
                        adapter?.notifyDataSetChanged()
                        alert.dismiss()
                        showToast("Thêm thành công ${model.getKey(newWord.first)}!")
                    } else
                        showToast("Thêm thất bại ${model.getKey(newWord.first)}!")
                }
            }

            view.animate().apply {
                rotationBy(180f)
                setDuration(250)
                withEndAction {
                    alert.show()
                    view.isEnabled = true
                }
            }.start()
        }
    }

    fun checkDialogWord(
        viewalert: View,
        idKey: Int,
        idMean: Int,
        idDesc: Int,
        oldKey: String? = null
    ): Pair<String, String>? {
        val newWord =
            viewalert.findViewById<EditText>(idKey).text.toString().trim()
        val meanWord =
            viewalert.findViewById<EditText>(idMean).text.toString().trim()
        val descWord =
            viewalert.findViewById<EditText>(idDesc).text.toString().trim()

        if (newWord.isBlank() || meanWord.isBlank()) {
            showToast("Hãy nhập đầy đủ thông tin!")
            return null
        }

        if ("$newWord$meanWord$descWord".any { it in "./|" }) {
            showToast("Từ không được chứa các ký tự '|' và '.' và '/'")
            return null
        }

        val fullKey = "$newWord . /$descWord/"
        if (model.dictionary.containsKey(fullKey) && fullKey != oldKey) {
            showToast("Đã tồn tại từ $newWord!")
            return null
        }
        if (model.dictionary[fullKey] == meanWord)
            return null
        return Pair(fullKey, meanWord)
    }

    private fun showToast(mess: String) {
        toast?.cancel()
        context?.let {
            toast = Toast.makeText(context, mess, Toast.LENGTH_LONG)
            toast?.show()
        }
    }

    fun onSearch(text: String? = binding.svSearchListword.query.toString()) {
        val query = text?.trim() ?: ""
        list.clear()
        if (query.isBlank())
            getListWord()
        else {
            val listSearch = model.dictionary.filter {
                model.getKey(it.key).contains(query, true)
            }.toMutableMap()
            getListWord(listSearch)
        }
        adapter?.notifyDataSetChanged()
    }

    fun sortListWord(position: Int = 0) {
        when (position) {
            0 -> onSearch()
            1 -> list.sortBy { it.first }
            2 -> list.sortByDescending { it.first }
            3 -> {
                onSearch()
                list.reverse()
            }
        }
        adapter?.notifyDataSetChanged()
    }

    private fun addAdapter(list: List<Triple<String, String, String>>) {
        //Adapter Spiner
        val listSort = listOf(
            R.drawable.ic_sort_old_new,
            R.drawable.ic_sort_a_z,
            R.drawable.ic_sort_z_a,
            R.drawable.ic_sort_new_old
        )
        binding.btnSortListword.apply {
            adapter = Adapter_Sort(requireContext(), R.layout.item_sort, listSort)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    sortListWord(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
        //Adapter RecycleView
        adapter = Adapter_ListWord(list) { view, pos, word ->
            view.backgroundTintList = ColorStateList.valueOf("#FFF6AF".toColorInt())
            val pop = PopupMenu(view.context, view)
            pop.menu.add(0, 0, 0, model.getKey(word)).isEnabled = false
            pop.menu.add(0, 1, 1, "Chi tiết")
            pop.menu.add(0, 2, 2, "Sửa")
            pop.menu.add(0, 3, 3, "Xoá")
            pop.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> menuDetail(model.getE(word).trim())
                    2 -> menuEdit(word, pos)
                    3 -> menuDelete(word, pos)
                }
                true
            }
            pop.setOnDismissListener {
                view.backgroundTintList = null
            }
            pop.show()
        }
        binding.rcListword.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcListword.adapter = adapter
    }

    fun menuDetail(key: String) {
        runCatching {
            val query =
                if (linkFileUser == "DataUserLanguage.txt") "https://m-dict.zlb.zapps.me/en_vn/find?keyword=$key" else "https://www.google.com/search?q=$key+là+gì?"
            startActivity(Intent(Intent.ACTION_VIEW, query.toUri()))
        }.onFailure {
            showToast("Không thể mở liên kết!")
        }
    }

    fun menuDelete(word: String, pos: Int) {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Xác nhận xoá")
            setMessage("Xác nhận xoá từ ${model.getKey(word)}!\nThao tác này không thể hoàn tác!")
            setPositiveButton("Xoá") { _, _ ->
                if (model.eraseWord(word)) {
                    list.removeAt(pos)
                    adapter?.notifyItemRemoved(pos)
                    adapter?.notifyItemRangeChanged(pos, list.size - pos)
                    showToast("Xoá thành công ${model.getKey(word)}!")
                } else
                    showToast("Xoá thất bại ${model.getKey(word)}!")
            }
            setNegativeButton("Huỷ", null)
        }.show()
    }

    fun menuEdit(word: String, pos: Int) {
        val viewalert = layoutInflater.inflate(R.layout.editword_dialog, null)
        viewalert.findViewById<EditText>(R.id.edt_editword_newword).setText(list[pos].first)
        viewalert.findViewById<EditText>(R.id.edt_editword_desc)
            .setText(list[pos].second.replace("/", ""))
        viewalert.findViewById<EditText>(R.id.edt_editword_meanword).setText(list[pos].third)

        val alert = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(viewalert)
            setCancelable(false)
        }.create()
        alert.show()

        viewalert.findViewById<Button>(R.id.btn_editword_cancel).setOnClickListener {
            alert.dismiss()
        }

        viewalert.findViewById<Button>(R.id.btn_editword_save).setOnClickListener {
            val newWord = checkDialogWord(
                viewalert,
                R.id.edt_editword_newword,
                R.id.edt_editword_meanword,
                R.id.edt_editword_desc,
                word
            )
            if (newWord != null) {
                if (model.editWord(word, newWord.first, newWord.second)) {
                    onSearch()
                    sortListWord(binding.btnSortListword.selectedItemPosition)
                    showToast("Cập nhật thành công thành ${model.getKey(newWord.first)}!")
                    alert.dismiss()
                } else
                    showToast("Cập nhật thất bại ${model.getKey(word)}!")
            }
        }

    }

    override fun onDestroyView() {
        _binding = null
        _model = null
        toast?.cancel()
        toast = null
        adapter = null
        super.onDestroyView()
    }

    override fun onPause() {
        super.onPause()
        model.updateData()
    }
}