package com.example.adme.Activities.ui.servicehistory;

public class ServiceHistoryItem {
    String clientName, clientLoc, clientComment, amount, star, imageURL;
    ServiceHistoryItem(String clientName, String clientLoc, String clientComment, String amount, String star, String imageURL){
        this.amount = amount;
        this.clientComment = clientComment;
        this.clientLoc = clientLoc;
        this.clientName = clientName;
        this.imageURL = imageURL;
        this.star = star;
    }
}
