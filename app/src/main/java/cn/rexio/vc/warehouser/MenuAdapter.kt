package cn.rexio.vc.warehouser

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class MenuAdapter(
    private val textList: List<Int> ,
    private val iconList: List<Int> ,
    private val onClickListerList: List<View.OnClickListener>
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        return MenuViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_menu_item, parent, false))
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.mIcon.setImageResource(iconList[position])
        holder.mText.setText(textList[position])
        holder.itemView.setOnClickListener(onClickListerList[position])
    }

    override fun getItemCount(): Int = textList.size

    inner class MenuViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val mIcon: ImageView = itemView.findViewById(R.id.ui_sub_menu_item_icon)
        val mText: TextView = itemView.findViewById(R.id.ui_sub_menu_item_text)

        override fun toString(): String {
            return super.toString() + " '" + mText.text + "'"
        }
    }

}