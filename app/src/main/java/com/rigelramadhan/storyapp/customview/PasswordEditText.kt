package com.rigelramadhan.storyapp.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.core.widget.doOnTextChanged
import com.rigelramadhan.storyapp.R

class PasswordEditText : CustomEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank()) {
                error = if (text.length <= 6) {
                    resources.getString(R.string.password_minimum)
                } else {
                    null
                }
            }
        }
    }
}