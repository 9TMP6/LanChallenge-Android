package com.example.project1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class Adapter_ChangeDoc(context: Context, val layoutId: Int, val list: List<String>) :
    ArrayAdapter<String>(context, layoutId, list) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(layoutId, parent, false)
        view.findViewById<TextView>(R.id.txt_item_TenDoc).text = list[position]
        return view
    }

    override fun getDropDownView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View? {
        return getView(position, convertView, parent)
    }
}