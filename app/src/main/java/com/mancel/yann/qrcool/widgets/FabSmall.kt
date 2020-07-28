package com.mancel.yann.qrcool.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.mancel.yann.qrcool.R
import kotlinx.android.synthetic.main.view_small_fab.view.*

/**
 * Created by Yann MANCEL on 28/07/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.widgets
 *
 * A [ConstraintLayout] subclass.
 */
class FabSmall(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {

    // FIELDS --------------------------------------------------------------------------------------

    var _offsetYAnimation = 0.0F
        private set

    val _labelView: TextView
        get() = this.view_label

    // CONSTRUCTORS --------------------------------------------------------------------------------

    init {
        View.inflate(context, R.layout.view_small_fab, this)

        // Attributes from xml file, called attrs.xml
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.FabSmall)

        // TextView
        this.view_label.text = attributes.getString(R.styleable.FabSmall_name)

        // FAB
        this.view_small_fab.setImageResource(attributes.getResourceId(
            R.styleable.FabSmall_iconSrc,
            R.mipmap.ic_launcher
        ))

        // Offset Y
        this._offsetYAnimation = attributes.getDimension(
            R.styleable.FabSmall_offset_y,
            this._offsetYAnimation
        )

        attributes.recycle()
    }

    // METHODS -------------------------------------------------------------------------------------

    // -- ConstraintLayout --

    override fun setOnClickListener(l: OnClickListener?) {
        this.view_small_fab.setOnClickListener(l)
    }
}