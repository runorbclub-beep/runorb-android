package com.cloud.runball.module.clan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseDialog;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.ClanItem;
import com.cloud.runball.model.ClanModel;
import com.cloud.runball.model.MatchRankDataModel;
import com.cloud.runball.module.clan.ClanActivity;
import com.cloud.runball.module.match_football_association.widget.adapter.AlphabetSortAdapter;
import com.cloud.runball.module.match_football_association.widget.domain.SortModel;
import com.cloud.runball.module.match_football_association.widget.utils.CharacterParser;
import com.cloud.runball.module.match_football_association.widget.widget.SideBarView;
import com.cloud.runball.module.rank.adapter.ClanRankAdapter;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.cloud.runball.utils.AppLogger;
import com.cloud.runball.utils.Constant;
import com.cloud.runball.widget.CircleTransform;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class SearchClanDialog extends BaseDialog {

  private Callback callback;

  private EditText etSearch;
  private XRecyclerView recyclerview;
  private int page = 1;
  private final List<ClanItem> data = new ArrayList<>();

  private CompositeDisposable disposable;

  public SearchClanDialog(Context context) {
    super(context, R.layout.dialog_search_clan);
  }

  @Override
  protected void onContentView(View contentView) {
    disposable = new CompositeDisposable();

    ImageView ivClose= contentView.findViewById(R.id.ivClose);
    ivClose.setOnClickListener(v -> {
      if (dialog != null) {
        if (disposable != null) {
          disposable.dispose();
          disposable = null;
        }
        dialog.dismiss();
      }
    });

    // 根据输入框输入值的改变来过滤搜索
    etSearch = contentView.findViewById(R.id.etSearch);
    etSearch.addTextChangedListener(new TextWatcher() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        loadListData(true, 1, s.toString());
      }
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
      @Override
      public void afterTextChanged(Editable s) { }
    });

    initList(contentView);

    loadListData(true, 1, "");
  }

  private void initList(View contentView) {
    Adapter adapter = new Adapter();
    //初始化我的数据信息
    recyclerview = contentView.findViewById(R.id.recyclerview);
    recyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
    recyclerview.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
    recyclerview.setArrowImageView(R.drawable.iconfont_downgrey);
    recyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
      @Override
      public void onRefresh() {
        loadListData(true, 1, etSearch.getText().toString());
      }
      @Override
      public void onLoadMore() {
        loadListData(false, page + 1, etSearch.getText().toString());
      }
    });
    recyclerview.setAdapter(adapter);
  }

  private void loadListData(boolean isRefresh, int page, String value) {
    HashMap<String, Object> map = new HashMap<>();
    map.put("page", page);
    map.put("limit", 10);
    map.put("title", value);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    disposable.add(
        apiServer.getClanList(requestBody).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ClanModel>() {
              @Override
              public void onSuccess(ClanModel model) {
                if (isRefresh) {
                  SearchClanDialog.this.page = 1;
                  SearchClanDialog.this.data.clear();
                } else {
                  SearchClanDialog.this.page = page;
                }
                List<ClanItem> list = model.getList();
                if (list != null) {
                  data.addAll(list);
                }
                Adapter adapter = (Adapter) recyclerview.getAdapter();
                if (adapter != null) {
                  adapter.notifyDataSetChanged();
                }
              }
              @Override
              public void onError(int code, String msg) {
                AppLogger.d("getClanList --- " + msg);
              }
              @Override
              public void onComplete() {
                super.onComplete();
                if (recyclerview != null) {
                  recyclerview.refreshComplete();
                  recyclerview.loadMoreComplete();
                }
              }
            })
        );
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  public interface Callback {
    void onItemClick(Dialog dialog, ClanItem itemData);
  }

  class Adapter extends RecyclerView.Adapter<ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_clan_search, parent, false);
      return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      ClanItem itemData = data.get(position);

      String imgUrl;
      if(itemData.getClanAvatar().startsWith("http")) {
        imgUrl = itemData.getClanAvatar();
      } else {
        imgUrl = Constant.getBaseUrl() + "/" + itemData.getClanAvatar();
      }
      Picasso.with(holder.ivHead.getContext())
          .load(imgUrl)
          .transform(new CircleTransform(holder.ivHead.getContext()))
          .placeholder(R.mipmap.default_head)
          .into(holder.ivHead);

      holder.tvName.setText(itemData.getTitle());
      holder.tvArea.setText(itemData.getAddress());
      holder.tvMemberCount.setText(holder.itemView.getContext().getString(R.string.association_match_join_sum, itemData.getClanCount() + ""));
      holder.itemView.setOnClickListener(v -> {
        if (callback != null) {
          callback.onItemClick(dialog, itemData);
        }
      });
    }

    @Override
    public int getItemCount() {
      return data.size();
    }
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    ImageView ivHead;
    TextView tvName;
    TextView tvArea;
    TextView tvMemberCount;
    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      ivHead = itemView.findViewById(R.id.ivHead);
      tvName = itemView.findViewById(R.id.tvName);
      tvArea = itemView.findViewById(R.id.tvArea);
      tvMemberCount = itemView.findViewById(R.id.tvMemberCount);
    }
  }

}
