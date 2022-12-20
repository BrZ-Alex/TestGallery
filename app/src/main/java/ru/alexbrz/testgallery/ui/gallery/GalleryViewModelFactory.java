package ru.alexbrz.testgallery.ui.gallery;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class GalleryViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;

    public GalleryViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GalleryViewModel.class))
            return (T) new GalleryViewModel(application);
        throw new IllegalArgumentException("Cant Find View Model Class");
    }
}
