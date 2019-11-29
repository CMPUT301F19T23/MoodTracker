package com.example.moodtracker.recycle;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 *This activtiy returns the data the adapter of each list
 * @param <T>
 */
public abstract class MyRecycleAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    protected Context context;
    protected int headerLayoutId, footerLayoutId, itemLayoutId;
    protected Object head, foot;
    private int iHeader = 0, iFooter = 0;

    protected List<T> mDatas;
    private static final int HEADER_TYPE = 0;  //header
    private static final int FOOTER_TYPE = -1; //tailer
    private static final int ITEM_TYPE = 1;  //

    public MyRecycleAdapter(Context context, int headerLayoutId, Object head, int footerLayoutId, Object foot,
                            int itemLayoutId, List<T> mDatas) {
        this.context = context;
        this.headerLayoutId = headerLayoutId;
        this.footerLayoutId = footerLayoutId;
        this.itemLayoutId = itemLayoutId;

        if (headerLayoutId != -1) {
            iHeader++;
        }

        if (footerLayoutId != -1) {
            iFooter++;
        }

        this.head = head;
        this.foot = foot;

        this.mDatas = mDatas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (headerLayoutId != -1 && viewType == HEADER_TYPE) {
            return createHeaderViewHolder(viewGroup);
        } else if (footerLayoutId != -1 && viewType == FOOTER_TYPE) {
            return createFooterViewHolder(viewGroup);
        } else/* if (viewType == ITEM_TYPE)*/ {
            return createItemViewHolder(viewGroup);
        }
    }

    private RecyclerView.ViewHolder createHeaderViewHolder(ViewGroup viewGroup) {
        View headerView = LayoutInflater.from(viewGroup.getContext()).inflate(headerLayoutId, viewGroup, false);
        return new HeaderViewHolder(headerView);
    }

    private RecyclerView.ViewHolder createFooterViewHolder(ViewGroup viewGroup) {
        View footerView = LayoutInflater.from(viewGroup.getContext()).inflate(footerLayoutId, viewGroup, false);
        return new FooterViewHolder(footerView);
    }

    private RecyclerView.ViewHolder createItemViewHolder(ViewGroup viewGroup) {
        //Instantiate the sublayout
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(itemLayoutId, viewGroup, false);
        //Get an instance of the ViewHolder
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            bindViewForItem(holder, position);
        } else if (holder instanceof HeaderViewHolder) {
            bindViewForHeader(holder);
        } else if (holder instanceof FooterViewHolder) {
            bindViewForFooter(holder);
        }
    }

    private void bindViewForHeader(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

        convertHeader(headerViewHolder, head);
    }

    private void bindViewForFooter(RecyclerView.ViewHolder holder) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) holder;

        convertFooter(footerViewHolder, foot);
    }

    private void bindViewForItem(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

        itemViewHolder.mConvertView.setOnClickListener(this);
        itemViewHolder.mConvertView.setOnLongClickListener(this);

        itemViewHolder.mConvertView.setTag(position - iHeader);
        convertItem(itemViewHolder, getItem(position));
    }

    public abstract void convertHeader(HeaderViewHolder helper, Object item);

    public abstract void convertFooter(FooterViewHolder helper, Object item);

    public abstract void convertItem(ItemViewHolder helper, T item);

    @Override
    public int getItemCount() {
        //return mDatas.size();
        return mDatas == null ? (iHeader + iFooter) : mDatas.size() + (iHeader + iFooter); //control the number of position
    }

    protected T getItem(int position) {
        return mDatas.get(position - iHeader);
    }

    @Override
    public int getItemViewType(int position) {
        if (HEADER_TYPE == position) {
            return 0;
        } else if (mDatas.size() >= position) {
            return 1;
        } else {
            return -1;
        }
    }

    public interface OnClickListener {
        void onClick(View view, int position);
    }

    private OnClickListener mOnClickListener = null;

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    @Override
    public void onClick(View view) {
        if (null != mOnClickListener) {
            mOnClickListener.onClick(view, (int) view.getTag());
        }
    }

    /**
     * press long
     */
    public interface OnLongClickListener {
        void onLongClick(View view, int position);
    }

    private OnLongClickListener mOnLongClickListener = null;

    public void setOnLongClickListener(OnLongClickListener listener) {
        mOnLongClickListener = listener;
    }

    @Override
    public boolean onLongClick(View view) {
        if (null != mOnLongClickListener) {
            mOnLongClickListener.onLongClick(view, (int) view.getTag());
        }

        // consume time
        return true;
    }


}