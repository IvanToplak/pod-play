package hr.from.ivantoplak.podplay.ui.common

import dagger.hilt.android.AndroidEntryPoint
import hr.from.ivantoplak.podplay.router.Router
import javax.inject.Inject

@AndroidEntryPoint
abstract class HiltFragment : BaseFragment() {

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var screenTitleProvider: ScreenTitleProvider

    protected fun setScreenTitleVisibility(show: Boolean) =
        screenTitleProvider.setTitleVisibility(show)
}