package com.example.project1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter_ListWord(
    private val list: List<Triple<String,String,String>>,
    val onClick: (View,Int ,String) -> Unit
) :
    RecyclerView.Adapter<Adapter_ListWord.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val key = view.findViewById<TextView>(R.id.txt_word_key)
        val mean = view.findViewById<TextView>(R.id.txt_word_mean)
        val ipa = view.findViewById<TextView>(R.id.txt_word_ipa)
    }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_word, parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnLongClickListener { view ->
            val pos = holder.bindingAdapterPosition
            if(pos != RecyclerView.NO_POSITION){
                val word = list[pos].first + " . " + list[pos].second
                onClick(view,pos,word)
            }
            true
        }
        return holder
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.key.text = list[position].first.trim()
        holder.ipa.text = list[position].second.trim()
        holder.mean.text = list[position].third.trim()
    }
}