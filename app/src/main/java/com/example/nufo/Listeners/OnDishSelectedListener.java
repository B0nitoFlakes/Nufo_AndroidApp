package com.example.nufo.Listeners;

import com.example.nufo.Helpers.DiaryHelperClass;

public interface OnDishSelectedListener {
    void onDishSelected(DiaryHelperClass dish);
    void onDishDeselected(DiaryHelperClass dish);
}
