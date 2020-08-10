package com.cookietech.adme.Activities.ui.today;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cookietech.adme.Architecture.DataRepository;
import com.cookietech.adme.Helpers.Service;

public class ViewServiceDetailsViewModel extends ViewModel {

    private MutableLiveData<Service> serviceData;
    private DataRepository repository;

    public ViewServiceDetailsViewModel(){
        repository = DataRepository.getInstance();

    }

    public MutableLiveData<Service> getServiceData(String service_id){
        return repository.getServiceData(service_id);
    }
}
