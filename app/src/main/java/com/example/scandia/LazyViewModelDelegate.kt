package com.example.scandia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import kotlin.reflect.KProperty

class LazyViewModelDelegate<T : ViewModel>(
    private val viewModelClass: Class<T>,
    private val viewModelProducer: () -> T
) {

    private lateinit var provider: ViewModelProvider

    operator fun getValue(owner: ViewModelStoreOwner, property: KProperty<*>): T {
        if (!::provider.isInitialized) {
            provider = ViewModelProvider(owner, object : ViewModelProvider.Factory {
                //Unchecked cast because we only request a ViewModel of type T from this provider
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return viewModelProducer.invoke() as T
                }
            })
        }

        return provider[viewModelClass]
    }
}

inline fun <reified T : ViewModel> viewModelFactory(noinline factory: () -> T) =
    LazyViewModelDelegate(T::class.java, factory)