package zhou.app.mywallpapers.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_add.view.*
import kotlinx.android.synthetic.main.item_image.view.*
import zhou.app.mywallpapers.R
import zhou.app.mywallpapers.model.Wallpaper
import zhou.app.mywallpapers.util.Callback
import zhou.app.mywallpapers.util.Callback3
import zhou.app.mywallpapers.util.OnClickCallback

/**
 * Created by zhou on 16-2-20.
 */
class WallpapersAdapter(ctx: Context? = null, wps: List<Wallpaper>? = null) : RecyclerView.Adapter<WallpapersAdapter.BaseHolder>() {

    val functionalCount = 1

    var select = -1
    var selectWallpaper: Wallpaper? = null
    var itemClickCallback: Callback<Wallpaper>? = null
    var addClickCallback: Callback<Wallpaper>? = null
    var itemLongClickCallback: Callback3<View, Wallpaper, Int>? = null
    var context: Context? = null
    var wallpapers: List<Wallpaper>? = null
        set(value) {
            field = value
            value?.forEachIndexed { index, wallpaper ->
                if (wallpaper.equals(selectWallpaper)) {
                    select = index + functionalCount
                    selectWallpaper = wallpaper
                    return
                }
            }
            select = -1
            selectWallpaper = null
        }

    init {
        this.context = ctx
        this.wallpapers = wps
    }

    private val onAddCallback = object : OnClickCallback {
        override fun onClick(view: View, position: Int) {
            addClickCallback?.call()
        }
    }
    private val onItemClickCallback = object : OnClickCallback {
        override fun onClick(view: View, position: Int) {
            val w = wallpapers!![position - functionalCount]
            if (position >= functionalCount) {
                notifyItemChanged(select)
                notifyItemChanged(position)
                select = position
                selectWallpaper = w
            }
            itemClickCallback?.call(w)
        }
    }

    private val onItemLongClickCallback = object : OnClickCallback {
        override fun onClick(view: View, position: Int) {
            itemLongClickCallback?.call(view, wallpapers!![position - 1], position)
        }
    }

    override fun onBindViewHolder(holder: BaseHolder?, position: Int) {
        if (holder is WallpaperHolder) {
            val wallpaper = wallpapers!![position - 1]

            if (TextUtils.isEmpty(wallpaper.title)) {
                holder.title.visibility = View.INVISIBLE
            } else {
                holder.title.visibility = View.VISIBLE
                holder.title.text = wallpaper.title
            }
            if (select < 0) {
                holder.checked = false
            } else {
                holder.checked = position == select
            }
            Glide.with(context)
                    .load(wallpaper.url)
                    .error(R.drawable.error)
                    .placeholder(R.drawable.placeholder)
                    .crossFade()
                    .into(holder.wallpaper)
        }
    }

    override fun getItemCount(): Int {
        return (wallpapers?.size ?: 0) + 1
    }

    override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): BaseHolder? {
        if (context == null) {
            context = p0!!.context
        }
        var holder: BaseHolder
        if (p1 == TYPE_ADD) {
            holder = FunctionalHolder(LayoutInflater.from(context).inflate(R.layout.item_add, null))
            holder.clickCallback = onAddCallback
        } else {
            holder = WallpaperHolder(LayoutInflater.from(context).inflate(R.layout.item_image, null))
            holder.clickCallback = onItemClickCallback
            holder.longClickCallback = onItemLongClickCallback
        }
        return holder
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return TYPE_ADD
        } else {
            return TYPE_ITEM
        }
    }

    open class BaseHolder(val root: View) : RecyclerView.ViewHolder(root) {

        var clickCallback: OnClickCallback? = null
        var longClickCallback: OnClickCallback? = null

        init {
            root.setOnClickListener {
                clickCallback?.onClick(root, adapterPosition)
            }
            root.setOnLongClickListener {
                longClickCallback?.onClick(root, adapterPosition)
                true
            }
        }
    }

    class FunctionalHolder(root: View) : BaseHolder(root) {

        val icon: ImageView
        val text: TextView

        init {
            icon = root.icon_add
            text = root.text
        }
    }

    class WallpaperHolder(root: View) : BaseHolder(root) {

        val wallpaper: ImageView
        val title: TextView
        val icon: ImageView

        var checked = false
            set(value) {
                field = value
                if (value) {
                    icon.visibility = View.VISIBLE
                } else {
                    icon.visibility = View.GONE
                }
            }

        init {
            wallpaper = root.icon
            title = root.title
            icon = root.wallpaper_icon
        }

    }

    companion object {
        const val TYPE_ADD = 1
        const val TYPE_ITEM = 0
    }

}