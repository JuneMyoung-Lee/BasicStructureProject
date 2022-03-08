package com.jmp.basicstructureproject.presentation.extension

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

fun LifecycleOwner.addObserver(
    onStartCallback: () -> Unit,
    onDestroyCallback: () -> Unit
) {
    if (lifecycle.currentState == Lifecycle.State.DESTROYED) return
    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart() {
            onStartCallback()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            onDestroyCallback()
        }
    })
}