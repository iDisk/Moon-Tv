package com.moontv.application.player.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.moontv.application.R
import com.moontv.application.model.SubTitle
import com.moontv.application.player.OnChangeSubtitleListener


class ChooseSubtitleAdapter(
    val context: Context?,
    val list: MutableList<SubTitle>,
    private val subtitleListener: OnChangeSubtitleListener
) :

    RecyclerView.Adapter<ChooseSubtitleAdapter.Item>() {

    class Item(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView: TextView = itemView.findViewById(R.id.textView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Item {

        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subtitle, parent, false)
        return Item(itemView)
    }

    override fun onBindViewHolder(holder: Item, position: Int) {
        holder.textView.text = list[position].language
        if (list[position].selected) {
            holder.itemView.requestFocus()
            holder.imageView.visibility = View.VISIBLE
        } else {
            holder.imageView.visibility = View.INVISIBLE
        }

        holder.itemView.setOnClickListener {
            subtitleListener.onChangeSubTitle(true, otherAction = false, pos = position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}
