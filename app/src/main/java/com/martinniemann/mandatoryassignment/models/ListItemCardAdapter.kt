package com.martinniemann.mandatoryassignment.models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.martinniemann.mandatoryassignment.R

class ListItemCardAdapter(private val items: List<SalesItem>,
                          private val onItemClicked: (position: Int) -> Unit)
    : RecyclerView.Adapter<ListItemCardAdapter.ItemViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int)
    : ListItemCardAdapter.ItemViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_card, viewGroup, false)
        return ItemViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(viewHolder: ItemViewHolder, position: Int) {
        viewHolder.descriptionTextView.text = items[position].description
        viewHolder.priceTextView.text = items[position].price.toString()
    }

    class ItemViewHolder(itemView: View, private val onItemClicked: (position: Int) -> Unit)
        : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val descriptionTextView: TextView = itemView.findViewById(R.id.description)
        val priceTextView: TextView = itemView.findViewById(R.id.price)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val position = bindingAdapterPosition
            onItemClicked(position)
        }
    }
}