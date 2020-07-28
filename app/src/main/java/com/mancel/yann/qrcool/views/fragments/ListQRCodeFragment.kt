package com.mancel.yann.qrcool.views.fragments

import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mancel.yann.qrcool.R
import com.mancel.yann.qrcool.viewModels.SharedViewModel
import com.mancel.yann.qrcool.views.adapters.QRCodeAdapter
import com.mancel.yann.qrcool.widgets.FabSmall
import kotlinx.android.synthetic.main.fragment_list_q_r_code.*
import kotlinx.android.synthetic.main.fragment_list_q_r_code.view.*

/**
 * Created by Yann MANCEL on 21/07/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.views.fragments
 *
 * A [BaseFragment] subclass.
 */
class ListQRCodeFragment : BaseFragment() {

    // FIELDS --------------------------------------------------------------------------------------

    private lateinit var _adapter: QRCodeAdapter
    private val _viewModel: SharedViewModel by activityViewModels()
    private var _isFABMenuOpen = false

    // METHODS -------------------------------------------------------------------------------------

    // -- BaseFragment --

    override fun getFragmentLayout(): Int = R.layout.fragment_list_q_r_code

    override fun configureDesign() {
        this.configureListenerOfFABs()
        this.configureRecyclerView()
        this.configureQRCodeEvents()
    }

    // -- Listener --

    /**
     * Configures the listener of each FloatingActionButton
     */
    private fun configureListenerOfFABs() {
        // Normal FAB
        this._rootView.fragment_list_fab.setOnClickListener {
            this.actionOnFabMenu()
        }

        // Mini FABs
        this.configureFABToNavigateToScanQRCodeFragment()
        this.configureFABToNavigateToOtherFragment()
    }

    /**
     * Configures the mini FloatingActionButton to navigate to ScanQRCodeFragment
     */
    private fun configureFABToNavigateToScanQRCodeFragment() {
        this._rootView.fragment_list_zxing_library.setOnClickListener {
            this.findNavController().navigate(R.id.action_listQRCodeFragment_to_scanQRCodeFragment)
        }
    }

    /**
     * Configures the mini FloatingActionButton to navigate to other Fragment
     */
    private fun configureFABToNavigateToOtherFragment() {
        this._rootView.fragment_list_ml_kit_library.setOnClickListener { /* Do nothing here */ }
    }

    // -- RecyclerView --

    /**
     * Configures the RecyclerView
     */
    private fun configureRecyclerView() {
        // Adapter
        this._adapter = QRCodeAdapter()

        // RecyclerView
        with(this._rootView.fragment_list_recycler_view) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@ListQRCodeFragment.requireContext())
            adapter = this@ListQRCodeFragment._adapter
        }
    }

    // -- LiveData --

    /**
     * Configures the QR Code events from LiveData
     */
    private fun configureQRCodeEvents() {
        this._viewModel
            .getQRCodes()
            .observe(
                this.viewLifecycleOwner,
                Observer { this._adapter.updateData(it) }
            )
    }

    // -- FAB Events --

    /**
     * Displays the FloatingActionButton menu
     */
    private fun actionOnFabMenu() {
        if (!this._isFABMenuOpen)
            this.openFABMenu()
        else
            this.closeFABMenu()

        this._isFABMenuOpen = !this._isFABMenuOpen
    }

    /**
     * Opens the FloatingActionButton menu
     */
    private fun openFABMenu() {
        this.animateOpenFAB(this.fragment_list_zxing_library)
        this.animateOpenFAB(this.fragment_list_ml_kit_library)
    }

    /**
     * Closes the FloatingActionButton menu
     */
    private fun closeFABMenu() {
        this.animateCloseFAB(this.fragment_list_zxing_library)
        this.animateCloseFAB(this.fragment_list_ml_kit_library)
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