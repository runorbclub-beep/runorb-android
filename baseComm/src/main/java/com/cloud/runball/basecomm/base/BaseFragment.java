package com.cloud.runball.basecomm.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements BaseView {


    public Context context;
    private ProgressDialog dialog;
    protected P presenter;
    protected Toolbar toolbar;

    protected abstract P createPresenter();

    protected abstract int getLayoutId();

    protected abstract void addListener();

    protected abstract void initView();


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        context = getContext();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container);
        presenter = createPresenter();
        initView();
        addListener();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.detachView();
        }
    }
}
