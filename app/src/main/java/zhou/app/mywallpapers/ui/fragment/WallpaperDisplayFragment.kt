package zhou.app.mywallpapers.ui.fragment

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_display.*
import org.jetbrains.anko.intentFor
import zhou.app.mywallpapers.R
import zhou.app.mywallpapers.model.Wallpaper
import zhou.app.mywallpapers.ui.adapter.WallpapersAdapter
import zhou.app.mywallpapers.util.Callback

/**
 * Created by zhou on 16-2-21.
 */
class WallpaperDisplayFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_display, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //        Glide.with(this).load(R.drawable.background_1).into(imageView)
        val option = BitmapFactory.Options()
        option.inSampleSize=3
        parent.background = BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.background_1,option))

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val adapter = WallpapersAdapter(context, listOf(Wallpaper("Test1", "xxx"), Wallpaper("Test2", "xxx"), Wallpaper("Test3", "xxx"), Wallpaper("Test4", "xxx"), Wallpaper("Test5", "xxx"), Wallpaper("Test6", "xxx"), Wallpaper("Test7", "xxx")))
        adapter.addClickCallback = object : Callback<Wallpaper> {
            override fun call(t: Wallpaper?) {
                val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activity.startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        }
        recyclerView.adapter = adapter
    }

    companion object {
        val RESULT_LOAD_IMAGE = 0x123
        fun newInstance(): WallpaperDisplayFragment {
            val f = WallpaperDisplayFragment()

            return f;
        }
    }
}