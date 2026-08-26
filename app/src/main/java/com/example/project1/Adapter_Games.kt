package com.example.project1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.project1.model.Game_Item

class Adapter_Games(private val list: List<Game_Item>, val onClick: (Int) -> Unit) :
    RecyclerView.Adapter<Adapter_Games.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tenGame = view.findViewById<TextView>(R.id.txtTenGame)
        val img = view.findViewById<ImageView>(R.id.imgGame)
    }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gameitem, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener { view ->
            view.isEnabled = false
            view.postDelayed({
                onClick(viewHolder.bindingAdapterPosition)
                view.isEnabled = true
            }, 120)
        }
        return viewHolder
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.tenGame.text = list[position].ten
        holder.img.setImageResource(list[position].img)
        holder.tenGame.setTextColor(list[position].color.toColorInt())
    }
}