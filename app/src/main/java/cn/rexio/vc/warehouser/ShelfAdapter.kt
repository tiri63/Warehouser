package cn.rexio.vc.warehouser

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
    private val type : Int
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
        holder.countView.text = item.count.toString()
        holder.modelView.text =
            if (item.model == null) context.getText(R.string.txt_nomodel) else item.model
        holder.goodsno.text =
            if (item.uid == null) context.getText(R.string.txt_no_id) else item.uid
        holder.shelfView.text = item.shelf
        holder.unitView.text = item.unit
        holder.itemView.setOnClickListener {
            val intent = Intent(context,IOActivity::class.java)
            intent.putExtra("maxNum",item.count)
            intent.putExtra("name",item.name)
            intent.putExtra("model",if (item.model == null) context.getText(R.string.txt_nomodel) else item.model)
            intent.putExtra("uid",if (item.uid == null) context.getText(R.string.txt_no_id) else item.uid)
            intent.putExtra("shelf.main","主货架")
            intent.putExtra("shelf.sub","次货架")
            intent.putExtra("shelf.alias",item.shelf)
            intent.putExtra("shelf.info","货架信息")
            intent.putExtra("usage.code",0)
            intent.putExtra("usage.alias",if (item.usage == null) context.getText(R.string.txt_nofor) else item.usage)
            intent.putExtra("usage.info","用途信息")
            startActivity(context,intent,null)
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
        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }
    }

    class ShelfItem(
        var name: String,
        var count: Int,
        var model: String?,
        var uid: String?,
        var shelf: String,
        var usage: String?,
        var unit: String?
    ) {
    }

}