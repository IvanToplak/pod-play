package hr.from.ivantoplak.podplay.ui.common

interface ScreenTitleProvider {

    fun addTitle(title: String)

    fun removeTitle()

    fun setTitleVisibility(show: Boolean)

    fun registerSetTitleFunction(setTitle: (String) -> Unit)

    fun registerSetTitleVisibilityFunction(setTitleVisibility: (Boolean) -> Unit)
}