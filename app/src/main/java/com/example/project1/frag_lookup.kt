package com.example.project1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.project1.databinding.FragmentFragLookupBinding
import com.example.project1.model.DictionaryModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class frag_lookup : Fragment(R.layout.fragment_frag_lookup) {
    private var _binding: FragmentFragLookupBinding? = null
    private var _model: DictionaryModel? = null
    private val model get() = _model!!
    private val binding get() = _binding!!
    private var _toast: Toast? = null
    private val toast get() = _toast!!
    private var density = 0f
    private var linkFileUser = "DataUserLanguage.txt"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFragLookupBinding.bind(view)
        linkFileUser = arguments?.getString("LINKFILEUSER") ?: "DataUserLanguage.txt"
        _model = DictionaryModel(requireContext(), linkFileUser)
        density = resources.displayMetrics.density
        _toast = Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT)
        model.addData()

        onClickButton()
    }

    fun onClickButton() {

        binding.btnClose.setOnClickListener { view ->
            view.postDelayed({ parentFragmentManager.popBackStack() }, 100)
        }
        binding.btnDetailLookup.setOnClickListener { detail() }
        binding.btnDetailEmptyLookup.setOnClickListener { detail() }
        binding.btnSearchLookup.setOnClickListener { view ->
            animationGone(binding.lEmptyLookup)
            animationGone(binding.lResultLookup)
            val searchText = binding.edtLookup.text.toString().trim()
            if (searchText.isBlank()) return@setOnClickListener

            view.isEnabled = false
            binding.edtLookup.isEnabled = false
            val word = onSearch(searchText)
            viewLifecycleOwner.lifecycleScope.launch {
                delay(500)
                if (word != null) {
                    binding.txtKeyLookup.text = word.first.replaceFirstChar { it.uppercase() }
                    binding.txtIpaLookup.text = word.second
                    binding.txtMeanLookup.text = word.third.replaceFirstChar { it.uppercase() }
                    animationVisibleResult()

                    binding.btnEditLookup.setOnClickListener { editWord(word) }
                    binding.btnDeleteLookup.setOnClickListener { deleteWord(word) }
                } else {
                    binding.txtEmptyLookup.text = "\"$searchText\" không có trong từ điển của bạn."
                    binding.lEmptyLookup.animate().apply {
                        translationY(0f)
                        setDuration(800)
                        start()
                    }
                }
                view.isEnabled = true
                binding.edtLookup.isEnabled = true
            }
        }
    }

    fun editWord(word: Triple<String, String, String>) {
        val viewalert = layoutInflater.inflate(R.layout.editword_dialog, null)
        val newKey = viewalert.findViewById<EditText>(R.id.edt_editword_newword)
        val newIpa = viewalert.findViewById<EditText>(R.id.edt_editword_desc)
        val newMean = viewalert.findViewById<EditText>(R.id.edt_editword_meanword)
        val oldKey = word.first + " . " + word.second

        newKey.setText(word.first)
        newIpa.setText(word.second.replace("/", ""))
        newMean.setText(word.third)

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
                newKey.text.toString(),
                newIpa.text.toString(),
                newMean.text.toString(),
                oldKey
            )
            if (newWord != null) {
                toast.cancel()
                if (model.editWord(oldKey, newWord.first, newWord.second)) {
                    toast.setText("Cập nhật thành công thành ${model.getKey(newWord.first)}!")
                    binding.edtLookup.text.clear()
                    animationGone(binding.lResultLookup)
                    alert.dismiss()
                } else
                    toast.setText("Cập nhật thất bại ${model.getKey(oldKey)}!")
                toast.show()
            }
        }

    }

    fun deleteWord(word: Triple<String, String, String>) {
        toast.cancel()
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Xác nhận xoá")
            setMessage("Xác nhận xoá từ ${word.first}!\nThao tác này không thể hoàn tác!")
            setPositiveButton("Xoá") { _, _ ->
                if (model.eraseWord("${word.first} . ${word.second}")) {
                    toast.setText("Xoá thành công ${word.first}!")
                    binding.edtLookup.text.clear()
                    animationGone(binding.lResultLookup)
                } else
                    toast.setText("Xoá thất bại ${word.first}!")
                toast.show()
            }
            setNegativeButton("Huỷ", null)
        }.show()
    }

    fun checkDialogWord(
        newWord: String, descWord: String, meanWord: String, oldKey: String
    ): Pair<String, String>? {

        if (newWord.isBlank() || meanWord.isBlank()) {
            toast.cancel()
            toast.setText("Hãy nhập đầy đủ thông tin!")
            toast.show()
            return null
        }

        if ("$newWord$meanWord$descWord".any { it in "./|" }) {
            toast.cancel()
            toast.setText("Từ không được chứa các ký tự '|' và '.' và '/'")
            toast.show()
            return null
        }

        val fullKey = "$newWord . /$descWord/"
        if (fullKey != oldKey && model.dictionary.containsKey(fullKey)) {
            toast.cancel()
            toast.setText("Đã tồn tại từ $newWord!")
            toast.show()
            return null
        }
        if (model.dictionary[fullKey] == meanWord)
            return null
        return Pair(fullKey, meanWord)
    }

    fun animationVisibleResult() {
        val view = binding.lResultLookup
        binding.lWord.visibility = View.GONE
        binding.txtMeanLookup.visibility = View.GONE
        binding.lButtonLookup.visibility = View.GONE
        view.animate().apply {
            translationY(0f)
            setDuration(800)
            withEndAction {
                binding.lWord.visibility = View.VISIBLE
                binding.txtMeanLookup.visibility = View.GONE
                binding.txtMeanLookup.animate().apply {
                    withStartAction { binding.txtMeanLookup.visibility = View.VISIBLE }
                    translationY(125f * density)
                    setDuration(500)
                    withEndAction {
                        binding.lButtonLookup.visibility = View.VISIBLE
                        binding.lButtonLookup.animate().apply {
                            translationY(235f * density)
                            setDuration(400)
                            start()
                        }
                    }
                    start()
                }
            }
            start()
        }

    }

    fun animationGone(view: View) {
        view.animate().apply {
            translationY(800f * density)
            setDuration(500)
            start()
        }
    }

    private fun onSearch(word: String): Triple<String, String, String>? {
        val target = word.lowercase()
        val ans = model.dictionary.entries.find { (key, value) ->
            model.getE(key).lowercase() == target
        } ?: return null

        val key = model.getKey(ans.key).trim()
        val desc = model.getIPA(ans.key).trim()
        val mean = ans.value.trim()
        return Triple(key, desc, mean)
    }


    fun detail(word: String = binding.edtLookup.text.toString().trim()) {
        if (word.isBlank()) return
        runCatching {
            val query =
                if (linkFileUser == "DataUserLanguage.txt") "https://m-dict.zlb.zapps.me/en_vn/find?keyword=$word" else "https://www.google.com/search?q=$word+là+gì?"
            startActivity(Intent(Intent.ACTION_VIEW, query.toUri()))
        }.onFailure {
            toast.cancel()
            toast.setText("Không thể mở trình duyệt!")
        }
    }

    override fun onPause() {
        super.onPause()
        model.updateData()
    }

    override fun onDestroyView() {
        _model = null
        _binding = null
        _toast?.cancel()
        _toast = null
        super.onDestroyView()
    }
}