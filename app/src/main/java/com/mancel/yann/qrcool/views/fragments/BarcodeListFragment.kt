package com.mancel.yann.qrcool.views.fragments

import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.mancel.yann.qrcool.R
import com.mancel.yann.qrcool.models.*
import com.mancel.yann.qrcool.utils.BarcodeTools
import com.mancel.yann.qrcool.utils.MessageTools
import com.mancel.yann.qrcool.viewModels.SharedViewModel
import com.mancel.yann.qrcool.views.adapters.BarcodeAdapter
import com.mancel.yann.qrcool.views.adapters.HorizontalSwipeCallback
import com.mancel.yann.qrcool.widgets.FabSmall
import kotlinx.android.synthetic.main.fragment_list_barcode.*
import kotlinx.android.synthetic.main.fragment_list_barcode.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Yann MANCEL on 21/07/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.views.fragments
 *
 * A [BaseFragment] subclass.
 */
class BarcodeListFragment : BaseFragment() {

    // FIELDS --------------------------------------------------------------------------------------

    private lateinit var _adapter: BarcodeAdapter
    private val _viewModel: SharedViewModel by sharedViewModel()

    // METHODS -------------------------------------------------------------------------------------

    // -- BaseFragment --

    override fun getFragmentLayout(): Int = R.layout.fragment_list_barcode

    override fun doOnCreateView() {
        this.configureListenerOfFABs()
        this.configureRecyclerView()
        this.configureBarcodeEvents()
        this.configureFABMenuEvents()
    }

    // -- Listener --

    /**
     * Configures the listener of each FloatingActionButton
     */
    private fun configureListenerOfFABs() {
        // Normal FAB
        this._rootView.fragment_list_fab.setOnClickListener {
            this._viewModel.toogleFabMenu()
        }

        // Mini FABs
        this.configureFABToNavigateToCameraXFragmentInBarcode1D()
        this.configureFABToNavigateToCameraXFragmentInBarcode2D()
    }

    /**
     * Configures the mini FloatingActionButton to navigate to CameraXFragment in barcode 1D
     */
    private fun configureFABToNavigateToCameraXFragmentInBarcode1D() {
        this._rootView.fragment_list_barcode_1d.setOnClickListener {
            // Action with argument
            val action =
                BarcodeListFragmentDirections
                    .actionBarcodeListFragmentToCameraXFragment(CameraXFragment.ScanConfig.BARCODE_1D)
            this.findNavController().navigate(action)
        }
    }

    /**
     * Configures the mini FloatingActionButton to navigate to CameraXFragment in barcode 2D
     */
    private fun configureFABToNavigateToCameraXFragmentInBarcode2D() {
        this._rootView.fragment_list_barcode_2d.setOnClickListener {
            // Action with argument
            val action =
                BarcodeListFragmentDirections
                    .actionBarcodeListFragmentToCameraXFragment(CameraXFragment.ScanConfig.BARCODE_2D)
            this.findNavController().navigate(action)
        }
    }

    /**
     * Event from a click on a CardView
     */
    private fun eventFromCardView(barcode: BarcodeOverlay) {
        // Action with argument
        val action = when (barcode) {
            is TextBarcode -> {
                BarcodeListFragmentDirections.actionBarcodeListFragmentToBarcodeDetailsFragment(
                    textBarcode = barcode
                )
            }
            is WifiBarcode -> {
                BarcodeListFragmentDirections.actionBarcodeListFragmentToBarcodeDetailsFragment(
                    wifiBarcode = barcode
                )
            }
            is UrlBarcode -> {
                BarcodeListFragmentDirections.actionBarcodeListFragmentToBarcodeDetailsFragment(
                    urlBarcode = barcode
                )
            }
            is SMSBarcode -> {
                BarcodeListFragmentDirections.actionBarcodeListFragmentToBarcodeDetailsFragment(
                    smsBarcode = barcode
                )
            }
            is GeoPointBarcode -> {
                BarcodeListFragmentDirections.actionBarcodeListFragmentToBarcodeDetailsFragment(
                    geoBarcode = barcode
                )
            }
        }

        this.findNavController().navigate(action)
    }

