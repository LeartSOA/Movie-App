package com.example.movieapp.ui.what_to_watch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class randomViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public randomViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is What to Watch fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}