package com.mancel.yann.qrcool.views.fragments

import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mancel.yann.qrcool.R
import com.mancel.yann.qrcool.models.QRCode
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

    // METHODS -------------------------------------------------------------------------------------

    // -- BaseFragment --

    override fun getFragmentLayout(): Int = R.layout.fragment_list_q_r_code

    override fun configureDesign() {
        this.configureListenerOfFABs()
        this.configureRecyclerView()
        this.configureQRCodeEvents()
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
        this.configureFABToNavigateToZXingFragment()
        this.configureFABToNavigateToMLKitFragment()
    }

    /**
     * Configures the mini FloatingActionButton to navigate to ZXingFragment
     */
    private fun configureFABToNavigateToZXingFragment() {
        this._rootView.fragment_list_zxing_library.setOnClickListener {
            this.findNavController().navigate(R.id.action_listQRCodeFragment_to_zXingFragment)
        }
    }

    /**
     * Configures the mini FloatingActionButton to navigate to MLKitFragment
     */
    private fun configureFABToNavigateToMLKitFragment() {
        this._rootView.fragment_list_ml_kit_library.setOnClickListener {
            this.findNavController().navigate(R.id.action_listQRCodeFragment_to_mLKitFragment)
        }
    }

    /**
     * Event from a click on a CardView
     */
    private fun eventFromCardView(qrCode: QRCode) {
        // Action with argument
        val action =
            ListQRCodeFragmentDirections
                .actionListQRCodeFragmentToDetailsQRCodeFragment(qrCode)

        this.findNavController().navigate(action)
    }

    // -- RecyclerView --

    /**
     * Configures the RecyclerView
     */
    private fun configureRecyclerView() {
        // Adapter
        this._adapter = QRCodeAdapter(this::eventFromCardView)

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

    /**
     * Configures the FloatingActionButton menu events from LiveData
     */
    private fun configureFABMenuEvents() {
        this._viewModel
            .isFABMenuOpen()
            .observe(
                this.viewLifecycleOwner,
                Observer { isOpen ->
                    if (isOpen)
                        this.openFABMenu()
                    else
                        this.closeFABMenu()
                }
            )
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

        this.animateOpenFAB(this.fragment_list_zxing_library)
        this.animateOpenFAB(this.fragment_list_ml_kit_library)
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