package com.mancel.yann.qrcool.views.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.View
import androidx.annotation.Keep
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.common.util.concurrent.ListenableFuture
import com.mancel.yann.qrcool.R
import com.mancel.yann.qrcool.analyzers.MLKitBarcodeAnalyzer
import com.mancel.yann.qrcool.lifecycles.ExecutorLifecycleObserver
import com.mancel.yann.qrcool.lifecycles.FullScreenLifecycleObserver
import com.mancel.yann.qrcool.models.BarcodeOverlay
import com.mancel.yann.qrcool.states.CameraState
import com.mancel.yann.qrcool.states.ScanState
import com.mancel.yann.qrcool.utils.MessageTools
import com.mancel.yann.qrcool.viewModels.SharedViewModel
import kotlinx.android.synthetic.main.fragment_camera_x.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
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
class CameraXFragment : BaseFragment() {

    /*
        See GitHub example:
            [1]: https://github.com/android/camera-samples/tree/master/CameraXBasic

        See CameraX's uses cases
            [2]: https://developer.android.com/training/camerax/preview
            [3]: https://developer.android.com/training/camerax/analyze
     */

    // ENUMS ---------------------------------------------------------------------------------------

    // Annotation for the ProGuard considerations of Navigation component
    @Keep
    enum class ScanConfig { BARCODE_1D, BARCODE_2D }

    // FIELDS --------------------------------------------------------------------------------------

    private val _scanConfig: ScanConfig by lazy {
        CameraXFragmentArgs.fromBundle(this.requireArguments()).config
    }

    private val _viewModel: SharedViewModel by sharedViewModel()

    private lateinit var _currentCameraState: CameraState

    private lateinit var _cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private var _camera: Camera? = null
    private var _preview: Preview? = null
    private var _imageAnalysis: ImageAnalysis? = null

    // Blocking camera operations are performed using this executor
    private lateinit var _cameraExecutor: ExecutorLifecycleObserver

    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0

