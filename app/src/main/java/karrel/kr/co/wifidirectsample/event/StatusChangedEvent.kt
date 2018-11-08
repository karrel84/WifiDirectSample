package karrel.kr.co.wifidirectsample.event

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by Rell on 2018. 11. 5..
 */
class StatusChangedEvent {

    companion object {
        private val observable = PublishSubject.create<WifiEnable>()

        open fun receive(): Observable<WifiEnable> = observable

        open fun send(value: WifiEnable) {
            observable.onNext(value)
        }
    }
}

enum class WifiEnable {
    ENABLE, DISABLE
}