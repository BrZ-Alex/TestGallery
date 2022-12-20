package ru.alexbrz.testgallery.util;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class StringDiffUtil {
    public static DiffUtil.ItemCallback<String> stringItemCallback = new DiffUtil.ItemCallback<String>() {
        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }
    };
}
