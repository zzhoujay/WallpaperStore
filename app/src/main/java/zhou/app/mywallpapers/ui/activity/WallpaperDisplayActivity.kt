package zhou.app.mywallpapers.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import zhou.app.mywallpapers.R
import zhou.app.mywallpapers.ui.fragment.WallpaperDisplayFragment

/**
 * Created by zhou on 16-2-21.
 */
class WallpaperDisplayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏

        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_display)

        supportFragmentManager.beginTransaction().add(R.id.container, WallpaperDisplayFragment.newInstance()).commit()

    }
}