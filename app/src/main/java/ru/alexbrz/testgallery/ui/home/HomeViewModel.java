package ru.alexbrz.testgallery.ui.home;

import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;

public class HomeViewModel extends ViewModel {



    void buttonChooseClicked(NavController navController){
        navController.navigate(HomeFragmentDirections.actionHomeFragmentToGalleryFragment());
    }
}
