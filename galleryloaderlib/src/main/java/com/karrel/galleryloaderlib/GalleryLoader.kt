package com.karrel.galleryloaderlib

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.karrel.galleryloaderlib.presenter.GalleryLoaderPresenter
import com.karrel.galleryloaderlib.presenter.GalleryLoaderPresenterImpl
import kotlinx.android.synthetic.main.gallery_loader.view.*

/**
 * Created by 이주영 on 2017-05-18.
 */

class GalleryLoader : AppCompatDialogFragment(), GalleryLoaderPresenter.View {

    lateinit var mBuilder: Builder
    private var mPresenter: GalleryLoaderPresenter? = null
    private var mToast: Toast? = null

    private lateinit var customView: View

    fun show(fragmentManager: FragmentManager) {
        val ft = fragmentManager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }


    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        customView = LayoutInflater.from(context).inflate(R.layout.gallery_loader, null)


        // layout to display
        dialog.setContentView(customView)

        mPresenter = GalleryLoaderPresenterImpl(context, this)

        // set color transpartent
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 버튼 세팅
        setupButtons()
    }

    fun startGallery() {
        mPresenter!!.startGallery()
    }

    private fun setupButtons() {
        customView.camera.setOnClickListener {
            if (mBuilder.haveFileName()) {
                mPresenter!!.startCamera(mBuilder.imageFileName, mBuilder.fileNameExtension)
            } else {
                mPresenter!!.startCamera()
            }
        }
        customView.gallery.setOnClickListener { mPresenter!!.startGallery() }
    }

    override fun errorMessage(message: String) {
        if (mToast != null) mToast!!.cancel()

        mToast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        mToast!!.show()
    }

    override fun grantUriPermission(packageName: String, photoURI: Uri, i: Int) {
        activity!!.grantUriPermission(packageName, photoURI, i)
    }

    override fun onSelectedImage(uri: Uri) {
        mBuilder.onImageSelectedListener!!.onImageSelected(uri)
        dismissAllowingStateLoss()
    }

    interface OnImageSelectedListener {
        fun onImageSelected(uri: Uri)
    }

    class Builder(var mContext: Context) {
        var onImageSelectedListener: OnImageSelectedListener? = null
        var imageFileName: String? = null
        var fileNameExtension: String? = null
        private var haveFileName = false

        fun setOnImageSelectedListener(onImageSelectedListener: OnImageSelectedListener): Builder {
            this.onImageSelectedListener = onImageSelectedListener
            return this
        }

        fun create(): GalleryLoader {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                throw RuntimeException("Missing required WRITE_EXTERNAL_STORAGE permission. Did you remember to request it first?")
            }

            if (onImageSelectedListener == null) {
                throw RuntimeException("You have to use setOnImageSelectedListener() or setOnMultiImageSelectedListener() for receive selected Uri")
            }

            val galleryLoader = GalleryLoader()
            galleryLoader.mBuilder = this
            return galleryLoader
        }

        /**
         * @param imageFileName     파일명
         * @param fileNameExtension 확장자
         * @return
         */
        fun setImageFileName(imageFileName: String, fileNameExtension: String): Builder {
            this.imageFileName = imageFileName
            this.fileNameExtension = fileNameExtension
            haveFileName = true
            return this
        }

        fun haveFileName(): Boolean {
            return haveFileName
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mPresenter!!.onActivityResult(requestCode, resultCode, data)
    }
}
