package com.example.project1.model

import android.content.Context
import android.widget.Toast
import java.io.File

class DictionaryModel(
    private val context: Context,
    private val linkFileUserData: String = "DataUserLanguage.txt",
    private val start:Int = 0
) {
    //start [0;size-1]
    private val linkFile: String = "Dictionary.txt"
    var word: String = ""
        get() = field.trim()
    val dictionary = LinkedHashMap<String, String>()
    val listWords = mutableListOf<String>()
    val listCheckedWords = mutableListOf<String>()
    var score = 0
    var changed = false

    fun checkWord(mean: String): Boolean {
        return dictionary[word] == mean
    }

    fun createPlay(): List<String> {
        if (listCheckedWords.size == listWords.size || dictionary.size < 4)
            return emptyList()
        var keyWord: String
        do {
            keyWord = listWords.random()
        } while (listCheckedWords.contains(keyWord))
        word = keyWord
        listCheckedWords.add(keyWord)
        val listMean = mutableListOf(dictionary[keyWord].toString())
        while (listMean.size < 4) {
            val m = dictionary[listWords.random()].toString()
            if (!listMean.contains(m))
                listMean.add(m)
        }
        listMean.shuffle()
        return listMean
    }

    fun addWord(key: String, mean: String): Boolean {
        if (dictionary[key] == null && !listWords.contains(key)) {
            dictionary.put(key.trim(), mean.trim())
            listWords.add(key.trim())
            changed = true
            return true
        }
        return false
    }

    fun eraseWord(key: String): Boolean {
        if (dictionary[key] == null)
            return false
        dictionary.remove(key)
        listWords.remove(key)
        changed = true
        return true
    }

    fun getIPA(w: String = this.word): String {
        return w.split(".").getOrNull(1) ?: ""
    }

    fun getKey(w: String = this.word): String {
        return w.split(".").getOrNull(0) ?: ""
    }

    fun getE(w: String = this.word): String {
        val key = getKey(w)
        return key.substringBefore("(", key).trim()
    }

    fun editWord(oldKey: String, newKey: String, newMean: String): Boolean {
        if (!dictionary.containsKey(oldKey)) return false

        if (oldKey == newKey) {
            dictionary[oldKey] = newMean
        } else {
            listWords[listWords.indexOf(oldKey)] = newKey
            val newListDic = LinkedHashMap<String, String>()
            for ((key, mean) in dictionary) {
                if (key == oldKey)
                    newListDic.put(newKey, newMean)
                else
                    newListDic.put(key, mean)
            }
            dictionary.clear()
            dictionary.putAll(newListDic)
        }
        changed = true
        return true
    }

    fun getlistMeanAns(w: String): List<String> {
        val list = mutableListOf(getKey())
        if (w.isBlank()) return list
        for (x in listWords) {
            if (list.size >= 5) break
            if (getE(x).contains(w) && !list.contains(getKey(x)))
                list.add(getKey(x).trim())
        }
        return list
    }

    fun addData() {
        var dic = mutableListOf<String>()
        val fileDataUser = File(context.filesDir, this.linkFileUserData)
        if (fileDataUser.exists()) {
            dic = fileDataUser.readLines().toMutableList()
        } else {
            if(linkFileUserData=="DataUserLanguage.txt"){
                runCatching {
                    context.assets.open(linkFile).bufferedReader().useLines { lines ->
                        dic = lines.toMutableList()
                    }
                }
                    .onFailure { exception ->
                        Toast.makeText(
                            context,
                            "Đọc file $linkFile không thành công !",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }
            }
        }

        runCatching {
            var count = 0
            for (i in start until dic.size) {
                if (dic[i].isBlank()) continue
                val s = dic[i].split('|')
                val key = s.getOrNull(0) ?: ""
                val mean = s.getOrNull(1) ?: ""
                if (dictionary[key.trim()] != null)
                    count++
                else
                    addWord(key, mean)
            }
            if(linkFileUserData=="DataUserLanguage.txt" && !fileDataUser.exists())
                updateData()
            changed = false
            if (count > 0)
                Toast.makeText(context, "Có $count từ bị trùng!", Toast.LENGTH_SHORT).show()
        }
            .onFailure {
                Toast.makeText(context, "Tiến trình đổ dữ liệu phát sinh lỗi !", Toast.LENGTH_LONG)
                    .show()
                return
            }
    }

    fun updateData() {
        if (!changed)
            return
        runCatching {
            val fileUserData = File(context.filesDir, this.linkFileUserData)
            fileUserData.bufferedWriter().use { out ->
                for ((key, mean) in dictionary) {
                    out.write("$key | $mean")
                    out.newLine()
                }
            }
            changed = false
        }
            .onFailure {
                Toast.makeText(context, "Lỗi lưu file!", Toast.LENGTH_LONG).show()
            }
    }
}