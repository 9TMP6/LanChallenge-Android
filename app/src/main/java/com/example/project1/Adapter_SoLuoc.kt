package com.example.project1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class Adapter_SoLuoc(private val list: List<Int>, val onClick: (Int) -> Unit)
    : RecyclerView.Adapter<Adapter_SoLuoc.ViewHolder>(){
    class ViewHolder(view:View): RecyclerView.ViewHolder(view){
        val view = view
        val img = view.findViewById<ImageView>(R.id.imgSoLuocItem)
    }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.soluoc,parent,false)
        val viewHolder= ViewHolder(view)
        viewHolder.itemView.setOnClickListener { onClick(viewHolder.bindingAdapterPosition) }
        return viewHolder
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.img.setImageResource(list[position])
    }
}