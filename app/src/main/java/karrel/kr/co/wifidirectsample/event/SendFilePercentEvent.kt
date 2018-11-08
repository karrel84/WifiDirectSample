package karrel.kr.co.wifidirectsample.event

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by Rell on 2018. 11. 7..
 */
class SendFilePercentEvent {
    companion object {
        private val observable = PublishSubject.create<Pair<Long, Long>>()

        fun receive(): Observable<Pair<Long, Long>> = observable

        fun send(total: Long, sum: Long) {
            observable.onNext(Pair(total, sum))
        }
    }
}