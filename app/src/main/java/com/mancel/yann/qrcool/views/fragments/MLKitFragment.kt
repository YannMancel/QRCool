package com.mancel.yann.qrcool.views.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.common.util.concurrent.ListenableFuture
import com.mancel.yann.qrcool.R
import com.mancel.yann.qrcool.analyzers.QRCodeAnalyzer
import com.mancel.yann.qrcool.utils.MessageTools
import com.mancel.yann.qrcool.viewModels.SharedViewModel
import kotlinx.android.synthetic.main.fragment_m_l_kit.view.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Created by Yann MANCEL on 29/07/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.views.fragments
 *
 * A [BaseFragment] subclass.
 */
@androidx.camera.core.ExperimentalGetImage
class MLKitFragment : BaseFragment() {

    /*
        See GitHub example:
            [1]: https://github.com/android/camera-samples/tree/master/CameraXBasic

        See CameraX's uses cases
            [2]: https://developer.android.com/training/camerax/preview
            [3]: https://developer.android.com/training/camerax/analyze
            [4]: https://developer.android.com/training/camerax/take-photo
     */

    // FIELDS --------------------------------------------------------------------------------------

    private val _viewModel: SharedViewModel by activityViewModels()

    private lateinit var _cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private var _camera: Camera? = null
    private var _preview: Preview? = null
    private var _imageCapture: ImageCapture? = null
    private var _imageAnalysis: ImageAnalysis? = null

    // Blocking camera operations are performed using this executor
    private lateinit var _cameraExecutor: ExecutorService

    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

    // METHODS -------------------------------------------------------------------------------------

    // -- BaseFragment --

    override fun getFragmentLayout(): Int = R.layout.fragment_m_l_kit

    override fun configureDesign() = this.configureListenerOfFAB()

    override fun actionAfterPermission() = this.configureCameraX()

    // -- Fragment --

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize our background executor
        this._cameraExecutor = Executors.newSingleThreadExecutor()

        // Wait for the views to be properly laid out
        this._rootView.fragment_m_l_kit_camera.post {
            this.configureCameraX()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Shut down our background executor
        this._cameraExecutor.shutdown()
    }

    // -- Actions -

    /**
     * Configures the listener of FAB
     */
    private fun configureListenerOfFAB() {
        this._rootView.fragment_m_l_kit_fab.setOnClickListener { this.takePicture() }
    }

    // -- CameraX --

    /**
     * Configures CameraX with Camera permission
     */
    private fun configureCameraX() {
        if (this.hasCameraPermission()) {
            this.configureCameraProvider()
        }
    }

    /**
     * Configures the [ProcessCameraProvider] of CameraX
     */
    private fun configureCameraProvider() {
        this._cameraProviderFuture = ProcessCameraProvider.getInstance(this.requireContext())

        this._cameraProviderFuture.addListener(
            Runnable {
                val cameraProvider = this._cameraProviderFuture.get()
                this.bindAllUseCases(cameraProvider)
            },
            ContextCompat.getMainExecutor(this.requireContext())
        )
    }

