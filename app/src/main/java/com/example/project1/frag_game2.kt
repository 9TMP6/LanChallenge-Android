package com.example.project1

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.project1.databinding.FragmentFragGame2Binding
import com.example.project1.model.DictionaryModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class frag_game2 : Fragment(R.layout.fragment_frag_game2) {
    private var _binding: FragmentFragGame2Binding? = null
    private var _model: DictionaryModel? = null
    private val binding get() = _binding!!
    private val model get() = _model!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFragGame2Binding.bind(view)
        val linkFileUser = arguments?.getString("LINKFILEUSER") ?: "DataUserLanguage.txt"
        val streak = arguments?.getInt("STREAK", 0)
        val startPoint = arguments?.getInt("START",0) ?: 0
        _model = DictionaryModel(requireContext(), linkFileUser,startPoint)
        model.addData()
        binding.pgScore.max = if (model.listWords.size > 250) 250 else model.listWords.size
        updateView()
        setOnClickButton()
        if (streak == 0) {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle("Giới Thiệu Trò Chơi")
                setIcon(R.drawable.ic_title_alert_intro_g2)
                setMessage(
                    """
                Chào mừng đến với thử thách từ vựng!
                
                1. Quan sát từ ý nghĩa hiển thị trên khung.
                2. Điền từ đúng vào phía dưới.
                3. Bấm kiểm tra và chờ kết quả thôi!
            """.trimIndent()
                )
                setPositiveButton("Bắt đầu chơi!", null)
                setCancelable(false)
                show()
            }
        }
    }

    fun setOnClickButton() {
        binding.btnClose.setOnClickListener { view ->
            view.postDelayed({ parentFragmentManager.popBackStack() }, 100)
        }
        binding.btnKiemTra.setOnClickListener { onClickKiemTra() }
        binding.swMoreDifficult.setOnCheckedChangeListener { _, isChecked ->
            binding.txtIPAG2.visibility = if (isChecked)
                View.GONE
            else
                View.VISIBLE
        }
    }

    fun onClickKiemTra() {
        if (binding.btnKiemTra.tag.toString() == "continue") {
            changeKiemTraButton("check")
            binding.lAnsG2.animate().apply {
                translationY(binding.lAnsG2.height.toFloat())
                setDuration(100)
                start()
            }
            updateView()
            return
        }
        binding.edtKeyG2.isEnabled = false
        val key = binding.edtKeyG2.text.toString().lowercase().trim()
        val ans = model.getE().lowercase().trim() == key
        val listAns = model.getlistMeanAns(key)
        if (ans) {
            model.score++
            binding.txtContentG2.setBackgroundResource(R.drawable.btn_true)
            binding.edtKeyG2.setTextColor("#52c234".toColorInt())
        } else {
            if (model.score > 0) model.score-- else 0
            binding.txtContentG2.setBackgroundResource(R.drawable.btn_false)
            binding.edtKeyG2.setTextColor("#CC1111".toColorInt())
        }
        changeKiemTraButton("continue", ans)
        changeAns(listAns, ans)
    }

    fun changeAns(list: List<String>, ans: Boolean) {
        val color = if (ans) "#52c234".toColorInt() else "#CC1111".toColorInt()
        binding.txtLableKetqua.setTextColor(color)
        binding.txtKetqua.setTextColor(color)
        binding.txtThongbao.setTextColor(color)
        binding.txtThongbao.text = if (ans) "Chính xác!" else "Sai rồi"
        val tuongtu = list.drop(1).joinToString(", ") { it.replaceFirstChar { c -> c.uppercase() } }
        if (tuongtu.isNotBlank()) {
            tuongtu.replaceFirstChar { "" }
            binding.txtKetquaTuongtu.text = "Có liên quan: $tuongtu."
            binding.txtKetquaTuongtu.visibility = View.VISIBLE
        } else
            binding.txtKetquaTuongtu.visibility = View.GONE
        binding.txtKetqua.text = list[0].replaceFirstChar { it.uppercase() }
        binding.lAnsG2.animate().apply {
            translationY(-20f)
            setDuration(250)
            withEndAction {
                binding.lAnsG2.animate()
                    .translationY(0f)
                    .setDuration(150)
                    .start()
            }
            start()
        }
    }

    private fun changeKiemTraButton(tag: String, ans: Boolean = false) {
        val btn = binding.btnKiemTra
        btn.tag = tag
        btn.setTextColor("#EEEEEE".toColorInt())
        if (tag == "check") {
            btn.text = "Kiểm Tra"
            btn.setBackgroundResource(R.drawable.btn_game)
            btn.setTextColor("#80CCCCCC".toColorInt())
        } else if (ans) {
            btn.text = "Tiếp Tục"
            btn.setBackgroundResource(R.drawable.btn_kiemtra_true)
        } else {
            btn.text = "Tiếp Tục"
            btn.setBackgroundResource(R.drawable.btn_kiemtra_false)
        }
    }

    private fun onEndGame() {
        val score = model.score
        val sumWord = model.listCheckedWords.size
        val percent = (score.toFloat() / sumWord * 100).toInt()
        val view = layoutInflater.inflate(R.layout.end_game, null)
        view.findViewById<TextView>(R.id.txtScoreEnd).text = "$score/$sumWord"
        view.findViewById<TextView>(R.id.txtPercentEnd).text = "$percent%"
        val alert = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(view)
            setCancelable(false)
        }.create()

        view.findViewById<Button>(R.id.btnHomeEnd).setOnClickListener { view ->
            view.postDelayed({
                parentFragmentManager.popBackStack()
                alert.dismiss()
            }, 100)
        }
        alert.show()
    }

    private fun updateView() {
        binding.txtScore.text = model.score.toString()
        binding.pgScore.progress = model.score
        binding.edtKeyG2.setTextColor("#EEEEEE".toColorInt())
        binding.txtContentG2.setBackgroundResource(R.drawable.btn_game)
        binding.edtKeyG2.text.clear()
        streakFire()

        val list = model.createPlay()
        if (list.isEmpty()) {
            if (model.listWords.size < 4) {
                MaterialAlertDialogBuilder(requireContext()).apply {
                    setTitle("Thông báo không đủ điều kiện")
                    setMessage("Hãy thêm ít nhất 4 từ vào từ điển của bạn để bắt đầu game!")
                    setPositiveButton("Đã hiểu") { _, _ ->
                        parentFragmentManager.popBackStack()
                    }
                    setCancelable(false)
                }.show()
                return
            } else {
                onEndGame()
                return
            }
        }
        binding.txtMeanG2.text = model.dictionary[model.word]?.trim()
        binding.txtIPAG2.text = model.getIPA()
        binding.edtKeyG2.hint = "từ nào có nghĩa ${binding.txtMeanG2.text}..."
        binding.edtKeyG2.isEnabled = true
    }

    private fun streakFire() {
        val score = model.score
        if (score <= 0) {
            binding.pgScore.secondaryProgress = 0
            binding.imgFire.isVisible = false
            binding.imgFire.tag = "#444444".toColorInt()
            changeColorStreak("#444444".toColorInt())
            return
        }
        binding.imgFire.isVisible = true
        val (color, target) = when {
            score <= 20 -> Pair("#70EAFC", 20)
            score <= 40 -> Pair("#3B82F6", 40)
            score <= 80 -> Pair("#A855F7", 80)
            score <= 160 -> Pair("#FF2E93", 160)
            else -> Pair("#FFD700", 250)
        }
        if (color.toColorInt() != binding.imgFire.tag) {
            changeColorStreak(color.toColorInt())
            binding.pgScore.secondaryProgress = target
        }
    }

    private fun changeColorStreak(color: Int) {
        binding.pgScore.progressTintList =
            android.content.res.ColorStateList.valueOf(color)
        binding.txtScore.setTextColor(color)
        binding.txtLabelContent.setTextColor(color)
        binding.imgFire.setColorFilter(color)
        binding.pgScoreDot.progressTintList =
            android.content.res.ColorStateList.valueOf(color)
        binding.imgFire.tag = color
        animationStreak(binding.pgScoreDot)
        animationStreak(binding.pgScore, 100)
        animationStreak(binding.txtScore, 250)
        animationStreak(binding.imgFire, 400)
    }

    private fun animationStreak(view: View, delay: Long = 0L) {
        view.animate().cancel()
        view.animate().apply {
            scaleX(1.3f)
            scaleY(1.3f)
            setDuration(150)
            setStartDelay(delay)
            withEndAction {
                view.animate()
                    .scaleX(0.97f)
                    .scaleY(0.97f)
                    .setDuration(150)
                    .withEndAction {
                        view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(300)
                            .start()
                    }
                    .start()
            }
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _model = null
    }
}