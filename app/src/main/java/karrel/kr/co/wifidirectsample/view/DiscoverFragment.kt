package karrel.kr.co.wifidirectsample.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import karrel.kr.co.wifidirectsample.R
import karrel.kr.co.wifidirectsample.event.PeerListEvent
import kotlinx.android.synthetic.main.fragment_discover.*

class DiscoverFragment : Fragment() {

    private val peerAdapter = PeerAdapter()

    private lateinit var disposiable : Disposable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPeerListView()
        setupBroadCastEvent()
    }

    private fun setupBroadCastEvent() {
        // 탐색된 디자이스 리스트
        disposiable = PeerListEvent.receive().observeOn(AndroidSchedulers.mainThread()).subscribe {
            println("MainActivity : ${it.deviceList.size}")
            peerAdapter.setData(it.deviceList)

            for (i in it.deviceList) {
                println("검색된 기기 : ${i.deviceName}")
            }
        }
    }

    private fun setupPeerListView() {
        searchedList.layoutManager = LinearLayoutManager(activity)
        searchedList.adapter = peerAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        disposiable?.dispose()
    }
}
