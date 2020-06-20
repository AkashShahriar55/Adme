package com.example.adme.Activities.ui.today;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.adme.Architecture.DataRepository;
import com.example.adme.Helpers.Service;

public class ViewServiceDetailsViewModel extends ViewModel {

    private MutableLiveData<Service> serviceData;
    private DataRepository repository;

    public ViewServiceDetailsViewModel(){
        repository = new DataRepository();

    }

    public MutableLiveData<Service> getServiceData(String service_id){
        return repository.getServiceData(service_id);
    }
}
