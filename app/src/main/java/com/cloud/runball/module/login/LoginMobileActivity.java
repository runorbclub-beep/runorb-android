package com.cloud.runball.module.login;

import android.os.CountDownTimer;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cloud.runball.R;
import com.cloud.runball.basecomm.base.BaseActivity;
import com.cloud.runball.basecomm.base.BasePresenter;
import com.cloud.runball.service.WristBallRetrofitHelper;
import com.cloud.runball.service.WristBallServer;
import com.github.phoenix.widget.Keyboard;
import com.github.phoenix.widget.PayEditText;

import com.cloud.runball.databinding.ActivityLoginMobileBinding;

/**
 * 登陆部分
 * @author ns467
 */
public class LoginMobileActivity extends BaseActivity {


    private static final String[] KEY = new String[]{
            "1", "2", "3",
            "4", "5", "6",
            "7", "8", "9",
            "", "0", "d"
    };

    private ActivityLoginMobileBinding binding;
    PayEditText payEditText;

    Keyboard keyboard;

    TextView tvSend;

    TextView tvPhone;

    Button btnSend;

    CountDownTimer timer;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_mobile;
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        binding = ActivityLoginMobileBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected void addListener() {

    }

    @Override
    protected String getTitleLabel() {
        return getString(R.string.title_login_mobile);
    }

    String phone;
    boolean hidden;
    @Override
    protected void initView() {
        payEditText = binding.PayEditTextPay;
        keyboard = binding.KeyboardViewPay;
        tvSend = binding.tvSend;
        tvPhone = binding.tvPhone;
        btnSend = binding.btnSend;
        phone = getIntent().getStringExtra("phone");
        hidden = getIntent().getBooleanExtra("hiddenTitle",false);
        if(hidden){
            HiddenNavigationTitle();
        }
        tvPhone.setText(phone);
        setSubView();
        initEvent();
        // click listener replacing @OnClick
        btnSend.setOnClickListener(this::onClick);
    }

    @Override
    protected void setOnResult() {

    }

    @Override
    protected void supportToolbar() {
        super.supportToolbar();
    }

    private void startTimer() {
        timer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long l) {
                //计时过程显示
                tvSend.setText(String.format(getResources().getString(R.string.phone_valid_code_send_again), (l / 1000)));
            }

            @Override
            public void onFinish() {
                //计时完毕时触发
                tvSend.setVisibility(View.GONE);
                btnSend.setVisibility(View.VISIBLE);
            }

        }.start();
    }

    private void setSubView() {
        //设置键盘
        keyboard.setKeyboardKeys(KEY);
        keyboard.setKeyBoardBackground(getResources().getColor(R.color.bg_content));
    }

    private void initEvent() {
        keyboard.setOnClickKeyboardListener(new Keyboard.OnClickKeyboardListener() {
            @Override
            public void onKeyClick(int position, String value) {
                if (position < 11 && position != 9) {
                    payEditText.add(value);
                } else if (position == 11) {
                    payEditText.remove();
                } else if (position == 9) {
                    payEditText.remove();
                }
            }
        });

        /**
         * 当密码输入完成时的回调
         */
        payEditText.setOnInputFinishedListener(new PayEditText.OnInputFinishedListener() {
            @Override
            public void onInputFinished(String password) {
                Toast.makeText(getApplication(), "验证码是：" + password, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnSend) {
            startTimer();
            tvSend.setVisibility(View.VISIBLE);
            btnSend.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

}
