package ru.alexbrz.testgallery.ui.gallery.paging;

import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;

public class ImagesLoader {

    private final Cursor imageCursor;

    public ImagesLoader(Cursor cursor){
        imageCursor = cursor;
    }

    public ImageLoaderResponse getImages(int page, int pageSize){
        ArrayList<String> images = new ArrayList<>();
        boolean isEnd = false;

        int index = page*pageSize;
        int lastIndex = index+pageSize;

        int columnData = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        try {
            while (index < lastIndex) {
                if(imageCursor.moveToPosition(index)) {
                    images.add(imageCursor.getString(columnData));
                }else{
                    isEnd = true;
                    break;
                }
                index++;
            }
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }

        return new ImageLoaderResponse(images, isEnd);
    }
}
