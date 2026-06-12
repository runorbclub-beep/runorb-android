package com.cloud.runball.module.match_football_association.widget.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.cloud.runball.R;
import com.cloud.runball.module.match_football_association.widget.domain.SortModel;

import java.util.List;

/**
 * 按照名称首字母进行排序的adapter
 */
public class AlphabetSortAdapter extends BaseAdapter implements SectionIndexer {

    private List<SortModel> list = null;
    private Context mContext;
    private int selectedPosition = -1;

    public AlphabetSortAdapter(Context mContext, List<SortModel> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    final static class ViewHolder {
        TextView tv_fistletters;
        View vLine1;
        TextView tv_info;
        ImageView ivSelected;
    }

    // 更新ListView
    public void updateListView(List<SortModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final SortModel mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.alphabet_list_item, null);
            viewHolder.tv_fistletters = view.findViewById(R.id.tv_fistletters);
            viewHolder.vLine1 = view.findViewById(R.id.vLine1);
            viewHolder.tv_info = view.findViewById(R.id.tv_info);
            viewHolder.ivSelected = view.findViewById(R.id.ivSelected);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // 根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tv_fistletters.setVisibility(View.VISIBLE);
            viewHolder.vLine1.setVisibility(View.VISIBLE);
            viewHolder.tv_fistletters.setText(mContent.fistLetter);
        } else {
            viewHolder.tv_fistletters.setVisibility(View.GONE);
            viewHolder.vLine1.setVisibility(View.GONE);
        }
        viewHolder.tv_info.setText(this.list.get(position).info);

        if (position == selectedPosition) {
            viewHolder.ivSelected.setImageLevel(2);
        } else  {
            viewHolder.ivSelected.setImageLevel(1);
        }
//        view.setOnClickListener(v -> {
//            selectedPosition = position;
//            notifyDataSetChanged();
//        });
        return view;
    }

    public void setPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }


    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).fistLetter.charAt(0);
    }

    /**
     * 获取第一次出现该首字母的List所在的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).fistLetter;
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据ListView的位置获取对应的首字母
     */
    public String getAlpha(int position) {
        return list.get(position).fistLetter;
    }

}
