package ru.alexbrz.testgallery.ui.gallery.paging;

import java.util.ArrayList;

class ImageLoaderResponse {
    private final ArrayList<String> images;
    private final Boolean isEnd;

    public ImageLoaderResponse(ArrayList<String> images, Boolean isEnd) {
        this.images = images;
        this.isEnd = isEnd;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public Boolean isEnd() {
        return isEnd;
    }
}
