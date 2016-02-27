package zhou.app.mywallpapers.ui.activity

import android.Manifest
import android.app.WallpaperManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_display.*
import org.jetbrains.anko.toast
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import zhou.app.mywallpapers.App
import zhou.app.mywallpapers.R
import zhou.app.mywallpapers.common.Config
import zhou.app.mywallpapers.model.Wallpaper
import zhou.app.mywallpapers.ui.dialog.DetailDialog
import zhou.app.mywallpapers.ui.fragment.WallpaperDisplayFragment
import zhou.app.mywallpapers.util.Event
import zhou.app.mywallpapers.util.getImagePathFromUri
import zhou.app.mywallpapers.util.loadWallpaperInputStream

/**
 * Created by zhou on 16-2-21.
 */
class WallpaperDisplayActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private var wallpaperFragment: WallpaperDisplayFragment? = null
    private var currWallpaper: Wallpaper? = null

    private var detailDialog: DetailDialog? = null

    override fun onPermissionsDenied(p0: Int, p1: MutableList<String>?) {
    }

    override fun onPermissionsGranted(p0: Int, p1: MutableList<String>?) {
    }

    @AfterPermissionGranted(Config.Flag.permission_flag)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_display)

        wallpaperFragment = WallpaperDisplayFragment.newInstance()

        supportFragmentManager.beginTransaction().add(R.id.container, wallpaperFragment).commit()


        if (!EasyPermissions.hasPermissions(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, "gg", Config.Flag.permission_flag, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        val d = WallpaperManager.getInstance(applicationContext).drawable

        println(d)

        wallpaper_preview.setImageDrawable(d)

        //        Glide.with(this).load("file:///android_asset/primary_1.jpg").into(wallpaper_preview)

    }

    override fun onResume() {
        super.onResume()
        App.instance.bus.register(this)
    }


    override fun onPause() {
        super.onPause()
        App.instance.bus.unregister(this)
    }

    @Subscribe
    fun handleEvent(event: Event) {
        when (event.code) {
            Config.Action.cache_dialog -> {
                if (event.value is DetailDialog) {
                    detailDialog = event.value
                }
            }
            Config.Action.preview_wallpaper -> {
                if (event.value != null && event.value is Wallpaper) {
                    currWallpaper = event.value
                    Glide.with(this).load(event.value.url)
                            .error(R.drawable.error)
                            .placeholder(R.drawable.placeholder)
                            .crossFade()
                            .into(wallpaper_preview)
                }
            }
            Config.Action.set_wallpaper -> {
                if (currWallpaper != null) {
                    val wallpaperInputStream = loadWallpaperInputStream(currWallpaper!!.url)
                    if (wallpaperInputStream != null) {
                        val wm = WallpaperManager.getInstance(applicationContext)
                        wm.setStream(wallpaperInputStream)
                        toast("壁纸设置成功")
                        return
                    }
                }
                toast("设置失败")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Config.Flag.result_pick_image && resultCode == RESULT_OK) {
            val path = getImagePathFromUri(applicationContext, data?.data)
            if (path == null) {
                toast("文件无效")
            } else {
                val wallpaper = Wallpaper(url = path)
                wallpaperFragment?.reloadWallpaper(wallpaper)
            }
        } else if (requestCode == Config.Flag.result_select_image && resultCode == RESULT_OK) {
            val path = getImagePathFromUri(applicationContext, data?.data)
            if (path == null) {
                toast("文件无效")
            } else {
                detailDialog?.imagePath = path
                println("path:$path")
            }
        }
    }

}