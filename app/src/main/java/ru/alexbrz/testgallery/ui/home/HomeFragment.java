package ru.alexbrz.testgallery.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import ru.alexbrz.testgallery.R;
import ru.alexbrz.testgallery.databinding.FragmentHomeBinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding fragmentHomeBinding;
    private HomeViewModel homeViewModel;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentHomeBinding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);

        homeViewModel = new ViewModelProvider(this.requireActivity()).get(HomeViewModel.class);

        fragmentHomeBinding.homeBtnChoose.setOnClickListener(view -> {
            homeViewModel.buttonChooseClicked(Navigation.findNavController(view));
        });

        String path = HomeFragmentArgs.fromBundle(getArguments()).getImagePath();

        if(path!=null){
            Glide.with(this)
                    .load("file://"+path)
                    .placeholder(R.drawable.icon_placeholder)
                    .into(fragmentHomeBinding.homeIvChosen);
        }

        return fragmentHomeBinding.getRoot();
    }
}