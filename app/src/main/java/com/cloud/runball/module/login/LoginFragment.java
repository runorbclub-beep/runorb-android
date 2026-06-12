package com.cloud.runball.module.login;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cloud.runball.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener{



    public LoginFragment() {
        // Required empty public constructor
    }


    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    //private CallbackManager fbCallback = CallbackManager.Factory.create();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root=inflater.inflate(R.layout.fragment_login, container, false);
        //root.findViewById(R.id.login_button).setOnClickListener(this);
        root.findViewById(R.id.login_wechat).setOnClickListener(this);


        /**
        //设置监听
        LoginManager.getInstance().registerCallback(fbCallback, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //登陆成功返回
                Logger.d("onSuccess,loginResult:facebookid="+loginResult.getAccessToken().getUserId());
                requestFacebookInfo();
                //上传到服务器
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
         **/

        return root;
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
        //LoginManager.getInstance().unregisterCallback(fbCallback);
    }

    /**
     * 请求个人资料
     * 请求个人数据，包括，facebookID，昵称，性别，头像。需要相关权限"user_status","user_gender"
     */
    private void requestFacebookInfo() {
        Bundle param = new Bundle();
        /**
        param.putString("fields", "id,name,gender,picture");
        GraphRequest graphRequest= GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (null != object) {
                            try {
                                JSONObject jsob = new JSONObject();
                                jsob.put("id", object.getString("id"));
                                jsob.put("name", object.getString("name"));
                                jsob.put("gender", object.getString("gender"));
                                JSONObject jsonObject = object.optJSONObject("picture");
                                if(jsonObject!=null){
                                    JSONObject data=jsonObject.optJSONObject("data");
                                    if(data!=null){
                                        jsob.put("icon", data.getString("url"));
                                    }
                                }
                                // 传递参数到ts层代码
                                //Utils.packCBParamsToTS(Utils.OptType.PlayerInfo, Utils.ResultCode.Success, jsob);
                            }
                            catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });
        graphRequest.setParameters(param);
        graphRequest.executeAsync();
        **/
    }

    @Override
    public void onClick(View v) {
        //if(v.getId()==R.id.login_button){
          //  LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends","email"));
        //}else if(v.getId()==R.id.login_wechat){
        //    startActivity(new Intent(getActivity(), WristBallActivity.class));
        //    getActivity().finish();
        //}
    }
}