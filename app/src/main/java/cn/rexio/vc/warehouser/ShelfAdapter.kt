package cn.rexio.vc.warehouser

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.TextView

class ShelfAdapter(
    private val nameList: List<String>,
    private val countList: List<Int>,
    private val maxList: List<Int>,
    private val context: Context,
    private val window: Window,
    private val io: Boolean,
    private val isOnClickEnabled: Boolean
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
        if (isOnClickEnabled)
            holder.itemView.setOnClickListener{
                val alertDialog =
                    AlertDialog.Builder(context).setView(R.layout.dialog_login)
                alertDialog.show()
                val builder = AlertDialog.Builder(context)
                builder.setCancelable(true)
                val view = window.layoutInflater.inflate(R.layout.dialog_io_layout, null, false)
                builder.setView(view)
                val dialog = builder.create()
                dialog.show()
                if(io)
                {
                    view.findViewById<TextView>(R.id.dialog_left).visibility = INVISIBLE
                }
                view.findViewById<EditText>(R.id.dialog_count).addTextChangedListener(object :
                    TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                    }

                    override fun afterTextChanged(s: Editable?) {
                        val current = s.toString().toIntOrNull()
                        var fin : String = "0"
                        if(current == null || current < 0)
                            fin = "0"
                        else
                        {
                            if(current > maxList[pos])
                                fin = maxList[pos].toString()
                            else
                                fin = current.toString()
                        }
                        view.findViewById<EditText>(R.id.dialog_count).setText(fin)
                    }
                })
            }
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