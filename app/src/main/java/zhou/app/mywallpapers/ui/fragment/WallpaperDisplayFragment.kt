package zhou.app.mywallpapers.ui.fragment

import android.app.WallpaperManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.fragment_display.*
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import zhou.app.mywallpapers.App
import zhou.app.mywallpapers.R
import zhou.app.mywallpapers.common.Config
import zhou.app.mywallpapers.model.Wallpaper
import zhou.app.mywallpapers.persistence.DatabaseManager
import zhou.app.mywallpapers.ui.activity.WallpaperDisplayActivity
import zhou.app.mywallpapers.ui.adapter.WallpapersAdapter
import zhou.app.mywallpapers.ui.dialog.DetailDialog
import zhou.app.mywallpapers.util.*


/**
 * Created by zhou on 16-2-21.
 */
class WallpaperDisplayFragment : Fragment() {

    var adapter: WallpapersAdapter? = null

    override fun onResume() {
        super.onResume()
        App.instance.bus.register(this)
    }

    override fun onPause() {
        super.onPause()
        App.instance.bus.unregister(this)
    }

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
                    val i = Intent(Intent.ACTION_GET_CONTENT)
                    i.type = "image/*"
                    activity.startActivityForResult(i, Config.Flag.result_pick_image);
                }
            }
            adapter!!.itemClickCallback = object : Callback<Wallpaper> {
                override fun call(t: Wallpaper?) {
                    notice(Event(Config.Action.preview_wallpaper, t))
                }
            }
            adapter!!.itemLongClickCallback = object : Callback3<View, Wallpaper, Int> {
                override fun call(t: View?, k: Wallpaper?, m: Int?) {
                    val popMenu = PopupMenu(context, t)
                    val menu = popMenu.menu
                    menu.add(0, Config.Id.menu_edit, 0, "编辑")
                    if (!k?.protect!!) {
                        menu.add(0, Config.Id.menu_delete, 1, "删除")
                    }
                    popMenu.setOnMenuItemClickListener {
                        when (it.itemId) {
                            Config.Id.menu_edit -> {
                                val d = DetailDialog.newInstance(k)
                                notice(Event(Config.Action.cache_dialog, d))
                                d.show(fragmentManager, "detail")
                            }
                            Config.Id.menu_delete -> {
                                async() {
                                    if (DatabaseManager.instance.delete(k?.id)) {
                                        uiThread {
                                            reloadWallpaper()
                                            toast("删除成功")
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

        topBar.setOnClickListener {
            notice(Event(Config.Action.set_wallpaper))
        }

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

    @Subscribe
    fun handleEvent(event: Event) {
        when (event.code) {
            Config.Action.reload_list -> {
                if (event.value is Wallpaper) {
                    reloadWallpaper(event.value)
                } else {
                    reloadWallpaper()
                }
            }
        }
    }

    companion object {

        fun newInstance(): WallpaperDisplayFragment {
            val f = WallpaperDisplayFragment()
            return f;
        }
    }
}