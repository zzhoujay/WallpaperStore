package zhou.app.mywallpapers.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v7.app.ActionBar
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import com.squareup.otto.Subscribe
import org.jetbrains.anko.toast
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import zhou.app.mywallpapers.App
import zhou.app.mywallpapers.R
import zhou.app.mywallpapers.model.Wallpaper
import zhou.app.mywallpapers.ui.dialog.DetailDialog
import zhou.app.mywallpapers.ui.fragment.WallpaperDisplayFragment
import zhou.app.mywallpapers.util.Event
import zhou.app.mywallpapers.util.getImagePathFromUri

/**
 * Created by zhou on 16-2-21.
 */
class WallpaperDisplayActivity : BaseActivity(), EasyPermissions.PermissionCallbacks {

    private var wallpaperFragment: WallpaperDisplayFragment? = null

    companion object {
        const val flag = 5
        const val PICK_IMAGE = 0x567
        const val CACHE_DIALOG = 0x432
    }

    private var detailDialog: DetailDialog? = null

    override fun onPermissionsDenied(p0: Int, p1: MutableList<String>?) {
        throw UnsupportedOperationException()
    }

    override fun onPermissionsGranted(p0: Int, p1: MutableList<String>?) {
        throw UnsupportedOperationException()
    }

    @AfterPermissionGranted(flag)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_display)

        wallpaperFragment = WallpaperDisplayFragment.newInstance()

        supportFragmentManager.beginTransaction().add(R.id.container, wallpaperFragment).commit()


        if (!EasyPermissions.hasPermissions(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, "gg", flag, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

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
            PICK_IMAGE -> {

            }
            CACHE_DIALOG -> {
                if (event.value is DetailDialog) {
                    detailDialog = event.value
                }
            }
        }
        println("activity:$event")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WallpaperDisplayFragment.RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            val path = getImagePathFromUri(applicationContext, data?.data)
            if (path == null) {
                toast("文件无效")
            } else {
                val wallpaper = Wallpaper(url = path)
                wallpaperFragment?.reloadWallpaper(wallpaper)
            }
        } else if (requestCode == DetailDialog.RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            val path = getImagePathFromUri(applicationContext, data?.data)
            if (path == null) {
                toast("文件无效")
            } else {
                detailDialog?.setImagePath(path)
                println("path:$path")
            }
        }
    }

}