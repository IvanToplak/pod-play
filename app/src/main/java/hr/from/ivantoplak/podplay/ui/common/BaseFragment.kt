package hr.from.ivantoplak.podplay.ui.common

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doOnCreate(savedInstanceState)
    }

    protected open fun doOnCreate(savedInstanceState: Bundle?) {}

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doOnViewCreated(view, savedInstanceState)
    }

    protected open fun doOnViewCreated(view: View, savedInstanceState: Bundle?) {}

    final override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        doOnActivityCreated(savedInstanceState)
    }

    protected open fun doOnActivityCreated(savedInstanceState: Bundle?) {}

    final override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        doOnCreateOptionsMenu(menu, inflater)
    }

    protected open fun doOnCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {}

    final override fun onStart() {
        super.onStart()
        doOnStart()
    }

    protected open fun doOnStart() {}

    final override fun onResume() {
        super.onResume()
        doOnResume()
    }

    protected open fun doOnResume() {}

    final override fun onPause() {
        doOnPause()
        super.onPause()
    }

    protected open fun doOnPause() {}

    final override fun onStop() {
        doOnStop()
        super.onStop()
    }

    protected open fun doOnStop() {}

    override fun onDestroyView() {
        doOnDestroyView()
        super.onDestroyView()
    }

    protected open fun doOnDestroyView() {}

    final override fun onDestroy() {
        doOnDestroy()
        super.onDestroy()
    }

    protected open fun doOnDestroy() {}
}