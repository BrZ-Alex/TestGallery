package ru.alexbrz.testgallery.ui.gallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.common.util.concurrent.ListenableFuture;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import ru.alexbrz.testgallery.R;
import ru.alexbrz.testgallery.databinding.ItemCameraBinding;
import ru.alexbrz.testgallery.databinding.ItemImageBinding;

class GalleryImageAdapter extends PagingDataAdapter<String, GalleryImageAdapter.GalleryViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final LifecycleOwner lifecycleOwner;
    private final ClickListener clickListener;

    protected GalleryImageAdapter(@NonNull DiffUtil.ItemCallback<String> diffCallback, LifecycleOwner lifecycleOwner, ClickListener clickListener) {
        super(diffCallback);
        this.lifecycleOwner = lifecycleOwner;
        this.clickListener = clickListener;
    }

    static abstract class GalleryViewHolder extends RecyclerView.ViewHolder{
        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        abstract void bind(String image, ClickListener clickListener);
    }

    static class ImageViewHolder extends GalleryViewHolder{
        private final ItemImageBinding binding;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemImageBinding.bind(itemView);
        }

        void bind(String image, ClickListener clickListener){
            Glide.with(binding.getRoot())
                    .load("file://" + image)
                    .placeholder(R.drawable.icon_placeholder)
                    .centerCrop()
                    .into(binding.itemImage);

            binding.getRoot().setOnClickListener(
                    view -> clickListener.onClick(image)
            );
        }
    }

    static class HeaderViewHolder extends GalleryViewHolder{
        private final ItemCameraBinding binding;
        private final LifecycleOwner lifecycleOwner;
        public HeaderViewHolder(@NonNull View itemView, LifecycleOwner lifecycleOwner) {
            super(itemView);
            this.lifecycleOwner = lifecycleOwner;
            binding = ItemCameraBinding.bind(itemView);
        }

        private void bindPreview(ProcessCameraProvider cameraProvider){
            Preview preview = new Preview.Builder().build();

            CameraSelector cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build();
            preview.setSurfaceProvider(binding.itemImage.getSurfaceProvider());

            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(lifecycleOwner,cameraSelector,preview);
        }

        void bind(String image, ClickListener clickListener) {
            ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(itemView.getContext());

            cameraProviderFuture.addListener(() -> {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }, ContextCompat.getMainExecutor(itemView.getContext()));

            binding.getRoot().setOnClickListener(
                    view -> clickListener.onClick(image)
            );
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_camera, parent, false);
            return new HeaderViewHolder(view, lifecycleOwner);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        holder.bind(getItem(position), clickListener);
    }
}