    // -- RecyclerView --

    /**
     * Configures the RecyclerView
     */
    private fun configureRecyclerView() {
        // Adapter
        this._adapter = BarcodeAdapter(this.requireContext(), this::eventFromCardView)

        // RecyclerView
        with(this._rootView.fragment_list_recycler_view) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@BarcodeListFragment.requireContext())
            adapter = this@BarcodeListFragment._adapter

            ItemTouchHelper(
                HorizontalSwipeCallback { adapterPosition ->
                    this@BarcodeListFragment.actionAfterHorizontalSwipe(adapterPosition)
                }
            ).attachToRecyclerView(this)
        }
    }

    // -- LiveData --

    /**
     * Configures the barcode events from LiveData
     */
    private fun configureBarcodeEvents() {
        this._viewModel
            .getBarcodes()
            .observe(this.viewLifecycleOwner) {
                val sortedData = it.sortedWith(BarcodeOverlay.AscendingDateComparator())
                this._adapter.updateData(sortedData)
            }
    }

    /**
     * Configures the FloatingActionButton menu events from LiveData
     */
    private fun configureFABMenuEvents() {
        this._viewModel
            .isFABMenuOpen()
            .observe(this.viewLifecycleOwner) { isOpen ->
                if (isOpen)
                    this.openFABMenu()
                else
                    this.closeFABMenu()
            }
    }

    // -- Actions --

    /**
     * Action after the horizontal swipe on RecyclerView's item
     */
    private fun actionAfterHorizontalSwipe(adapterPosition: Int) {
        val barcode = this._adapter.getDataAt(adapterPosition)

        // Remove barcode
        this@BarcodeListFragment._viewModel.removeBarcode(barcode)

        MessageTools.showMessageWithSnackbar(
            this._rootView.fragment_list_coordinator_layout,
            this.getString(R.string.remove_item, barcode._rawValue),
            this.getString(android.R.string.cancel)
        ) {
            val newBarcode = BarcodeTools.createNewBarcodeFromCopy(barcode)

            // Add barcode
            this._viewModel.addBarcodes(listOf(newBarcode))
        }
    }

    // -- FAB Events --

    /**
     * Opens the FloatingActionButton menu
     */
    private fun openFABMenu() {
        // Animate normal FAB
        ViewCompat
            .animate(this._rootView.fragment_list_fab)
            .rotation(45F)
            .setDuration(300L)
            .setInterpolator(OvershootInterpolator(10F))
            .start()

        this.animateOpenFAB(this.fragment_list_barcode_1d)
        this.animateOpenFAB(this.fragment_list_barcode_2d)
    }

    /**
     * Closes the FloatingActionButton menu
     */
    private fun closeFABMenu() {
        // Animate normal FAB
        ViewCompat
            .animate(this._rootView.fragment_list_fab)
            .rotation(0F)
            .setDuration(300L)
            .setInterpolator(OvershootInterpolator(10F))
            .start()

        this.animateCloseFAB(this.fragment_list_barcode_1d)
        this.animateCloseFAB(this.fragment_list_barcode_2d)
    }

    /**
     * Animates the [FabSmall] during the opening
     */
    private fun animateOpenFAB(fab: FabSmall) {
        ViewCompat
            .animate(fab)
            .translationY(-fab._offsetYAnimation)
            .withStartAction { fab.visibility = View.VISIBLE }
            .withEndAction {
                fab._labelView
                    .animate()
                    .alpha(1F)
                    .duration = 200L
            }
    }

    /**
     * Animates the [FabSmall] during the closing
     */
    private fun animateCloseFAB(fab: FabSmall) {
        ViewCompat
            .animate(fab)
            .translationY(0F)
            .withStartAction {
                fab._labelView
                    .animate()
                    .alpha(0F)
            }
            .withEndAction { fab.visibility = View.GONE }
    }
}