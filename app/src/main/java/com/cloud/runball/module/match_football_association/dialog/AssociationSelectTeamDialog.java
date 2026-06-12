package com.cloud.runball.module.match_football_association.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseDialog;
import com.cloud.runball.module.match_football_association.widget.adapter.AlphabetSortAdapter;
import com.cloud.runball.module.match_football_association.widget.domain.SortModel;
import com.cloud.runball.module.match_football_association.widget.utils.CharacterParser;
import com.cloud.runball.module.match_football_association.widget.widget.SideBarView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AssociationSelectTeamDialog extends BaseDialog {

  private Callback callback;

  private final List<String> data = new ArrayList<>();
  private int selectedPosition = -1;

  private final static String TAG = "shz_debug";

  private PinyinComparator mPinyinComparator;
  private AlphabetSortAdapter mAlphabetAadpter;

  private List<SortModel> sourceDataList;
  // 中文转拼音工具栏（字母列表实现的核心工具栏）
  private CharacterParser mCharacterParser;

  private EditText et_searchview;
  private ListView sortListView;
  private SideBarView sideBarView;

  private ImageView ivClose;

  private final List<String> sideBarData = new ArrayList<>();

  public AssociationSelectTeamDialog(Context context) {
    super(context, R.layout.dialog_association_select_team);
  }

  @Override
  protected void onContentView(View contentView) {
    sortListView = contentView.findViewById(R.id.name_listview);
    sideBarView = contentView.findViewById(R.id.sideBarView);
    et_searchview = contentView.findViewById(R.id.et_searchview);

    ivClose= contentView.findViewById(R.id.ivClose);
    ivClose.setOnClickListener(v -> {
      if (dialog != null) {
        dialog.dismiss();
      }
    });

    contentView.findViewById(R.id.tvSubmit).setOnClickListener(view -> {
      if (callback != null) {
        if (selectedPosition < 0) {
          Toast.makeText(view.getContext(), "请选择队伍", Toast.LENGTH_SHORT).show();
        } else {
          callback.onSubmit(dialog, sourceDataList.get(selectedPosition).info);
        }
      }
    });
  }

  public void setData(List<String> data) {
    this.data.clear();
    this.data.addAll(data);

    initValues();
    initListeners();

    sortListView.setAdapter(mAlphabetAadpter);
  }

  private void initValues() {
    mCharacterParser = new CharacterParser();
    mPinyinComparator = new PinyinComparator();
    sourceDataList = loadFakeData(data);
    // 根据a-z进行排序源数据
    Collections.sort(sourceDataList, mPinyinComparator);
    mAlphabetAadpter = new AlphabetSortAdapter(dialog.getContext(), sourceDataList);
    sortListView.setAdapter(mAlphabetAadpter);

    Collections.sort(sideBarData, new CharactersComparator());
    sideBarView.updateListView(sideBarData);
  }

  private void initListeners() {
    sortListView.setOnItemClickListener((parent, view, position, id) -> {
      // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
      selectedPosition = position;
      mAlphabetAadpter.setPosition(selectedPosition);
    });

    sortListView.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
          case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
            //手指用力滑动
            //手指离开listview后由于惯性继续滑动
            break;
          default:
            break;
        }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

      }
    });

//    sortListView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//        @Override
//        public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//            int positon = sortListView.getFirstVisiblePosition();
//            String alpha = mAlphabetAadpter.getAlpha(positon);
//            sideBarView.setCurrCharacter(alpha);
//            Log.d(TAG, "onScrollChange positon:" + positon + ", alpha:" + alpha);
//        }
//    });

    sortListView.setOnTouchListener((v, event) -> {
      return false;
    });

    // 设置右侧触摸监听
    sideBarView.setOnTouchingLetterChangedListener(s -> {
      // 该字母首次出现的位置
      Log.d(TAG, "onTouchingLetterChanged:" + s);
      int position = mAlphabetAadpter.getPositionForSection(s.charAt(0));
      if (position != -1) {
        sortListView.setSelection(position);
      }
    });

    // 根据输入框输入值的改变来过滤搜索
    et_searchview.addTextChangedListener(new TextWatcher() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
        startSearch(s.toString());
      }
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
      @Override
      public void afterTextChanged(Editable s) { }
    });

  }

  // 加载数据
  private List<SortModel> loadFakeData(List<String> data) {
    List<SortModel> mSortList = new ArrayList<>();
    sideBarData.clear();
    for (int i = 0; i < data.size(); i++) {
      SortModel sortModel = new SortModel();
      sortModel.info = data.get(i);
      sortModel.fistLetter = mCharacterParser.getSortKey(data.get(i));
      if (!sideBarData.contains(sortModel.fistLetter)) {
        sideBarData.add(sortModel.fistLetter);
      }
      mSortList.add(sortModel);
    }
    return mSortList;
  }


  /**
   * 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
   */
  private void startSearch(String filterStr) {
    List<SortModel> startSearchList = new ArrayList<>();
    if (TextUtils.isEmpty(filterStr)) {
      startSearchList = sourceDataList;
    } else {
      for (SortModel sortModel : sourceDataList) {
        String name = sortModel.info;
        if (name.toUpperCase().contains(filterStr.toUpperCase())
            || mCharacterParser.getPinYin(name).toUpperCase().contains(filterStr.toUpperCase())) {
          startSearchList.add(sortModel);
        }
      }
    }

    Collections.sort(startSearchList, mPinyinComparator);
    mAlphabetAadpter.updateListView(startSearchList);
//    sideBarView.updateListView(charactersData);
  }

  public static class CharactersComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
      return o1.compareTo(o2);
    }
  }


  // 根据a-z进行排序
  public static class PinyinComparator implements Comparator<SortModel> {
    public int compare(SortModel o1, SortModel o2) {
      if (o1.fistLetter.equals("#")) {
        return -1;
      } else {
        return o1.fistLetter.compareTo(o2.fistLetter);
      }
    }
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  public interface Callback {
    void onSubmit(Dialog dialog, String team);
  }

}
