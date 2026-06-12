package com.cloud.runball.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.RadioGroup;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseDialog;
import com.cloud.runball.constant.SexConstant;

public class FinishPageDialog extends BaseDialog {

  private String curSex;
  private Callback callback;

  public FinishPageDialog(Context context) {
    super(context, R.layout.dialog_finish_page);
  }

  @Override
  protected void onContentView(View contentView) {
    RadioGroup sexGroup = contentView.findViewById(R.id.rgSex);
    sexGroup.check(R.id.rbMan);
    curSex = SexConstant.SEX_MAN;
    sexGroup.setOnCheckedChangeListener((group, checkedId) -> {
      if (checkedId == R.id.rbMan) {
        curSex = SexConstant.SEX_MAN;
      } else if (checkedId == R.id.rbWoman) {
        curSex = SexConstant.SEX_WOMEN;
      }
    });

    contentView.findViewById(R.id.tvSubmit).setOnClickListener(view -> {
      if (callback != null) {
        callback.onSubmit(dialog, curSex);
      }
    });
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  public interface Callback {
    void onSubmit(Dialog dialog, String sex);
  }

}
