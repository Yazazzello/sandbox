package by.yazazzello.forum.client.ui

import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Test

class SandBoxTests {

    @Test
    fun sample(){
        Maybe.fromCallable<String> { null }
                .defaultIfEmpty("some")
                .switchIfEmpty(Single.just("backplan"))
                .test()
                .assertResult("some")
    }
}