package com.mancel.yann.qrcool.views.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

/**
 * Created by Yann MANCEL on 21/07/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.views.fragments
 *
 * An abstract [Fragment] subclass.
 */
abstract class BaseFragment : Fragment() {

    // FIELDS --------------------------------------------------------------------------------------

    protected lateinit var _rootView: View

    companion object {
        const val REQUEST_CODE_PERMISSION_CAMERA = 1000
    }

    // METHODS -------------------------------------------------------------------------------------

    @LayoutRes
    protected abstract fun getFragmentLayout(): Int

    protected abstract fun doOnCreateView()

    // -- Fragment --

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        this._rootView = inflater.inflate(this.getFragmentLayout(), container, false)

        this.doOnCreateView()

        return this._rootView
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            // Access to the external storage or the camera of device
            REQUEST_CODE_PERMISSION_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    this.actionAfterPermission()
                }
                else {
                    this.findNavController().popBackStack()
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    // -- Permission --

    /** Checks the permission: [Manifest.permission.CAMERA] */
    protected fun hasCameraPermission(): Boolean {
        val permissionResult = ContextCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.CAMERA
        )

        return when (permissionResult) {
            PackageManager.PERMISSION_GRANTED -> true

            else -> {
                this.requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CODE_PERMISSION_CAMERA
                )

                false
            }
        }
    }

    /** Method to override to perform action after the granted permission */
    protected open fun actionAfterPermission() { /* Do nothing here */ }
}