package by.yazazzello.forum.client.features

import android.support.v7.widget.RecyclerView
import android.view.View

class Bindable<T>(var model: T) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Bindable<*>

        if (model != other.model) return false

        return true
    }

    override fun hashCode(): Int {
        return model?.hashCode() ?: 0
    }

    fun set(value: T){
        model = value
    }
}

abstract class BasicItemVH constructor(view: View?) : RecyclerView.ViewHolder(view) {
    abstract fun bind(bindable: Bindable<*>)
    open fun recycle() {}
}