    /**
     * Binds all use cases, [Preview], [ImageCapture] and [ImageAnalysis], to the Fragment's lifecycle
     * @param cameraProvider a [ProcessCameraProvider]
     */
    private fun bindAllUseCases(cameraProvider: ProcessCameraProvider) {
        // CameraSelector
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        // Metrics
        val metrics = DisplayMetrics().also {
            this._rootView.fragment_m_l_kit_camera.display.getRealMetrics(it)
        }

        // Ratio
        val screenAspectRatio = this.getAspectRatio(metrics.widthPixels, metrics.heightPixels)

        // Rotation
        val rotation = this._rootView.fragment_m_l_kit_camera.display.rotation

        // Use case: Preview
        this._preview = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()

        // Use case: ImageCapture
        this._imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetResolution(Size(1920, 1080))
            .setTargetRotation(rotation)
            .setFlashMode(ImageCapture.FLASH_MODE_OFF)
            .build()

        // Use case: ImageAnalysis
        this._imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetAspectRatio(screenAspectRatio)
//            .setTargetResolution(Size(1920, 1080))
            .setTargetRotation(rotation)
            .build()
            .also {
                it.setAnalyzer(
                    this._cameraExecutor,
                    ImageAnalysis.Analyzer { image ->
                        /*
                            if [ImageAnalysis]..setTargetAspectRatio(screenAspectRatio)     [Fail]
                                W/TAG A: cropRect: Rect(0, 0 - 864, 480)
                                W/TAG A: width: 864
                                W/TAG A: height: 480

                            if [ImageAnalysis].setTargetResolution(Size(1920, 1080))        [Fail]
                                W/TAG A: cropRect: Rect(0, 0 - 480, 640)
                                W/TAG A: width: 480
                                W/TAG A: height: 640
                         */
                        image.close()
                    }
//                    QRCodeAnalyzer { barcodes ->
//                        MessageTools.showMessageWithSnackbar(
//                            this@MLKitFragment._rootView.fragment_m_l_kit_coordinator_layout,
//                            if(barcodes.isNotEmpty()) "${barcodes[0].rawValue}" else "No data"
//                        )
//                    }
                )
            }

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // Binds Preview to the Fragment's lifecycle
            this._camera = cameraProvider.bindToLifecycle(
                this.viewLifecycleOwner,
                cameraSelector,
                this._preview, this._imageCapture //, this._imageAnalysis
            )

            // Connects Preview to the view into xml file
            this._preview?.setSurfaceProvider(
                this._rootView.fragment_m_l_kit_camera.createSurfaceProvider()
            )
        } catch(e: Exception) {
            Log.e(
                this.javaClass.simpleName,
                "Use case binding failed: $e"
            )
        }
    }

    /**
     * Take picture thanks to [ImageCapture] use case of CameraX
     */
    private fun takePicture() {
        this._imageCapture?.takePicture(
            this._cameraExecutor,
            object : ImageCapture.OnImageCapturedCallback() {

                override fun onCaptureSuccess(image: ImageProxy) {
                    /*
                        if [ImageCapture].setTargetAspectRatio(screenAspectRatio)   [Fail]
                        so
                            width: 3840
                            height: 2160
                            Rect(0, 0 - 3840, 2160)

                        if [ImageCapture].setTargetResolution(Size(1920, 1080))     [Success]
                        so
                            width: 480
                            height: 640
                            Rect(60, 0 - 420, 640)
                     */

                    QRCodeAnalyzer { barcodes ->
                        if(barcodes.isEmpty()) {
                            MessageTools.showMessageWithSnackbar(
                                this@MLKitFragment._rootView.fragment_m_l_kit_coordinator_layout,
                                this@MLKitFragment.getString(R.string.no_data)
                            )
                        } else {
                            this@MLKitFragment.notifyScanOfQRCode(
                                barcodes.first().rawValue
                                    ?: this@MLKitFragment.getString(R.string.no_raw_value)
                            )
                        }
                    }
                    .also {
                        it.analyze(image)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(
                        this.javaClass.simpleName,
                        "Image capture failed: $exception"
                    )
                }
            }
        )
    }

    // -- Ratio --

    /**
     *  [androidx.camera.core.ImageAnalysisConfig] requires enum value of
     *  [androidx.camera.core.AspectRatio]. Currently it has values of 4:3 & 16:9.
     *
     *  Detecting the most suitable ratio for dimensions provided in @params by counting absolute
     *  of preview ratio to one of the provided values.
     *  @param width    an [Int] that contains the preview width
     *  @param height   an [Int] that contains the preview height
     *  @return suitable aspect ratio
     */
    private fun getAspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    // -- QR Code --

    /**
     * Notifies when a QR Code has been checked
     */
    private fun notifyScanOfQRCode(textOfQRCode: String) {
        // Add QR Code
        this._viewModel.addQRCode(textOfQRCode)

        // Finishes this fragment
        this.findNavController().popBackStack()
    }
}