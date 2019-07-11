package by.yazazzello.forum.client.features.thread

import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/**
 * Created by yazazzello on 8/26/17.
 */
@Module
class ThreadModule {

    @Provides
    internal fun provideScrollSubject() = PublishSubject.create<Int>().apply {
        throttleFirst(500, TimeUnit.MILLISECONDS)
    }

    @Provides
    internal fun provideThreadAdapter(picasso: Picasso,
                                      scrollSubject: PublishSubject<Int>) = ThreadAdapter(picasso, scrollSubject)
}