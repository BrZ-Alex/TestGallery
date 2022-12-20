package ru.alexbrz.testgallery.ui.gallery;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;
import io.reactivex.rxjava3.core.Flowable;
import ru.alexbrz.testgallery.BuildConfig;
import ru.alexbrz.testgallery.ui.gallery.paging.ImagePagingSource;
import ru.alexbrz.testgallery.ui.gallery.paging.ImagesLoader;

class GalleryViewModel extends AndroidViewModel {
    private Flowable<PagingData<String>> imagesFlowable;

    public Flowable<PagingData<String>> getImagesFlowable() {
        return imagesFlowable;
    }

    public GalleryViewModel(@NonNull Application application) {
        super(application);
    }

    public void createImagesFlowable(){
        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Images.ImageColumns.DATA};
        String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        Cursor cursor = getApplication().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
        ImagesLoader imagesLoader = new ImagesLoader(cursor);

        Pager<Integer, String> pager = new Pager<>(
                new PagingConfig(10),   () -> new ImagePagingSource(imagesLoader)
        );

        imagesFlowable = PagingRx.getFlowable(pager);
    }

    public void imageClicked(NavController navController, String imagePath){
        navigateToHome(navController, imagePath);
    }

    public void cameraFinished(NavController navController){
        navigateToHome(navController, imageFilePath);
    }

    private void navigateToHome(NavController navController, String imagePath){
        navController.navigate(GalleryFragmentDirections.actionGalleryFragmentToHomeFragment().setImagePath(imagePath));
    }

    private String imageFilePath;

    public void cameraClicked(ActivityResultLauncher<Intent> launcher, Context context){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile(context);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (photoFile != null) {
            Uri imageUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            launcher.launch(intent);
        }
    }

    private File createImageFile(Context context) throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    public void deleteEmptyFile(){
        File file=new File(imageFilePath);
        file.delete();
    }
}
