package zhou.app.mywallpapers.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.dialog_detial.view.*
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import zhou.app.mywallpapers.App
import zhou.app.mywallpapers.R
import zhou.app.mywallpapers.model.Wallpaper
import zhou.app.mywallpapers.persistence.DatabaseManager
import zhou.app.mywallpapers.ui.activity.WallpaperDisplayActivity
import zhou.app.mywallpapers.ui.fragment.WallpaperDisplayFragment
import zhou.app.mywallpapers.util.Event
import zhou.app.mywallpapers.util.notice

/**
 * Created by zhou on 16-2-23.
 */
class DetailDialog : DialogFragment() {

    companion object {
        val WALLPAPER = "wallpaper"
        val RESULT_LOAD_IMAGE = 18
        val DISMISS = 0x111
        val RELOAD_IMAGE = 0x456

        fun newInstance(wallpaper: Wallpaper? = null): DetailDialog {
            val f = DetailDialog()
            if (wallpaper != null) {
                val b = Bundle()
                b.putParcelable(WALLPAPER, wallpaper)
                f.arguments = b
            }
            return f
        }
    }

    var newImagePath: String? = null

    var image: ImageView? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        App.instance.bus.register(this)
    }

    override fun onDetach() {
        super.onDetach()
        App.instance.bus.unregister(this)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_detial, null)

        var wallpaper: Wallpaper? = null

        if (arguments.containsKey(WALLPAPER)) {
            wallpaper = arguments.getParcelable(WALLPAPER)
        }

        image = view.imageView

        view.editText.setText(wallpaper?.title ?: "")
        Glide.with(this).load(wallpaper?.url).into(view.imageView)

        view.imageView.setOnClickListener {
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.type = "image/*"
            activity.startActivityForResult(i, RESULT_LOAD_IMAGE);
        }


        builder.setView(view).setPositiveButton("确定", { p0, p1 ->
            val title = view.editText.text.toString()
            if (wallpaper != null && (!wallpaper!!.title.equals(title) || (newImagePath != null && !wallpaper!!.url.equals(newImagePath)))) {
                wallpaper!!.title = title
                if (newImagePath != null) {
                    wallpaper!!.url = newImagePath!!
                }
                async() {
                    DatabaseManager.instance.update(wallpaper!!)
                    uiThread {
                        notice(Event(WallpaperDisplayFragment.ACTION_RELOAD, null, this@DetailDialog, WallpaperDisplayFragment::class.java))
                        dismiss()
                    }
                }
            }
        }).setNegativeButton("取消", null)

        return builder.create()
    }

    @Subscribe
    fun handleEvent(event: Event) {
        when (event.code) {
            RELOAD_IMAGE -> {
                if (event.value != null && image != null && event.value is String) {
                    Glide.with(this).load(event.value).into(image)
                    newImagePath = event.value
                    println("gg:${event.value}")
                }
            }
        }
        println("event:$event")
    }

    fun setImagePath(value: String) {
        if (image != null) {
            image!!.postDelayed({
                Glide.with(this).load(newImagePath).into(image)
            }, 500)
        }
        this.newImagePath = value
    }


}