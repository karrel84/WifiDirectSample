/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package karrel.kr.co.wifidirectsample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.*
import android.net.wifi.p2p.WifiP2pManager.Channel
import android.util.Log
import karrel.kr.co.wifidirectsample.event.*

class WiFiDirectBroadcastReceiver(private val manager: WifiP2pManager?, private val channel: Channel) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        println("WiFiDirectBroadcastReceiver action : $action")

        // 와이파이 활성화 / 비활성화
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION == action) {
            val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
            sendStatusChange(state)
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION == action) {
            // 사용 가능한 피어 목록이 변경되었음을 나타내는 브로드 캐스트 인텐트, PEERS_CHANGED
            if (manager != null) {
                val peerListListener = WifiP2pManager.PeerListListener { peerList -> sendPeerListEvent(peerList) }
                manager.requestPeers(channel, peerListListener)
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION == action) {
            // Wifi p2p 연결 상태가 변경되었음을 나타내는 브로드캐스트 의도, CONNECTION_STATE_CHANGE
            if (manager != null) {
                setConnectionInfo(intent, manager)
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION == action) {
            // 해당 장치의 세부 정보가 변경되었음을 나타내는 브로드 캐스트 의도, THIS_DEVICE_CHANGED
            sendMyDeviceInfo(intent)

        }
    }

    // 접속 가능 와이파이 다이렉트 정보 보내기
    private fun sendPeerListEvent(peerList: WifiP2pDeviceList) {
        PeerListEvent.send(peerList)
    }

    // 내 정보 보내기
    private fun sendMyDeviceInfo(intent: Intent) {
        val myDeviceInfo = intent.getParcelableExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
        MyDeviceInfoEvent.send(myDeviceInfo)

        println("WiFiDirectBroadcastReceiver myDeviceInfo : $myDeviceInfo")
    }

    // 접속정보 보내기
    private fun setConnectionInfo(intent: Intent, manager: WifiP2pManager) {
        val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
        if (networkInfo.isConnected) {
            manager.requestConnectionInfo(channel) { info ->
                println("WiFiDirectBroadcastReceiver info : $info")
                ConnectionInfoEvent.send(info)
            }
        } else {
            ResetDataEvent.send(true)
        }
    }

    // 상태변경 보내기
    private fun sendStatusChange(state: Int) {
        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            StatusChangedEvent.send(WifiEnable.ENABLE)
        } else {
            StatusChangedEvent.send(WifiEnable.DISABLE)
        }
    }
}
