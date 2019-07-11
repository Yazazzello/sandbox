package by.yazazzello.forum.client.ui

import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers
import org.junit.Ignore
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by yazazzello on 10/21/17.
 */
class RxSamplesTests {


    @Test
    @Ignore
    fun sandboxRetryWHen() {
        val some = WraperInt()
        Maybe.fromCallable<Int> {
            if (some.someValue < 3) {
                some.someValue.inc()
                throw IllegalArgumentException("beee")
            }
            some.someValue
        }

                .doOnError { println("error is detected " + it.message + " counter ${some.someValue}") }
                .retryWhen {
                    Flowable.range(1, 5).delay(200, TimeUnit.MILLISECONDS, Schedulers.trampoline()).doOnNext {

                        some.someValue += 1
                        println("retry ${some.someValue}")


                    }
                }
                .subscribe({
                    println("value $it")
                }, {
                    //                    it.printStackTrace()
                })
    }

    @Test
    @Ignore
    fun sandboxRetry() {
        val rand = Random(8)

        Maybe.fromCallable<Int> {

            val nextInt = rand.nextInt(10)
            println("callable $nextInt")
            if (nextInt < 8) {
                throw IllegalArgumentException("beee")
            }
            nextInt
        }

                .doOnError { println("error is detected " + it.message ) }
                .retry(5)
                .subscribe({
                    println("value $it")
                }, {
                    //                    it.printStackTrace()
                })
    }

    @Test
    @Ignore
    fun sandboxExceptionRetry() {
        Maybe.just(null)
                .map { throw IllegalArgumentException("some") }
                .subscribe(
                        { },
                        { print("got it")})
    }

    class WraperInt {
        var someValue: Int = 0
    }
}