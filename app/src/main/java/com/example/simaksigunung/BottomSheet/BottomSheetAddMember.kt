package com.example.simaksigunung.BottomSheet

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simaksigunung.BottomSheet.BottomSheetListener.BottomSheetListener
import com.example.simaksigunung.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetAddMember : BottomSheetDialogFragment(){
    private var listener : BottomSheetListener? = null
    private var initialScrollViewPaddingBottom = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.bottom_sheet_add_personel, container, false)
        var scrollView = view.findViewById<View>(R.id.scrollView)
        initialScrollViewPaddingBottom = view.findViewById<View>(R.id.scrollView).paddingBottom
        val onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                scrollView.viewTreeObserver.addOnGlobalLayoutListener {
                    val rect = android.graphics.Rect()
                    scrollView.getWindowVisibleDisplayFrame(rect)
                    val screenHeight = scrollView.rootView.height
                    val keypadHeight = screenHeight - rect.bottom

                    if (keypadHeight == keypadHeight) { // Keyboard muncul
                        val paddingBottom = screenHeight - initialScrollViewPaddingBottom
                        if (paddingBottom > 0) {
                            scrollView.setPadding(
                                scrollView.paddingLeft,
                                scrollView.paddingTop,
                                scrollView.paddingRight,
                                paddingBottom
                            )

                        }
                    } else { // Keyboard tertutup
                        scrollView.setPadding(
                            scrollView.paddingLeft,
                            scrollView.paddingTop,
                            scrollView.paddingRight,
                            initialScrollViewPaddingBottom
                        )
                    }
                }
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Pastikan activity mengimplementasikan interface
        if (context is BottomSheetListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement BottomSheetListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setWindowAnimations(R.style.BottomSheetAnimationExit)
        return dialog
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.BottomSheetAnimationEnter)
        dialog?.window?.setDimAmount(0.5f) // Set transparansi latar belakang
    }
    override fun onStop() {
        super.onStop()
        dismiss()
    }
}