package cn.rexio.vc.warehouser

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
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
    private var nameList: List<String>,
    private var countList: List<Int>,
    private var modelList: List<String?>,
    private var goodsnoList: List<String?>,
    private var shelfList : List<String>,
    private var forList : List<String?>,
    private val context: Context,
    private val window: Window
) : RecyclerView.Adapter<ShelfAdapter.ShelfViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShelfViewHolder {

        return ShelfViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_shelf_item, parent, false)
        )

    }

    override fun onBindViewHolder(holder: ShelfViewHolder, position: Int) {
        var pos = holder.bindingAdapterPosition
        holder.nameView.text = nameList[pos]
        holder.countView.text = countList[pos].toString()
        holder.modelView.text = if(modelList[pos]==null) context.getText(R.string.txt_nomodel) else modelList[pos]
        holder.goodsno.text = if(goodsnoList[pos]==null) context.getText(R.string.txt_no_id) else goodsnoList[pos]
        holder.shelfView.text = shelfList[pos]
        holder.itemView.setOnClickListener{
            val builder = AlertDialog.Builder(context)
            builder.setCancelable(true)
            val view = window.layoutInflater.inflate(R.layout.dialog_io_layout, null, false)
            builder.setView(view)
            val dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
            var current = 0
            view.findViewById<TextView>(R.id.dialog_left).text = String.format(context.getText(R.string.txt_dialog_item_count_left).toString(),countList[pos])
            view.findViewById<TextView>(R.id.dialog_title).text = nameList[pos]
            view.findViewById<TextView>(R.id.dialog_goods_model).text = modelList[pos]
            view.findViewById<TextView>(R.id.dialog_goods_number).text = goodsnoList[pos]
            view.findViewById<TextView>(R.id.dialog_shelf).text = shelfList[pos]
            view.findViewById<TextView>(R.id.dialog_for).text = if(forList[pos]==null) context.getText(R.string.txt_nofor) else forList[pos]
            view.findViewById<TextView>(R.id.dialog_import).setOnClickListener {
                Toast.makeText(context,"尝试入库" + nameList[pos] + " " + view.findViewById<EditText>(R.id.dialog_count).text + " 个",Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            view.findViewById<TextView>(R.id.dialog_export).setOnClickListener {
                Toast.makeText(context,"尝试出库" + nameList[pos] + " " + view.findViewById<EditText>(R.id.dialog_count).text + " 个（无数量验证）",Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            view.findViewById<TextView>(R.id.dialog_cancel).setOnClickListener {
                dialog.dismiss()
            }
            view.findViewById<ImageView>(R.id.dialog_plus).setOnClickListener {
                if(current < countList[pos])
                    current++
                view.findViewById<EditText>(R.id.dialog_count).setText(current.toString())
            }
            view.findViewById<ImageView>(R.id.dialog_minus).setOnClickListener {
                if(current > 0)
                    current--
                view.findViewById<EditText>(R.id.dialog_count).setText(current.toString())
            }
            view.findViewById<ImageView>(R.id.dialog_min).setOnClickListener {
                current = 0
                view.findViewById<EditText>(R.id.dialog_count).setText(current.toString())
            }
            view.findViewById<ImageView>(R.id.dialog_max).setOnClickListener {
                current = countList[pos]
                view.findViewById<EditText>(R.id.dialog_count).setText(current.toString())
            }


        }
    }

    override fun getItemCount(): Int = nameList.size

    inner class ShelfViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameView: TextView = itemView.findViewById(R.id.ui_shelf_item_name)
        val countView: TextView = itemView.findViewById(R.id.ui_shelf_item_count)
        val modelView : TextView = itemView.findViewById(R.id.ui_goods_model)
        val goodsno : TextView = itemView.findViewById(R.id.ui_goods_number)
        val shelfView : TextView = itemView.findViewById(R.id.ui_shelf_id)
        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }
    }

}