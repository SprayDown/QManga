package org.spray.qmanga.ui.base.listener;

import android.content.res.Resources;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * Created by Ali Hosseini on 8/8/16.
 * this class provide infinite scrolling behavior for recycler View
 */
public class InfiniteScrollProvider extends RecyclerView.OnScrollListener {

    /**
     * Current window height pixels
     */
    private final int windowHeight;
    /**
     * Setup infinite scrolling behavior for reverse scrolling direction
     */
    private boolean isReverse;

    /**
     * Counter for scrolling steps
     */
    private int page;

    /**
     * Number of items per page
     */
    private int itemsPerPage;

    /**
     * {@link RecyclerView} that we want to provide infinite scrolling behavior for it
     */
    private RecyclerView recyclerView;

    /**
     * listener that trigger when user reach end of list.
     */
    private OnLoadMoreListener onLoadMoreListener;

    /**
     * {@link GridLayoutManager} that is attached to {@link #recyclerView}
     * <p/>
     * used to determine {@link #lastVisibleItem},{@link #totalItemCount}
     */
    private RecyclerView.LayoutManager layoutManager;

    /**
     * position of last visible item
     */
    private int lastVisibleItem;

    /**
     * position of first visible item
     */
    private int firstVisibleItem;

    /**
     * total items badgeCount of {@link #recyclerView}
     */
    private int totalItemCount;

    /**
     * {@link #onLoadMoreListener} called when {@link #recyclerView} reach to item with position {@link #totalItemCount}
     */
    private int threshold;

    /**
     * span count of {@link #layoutManager}
     */
    private int spanCount;

    /**
     * determines is nested scroll or not
     */
    private boolean nestedScroll;


    public InfiniteScrollProvider() {
        this.windowHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        this.nestedScroll = false;
        this.isReverse = false;
        this.threshold = 3;
        this.spanCount = 1;
        this.page = 0;
    }

    /**
     * this function attach {@link #recyclerView} to provide infinite scroll for it
     *
     * @param recyclerView see {@link #recyclerView} for more information
     * @param itemsPerPage Specifies how many items appear on each page
     */
    public void attach(RecyclerView recyclerView, int itemsPerPage) {
        attach(recyclerView, itemsPerPage, false);
    }

    /**
     * this function attach {@link #recyclerView} to provide infinite scroll for it
     *
     * @param recyclerView     see {@link #recyclerView} for more information
     * @param itemsPerPage     Specifies how many items appear on each page
     * @param reverseScrolling If the scroll is from the bottom to up,
     *                         like a chat page, infiniteScroll can be mirrored
     */
    public void attach(RecyclerView recyclerView, int itemsPerPage, boolean reverseScrolling) {
        this.page = 0;
        this.isReverse = reverseScrolling;
        this.itemsPerPage = itemsPerPage;
        this.recyclerView = recyclerView;
        this.layoutManager = recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(this);
    }


    /**
     * this function detaches {@link #recyclerView} from this infinite scroll.
     */
    public void detach() {
        try {
            this.recyclerView.removeOnScrollListener(this);
            this.isReverse = false;
            this.recyclerView = null;
            this.layoutManager = null;
            this.page = 0;
            Log.i("InfiniteScroll", "detach: Done");
        } catch (NullPointerException e) {
            Log.i("InfiniteScroll", "detach: No recycler attached");
        }
    }

    /**
     * @param onLoadMoreListener callback for notifying when user reach list ends.
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }


    /**
     * manually calling {@link OnLoadMoreListener}
     */
    public void retryLoadMore() {
        if (onLoadMoreListener != null) {
            onLoadMoreListener.onLoadMore(this.page);
        }
    }

    /**
     * @param threshold Setting the infinite scroll loading threshold
     *                  it can't be smaller than 2 for logical reasons!
     */
    public void setThreshold(int threshold) {
        if (threshold <= 2) {
            this.threshold = 2;
        } else {
            this.threshold = threshold;
        }
    }

    /**
     * @return the current page number
     */
    public int getCurrentPage() {
        return page;
    }

    public void setHasNestedScroll(boolean nestedScroll) {
        this.nestedScroll = nestedScroll;
    }

    /**
     * @param page manually changes the page number
     */
    public void setCurrentPage(int page) {
        this.page = page;
    }


    /**
     * this function get scrolling control of {@link #recyclerView} and whenever
     * user reached list ends, {@link #onLoadMoreListener#onLoadMoreListener} will be called
     */
    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

        if (layoutManager instanceof GridLayoutManager) {
            lastVisibleItem = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            firstVisibleItem = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof LinearLayoutManager) {
            lastVisibleItem = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            firstVisibleItem = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
            int[] firstVisibleItemPositions = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null);
            // get maximum element within the list
            lastVisibleItem = getLastVisibleItem(lastVisibleItemPositions);
            firstVisibleItem = getFirstVisibleItem(firstVisibleItemPositions);
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }

        if (nestedScroll) {
            checkLastItemCorrectness();
        }

        try {
            totalItemCount = layoutManager.getItemCount();
        } catch (NullPointerException e) {
            totalItemCount = 0;
        }

        if (totalItemCount > threshold && itemsPerPage > 0) {

            if (checkReachedTheThreshold(spanCount * threshold) && onLoadMoreListener != null) {

                int step = (totalItemCount + itemsPerPage - 1) / itemsPerPage;

                if (step > page) {
                    page = step;
                    onLoadMoreListener.onLoadMore(page);
                    Log.i("InfiniteScroll", "End Of the List This is step : " + page);
                }
            }
        }

        super.onScrolled(recyclerView, dx, dy);
    }

    /**
     * Checking correctness of the {@see lastVisibleItem}
     * Some parent views like nested scroll view affects
     * recycler view scrolling behaviour and this prevents
     * the layout manager from returning the last item correctly
     */
    private void checkLastItemCorrectness() {

        View item = layoutManager.getChildAt(0);
        if (item != null) {

            int itemHeight = item.getHeight();
            int maxVisibleItem = spanCount + firstVisibleItem + ((windowHeight + itemHeight - 1) / itemHeight) * spanCount;
            if (lastVisibleItem > maxVisibleItem) {
                lastVisibleItem = maxVisibleItem;
            }
        }
    }


    /**
     * @return check if scroll position is on threshold or not
     * it depends on {@param isReverse} which specifies
     * the direction of the infinite scroll
     */
    private boolean checkReachedTheThreshold(int threshold) {
        if (isReverse) {
            return firstVisibleItem < threshold;
        } else {
            return lastVisibleItem > (totalItemCount - threshold);
        }
    }

    /**
     * @return Last visible item position for staggeredGridLayoutManager
     */
    private int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int position : lastVisibleItemPositions) {
            if (position > maxSize) {
                maxSize = position;
            }
        }
        return maxSize;
    }

    /**
     * @return First visible item position for staggeredGridLayoutManager
     */
    private int getFirstVisibleItem(int[] firstVisibleItemPositions) {
        int minSize = 0;
        if (firstVisibleItemPositions.length > 0) {
            minSize = firstVisibleItemPositions[0];
            for (int position : firstVisibleItemPositions) {
                if (position < minSize) {
                    minSize = position;
                }
            }
        }
        return minSize;
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int step);
    }

}