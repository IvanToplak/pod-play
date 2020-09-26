package hr.from.ivantoplak.podplay.coroutines

import kotlin.coroutines.CoroutineContext

interface CoroutineContextProvider {

    fun main(): CoroutineContext

    fun io(): CoroutineContext

    fun default(): CoroutineContext
}