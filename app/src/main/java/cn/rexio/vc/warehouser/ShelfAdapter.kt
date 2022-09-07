package cn.rexio.vc.warehouser

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class ShelfAdapter(
    private val nameList: List<String>,
    private val countList: List<Int>
) : RecyclerView.Adapter<ShelfAdapter.ShelfViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShelfViewHolder {

        return ShelfViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_shelf_item, parent, false))

    }

    override fun onBindViewHolder(holder: ShelfViewHolder, position: Int) {
        holder.nameView.text = nameList[position]
        holder.countView.text = countList[position].toString()
        holder.itemView.setOnClickListener({

        })
    }

    override fun getItemCount(): Int = nameList.size

    inner class ShelfViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameView: TextView = itemView.findViewById(R.id.ui_shelf_item_name)
        val countView: TextView = itemView.findViewById(R.id.ui_shelf_item_count)

        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }
    }

}