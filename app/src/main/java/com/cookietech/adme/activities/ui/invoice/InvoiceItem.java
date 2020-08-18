package com.cookietech.adme.activities.ui.invoice;

import java.util.ArrayList;

public class InvoiceItem {
    private CustomerDetails customerDetails;
    private ArrayList<Services> selectServicesList;
    private String state;

    public InvoiceItem(){}

    public InvoiceItem(CustomerDetails customerDetails, ArrayList<Services> selectServicesList, String state) {
        this.customerDetails = customerDetails;
        this.selectServicesList = selectServicesList;
        this.state = state;
    }

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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
