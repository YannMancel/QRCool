package com.mancel.yann.qrcool.views.fragments

import androidx.navigation.fragment.findNavController
import com.mancel.yann.qrcool.R
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

    // Todo - 21/07/2020 - Create Adapter

    // METHODS -------------------------------------------------------------------------------------

    // -- BaseFragment --

    override fun getFragmentLayout(): Int = R.layout.fragment_list_q_r_code

    override fun configureDesign() {
        this.configureFABToNavigateToScanQRCodeFragment()
        this.configureRecyclerView()
    }

    // -- Listener --

    /**
     * Configures the FloatingActionButton to navigate to ScanQRCodeFragment
     */
    private fun configureFABToNavigateToScanQRCodeFragment() {
        this._RootView.fragment_list_fab.setOnClickListener {
            this.findNavController().navigate(R.id.action_listQRCodeFragment_to_scanQRCodeFragment)
        }
    }

    // -- RecyclerView --

    /**
     * Configures the RecyclerView
     */
    private fun configureRecyclerView() {
        // Todo - 21/07/2020 - Configures the RecyclerView
    }
}