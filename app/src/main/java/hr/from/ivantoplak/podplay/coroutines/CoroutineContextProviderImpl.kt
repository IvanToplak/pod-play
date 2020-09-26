package hr.from.ivantoplak.podplay.coroutines

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class CoroutineContextProviderImpl @Inject constructor() : CoroutineContextProvider {

    override fun main(): CoroutineContext = Dispatchers.Main

    override fun io(): CoroutineContext = Dispatchers.IO

    override fun default(): CoroutineContext = Dispatchers.Default
}