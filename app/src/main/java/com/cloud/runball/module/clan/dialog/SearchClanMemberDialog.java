package com.cloud.runball.module.clan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseDialog;
import com.cloud.runball.basecomm.service.WristBallObserver;
import com.cloud.runball.bean.ClanItem;
import com.cloud.runball.model.ClanMemberItem;
import com.cloud.runball.model.ClanMemberModel;
import com.cloud.runball.model.ClanModel;
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
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class SearchClanMemberDialog extends BaseDialog {

  private Callback callback;

  private EditText etSearch;
  private XRecyclerView recyclerview;
  private int page = 1;
  private final List<ClanMemberItem> data = new ArrayList<>();
  private String clanId;

  private CompositeDisposable disposable;

  public SearchClanMemberDialog(Context context) {
    super(context, R.layout.dialog_search_clan_member);
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
    map.put("user_clan_id", clanId);
    // 1待审核成员 2获取俱乐部成员
    map.put("status", 2);
    // 0是不含队长的 1是含队长的
    map.put("is_captain", 0);
    map.put("title", value);
    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), new JSONObject(map).toString());
    WristBallServer apiServer = WristBallRetrofitHelper.getInstance().getWristBallService();
    disposable.add(
        apiServer.getClanMemberList(requestBody).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new WristBallObserver<ClanMemberModel>() {
              @Override
              public void onSuccess(ClanMemberModel model) {
                if (isRefresh) {
                  SearchClanMemberDialog.this.page = 1;
                  SearchClanMemberDialog.this.data.clear();
                } else {
                  SearchClanMemberDialog.this.page = page;
                }
                List<ClanMemberItem> list = model.getList();
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

  public void setData(String clanId) {
    this.clanId = clanId;
    loadListData(true, 1, "");
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  public interface Callback {
    void onItemClick(Dialog dialog, ClanMemberItem itemData);
  }

  class Adapter extends RecyclerView.Adapter<ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_clan_member_search, parent, false);
      return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      ClanMemberItem itemData = data.get(position);

      String imgUrl;
      if(itemData.getUserImg().startsWith("http")) {
        imgUrl = itemData.getUserImg();
      } else {
        imgUrl = Constant.getBaseUrl() + "/" + itemData.getUserImg();
      }
      Picasso.with(holder.ivHead.getContext())
          .load(imgUrl)
          .transform(new CircleTransform(holder.ivHead.getContext()))
          .placeholder(R.mipmap.default_head)
          .into(holder.ivHead);

      holder.tvName.setText(itemData.getUserName());
      holder.tvArea.setText(itemData.getAddress());
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
    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      ivHead = itemView.findViewById(R.id.ivHead);
      tvName = itemView.findViewById(R.id.tvName);
      tvArea = itemView.findViewById(R.id.tvArea);
    }
  }

}
