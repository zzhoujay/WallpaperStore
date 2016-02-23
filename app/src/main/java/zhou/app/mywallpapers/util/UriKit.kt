package zhou.app.mywallpapers.util

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

/**
 * Created by zhou on 16-2-23.
 */

    fun getImagePathFromUri(context: Context, fileUrl: Uri?): String? {
        var fileName: String? = null
        val filePathUri = fileUrl
        if (fileUrl != null) {
            if (fileUrl.scheme.compareTo("content") == 0) {
                // content://开头的uri
                val cursor = context.contentResolver.query(fileUrl, null, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    fileName = cursor.getString(column_index) // 取出文件路径

                    // Android 4.1 更改了SD的目录，sdcard映射到/storage/sdcard0
                    if (!fileName!!.startsWith("/storage") && !fileName.startsWith("/mnt")) {
                        // 检查是否有”/mnt“前缀
                        fileName = "/mnt" + fileName
                    }
                    cursor.close()
                }
            } else if (fileUrl.scheme.compareTo("file") == 0)
            // file:///开头的uri
            {
                fileName = filePathUri.toString()// 替换file://
                fileName = filePathUri.toString().replace("file://", "")
                @SuppressLint("SdCardPath") val index = fileName.indexOf("/sdcard")
                fileName = if (index == -1) fileName else fileName.substring(index)
                if (!fileName.startsWith("/mnt")) {
                    // 加上"/mnt"头
                    fileName += "/mnt"
                }
            }
        }
        return fileName
    }


