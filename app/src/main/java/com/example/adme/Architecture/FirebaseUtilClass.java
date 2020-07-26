package com.example.adme.Architecture;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.adme.Helpers.MyPlaces;
import com.example.adme.Helpers.Service;
import com.example.adme.Helpers.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseUtilClass {


    private static final String TAG = "FirebaseUtilClass";

    public static final String LOCATION = "langLat";
    public static final String CURRENT_USER_ID = "current_user";
    public static final String USER_COLLECTION_ID ="Adme_User";
    public static final String USER_NAME ="user_name";
    public static final String USER_MAIN_DATA_COLLECTION_NAME = "Main_Data";
    public static final String SERVICE_PROVIDER_DOCUMENT_NAME = "Service_Provider_Data";
    public static final String COLLECTION_ADME_SERVICE_LIST = "Adme_Service_list";
    public static final String ENTRY_USER_STATUS = "status";

    public static final String MODE_CLIENT = "Client";
    public static final String MODE_SERVICE_PROVIDER = "ServiceProvider";
    public static final String STATUS_ONLINE = "Online";
    public static final String  STATUS_OFFLINE = "Offline";
    private static final String STATUS = "status";
    public static final String CONTACTS = "contacts";
    public static final String ENTRY_MONTHLY_SUBSCRIPTION_PAID = "Paid";

    public static final String ENTRY_PRESSED_TODAY = "pressed_today";
    public static final String ENTRY_REQUESTED_TODAY = "requested_today";
    public static final String ENTRY_COMPLETED_TODAY = "completed_today";
    public static final String ENTRY_INCOME_TODAY = "income_today";
    public static final String ENTRY_INCOME_TOTAL = "income_total";
    public static final String ENTRY_DUE = "due";
    public static final String ENTRY_MONTHLY_SUBSCRIPTION = "monthly_subscription";
    public static final String ENTRY_SERVICE_REFERENCE = "service_reference";

    public static final String ENTRY_PHONE_NO = "phone_no";
    public static final String ENTRY_EMAIL = "email";
    public static final String ENTRY_PHONE_NO_PRIVACY = "privacy_phone";
    public static final String ENTRY_EMAIL_PRIVACY = "privacy_email";
    public static final String ENTRY_PRIVACY_PUBLIC = "Public";
    public static final String ENTRY_PRIVACY_PRIVATE = "Private";

    public static final String ENTRY_LOCATION = "location";
    public static final String ENTRY_LOCATION_DISPLAY_NAME = "display_name";
    public static final String ENTRY_LOCATION_ADDRESS = "address";
    public static final String ENTRY_LOCATION_LATITUDE = "latitude";
    public static final String ENTRY_LOCATION_LONGITUDE = "longitude";

    public static final String ENTRY_SERVICE_TITLE = "service_title";
    public static final String ENTRY_SERVICE_DESCRIPTION = "service_description";
    public static final String ENTRY_SERVICE_PRICE = "service_price";

    public static final String ENTRY_CLIENT_APPOINTMENTS = "client_appointments";

    public static final String STORAGE_FOLDER_SERVICE_PORTFOLIO = "service_portfolio";

    public static final String ENTRY_SERVICE_CATEGORY = "category";
    public static final String ENTRY_MAIN_SERVICE_DESCRIPTION = "description";
    public static final String ENTRY_SERVICE_RATING = "rating";
    public static final String ENTRY_SERVICE_REVIEWS = "reviews";

    public static final String NOTIFICATION_APPOINTMENT_TYPE = "appointment";
    public static final String NOTIFICATION_INVOICE_TYPE = "invoice";
    public static final String NOTIFICATION_RATING_TYPE = "rating";
    public static final String NOTIFICATION_NONE_TYPE = "none";

    public static final String APPOINTMENT_STATE_CLINT_SEND = "clientSent";
    public static final String APPOINTMENT_STATE_SERVICE_PROVIDER_SEND = "serviceProviderSent";
    public static final String APPOINTMENT_STATE_CLINT_APPROVED = "clientApproved";
    public static final String APPOINTMENT_STATE_FINISHED = "finished";
    public static final String APPOINTMENT_STATE_CLINT_CANCELED = "clientCanceled";
    public static final String APPOINTMENT_STATE_SERVICE_PROVIDER_CANCELED = "serviceProviderCanceled";
    public static final String APPOINTMENT_STATE_TIMEOUT_CANCELED = "timeoutCanceled";
    public static final String APPOINTMENT_STATE_INVOICE_SEND = "invoiceSent";

    public static final String ENTRY_EDITABLE = "Editable";
    public static final String ENTRY_NOT_EDITABLE = "notEditable";

    public static final String ENTRY_PENDING = "pending";
    public static final String ENTRY_FINISHED = "finished";

    public static final String VALUE_USER_PHOTO = "user_photo";
    public static final String VALUE_DEFAULT_AVATAR = "default_avatar";

    public static final String ENTRY_FEATURE_IMAGES = "feature_images";



    //create database reference
    private FirebaseFirestore db;
    private CollectionReference userRef;
    private FirebaseAuth mAuth;
    private MutableLiveData<User> userData;
    private MutableLiveData<List<Service>> services = new MutableLiveData<>();
    private MutableLiveData<Service> serviceData = new MutableLiveData<>();
    private List<Service> serviceList = new ArrayList<>();
    private String user_id;
    private CollectionReference servicesRef;
    private FirebaseStorage storage;
    private FirebaseFunctions mFunctions;

    public FirebaseUtilClass() {
        userData = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection(USER_COLLECTION_ID);
        servicesRef = db.collection(COLLECTION_ADME_SERVICE_LIST);
        mAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
        storage = FirebaseStorage.getInstance("gs://adme-bf48a.appspot.com");
    }

    public MutableLiveData<User> getUserData() {
        if(mAuth.getCurrentUser() != null){
            user_id = mAuth.getCurrentUser().getUid();
            fetchUserData();
        }
        return userData;
    }


    public MutableLiveData<List<Service>> getServices() {
        servicesRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                List<Service> serviceList = new ArrayList<>();
                if(queryDocumentSnapshots != null){
                    for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                        serviceList.add(documentSnapshot.toObject(Service.class));
                    }
                }

                services.setValue(serviceList);
            }
        });
        return services;
    }

    private void fetchUserData() {
        userRef.document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    User user = documentSnapshot.toObject(User.class);
                    user.setmUserId(user_id);
                    userData.setValue(user);
                }
            }
        });
    }

    public void createUser(FirebaseUser user, CreateUserCommunicator communicator){
        userRef.document(user.getUid()).get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                DocumentSnapshot document = task1.getResult();


                if (document.exists()) {
                    User current_user = document.toObject(User.class);
                    // User is already exist in database
                    //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    communicator.userAlreadyExists(current_user);
                } else {
                    // User hasn't created yet
                    // create new user in database
                    Log.d(TAG, "No such document");
                    String username;
                    String email = null;
                    String phone = null;
                    String profile_photo_url = "default_avatar";
                    String NULL = "";

                    if (user.getDisplayName() != null){
                        username = user.getDisplayName();
                    }
                    else{
                        username = "Adme User";
                    }

                    if(user.getEmail() != null){
                        email = user.getEmail();
                    }

                    if (user.getPhoneNumber() != null){
                        phone = user.getPhoneNumber();
                    }

                    if (user.getPhotoUrl() !=null){
                        profile_photo_url = "user_photo";
                    }

                    String joined = String.valueOf(Objects.requireNonNull(user.getMetadata()).getCreationTimestamp());
                    String user_id = user.getUid();
                    User new_user = new User(username,email,phone,profile_photo_url,joined,user_id);
                    /*** Insert into fireStore database**/
                    userRef.document(user.getUid()).set(new_user).addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "onSuccess: successfully created user");
                        communicator.onUserCreatedSuccessfully(new_user);
                    });
                }
            } else {
                Log.d(TAG, "get failed with ", task1.getException());
            }

        });
    }

    public void updateUserLocation(FirebaseUser user, MyPlaces place, UpdateLocationInfoCommunicator communicator) {
        Map<String,String> location = new HashMap<>();
        location.put(ENTRY_LOCATION_LATITUDE,place.getLatitude());
        location.put(ENTRY_LOCATION_LONGITUDE,place.getLongitude());
        location.put(ENTRY_LOCATION_DISPLAY_NAME,place.getName());
        location.put(ENTRY_LOCATION_ADDRESS,place.getFormattedAddress());

        Log.d("akash-debug", "updateUserLocation: "+user.getUid());

        userRef.document(user.getUid()).update(ENTRY_LOCATION, location).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("akash-debug", "onSuccess: location info updated");
                communicator.onLocationInfoUpdated();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("akash-debug", "onFailure: "+ e);
            }
        });
    }


    public FirebaseUser getCurrentUser(){
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        if(mCurrentUser!=null){
            return mCurrentUser;
        }

        return null;
    }



    DatabaseOperationListener uploadUserServiceListener;
    public void uploadUserService(Service service,DatabaseOperationListener listener){
        uploadUserServiceListener = listener;
        servicesRef.add(service).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                addServiceInUserDocument(service,documentReference.getId());
            }
        });

    }

    private void addServiceInUserDocument(Service service, String service_id) {
        Map<String,String> service_reference = new HashMap<>();
        service_reference.put(ENTRY_SERVICE_CATEGORY,service.getCategory());
        service_reference.put(ENTRY_MAIN_SERVICE_DESCRIPTION,service.getDescription());
        service_reference.put(ENTRY_SERVICE_RATING,service.getRating());
        service_reference.put(ENTRY_SERVICE_REVIEWS,service.getReviews());
        service_reference.put(ENTRY_SERVICE_REFERENCE,service_id);
        userRef.document(service.getUser_ref()).update(FirebaseUtilClass.ENTRY_SERVICE_REFERENCE, FieldValue.arrayUnion(service_reference)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                uploadUserServiceListener.onSuccess(null);
            }
        });
    }

    public void signInWithEmailAndPassword(String email, String password, DatabaseOperationListener listener) {


        mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                listener.onSuccess(authResult);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailure(e);
            }
        });
    }


    public MutableLiveData<Service> getServiceData(String service_id){
        servicesRef.document(service_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot != null){
                    Service service = documentSnapshot.toObject(Service.class);
                    service.setmServiceId(documentSnapshot.getId());
                    serviceData.setValue(service);
                }
            }
        });
        return serviceData;
    }



    public void updateUserService(String serviceId,Service service,DatabaseOperationListener listener){
        servicesRef.document(serviceId).set(service).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onSuccess(null);
            }
        });

    }

    public void updateServiceInUserInfo(Service service,User userData,int serviceIndex,DatabaseOperationListener listener) {
        Log.d("akash_debug", "updateServiceInUserInfo: "+userData.getService_reference().size());
        List<Map<String,String>> services = userData.getService_reference();
        Map<String,String> service_reference = new HashMap<>();
        service_reference.put(ENTRY_SERVICE_CATEGORY,service.getCategory());
        service_reference.put(ENTRY_MAIN_SERVICE_DESCRIPTION,service.getDescription());
        service_reference.put(ENTRY_SERVICE_RATING,service.getRating());
        service_reference.put(ENTRY_SERVICE_REVIEWS,service.getReviews());
        service_reference.put(ENTRY_SERVICE_REFERENCE,service.getmServiceId());
        services.set(serviceIndex,service_reference);
        userRef.document(service.getUser_ref()).update(FirebaseUtilClass.ENTRY_SERVICE_REFERENCE,services).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onSuccess(null);
            }
        });
    }

    public void deleteImageFromDatabase(String imageUrl){
        StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);

        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("akash_debug", "onSuccess: image delete");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("akash_debug", "onFailure: image delete "+e);
            }
        });

    }



    public boolean checkIfAlreadyLoggedIn(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            return true;
        }
        return false;
    }

    public interface UpdateLocationInfoCommunicator{
        void onLocationInfoUpdated();
    }

    public interface DatabaseOperationListener{
        void onSuccess(Object object);
        void onFailure(Exception e);
    }


    public interface CreateUserCommunicator{
        void userAlreadyExists(User user);
        void onUserCreatedSuccessfully(User user);
    }





    //for profile update

    public void updateUserName(String name)
    {
        userRef.document(getCurrentUser().getUid()).update(USER_NAME,name).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("user name update","success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("user name update","failed");
            }
        });
    }

    public void updatePhoneNumberMode(String mode)
    {

        userRef.document(getCurrentUser().getUid()).update(CONTACTS+"."+ENTRY_PHONE_NO_PRIVACY,mode).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("success","phone number privacy updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("failed",e.toString()+"phone number privacy update failed");
            }
        });
    }

    public void deletePhoneNumber()
    {

        userRef.document(getCurrentUser().getUid()).update(CONTACTS+"."+ENTRY_PHONE_NO,null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("success","phone number deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("failed",e.toString()+"phone number deletion failed");
            }
        });
    }

    public void addPhoneNumber(String number)
    {

        userRef.document(getCurrentUser().getUid()).update(CONTACTS+"."+ENTRY_PHONE_NO,number).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("success","phone number added");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("failed",e.toString()+"phone number addition failed");
            }
        });
    }

    public void updateEmailMode(String mode)
    {

        userRef.document(getCurrentUser().getUid()).update(CONTACTS+"."+ENTRY_EMAIL_PRIVACY,mode).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("success","email privacy updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("failed",e.toString()+"email privacy update failed");
            }
        });
    }

    public void deleteEmail()
    {

        userRef.document(getCurrentUser().getUid()).update(CONTACTS+"."+ENTRY_EMAIL,null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("success","email deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("failed",e.toString()+"email deletion failed");
            }
        });
    }

    public void addEmail(String number)
    {

        userRef.document(getCurrentUser().getUid()).update(CONTACTS+"."+ENTRY_EMAIL,number).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("success","email added");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("failed",e.toString()+"email addition failed");
            }
        });
    }


    public void updateUserStatus(String userId,String status){
        userRef.document(userId).update(FirebaseUtilClass.ENTRY_USER_STATUS,status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }





    //for profile ends

    //For Status update
    public void updateStatus(String status){
        userRef.document(mAuth.getCurrentUser().getUid()).update(STATUS,status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("SUCCESS","Status Updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FAILURE","Status Update failed");
            }
        });
    }
}
