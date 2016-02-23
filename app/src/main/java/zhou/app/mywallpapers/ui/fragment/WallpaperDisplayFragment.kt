package zhou.app.mywallpapers.ui.fragment

import android.app.WallpaperManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import kotlinx.android.synthetic.main.fragment_display.*
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import zhou.app.mywallpapers.R
import zhou.app.mywallpapers.model.Wallpaper
import zhou.app.mywallpapers.persistence.DatabaseManager
import zhou.app.mywallpapers.ui.adapter.WallpapersAdapter
import zhou.app.mywallpapers.ui.dialog.DetailDialog
import zhou.app.mywallpapers.util.Callback
import zhou.app.mywallpapers.util.Callback3
import zhou.app.mywallpapers.util.toast


/**
 * Created by zhou on 16-2-21.
 */
class WallpaperDisplayFragment : BaseFragment() {

    var adapter: WallpapersAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_display, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        if (adapter == null) {
            adapter = WallpapersAdapter(context)
            adapter!!.addClickCallback = object : Callback<Wallpaper> {
                override fun call(t: Wallpaper?) {
                    val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activity.startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            }
            adapter!!.itemClickCallback = object : Callback<Wallpaper> {
                override fun call(t: Wallpaper?) {
                    if (t != null) {
                        Glide.with(this@WallpaperDisplayFragment).load(t.url).asBitmap().centerCrop().into(object : SimpleTarget<Bitmap>(parent.width, parent.height) {
                            override fun onResourceReady(p0: Bitmap?, p1: GlideAnimation<in Bitmap>?) {
                                parent.background = BitmapDrawable(resources, p0)
                            }
                        })
                    } else {
                        toast("gg")
                    }
                }
            }
            adapter!!.itemLongClickCallback = object : Callback3<View, Wallpaper, Int> {
                override fun call(t: View?, k: Wallpaper?, m: Int?) {
                    val popMenu = PopupMenu(context, t)
                    val menu = popMenu.menu
                    menu.add(0, MENU_ID_EDIT, 0, "编辑")
                    if (!k?.protect!!) {
                        menu.add(0, MENU_ID_DELETE, 1, "删除")
                    }
                    popMenu.setOnMenuItemClickListener {
                        when (it.itemId) {
                            MENU_ID_EDIT -> {
                                toast("Edit")
                                val d = DetailDialog.newInstance(k)
                                notice(d)
                                d.show(fragmentManager, "detail")
                            }
                            MENU_ID_DELETE -> {
                                toast("Delete")
                                async() {
                                    if (DatabaseManager.instance.delete(k?.id)) {
                                        uiThread {
                                            reloadWallpaper()
                                        }
                                    }
                                }
                            }
                        }
                        true
                    }
                    popMenu.show()
                }
            }
        }
        recyclerView.adapter = adapter

        val wm = WallpaperManager.getInstance(context)

        parent.background = wm.fastDrawable

        reloadWallpaper()
    }

    fun reloadWallpaper(wallpaper: Wallpaper? = null) {
        async() {
            if (wallpaper != null) {
                DatabaseManager.instance.insert(wallpaper)
            }
            val wallpapers = DatabaseManager.instance.select()
            uiThread {
                adapter!!.wallpapers = wallpapers
                adapter!!.notifyDataSetChanged()
            }
        }
    }

    companion object {
        val RESULT_LOAD_IMAGE = 0x123
        val MENU_ID_EDIT = 0x123
        val MENU_ID_DELETE = 0x234
        val ACTION_RELOAD = 0x134
        fun newInstance(): WallpaperDisplayFragment {
            val f = WallpaperDisplayFragment()

            return f;
        }
    }
}