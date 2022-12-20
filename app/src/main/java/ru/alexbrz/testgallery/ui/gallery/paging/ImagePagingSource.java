package ru.alexbrz.testgallery.ui.gallery.paging;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingSource;
import androidx.paging.PagingState;
import kotlin.coroutines.Continuation;

public class ImagePagingSource extends PagingSource<Integer, String> {

    private final ImagesLoader imagesLoader;

    public ImagePagingSource(ImagesLoader imagesLoader) {
        this.imagesLoader = imagesLoader;
    }

    @Nullable
    @Override
    public Object load(@NonNull LoadParams<Integer> loadParams, @NonNull Continuation<? super LoadResult<Integer, String>> continuation) {
        try {
            Integer pageNumber = 0;
            if(loadParams.getKey() != null) pageNumber = loadParams.getKey();

            ImageLoaderResponse imageLoaderResponse = imagesLoader.getImages(pageNumber, loadParams.getLoadSize());

            ArrayList<String> images = imageLoaderResponse.getImages();
            if(pageNumber==0) images.add(0, "");
            Integer prevKey = pageNumber>0? pageNumber-1 : null;
            Integer nextKey = imageLoaderResponse.isEnd()? null : pageNumber+1;

            return new LoadResult.Page<>(
                    imageLoaderResponse.getImages(),
                    prevKey,
                    nextKey
            );
        } catch (Exception e) {
            return new LoadResult.Error<Integer, String>(e);
        }
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, String> pagingState) {
        return null;
    }
}
