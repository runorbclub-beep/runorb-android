package com.cloud.city.adapter;


import com.cloud.city.model.City;

public interface OnPickListener {
    void onPick(int position, City data);
    void onLocate();
    void onCancel();
}
