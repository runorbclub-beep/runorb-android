package com.cloud.city.adapter;


import com.cloud.city.model.City;

public interface InnerListener {
    void dismiss(int position, City data);
    void locate();
}
