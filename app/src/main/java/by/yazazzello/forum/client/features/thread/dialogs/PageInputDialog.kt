package by.yazazzello.forum.client.features.thread.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import by.yazazzello.forum.client.R
import timber.log.Timber

/**
 * Created by yazazzello on 11/23/16.
 */

class PageInputDialog : DialogFragment() {

    private var selectedPage: Int = 0
    private lateinit var etInput: TextInputEditText

    interface PageInputCallback {

        fun onPageInput(pageNumber: Int)
    }

    private var maxPage: Int = 0

    var pageInputCallback: PageInputCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        maxPage = arguments?.getInt(KEY_PAGE) ?: 0
        ++maxPage
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_page_input, null, false)
        etInput = view.findViewById(R.id.et_page_input)
        val tiLayout = view.findViewById(R.id.ti_page) as TextInputLayout
        etInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                val string = editable.toString()
                try {
                    val page = Integer.parseInt(string)
                    if (page > maxPage || page < 0) {
                        tiLayout.isErrorEnabled = true
                        tiLayout.error = "there is no such page"
                    } else {
                        tiLayout.isErrorEnabled = false
                        selectedPage = page
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }

            }
        })
        etInput.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                this@PageInputDialog.hideKeyboard(etInput)
                this@PageInputDialog.notifyPageSelected()
                return@OnEditorActionListener true
            }
            false
        })
        val maxPageStr = maxPage.toString()
        etInput.setText(maxPageStr)
        etInput.setSelection(maxPageStr.length - 1, maxPageStr.length)
        tiLayout.hint =  "max page is $maxPage"
        val builder = AlertDialog.Builder(context!!)
                .setTitle("Go to page")
                .setView(view)
                .setPositiveButton(android.R.string.ok) { _, _ -> this@PageInputDialog.notifyPageSelected() }
        return builder.create()

    }

    private fun notifyPageSelected() {
        if (pageInputCallback != null) {

            dialog.dismiss()
            pageInputCallback!!.onPageInput(if (selectedPage == 0) selectedPage else --selectedPage)
        }
    }

    override fun onResume() {
        super.onResume()
        showKeyboard(etInput)
    }

    private fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showKeyboard(view: View) {
        try {
            view.postDelayed({
                val imm = this@PageInputDialog.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.showSoftInput(view, 0)
            }, 200)
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    companion object {

        private val KEY_PAGE = "PAGE"

        fun newInstance(maxPage: Int): PageInputDialog {

            val args = Bundle()
            args.putInt(KEY_PAGE, maxPage)
            val fragment = PageInputDialog()
            fragment.arguments = args
            return fragment
        }
    }
}
