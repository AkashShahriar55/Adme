package com.example.adme.Activities.ui.invoice;

import java.util.ArrayList;

public class InvoiceItem {
    private CustomerDetails customerDetails;
    private ArrayList<Services> selectServicesList;

    public InvoiceItem(){}

    public InvoiceItem(CustomerDetails customerDetails, ArrayList<Services> selectServicesList) {
        this.customerDetails = customerDetails;
        this.selectServicesList = selectServicesList;
    }

    public CustomerDetails getCustomerDetails() {
        return customerDetails;
    }

    public void setCustomerDetails(CustomerDetails customerDetails) {
        this.customerDetails = customerDetails;
    }

    public ArrayList<Services> getSelectServicesList() {
        return selectServicesList;
    }

    public void setSelectServicesList(ArrayList<Services> selectServicesList) {
        this.selectServicesList = selectServicesList;
    }
}
