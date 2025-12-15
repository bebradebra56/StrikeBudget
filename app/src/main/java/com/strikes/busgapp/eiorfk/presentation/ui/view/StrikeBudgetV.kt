package com.strikes.busgapp.eiorfk.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.strikes.busgapp.eiorfk.presentation.app.StrikeBudgetApplication
import com.strikes.busgapp.eiorfk.presentation.ui.load.StrikeBudgetLoadFragment
import org.koin.android.ext.android.inject

class StrikeBudgetV : Fragment(){

    private lateinit var strikeBudgetPhoto: Uri
    private var strikeBudgetFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val strikeBudgetTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        strikeBudgetFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        strikeBudgetFilePathFromChrome = null
    }

    private val strikeBudgetTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            strikeBudgetFilePathFromChrome?.onReceiveValue(arrayOf(strikeBudgetPhoto))
            strikeBudgetFilePathFromChrome = null
        } else {
            strikeBudgetFilePathFromChrome?.onReceiveValue(null)
            strikeBudgetFilePathFromChrome = null
        }
    }

    private val strikeBudgetDataStore by activityViewModels<StrikeBudgetDataStore>()


    private val strikeBudgetViFun by inject<StrikeBudgetViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (strikeBudgetDataStore.strikeBudgetView.canGoBack()) {
                        strikeBudgetDataStore.strikeBudgetView.goBack()
                        Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "WebView can go back")
                    } else if (strikeBudgetDataStore.strikeBudgetViList.size > 1) {
                        Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "WebView can`t go back")
                        strikeBudgetDataStore.strikeBudgetViList.removeAt(strikeBudgetDataStore.strikeBudgetViList.lastIndex)
                        Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "WebView list size ${strikeBudgetDataStore.strikeBudgetViList.size}")
                        strikeBudgetDataStore.strikeBudgetView.destroy()
                        val previousWebView = strikeBudgetDataStore.strikeBudgetViList.last()
                        strikeBudgetAttachWebViewToContainer(previousWebView)
                        strikeBudgetDataStore.strikeBudgetView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (strikeBudgetDataStore.strikeBudgetIsFirstCreate) {
            strikeBudgetDataStore.strikeBudgetIsFirstCreate = false
            strikeBudgetDataStore.strikeBudgetContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return strikeBudgetDataStore.strikeBudgetContainerView
        } else {
            return strikeBudgetDataStore.strikeBudgetContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "onViewCreated")
        if (strikeBudgetDataStore.strikeBudgetViList.isEmpty()) {
            strikeBudgetDataStore.strikeBudgetView = StrikeBudgetVi(requireContext(), object :
                StrikeBudgetCallBack {
                override fun strikeBudgetHandleCreateWebWindowRequest(strikeBudgetVi: StrikeBudgetVi) {
                    strikeBudgetDataStore.strikeBudgetViList.add(strikeBudgetVi)
                    Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "WebView list size = ${strikeBudgetDataStore.strikeBudgetViList.size}")
                    Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "CreateWebWindowRequest")
                    strikeBudgetDataStore.strikeBudgetView = strikeBudgetVi
                    strikeBudgetVi.strikeBudgetSetFileChooserHandler { callback ->
                        strikeBudgetHandleFileChooser(callback)
                    }
                    strikeBudgetAttachWebViewToContainer(strikeBudgetVi)
                }

            }, strikeBudgetWindow = requireActivity().window).apply {
                strikeBudgetSetFileChooserHandler { callback ->
                    strikeBudgetHandleFileChooser(callback)
                }
            }
            strikeBudgetDataStore.strikeBudgetView.strikeBudgetFLoad(arguments?.getString(
                StrikeBudgetLoadFragment.STRIKE_BUDGET_D) ?: "")
//            ejvview.fLoad("www.google.com")
            strikeBudgetDataStore.strikeBudgetViList.add(strikeBudgetDataStore.strikeBudgetView)
            strikeBudgetAttachWebViewToContainer(strikeBudgetDataStore.strikeBudgetView)
        } else {
            strikeBudgetDataStore.strikeBudgetViList.forEach { webView ->
                webView.strikeBudgetSetFileChooserHandler { callback ->
                    strikeBudgetHandleFileChooser(callback)
                }
            }
            strikeBudgetDataStore.strikeBudgetView = strikeBudgetDataStore.strikeBudgetViList.last()

            strikeBudgetAttachWebViewToContainer(strikeBudgetDataStore.strikeBudgetView)
        }
        Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "WebView list size = ${strikeBudgetDataStore.strikeBudgetViList.size}")
    }

    private fun strikeBudgetHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        strikeBudgetFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "Launching file picker")
                    strikeBudgetTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "Launching camera")
                    strikeBudgetPhoto = strikeBudgetViFun.strikeBudgetSavePhoto()
                    strikeBudgetTakePhoto.launch(strikeBudgetPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                strikeBudgetFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun strikeBudgetAttachWebViewToContainer(w: StrikeBudgetVi) {
        strikeBudgetDataStore.strikeBudgetContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            strikeBudgetDataStore.strikeBudgetContainerView.removeAllViews()
            strikeBudgetDataStore.strikeBudgetContainerView.addView(w)
        }
    }


}