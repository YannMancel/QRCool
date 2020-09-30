package com.mancel.yann.qrcool.views.activities

import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.mancel.yann.qrcool.R

/**
 * Created by Yann MANCEL on 21/07/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.views.activities
 *
 * A [BaseActivity] subclass.
 */
class MainActivity : BaseActivity() {

    // FIELDS --------------------------------------------------------------------------------------

    private lateinit var _navController: NavController
    private lateinit var _appBarConfiguration: AppBarConfiguration

    // METHODS -------------------------------------------------------------------------------------

    // -- BaseActivity --

    override fun getActivityLayout(): Int = R.layout.activity_main

    override fun doOnCreate() = this.configureActionBarForNavigation()

    // -- Activity --

    override fun onSupportNavigateUp(): Boolean {
        return this._navController.navigateUp(this._appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    // -- Action bar --

    private fun configureActionBarForNavigation() {
        // NavController
        this._navController = this.findNavController(R.id.activity_main_NavHostFragment)

        // AppBarConfiguration
        this._appBarConfiguration = AppBarConfiguration(this._navController.graph)

        // Action bar
        this.setupActionBarWithNavController(this._navController, this._appBarConfiguration)
    }
}