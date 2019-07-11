package by.yazazzello.forum.client.helpers

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import timber.log.Timber

abstract class EndlessRecyclerOnScrollListener(val mLinearLayoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {
    private var previousTotal = 0 // The total number of items in the dataset after the last load
    private var loading = true // True if we are still waiting for the last set of data to load.
    private val visibleThreshold = 5 // The minimum amount of items to have below your current scroll position before loading more.
    private var firstVisibleItem: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy > 0) {   //check for scroll down
            visibleItemCount = recyclerView!!.childCount
            totalItemCount = mLinearLayoutManager.itemCount
            firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition()

            if (loading) {
                if (totalItemCount > previousTotal) {
                    Timber.d("onScrolled() totalItemCount = $totalItemCount + previousTotal = $previousTotal")
                    loading = false
                    previousTotal = totalItemCount
                    Timber.d("is loading set to false")
                }
            }
            if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
                // End has been reached
                Timber.d("End has been reached")
                onLoadMore()
                loading = true
            }
        }
    }


    fun resetPageCount() {
        firstVisibleItem = 0
        visibleItemCount = 0
        totalItemCount = 0
        previousTotal = 0
        loading = true
    }

    abstract fun onLoadMore()
}