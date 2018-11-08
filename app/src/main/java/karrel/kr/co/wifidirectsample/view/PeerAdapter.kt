package karrel.kr.co.wifidirectsample.view

import android.net.wifi.p2p.WifiP2pDevice
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import karrel.kr.co.wifidirectsample.R
import karrel.kr.co.wifidirectsample.event.ConnectPeerEvent
import kotlinx.android.synthetic.main.item_peer.view.*

/**
 * Created by Rell on 2018. 11. 6..
 */
class PeerAdapter : RecyclerView.Adapter<PeerAdapter.PeerViewHolder>() {

    private var deviceArrayList = arrayListOf<WifiP2pDevice>()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): PeerViewHolder {
        return PeerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_peer, parent, false))
    }

    override fun getItemCount(): Int {
        if (deviceArrayList == null) return 0

        return deviceArrayList!!.size
    }

    override fun onBindViewHolder(holder: PeerViewHolder, position: Int) {
        holder.setData(deviceArrayList[position])
    }

    fun setData(deviceList: Collection<WifiP2pDevice>) {
        this.deviceArrayList = arrayListOf()

        for (device in deviceList) {
            deviceArrayList.add(device)
        }

        notifyDataSetChanged()
    }


    class PeerViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        private lateinit var device: WifiP2pDevice
        val name: TextView = view?.name!!
        val address: TextView = view?.address!!

        init {

            view?.rootView?.setOnClickListener {
                ConnectPeerEvent.send(device)
            }
        }

        fun setData(device: WifiP2pDevice) {
            this.device = device
            name.text = device.deviceName
            address.text = device.deviceAddress
        }
    }
}