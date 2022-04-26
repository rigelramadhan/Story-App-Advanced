package com.rigelramadhan.storyapp.customview

import android.content.Context
import android.util.AttributeSet
import android.util.Patterns
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.rigelramadhan.storyapp.R

class EmailEditText : CustomEditText {
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
                error = if (!isEmailValid(text.toString())) {
                    resources.getString(R.string.valid_email)
                } else {
                    null
                }
            }
        }

        doAfterTextChanged {
            if (text.isNullOrEmpty()) {
                error = resources.getString(R.string.email_cannot_empty)
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}