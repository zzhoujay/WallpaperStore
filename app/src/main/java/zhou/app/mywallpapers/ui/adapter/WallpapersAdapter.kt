package zhou.app.mywallpapers.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_add.view.*
import zhou.app.mywallpapers.R
import zhou.app.mywallpapers.model.Wallpaper

import kotlinx.android.synthetic.main.item_image.view.*
import zhou.app.mywallpapers.util.Callback
import zhou.app.mywallpapers.util.OnClickCallback

/**
 * Created by zhou on 16-2-20.
 */
class WallpapersAdapter(var context: Context? = null, var wallpapers: List<Wallpaper>? = null) : RecyclerView.Adapter<WallpapersAdapter.Holder>() {

    private var itemClickCallback: Callback<Wallpaper>? = null
    private var addClickCallback: Callback<Wallpaper>? = null

    private val onAddCallback = object : OnClickCallback {
        override fun onClick(view: View, position: Int) {
            //            addClickCallback?.call()
        }
    }
    private val onItemClickCallback = object : OnClickCallback {
        override fun onClick(view: View, position: Int) {
            itemClickCallback?.call(wallpapers!![position])
        }
    }


    override fun onBindViewHolder(holder: Holder?, position: Int) {
        val wallpaper = wallpapers!![position]

        //        Glide.with(context).load(wallpaper.url).into(holder!!.icon)
        holder!!.title.text = wallpaper.title
        //        holder.icon.setImageResource(R.drawable.background_1)
    }

    override fun getItemCount(): Int {
        return wallpapers?.size ?: 0
    }

    override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): Holder? {
        if (context == null) {
            context = p0!!.context
        }
        var holder: Holder? = null
        if (p1 == Holder.TYPE_ADD) {
            holder = Holder(LayoutInflater.from(context).inflate(R.layout.item_add, null), Holder.TYPE_ADD)
            holder.addCallback = onAddCallback
        } else {
            holder = Holder(LayoutInflater.from(context).inflate(R.layout.item_image, null), Holder.TYPE_ITEM)
            holder.itemClickCallback = onItemClickCallback
        }
        return holder
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return Holder.TYPE_ADD
        } else {
            return Holder.TYPE_ITEM
        }
    }

    class Holder(val root: View, val type: Int) : RecyclerView.ViewHolder(root) {

        companion object {
            val TYPE_ADD = 1
            val TYPE_ITEM = 0
        }

        val icon: ImageView
        val title: TextView

        var addCallback: OnClickCallback? = null
        var itemClickCallback: OnClickCallback? = null

        init {

            if (type == TYPE_ADD) {
                icon = root.icon_add
                title = root.text
                root.setOnClickListener {
                    addCallback?.onClick(root, adapterPosition)
                }
            } else {
                icon = root.icon
                title = root.title
                root.setOnClickListener {
                    itemClickCallback?.onClick(root, adapterPosition)
                }
            }

        }
    }

}