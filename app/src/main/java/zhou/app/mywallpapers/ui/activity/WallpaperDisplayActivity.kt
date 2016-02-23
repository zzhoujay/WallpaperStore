package zhou.app.mywallpapers.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import org.jetbrains.anko.toast
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import zhou.app.mywallpapers.R
import zhou.app.mywallpapers.model.Wallpaper
import zhou.app.mywallpapers.ui.dialog.DetailDialog
import zhou.app.mywallpapers.ui.fragment.WallpaperDisplayFragment
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

    var detailDialog: DetailDialog? = null


    @AfterPermissionGranted(flag)
    override fun onCreate(savedInstanceState: Bundle?) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏

        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_display)

        wallpaperFragment = WallpaperDisplayFragment.newInstance()

        supportFragmentManager.beginTransaction().add(R.id.container, wallpaperFragment).commit()


        if (!EasyPermissions.hasPermissions(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, "gg", flag, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }


    fun switchNavigationBar() {
        val uiOptions = window.decorView.systemUiVisibility
        var newUiOptions = uiOptions

        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        window.decorView.systemUiVisibility = newUiOptions;
    }

    override fun onResume() {
        super.onResume()
        switchNavigationBar()
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
        } else if (resultCode == DetailDialog.RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            val path = getImagePathFromUri(applicationContext, data?.data)
            if (path == null) {
                toast("文件无效")
            } else {
                detailDialog?.newImagePath = path
            }
        }
    }

    override fun notice(n: Any?) {
        when (n) {
            DetailDialog -> {
                detailDialog = n as DetailDialog
            }
            WallpaperDisplayFragment.ACTION_RELOAD -> {
                wallpaperFragment?.reloadWallpaper()
            }
            DetailDialog.DISMISS -> {
                detailDialog = null
            }
        }
    }
}