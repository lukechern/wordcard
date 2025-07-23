package com.x7ree.wordcard.utils

import android.content.Context
import android.net.wifi.WifiManager
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

/**
 * 网络工具类，用于获取设备的局域网IP地址
 */
class NetworkUtils_7ree {
    companion object {
        /**
         * 获取设备的局域网IP地址
         */
        fun getLocalIpAddress(): String? {
            try {
                val interfaces = NetworkInterface.getNetworkInterfaces()
                while (interfaces.hasMoreElements()) {
                    val networkInterface = interfaces.nextElement()
                    val addresses = networkInterface.inetAddresses
                    while (addresses.hasMoreElements()) {
                        val address = addresses.nextElement()
                        if (!address.isLoopbackAddress && address is Inet4Address) {
                            val ip = address.hostAddress
                            // 过滤掉移动网络IP，只返回局域网IP
                            if (ip?.startsWith("192.168.") == true || 
                                ip?.startsWith("10.") == true || 
                                ip?.startsWith("172.") == true) {
                                return ip
                            }
                        }
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            return null
        }
        
        /**
         * 通过WiFi管理器获取IP地址（备用方法）
         */
        fun getWifiIpAddress(context: Context): String? {
            try {
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                val ipInt = wifiInfo.ipAddress
                
                if (ipInt != 0) {
                    return String.format(
                        "%d.%d.%d.%d",
                        ipInt and 0xff,
                        ipInt shr 8 and 0xff,
                        ipInt shr 16 and 0xff,
                        ipInt shr 24 and 0xff
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
        
        /**
         * 获取最佳的局域网IP地址
         */
        fun getBestLocalIpAddress(context: Context): String? {
            // 优先使用NetworkInterface方法
            getLocalIpAddress()?.let { return it }
            
            // 备用WiFi管理器方法
            return getWifiIpAddress(context)
        }
    }
}