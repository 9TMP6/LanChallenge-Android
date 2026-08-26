package com.example.project1

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.project1.databinding.FragmentFragGame1Binding
import com.example.project1.model.DictionaryModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class frag_game1 : Fragment(R.layout.fragment_frag_game1) {

    private var _binding: FragmentFragGame1Binding? = null
    private var _model: DictionaryModel? = null
    private val binding
        get() = _binding!!
    private val model
        get() = _model!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFragGame1Binding.bind(view)
        val linkFileUser = arguments?.getString("LINKFILEUSER") ?: "DataUserLanguage.txt"
        val streak = arguments?.getInt("STREAK", 0)
        val startPoint = arguments?.getInt("START",0) ?: 0

        _model = DictionaryModel(requireContext(),linkFileUser,startPoint)
        model.addData()
        val listButton =
            listOf(binding.btnOneG1, binding.btnTwoG1, binding.btnThreeG1, binding.btnFourG1)
        binding.pgScore.max = if (model.listWords.size > 250) 250 else model.listWords.size
        updateView(listButton)
        //setonclickClose
        binding.btnClose.setOnClickListener { view ->
            view.postDelayed({ parentFragmentManager.popBackStack() }, 100)
        }

        //setOnClickKiemTre
        binding.btnKiemTra.setOnClickListener { onClickKiemTra(listButton) }

        //setOnClickAns
        for (x in listButton)
            x.setOnClickListener { onClickAns(listButton, x) }

        if (streak == 0) {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle("Giới Thiệu Trò Chơi")
                setIcon(R.drawable.ic_title_alert_intro_g1)
                setMessage(
                    """
                Chào mừng đến với thử thách từ vựng!

                1. Quan sát từ vựng hiển thị trên màn hình.
                2. Chọn đáp án đúng trong 4 lựa chọn bên dưới.
                3. Chinh phục các câu hỏi cho đến khi kiệt sức!
            """.trimIndent()
                )
                setPositiveButton("Bắt đầu chơi!", null)
                setCancelable(false)
                show()
            }
        }
    }

    private fun onClickAns(listButton: List<Button>, view: View) {
        for (x in listButton) {
            if (x == view) {
                x.isSelected = true
                x.setBackgroundResource(R.drawable.btn_game_selected)
                continue
            }
            x.setBackgroundResource(R.drawable.btn_game)
            x.isSelected = false
        }
    }

    private fun onClickKiemTra(listButton: List<Button>) {
        if (binding.btnKiemTra.tag.toString() == "continue") {
            changeKiemTraButton("check")
            updateView(listButton)
            return
        }
        val view = listButton.firstOrNull { it.isSelected } ?: return
        var ans = false
        for (x in listButton) {
            x.isEnabled = false
            x.isSelected = false
            if (model.checkWord(x.text.toString())) {
                x.setTextColor("#52C234".toColorInt())
                x.setBackgroundResource(R.drawable.btn_true)
                if (x == view) {
                    ans = true
                    model.score++
                    binding.txtContent.setBackgroundResource(R.drawable.btn_true)
                }
                continue
            }
            if (x == view) {
                view.setTextColor("#CC1111".toColorInt())
                if (model.score > 0) model.score--
                view.setBackgroundResource(R.drawable.btn_false)
                binding.txtContent.setBackgroundResource(R.drawable.btn_false)
                continue
            }
            x.setTextColor(Color.DKGRAY)
        }
        changeKiemTraButton("continue", ans)
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
            binding.txtLabelContent.text = "làm tốt lắm !"
        } else {
            btn.text = "Tiếp Tục"
            btn.setBackgroundResource(R.drawable.btn_kiemtra_false)
            binding.txtLabelContent.text = "hãy nhớ kỹ từ đó !"
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

    fun updateView(listButton: List<Button>) {
        binding.txtContent.setBackgroundResource(R.drawable.btn_game)
        binding.txtLabelContent.text = "bạn biết từ này chứ?"
        binding.txtScore.text = model.score.toString()
        binding.pgScore.progress = model.score
        streakFire()

        val listAns = model.createPlay()
        if (listAns.isEmpty()) {
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
        binding.txtKeyG1.text = model.getKey().trim()
        binding.txtIPAG1.text = model.getIPA().trim()

        for (i in listButton.indices) {
            listButton[i].apply {
                text = listAns[i]
                setTextColor("#EEEEEE".toColorInt())
                setBackgroundResource(R.drawable.btn_game)
                isEnabled = true
            }
        }

    }

    private fun changeColorStreak(color: Int) {
        binding.pgScore.progressTintList =
            android.content.res.ColorStateList.valueOf(color)
        binding.txtScore.setTextColor(color)
        binding.imgFire.setColorFilter(color)
        binding.pgScoreDot.progressTintList =
            android.content.res.ColorStateList.valueOf(color)
        binding.imgFire.tag = color
        animationStreak(binding.pgScoreDot)
        animationStreak(binding.pgScore, 100)
        animationStreak(binding.txtScore, 250)
        animationStreak(binding.imgFire, 400)
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
        val (color, target) = if (score <= 20) Pair("#68E447", 20)
        else if (score <= 40) Pair("#FFC107", 40)
        else if (score <= 80) Pair("#FF9800", 80)
        else if (score <= 160) Pair("#00BFFF", 160)
        else Pair("#6E1CFF", 250)
        if (color.toColorInt() != binding.imgFire.tag) {
            changeColorStreak(color.toColorInt())
            binding.pgScore.secondaryProgress = target
        }
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
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(300)
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