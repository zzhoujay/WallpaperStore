package zhou.app.mywallpapers.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_display.*
import zhou.app.mywallpapers.R
import zhou.app.mywallpapers.model.Wallpaper
import zhou.app.mywallpapers.ui.adapter.WallpapersAdapter

/**
 * Created by zhou on 16-2-21.
 */
class WallpaperDisplayFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_display, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = WallpapersAdapter(context, listOf(Wallpaper("Test1", "xxx"), Wallpaper("Test2", "xxx"), Wallpaper("Test3", "xxx"), Wallpaper("Test4", "xxx"), Wallpaper("Test5", "xxx"), Wallpaper("Test6", "xxx"), Wallpaper("Test7", "xxx")))
    }

    companion object {
        fun newInstance(): WallpaperDisplayFragment {
            val f = WallpaperDisplayFragment()

            return f;
        }
    }
}