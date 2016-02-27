package zhou.app.mywallpapers.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.dialog_detail.view.*
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import zhou.app.mywallpapers.App
import zhou.app.mywallpapers.R
import zhou.app.mywallpapers.common.Config
import zhou.app.mywallpapers.model.Wallpaper
import zhou.app.mywallpapers.persistence.DatabaseManager
import zhou.app.mywallpapers.ui.fragment.WallpaperDisplayFragment
import zhou.app.mywallpapers.util.Event
import zhou.app.mywallpapers.util.notice

/**
 * Created by zhou on 16-2-23.
 */
class DetailDialog : DialogFragment() {

    companion object {

        fun newInstance(wallpaper: Wallpaper? = null): DetailDialog {
            val f = DetailDialog()
            if (wallpaper != null) {
                val b = Bundle()
                b.putParcelable(Config.Tag.wallpaper, wallpaper)
                f.arguments = b
            }
            return f
        }
    }

    var imagePath: String? = null
        set(value) {
            if (image != null && value != null) {
                image!!.postDelayed({
                    Glide.with(this)
                            .load(value)
                            .error(R.drawable.error)
                            .placeholder(R.drawable.placeholder)
                            .crossFade()
                            .into(image)
                }, 500)
            }
            field = value
        }

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

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_detail, null)

        var wallpaper: Wallpaper? = null

        if (arguments.containsKey(Config.Tag.wallpaper)) {
            wallpaper = arguments.getParcelable(Config.Tag.wallpaper)
        }

        image = view.imageView

        view.editText.setText(wallpaper?.title ?: "")
        Glide.with(this).load(wallpaper?.url).into(view.imageView)

        view.imageView.setOnClickListener {
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.type = "image/*"
            activity.startActivityForResult(i, Config.Flag.result_select_image);
        }


        builder.setView(view).setPositiveButton("确定", { p0, p1 ->
            val title = view.editText.text.toString()
            if (wallpaper != null && (!wallpaper!!.title.equals(title) || (imagePath != null && !wallpaper!!.url.equals(imagePath)))) {
                wallpaper!!.title = title
                if (imagePath != null) {
                    wallpaper!!.url = imagePath!!
                }
                async() {
                    DatabaseManager.instance.update(wallpaper!!)
                    uiThread {
                        notice(Event(Config.Action.reload_list, null, this@DetailDialog, WallpaperDisplayFragment::class.java))
                        dismiss()
                    }
                }
            }
        }).setNegativeButton("取消", null)

        return builder.create()
    }


}