package com.mancel.yann.qrcool.views.fragments

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mancel.yann.qrcool.R
import com.mancel.yann.qrcool.viewModels.SharedViewModel
import com.mancel.yann.qrcool.views.adapters.QRCodeAdapter
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
        this.configureFABToNavigateToScanQRCodeFragment()
        this.configureRecyclerView()
        this.configureQRCodeEvents()
    }

    // -- Listener --

    /**
     * Configures the FloatingActionButton to navigate to ScanQRCodeFragment
     */
    private fun configureFABToNavigateToScanQRCodeFragment() {
        this._rootView.fragment_list_fab.setOnClickListener {
            this.findNavController().navigate(R.id.action_listQRCodeFragment_to_scanQRCodeFragment)
        }
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
}