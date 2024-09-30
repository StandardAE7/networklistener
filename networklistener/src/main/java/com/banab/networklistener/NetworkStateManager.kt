package com.banab.networklistener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import androidx.lifecycle.LiveData
import com.banab.networklistener.iNetworkListener

class NetworkStateManager(private val context: Context) {

    private val listeners = mutableListOf<iNetworkListener>()

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // Add listeners to be notified of network state changes
    fun addListener(listener: iNetworkListener) {
        listeners.add(listener)
        checkInitialState(listener)
    }

    // Remove listener when no longer needed
    fun removeListener(listener: iNetworkListener) {
        listeners.remove(listener)
    }

    // Register the BroadcastReceiver to listen to network changes
    fun startListening() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(networkReceiver, filter)
    }

    // Unregister the BroadcastReceiver when not needed
    fun stopListening() {
        context.unregisterReceiver(networkReceiver)
    }

    // Check the initial network state when the listener is added
    private fun checkInitialState(listener: iNetworkListener) {
        if (isNetworkAvailable()) {
            listener.onNetworkAvailable()
        } else {
            listener.onNetworkUnavailable()
        }
    }

    // Check if the network is currently available
    private fun isNetworkAvailable(): Boolean {
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    // BroadcastReceiver to handle network state changes
    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val isConnected = isNetworkAvailable()

            for (listener in listeners) {
                if (isConnected) {
                    listener.onNetworkAvailable()
                } else {
                    listener.onNetworkUnavailable()
                }
            }
        }
    }
}
