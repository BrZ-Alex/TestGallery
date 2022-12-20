package ru.alexbrz.testgallery.ui.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import ru.alexbrz.testgallery.BuildConfig;
import ru.alexbrz.testgallery.ui.gallery.paging.MediaSpaceDecoration;
import ru.alexbrz.testgallery.R;
import ru.alexbrz.testgallery.databinding.FragmentGalleryListBinding;
import ru.alexbrz.testgallery.util.StringDiffUtil;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static autodispose2.AutoDispose.autoDisposable;

public class GalleryFragment extends Fragment {

    private FragmentGalleryListBinding fragmentGalleryListBinding;
    private GalleryViewModel viewModel;
    private NavController navController;
    private GalleryImageAdapter galleryImageAdapter;

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for(String permission: permissionsRequired){
            if(ContextCompat.checkSelfPermission(requireContext(),permission)== PackageManager.PERMISSION_DENIED){
                requestPermissions.launch(permissionsRequired);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentGalleryListBinding = FragmentGalleryListBinding.inflate(inflater);

        viewModel = new ViewModelProvider(
                requireActivity(),
                new GalleryViewModelFactory(requireActivity().getApplication())
        ).get(GalleryViewModel.class);

        galleryImageAdapter = new GalleryImageAdapter(StringDiffUtil.stringItemCallback, getViewLifecycleOwner(), this::onItemClick);
        RecyclerView imagesRecyclerView = fragmentGalleryListBinding.galleryListRvImages;
        imagesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        imagesRecyclerView.setAdapter(galleryImageAdapter);
        imagesRecyclerView.addItemDecoration(new MediaSpaceDecoration(getResources().getDimensionPixelSize(R.dimen.photos_list_spacing), 3));

        subscribeToImages();

        return fragmentGalleryListBinding.getRoot();
    }

    private void subscribeToImages(){
        viewModel.createImagesFlowable();
        viewModel.getImagesFlowable()
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(pagingData -> galleryImageAdapter.submitData(getLifecycle(), pagingData));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(fragmentGalleryListBinding.getRoot());
    }

    private void onItemClick(String item){
        if(item.isEmpty()){
            viewModel.cameraClicked(launchCamera, requireContext());
        }else{
            viewModel.imageClicked(navController, item);
        }
    }

    ActivityResultLauncher<Intent> launchCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    viewModel.cameraFinished(navController);
                }else if(result.getResultCode() == Activity.RESULT_CANCELED) {
                    viewModel.deleteEmptyFile();
                }
            });

    private final String[] permissionsRequired = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    ActivityResultLauncher<String[]> requestPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                //here we should check all permissions if they granted and show rationale if it needs
                Boolean allGranted = true;
                for(Map.Entry<String, Boolean> entry: permissions.entrySet()){
                    if(!entry.getValue()) allGranted = false;
                }
                if(allGranted){
                    subscribeToImages();
                }
            });
}