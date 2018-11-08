package karrel.kr.co.wifidirectsample.view

import android.annotation.SuppressLint
import android.net.wifi.p2p.WifiP2pInfo
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import karrel.kr.co.wifidirectsample.R
import karrel.kr.co.wifidirectsample.util.copyFile
import kotlinx.android.synthetic.main.fragment_server.*
import java.io.File
import java.io.FileOutputStream
import java.net.ServerSocket
import java.util.*

@SuppressLint("ValidFragment")
class ServerFragment @SuppressLint("ValidFragment") constructor(val info: WifiP2pInfo) : Fragment() {

    private var disposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_server, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()
        runServer()
    }

    private fun setupData() {
        groupOwner.text = "yes"

        Observable.just(info)
                .subscribeOn(Schedulers.io())
                .map { it.groupOwnerAddress.hostName }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    // 호스트 이름
                    hostName.text = it
                }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    private fun runServer() {
        disposable?.dispose()
        disposable = Observable.just("")
                .subscribeOn(Schedulers.io())
                .map {
                    val serverSocket = ServerSocket(8988)
                    val client = serverSocket.accept()

                    val savePath = "${Environment.getExternalStorageDirectory()}/${activity?.packageName}/${System.currentTimeMillis()}.png"
                    val f = File(savePath)

                    val dirs = File(f.parent)
                    if (!dirs.exists())
                        dirs.mkdirs()
                    f.createNewFile()

                    val inputstream = client.getInputStream()
                    copyFile(inputstream, FileOutputStream(f))
                    serverSocket.close()
                    f
                }
                .doOnError {
                    it.printStackTrace()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    runServer()
                    textView.text = "${it.path} 로 파일을 저장하는데 성공하였습니다.\n${Calendar.getInstance().time}}"
                }

    }
}
