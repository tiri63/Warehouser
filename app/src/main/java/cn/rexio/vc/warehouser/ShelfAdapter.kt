package cn.rexio.vc.warehouser

import android.app.AlertDialog
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class ShelfAdapter(
    private var itemList: MutableList<ShelfItem>,
    private val context: Context,
    private val window: Window
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
        holder.itemView.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setCancelable(true)
            val view = window.layoutInflater.inflate(R.layout.dialog_io_layout, null, false)
            builder.setView(view)
            val dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
            var current = 0
            view.findViewById<TextView>(R.id.dialog_left).text = String.format(
                context.getText(R.string.txt_dialog_item_count_left).toString(),
                item.count
            )
            view.findViewById<TextView>(R.id.dialog_title).text = item.name
            view.findViewById<TextView>(R.id.dialog_goods_model).text =
                if (item.model == null) context.getText(R.string.txt_nomodel) else item.model
            view.findViewById<TextView>(R.id.dialog_goods_number).text =
                if (item.uid == null) context.getText(R.string.txt_no_id) else item.uid
            view.findViewById<TextView>(R.id.dialog_shelf).text = item.shelf
            view.findViewById<TextView>(R.id.dialog_for).text =
                if (item.usage == null) context.getText(R.string.txt_nofor) else item.usage
            view.findViewById<TextView>(R.id.dialog_import).setOnClickListener {
                val uid = item.uid ?: "000000"
                if (item.uid != null)
                    HiroUtils.sendRequest("baseURL/import",
                        arrayListOf("method", "shelf", "uid", "count"),
                        arrayListOf("0", item.shelf, uid, view.findViewById<EditText>(R.id.dialog_count).text.toString()), {
                            if (it == "success") {
                                //入库成功
                            } else {
                                //入库失败
                            }
                        }, {
                            //网络问题
                        }, "success"
                    )
                dialog.dismiss()
            }
            view.findViewById<TextView>(R.id.dialog_export).setOnClickListener {
                val uid = item.uid ?: "000000"
                if (item.uid != null)
                    HiroUtils.sendRequest("baseURL/export",
                        arrayListOf("shelf", "uid", "count"),
                        arrayListOf(item.shelf, uid, view.findViewById<EditText>(R.id.dialog_count).text.toString()), {
                            if (it == "success") {
                                //出库成功
                            } else {
                                //出库失败
                            }
                        }, {
                            //网络问题
                        }, "success"
                    )
                dialog.dismiss()
            }
            view.findViewById<TextView>(R.id.dialog_cancel).setOnClickListener {
                dialog.dismiss()
            }
            view.findViewById<ImageView>(R.id.dialog_plus).setOnClickListener {
                if (current < item.count)
                    current++
                view.findViewById<EditText>(R.id.dialog_count).setText(current.toString())
            }
            view.findViewById<ImageView>(R.id.dialog_minus).setOnClickListener {
                if (current > 0)
                    current--
                view.findViewById<EditText>(R.id.dialog_count).setText(current.toString())
            }
            view.findViewById<ImageView>(R.id.dialog_min).setOnClickListener {
                current = 0
                view.findViewById<EditText>(R.id.dialog_count).setText(current.toString())
            }
            view.findViewById<ImageView>(R.id.dialog_max).setOnClickListener {
                current = item.count
                view.findViewById<EditText>(R.id.dialog_count).setText(current.toString())
            }


        }
    }

    override fun getItemCount(): Int = itemList.size

    inner class ShelfViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameView: TextView = itemView.findViewById(R.id.ui_shelf_item_name)
        val countView: TextView = itemView.findViewById(R.id.ui_shelf_item_count)
        val modelView: TextView = itemView.findViewById(R.id.ui_goods_model)
        val goodsno: TextView = itemView.findViewById(R.id.ui_goods_number)
        val shelfView: TextView = itemView.findViewById(R.id.ui_shelf_id)
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
        var usage: String?
    ) {
    }

}