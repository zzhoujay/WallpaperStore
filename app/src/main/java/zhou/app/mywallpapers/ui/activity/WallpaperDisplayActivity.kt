package zhou.app.mywallpapers.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
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
    }

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

    fun handleEvent(event: Event) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WallpaperDisplayFragment.RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            val path = getImagePathFromUri(applicationContext, data?.data)
            if (path == null) {
                toast("文件无效")
            } else {
                val wallpaper = Wallpaper(url = path)
                notice(Event(WallpaperDisplayFragment.ACTION_RELOAD, wallpaper))
            }
        } else if (resultCode == DetailDialog.RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            val path = getImagePathFromUri(applicationContext, data?.data)
            if (path == null) {
                toast("文件无效")
            } else {
                notice(Event(DetailDialog.RELOAD_IMAGE, path))
            }
        }
    }

}