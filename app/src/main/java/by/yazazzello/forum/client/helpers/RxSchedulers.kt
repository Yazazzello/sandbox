
package by.yazazzello.forum.client.helpers

import io.reactivex.Scheduler

class RxSchedulers(val ioScheduler: Scheduler, val mainThreadScheduler: Scheduler, val singleThreadScheduler: Scheduler)