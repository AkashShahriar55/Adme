package com.example.adme.Architecture;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.adme.Helpers.MyPlaces;
import com.example.adme.Helpers.Service;
import com.example.adme.Helpers.ServiceProviderData;
import com.example.adme.Helpers.User;
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
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseUtilClass {


    private static final String TAG = "FirebaseUtilClass";

    public static final String LOCATION = "langLat";
    public static final String CURRENT_USER_ID = "current_user";
    public static final String USER_COLLECTION_ID ="Adme_User";
    public static final String USER_MAIN_DATA_COLLECTION_NAME = "Main_Data";
    public static final String SERVICE_PROVIDER_DOCUMENT_NAME = "Service_Provider_Data";
    public static final String COLLECTION_ADME_SERVICE_LIST = "Adme_Service_list";

    public static final String MODE_CLIENT = "Client";
    public static final String MODE_SERVICE_PROVIDER = "Service provider";
    public static final String STATUS_ONLINE = "Online";
    public static final String  STATUS_OFFLINE = "Offline";
    public static final String ENTRY_MONTHLY_SUBSCRIPTION_PAID = "Paid";

    public static final String ENTRY_PRESSED_TODAY = "pressed_today";
    public static final String ENTRY_REQUESTED_TODAY = "requested_today";
    public static final String ENTRY_COMPLETED_TODAY = "completed_today";
    public static final String ENTRY_INCOME_TODAY = "income_today";
    public static final String ENTRY_INCOME_TOTAL = "income_total";
    public static final String ENTRY_DUE = "due";
    public static final String ENTRY_MONTHLY_SUBSCRIPTION = "monthly_subscription";
    public static final String ENTRY_SERVICE_REFERENCE = "service_reference";

    public static final String ENTRY_PHONE_NO_ONE = "phone_no_one";
    public static final String ENTRY_PHONE_NO_TWO = "phone_no_two";
    public static final String ENTRY_PHONE_NO_ONE_PRIVACY = "privacy_one";
    public static final String ENTRY_PHONE_NO_TWO_PRIVACY = "privacy_two";
    public static final String ENTRY_PHONE_NO_PRIVACY_PUBLIC = "Public";
    public static final String ENTRY_PHONE_NO_PRIVACY_PRIVATE = "Private";

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


    //create database reference
    private FirebaseFirestore db;
    private CollectionReference userRef;
    private FirebaseAuth mAuth;
    private MutableLiveData<User> userData;
    private MutableLiveData<List<Service>> services = new MutableLiveData<>();
    private List<Service> serviceList = new ArrayList<>();
    private String user_id;
    private CollectionReference servicesRef;

    public FirebaseUtilClass() {
        userData = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection(USER_COLLECTION_ID);
        servicesRef = db.collection(COLLECTION_ADME_SERVICE_LIST);
        mAuth = FirebaseAuth.getInstance();
    }

    public MutableLiveData<User> getUserData() {
        if(mAuth.getCurrentUser() != null){
            user_id = mAuth.getCurrentUser().getUid();
            fetchUserData();
        }
        return userData;
    }


    public MutableLiveData<List<Service>> getServices() {
        fetchUserServices();
        return services;
    }

    private void fetchUserData() {
        userRef.document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                user.setmUserId(user_id);
                userData.setValue(user);
            }
        });

        userRef.document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    User user = documentSnapshot.toObject(User.class);
                    user.setmUserId(user_id);
                    userData.setValue(user);
                    Log.d("akash_debug", "onEvent: "+userData.getValue().getService_reference().size());
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
                    String NULL = "";
                    assert user != null;
                    if (user.getDisplayName() != null){
                        username = user.getDisplayName();
                    }
                    else{
                        username = "Adme_User";
                    }
                    if(user.getEmail() != null){
                        email = user.getEmail();
                    }

                    String joined = String.valueOf(user.getMetadata().getCreationTimestamp());
                    String user_id = user.getUid();
                    User new_user = new User(username,email,joined,user_id);
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

    public void fetchUserServices(){
    }

    DatabaseOperationListener uploadUserServiceListener;
    public void uploadUserService(Service service,DatabaseOperationListener listener){
        uploadUserServiceListener = listener;
        servicesRef.add(service).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                updateServiceInUserDocument(service,documentReference.getId());
            }
        });

    }

    private void updateServiceInUserDocument(Service service,String service_id) {
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

}
