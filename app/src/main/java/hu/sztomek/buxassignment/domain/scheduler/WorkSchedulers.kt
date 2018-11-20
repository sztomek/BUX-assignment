package hu.sztomek.buxassignment.domain.scheduler

import io.reactivex.Scheduler

interface WorkSchedulers {

    fun io(): Scheduler

    fun ui(): Scheduler

    fun computation(): Scheduler

}