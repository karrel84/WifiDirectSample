package karrel.kr.co.wifidirectsample.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import karrel.kr.co.wifidirectsample.R
import karrel.kr.co.wifidirectsample.event.SendFilePercentEvent
import kotlinx.android.synthetic.main.activity_progress.*

/**
 * Created by Rell on 2018. 11. 7..
 */
class ProgressActivity : AppCompatActivity() {
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        setupEvent()
    }

    private fun setupEvent() {
        disposable = SendFilePercentEvent.receive()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .map {
                    val total = it.first
                    val sum = it.second
                    val percent = sum.toFloat() / total.toFloat() * 100
                    Pair("파일을 전송중입니다.\n${percent.toLong()}%($sum / $total)", percent)
                }
                .subscribe {
                    message.text = it.first
                    if (it.second == 100f) finish()
                }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}