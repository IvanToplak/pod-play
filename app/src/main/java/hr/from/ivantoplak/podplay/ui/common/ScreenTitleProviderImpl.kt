package hr.from.ivantoplak.podplay.ui.common

import java.util.*

class ScreenTitleProviderImpl : ScreenTitleProvider {

    private val titles: Deque<String> = ArrayDeque<String>()
    private var setTitle: ((String) -> Unit)? = null
    private var setTitleVisibility: ((Boolean) -> Unit)? = null

    override fun addTitle(title: String) {
        if (titles.peek() != title) {
            titles.remove(title)
            titles.push(title)
            setTitle?.invoke(title)
        }
    }

    override fun removeTitle() {
        titles.peek()?.let {
            titles.pop()
            titles.peek()?.let { title ->
                setTitle?.invoke(title)
            }
        }
    }

    override fun setTitleVisibility(show: Boolean) {
        setTitleVisibility?.invoke(show)
    }

    override fun registerSetTitleFunction(setTitle: (String) -> Unit) {
        this.setTitle = setTitle
    }

    override fun registerSetTitleVisibilityFunction(setTitleVisibility: (Boolean) -> Unit) {
        this.setTitleVisibility = setTitleVisibility
    }
}