package zhou.app.mywallpapers.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.fragment_display.*
import org.jetbrains.anko.async
import org.jetbrains.anko.enabled
import org.jetbrains.anko.uiThread
import zhou.app.mywallpapers.App
import zhou.app.mywallpapers.R
import zhou.app.mywallpapers.common.Config
import zhou.app.mywallpapers.model.Wallpaper
import zhou.app.mywallpapers.persistence.DatabaseManager
import zhou.app.mywallpapers.ui.adapter.WallpapersAdapter
import zhou.app.mywallpapers.ui.dialog.WallpaperDetailDialog
import zhou.app.mywallpapers.util.*


/**
 * Created by zhou on 16-2-21.
 * 壁纸展示的Fragment
 */
class WallpaperDisplayFragment : Fragment() {

    var adapter: WallpapersAdapter? = null
    var needToReload = false
        private set

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
                                val d = WallpaperDetailDialog.newInstance(k)
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

        topBar.setOnClickListener {
            notice(Event(Config.Action.set_wallpaper))
        }

        recyclerView.adapter = adapter

        if (needToReload) {
            reloadWallpaper()
        }

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

    fun onPermissionReady() {
        if (adapter != null) {
            reloadWallpaper()
        } else {
            needToReload = true
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

    fun generateAnimation(show: Boolean, top: Boolean): Animation {
        val fromY = if (top) -1f else 1f
        val fromA = if (show) 0f else 1f
        val toA = if (show) 1f else 0f
        val animationTranslate = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, fromY, Animation.RELATIVE_TO_SELF, 0f)
        val animationAlpha = AlphaAnimation(fromA, toA)
        val animationSet = AnimationSet(true)
        animationSet.interpolator = AccelerateInterpolator()
        animationSet.duration = 200
        animationSet.addAnimation(animationTranslate)
        animationSet.addAnimation(animationAlpha)
        return animationSet
    }

    val showAnimationTopBar: Animation by lazy {
        generateAnimation(true, true)
    }
    val showAnimationWallpapers: Animation by lazy {
        generateAnimation(true, false)
    }

    var optionBarVisible: Boolean
        get() = wallpapers.visibility == View.VISIBLE
        set(value) {
            if (value) {
                topBarLayout.startAnimation(showAnimationTopBar)
                topBarLayout.visibility = View.VISIBLE
                wallpapers.startAnimation(showAnimationWallpapers)
                wallpapers.visibility = View.VISIBLE
            } else {
                topBarLayout.visibility = View.INVISIBLE
                wallpapers.visibility = View.INVISIBLE
            }
        }

    var topBarEnable: Boolean
        get() = topBar.enabled
        set(value) {
            topBar.enabled = value
        }

}