        // For ML Kit: 1280x720 or 1920x1080 -> Ratio: 1.7
        private const val RESOLUTION_MAX = 1920
        private const val RESOLUTION_MIN = 1080
    }

    // METHODS -------------------------------------------------------------------------------------

    // -- BaseFragment --

    override fun getFragmentLayout(): Int = R.layout.fragment_camera_x

    override fun doOnCreateView() {
        this.configureFullScreenLifecycleObserver()
        this.configureExecutorLifecycleObserver()
        this.configureCameraEvents()
    }

    override fun actionAfterPermission() = this.configureCameraX()

    // -- Fragment --

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Wait for the views to be properly laid out
        this._rootView.fragment_camera_preview.post {
            this._viewModel.changeCameraStateToSetupCamera()
        }
    }

    // -- LifecycleObserver --

    private fun configureFullScreenLifecycleObserver() {
        this.lifecycle.addObserver(
            FullScreenLifecycleObserver(
                this.lifecycle,
                this.requireActivity()
            )
        )
    }

    private fun configureExecutorLifecycleObserver() {
        this._cameraExecutor = ExecutorLifecycleObserver()
        this.lifecycle.addObserver(this._cameraExecutor)
    }

    // -- LiveData --

    private fun configureCameraEvents() {
        this._viewModel
            .getCameraState()
            .observe(this.viewLifecycleOwner) { cameraState ->
                cameraState?.let {
                    this.updateUIWithCameraEvents(it)
                }
            }
    }

    // -- CameraState --

    private fun updateUIWithCameraEvents(state: CameraState) {
        // To update CameraSelector
        this._currentCameraState = state

        when (state) {
            is CameraState.SetupCamera -> this.handleCameraStateWithSetupCamera()
            is CameraState.PreviewReady -> this.handleCameraStateWithPreviewReady()
            is CameraState.Error -> this.handleCameraStateWithError(state._errorMessage)
        }
    }

    private fun handleCameraStateWithSetupCamera() = this.configureCameraX()

    private fun handleCameraStateWithPreviewReady() { /* Do nothing here */ }

    private fun handleCameraStateWithError(errorMessage: String) { /* Do nothing here */ }

    // -- CameraX --

    private fun configureCameraX() {
        if (this.hasCameraPermission())
            this.configureCameraProviderOfCameraX()
        else
            this._viewModel.changeCameraStateToError(this.getString(R.string.no_permission))
    }

    private fun configureCameraProviderOfCameraX() {
        this._cameraProviderFuture = ProcessCameraProvider.getInstance(this.requireContext())

        this._cameraProviderFuture.addListener(
            Runnable {
                val cameraProvider = this._cameraProviderFuture.get()
                this.bindAllUseCasesOfCameraX(cameraProvider)
                this._viewModel.changeCameraStateToPreviewReady()
            },
            ContextCompat.getMainExecutor(this.requireContext())
        )
    }

    /** Binds all use cases, [Preview] and [ImageAnalysis], to the Fragment's lifecycle */
    private fun bindAllUseCasesOfCameraX(cameraProvider: ProcessCameraProvider) {
        // Metrics
        val metrics = DisplayMetrics().also {
            this._rootView.fragment_camera_preview.display.getRealMetrics(it)
        }

        // Rotation
        val rotation = this._rootView.fragment_camera_preview.display.rotation

        // Use case: Preview
        this._preview = this.buildPreviewOfCameraX(
            this.getAspectRatio(metrics.widthPixels, metrics.heightPixels),
            rotation
        )

        // Use case: ImageAnalysis
        this._imageAnalysis = this.buildImageAnalysisOfCameraX(
            this.getResolution(),
            rotation
        )

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // Binds Preview to the Fragment's lifecycle
            this._camera = cameraProvider.bindToLifecycle(
                this.viewLifecycleOwner,
                this.buildCameraSelectorOfCameraX(),
                this._preview, this._imageAnalysis
            )

            // Connects Preview to the view into xml file
            this._preview?.setSurfaceProvider(
                this._rootView.fragment_camera_preview.createSurfaceProvider()
            )
        } catch(e: Exception) {
            Log.e(
                this.javaClass.simpleName,
                "Use case binding failed: $e"
            )
        }
    }

    private fun buildCameraSelectorOfCameraX(): CameraSelector {
        return CameraSelector.Builder()
            .requireLensFacing(this._currentCameraState._lensFacing)
            .build()
    }

    private fun buildPreviewOfCameraX(ratio: Int, rotation: Int): Preview {
        return Preview.Builder()
            .setTargetAspectRatio(ratio)
            .setTargetRotation(rotation)
            .build()
    }

    private fun buildImageAnalysisOfCameraX(resolution: Size, rotation: Int): ImageAnalysis {
        return ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetResolution(resolution)
            .setTargetRotation(rotation)
            .build()
            .also {
                it.setAnalyzer(
                    this._cameraExecutor._executor,
                    this.getAnalyzer(it)
                )
            }
    }

    // -- Ratio --

    /**
     *  [ImageAnalysis] requires enum value of [androidx.camera.core.AspectRatio].
     *  Currently it has values of 4:3 & 16:9.
     *
     *  Detecting the most suitable ratio for dimensions provided in @params by counting absolute
     *  of preview ratio to one of the provided values.
     *  @param width    an [Int] that contains the preview width
     *  @param height   an [Int] that contains the preview height
     *  @return suitable aspect ratio
     */
    private fun getAspectRatio(width: Int, height: Int): Int {
        /*
            Ex:
                width ..................................... 1080
                height .................................... 2400

                previewRatio .............................. 2400/1080 = 2.2

                Ratio 4/3 ................................. 1.3
                Ratio 16/9 ................................ 1.7

                Absolute of [previewRatio - Ratio 4/3] .... 0.8
                Absolute of [previewRatio - Ratio 16/9] ... 0.4
         */

        val previewRatio = max(width, height).toDouble() / min(width, height).toDouble()
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    // -- Resolution --

    private fun getResolution(): Size =
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            Size(RESOLUTION_MIN, RESOLUTION_MAX)
        else
            Size(RESOLUTION_MAX, RESOLUTION_MIN)

    // -- Analyzer --

    private fun getAnalyzer(imageAnalysis: ImageAnalysis): ImageAnalysis.Analyzer {
        return MLKitBarcodeAnalyzer(this._scanConfig) { scanState ->
            /*
                ex: In Portrait mode

                    if [ImageAnalysis].setTargetAspectRatio(screenAspectRatio)   [Fail]
                    so
                        image.width ....................... 864
                        image.height ...................... 480
                        image.cropRect .................... Rect(0, 0 - 864, 480)

                    if [ImageAnalysis].setTargetResolution(Size(1920, 1080))     [Fail]
                    so
                        image.width ....................... 480
                        image.height ...................... 640
                        image.cropRect .................... Rect(0, 0 - 480, 640)

                    if [ImageAnalysis].setTargetResolution(Size(1080, 1920))     [Success]
                    so
                        image.width ....................... 1920
                        image.height ...................... 1080
                        image.cropRect .................... Rect(0, 0 - 1920, 1080)
             */

            when (scanState) {
                is ScanState.Success -> {
                    val barcodes = scanState._barcodes

                    if (barcodes.isNotEmpty()) {
                        // Clears QRCodeAnalyzer to avoid the same multiple answers
                        imageAnalysis.clearAnalyzer()

                        // Add new barcode(s)
                        this.notifyScanOfBarcodesBeforeToFinishFragment(barcodes)
                    }
                }

                is ScanState.Failure -> {
                    MessageTools.showMessageWithSnackbar(
                        this._rootView.fragment_camera_coordinator_layout,
                        this.getString(R.string.analyzer_failed_scan, scanState._exception.message)
                    )
                }
            }
        }
    }

    // -- Barcode --

    private fun notifyScanOfBarcodesBeforeToFinishFragment(barcodes: List<BarcodeOverlay>) {
        // Add barcodes
        this._viewModel.addBarcodes(barcodes)

        // Finishes this fragment
        this.findNavController().popBackStack()
    }
}