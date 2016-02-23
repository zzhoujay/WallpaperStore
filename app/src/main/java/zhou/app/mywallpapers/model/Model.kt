package zhou.app.mywallpapers.model

import android.os.Parcel
import android.os.Parcelable

import java.util.*

/**
 * Created by zhou on 16-2-20.
 */

data class Wallpaper(var title: String = "", var url: String, var protect: Boolean = false, var date: Date = Date()) : Parcelable {
    var id: Int? = null

    constructor(id: Int, title: String = "", url: String, protect: Boolean, date: Date) : this(title, url, protect, date) {
        this.id = id
    }

    constructor(source: Parcel): this(source.readString(), source.readString(), 1.toByte().equals(source.readByte()), source.readSerializable() as Date)

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(title)
        dest?.writeString(url)
        dest?.writeByte((if (protect) 1 else 0).toByte())
        dest?.writeSerializable(date)
    }

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<Wallpaper> = object : Parcelable.Creator<Wallpaper> {
            override fun createFromParcel(source: Parcel): Wallpaper {
                return Wallpaper(source)
            }

            override fun newArray(size: Int): Array<Wallpaper?> {
                return arrayOfNulls(size)
            }
        }
    }
}