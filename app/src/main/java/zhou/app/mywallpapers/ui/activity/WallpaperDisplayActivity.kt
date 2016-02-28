package zhou.app.mywallpapers.ui.activity

import android.Manifest
import android.app.WallpaperManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_display.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import zhou.app.mywallpapers.App
import zhou.app.mywallpapers.R
import zhou.app.mywallpapers.common.Config
import zhou.app.mywallpapers.model.Wallpaper
import zhou.app.mywallpapers.ui.dialog.WallpaperDetailDialog
import zhou.app.mywallpapers.ui.fragment.WallpaperDisplayFragment
import zhou.app.mywallpapers.util.Callback0
import zhou.app.mywallpapers.util.Event
import zhou.app.mywallpapers.util.getImagePathFromUri
import zhou.app.mywallpapers.util.loadWallpaperInputStream

/**
 * Created by zhou on 16-2-21.
 * 壁纸选择Activity
 */
class WallpaperDisplayActivity : AppCompatActivity() {

    private var wallpaperFragment: WallpaperDisplayFragment? = null
    private var currWallpaper: Wallpaper? = null

    private var wallpaperDetailDialog: WallpaperDetailDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        wallpaper_preview.setImageDrawable(WallpaperManager.getInstance(applicationContext).drawable)

        wallpaperFragment = WallpaperDisplayFragment.newInstance()

        supportFragmentManager.beginTransaction().add(R.id.container, wallpaperFragment).commit()

        wallpaper_preview.onTapCallback = object : Callback0 {
            override fun call() {
                if (currWallpaper != null) {
                    wallpaperFragment?.optionBarVisible = !(wallpaperFragment?.optionBarVisible ?: false)
                }
            }
        }

        checkPermission()
    }

    fun checkPermission() {

        Dexter.checkPermission(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                wallpaperFragment?.onPermissionReady()
            }

            override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {
                p1?.continuePermissionRequest()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                alert(R.string.permission_message, R.string.permission_title, {
                    builder.setPositiveButton(R.string.permission_positive, { dialog, which ->
                        checkPermission()
                    }).setNegativeButton(R.string.permission_negative, { dialog, which ->
                        finish()
                    })
                }).show()

            }

        }, Manifest.permission.WRITE_EXTERNAL_STORAGE)

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
                if (event.value is WallpaperDetailDialog) {
                    wallpaperDetailDialog = event.value
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
                wallpaperFragment?.topBarEnable = currWallpaper != null
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
                wallpaperDetailDialog?.imagePath = path
                println("path:$path")
            }
        }
    }

}