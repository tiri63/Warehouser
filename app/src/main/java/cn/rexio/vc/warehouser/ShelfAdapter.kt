package cn.rexio.vc.warehouser

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity

/**@param
 * type:0 - Normal,1 - UID Search View
 */
class ShelfAdapter(
    private var itemList: MutableList<ShelfItem>,
    private val context: Context,
    private val window: Window,
    private val type: Int,
    private var onPress: (ShelfItem) -> Unit,
) : RecyclerView.Adapter<ShelfAdapter.ShelfViewHolder>() {

    fun clear() {
        itemList.clear()
    }

    fun add(shelfItem: ShelfItem) {
        itemList.add(shelfItem)
        this.notifyItemInserted(itemList.size - 1)
    }

    fun delete(position: Int) {
        itemList.removeAt(position)
        this.notifyItemRemoved(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAdapter(newItemList: MutableList<ShelfItem>) {
        itemList = newItemList
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShelfViewHolder {

        return ShelfViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_shelf_item, parent, false)
        )

    }

    override fun onBindViewHolder(holder: ShelfViewHolder, position: Int) {
        val pos = holder.bindingAdapterPosition
        val item = itemList[pos]
        holder.nameView.text = item.name

        holder.modelView.text = item.model ?: context.getText(R.string.txt_nomodel)
        holder.goodsno.text = item.uid ?: context.getText(R.string.txt_no_id)
        holder.shelfView.text = "${item.shelf.main}-${item.shelf.sub}(${item.shelf.alias})"
        holder.departView.text = item.depart ?: context.getText(R.string.txt_nodepart)

        holder.itemView.setOnClickListener {
            onPress.invoke(item)
        }
        if (type == 1) {
            holder.shelfView.visibility = View.INVISIBLE
            holder.countView.visibility = View.INVISIBLE
            holder.unitView.visibility = View.INVISIBLE
            holder.departView.visibility = View.INVISIBLE
        } else {
            holder.shelfView.visibility = View.VISIBLE
            holder.countView.visibility = View.VISIBLE
            holder.unitView.visibility = View.VISIBLE
            holder.departView.visibility = View.VISIBLE
            holder.countView.text = item.count.toString()
            holder.unitView.text = item.unit ?: ""
        }
    }

    override fun getItemCount(): Int = itemList.size

    inner class ShelfViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameView: TextView = itemView.findViewById(R.id.ui_shelf_item_name)
        val countView: TextView = itemView.findViewById(R.id.ui_shelf_item_count)
        val modelView: TextView = itemView.findViewById(R.id.ui_goods_model)
        val goodsno: TextView = itemView.findViewById(R.id.ui_goods_number)
        val shelfView: TextView = itemView.findViewById(R.id.ui_shelf_id)
        val unitView: TextView = itemView.findViewById(R.id.ui_shelf_item_unit)
        val departView: TextView = itemView.findViewById(R.id.ui_goods_department)
        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }
    }

    class ShelfItem(
        var name: String,
        var count: Int,
        var model: String?,
        var uid: String?,
        var shelf: HiroUtils.Shelf,
        var usage: String?,
        var unit: String?,
        var depart: String?
    ) {
    }

}