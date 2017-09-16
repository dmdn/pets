package by.ddv.zoo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;
import java.util.List;

import by.ddv.zoo.models.Category;
import by.ddv.zoo.models.Pet;
import by.ddv.zoo.models.RealmString;
import by.ddv.zoo.models.Tag;
import by.ddv.zoo.network.RequestInterface;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyLogger";

    public static final String BASE_URL = "http://petstore.swagger.io/v2/";
    private CompositeDisposable compositeDisposable;

    private Realm realm;

    private RecyclerView recyclerView;
    private MyRealmRecyclerViewAdapter recyclerViewAdapter;
    private RealmResults<Pet> petsListRealm;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();

        compositeDisposable = new CompositeDisposable();

        //ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);

        if (hasConnection(this)){//checking for an internet connection

            progressDialog.show();//show ProgressDialog

            sendNetworkRequestWithChangedData();//sending data created locally
            deleteOldDataFromRealm();//delete old data from Realm
            loadJsonToRealm();//get JSON from net and save to Realm

            progressDialog.dismiss();//hide ProgressDialog
        }
        setUpRecyclerView();//get data from Realm

    }

    private void sendNetworkRequestWithChangedData() {

        try {
            RealmResults<Pet> petsListRealmPOST = realm.where(Pet.class).equalTo("change", "created_by_me").findAll();
            RealmResults<Pet> petsListRealmPUT = realm.where(Pet.class).equalTo("change", "changed_by_me").findAll();
            RealmResults<Pet> petsListRealmDELETE = realm.where(Pet.class).equalTo("change", "deleted_by_me").findAll();

            if (petsListRealmPOST.size() != 0){
                for (Pet pet:petsListRealmPOST) {
                    Pet petJson = createPetJson(pet);
                    Gson gson = new GsonBuilder().create();

                    //create retrofit instance
                    RequestInterface requestInterface = new Retrofit.Builder()
                            .baseUrl(MainActivity.BASE_URL)
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build().create(RequestInterface.class);

                    requestInterface.createPetRx(petJson)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new DisposableObserver<Pet>() {
                                @Override
                                public void onNext(Pet value) {
                                    Toast.makeText(MainActivity.this, "PET id=" + value.getId() + " added to the server", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(TAG, e.getLocalizedMessage());
                                    Toast.makeText(MainActivity.this, "Something went wrong :(", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onComplete() {
                                }
                            });
                }



            }

            if (petsListRealmPUT.size() != 0){
                for (Pet pet:petsListRealmPUT) {
                    Pet petJson = createPetJson(pet);
                    Gson gson = new GsonBuilder().create();

                    //create retrofit instance
                    RequestInterface requestInterface = new Retrofit.Builder()
                            .baseUrl(MainActivity.BASE_URL)
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build().create(RequestInterface.class);

                    requestInterface.updatePetRx(petJson)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new DisposableObserver<Pet>() {
                                @Override
                                public void onNext(Pet value) {
                                    Toast.makeText(MainActivity.this, "PET id=" + value.getId() + " updated on the server", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(TAG, e.getLocalizedMessage());
                                    Toast.makeText(MainActivity.this, "Something went wrong :(", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onComplete() {
                                }
                            });
                }



            }

            if (petsListRealmDELETE.size() != 0){
                for (Pet pet:petsListRealmDELETE) {
                    Pet petJson = createPetJson(pet);
                    Gson gson = new GsonBuilder().create();

                    //create retrofit instance
                    RequestInterface requestInterface = new Retrofit.Builder()
                            .baseUrl(MainActivity.BASE_URL)
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build().create(RequestInterface.class);

                    requestInterface.deletePetRx(petJson.getId())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new DisposableObserver<Void>() {
                                @Override
                                public void onNext(Void value) {
                                    Toast.makeText(MainActivity.this, "PET id=" + value + " deleted on the server", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(TAG, e.getLocalizedMessage());
                                    Toast.makeText(MainActivity.this, "Something went wrong :(", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                }



            }
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            Toast.makeText(MainActivity.this, "Something went wrong :(", Toast.LENGTH_LONG).show();
        }
    }

    private Pet createPetJson(Pet pet) {
        long id = pet.getId();
        Category category = new Category();
        category.setId(pet.getCategory().getId());
        category.setName(pet.getCategory().getName());
        String name = pet.getName();
        List<String> photoUrls = realmListToArrayListString(pet.getPhotoUrlsRealm());
        List<Tag> tags = realmListToArrayListTag(pet.getTagsRealm());
        String status = pet.getStatus();

        Pet petJson = new Pet();
        petJson.setId(id);
        petJson.setCategory(category);
        petJson.setName(name);
        petJson.setPhotoUrls(photoUrls);
        petJson.setTags(tags);
        petJson.setStatus(status);

        return petJson;
    }


    private void loadJsonToRealm() {

        RequestInterface requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RequestInterface.class);

        //get PET status=available
        compositeDisposable.add(requestInterface.getPetRx1()
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(new Function<List<Pet>, List<Pet>>() {
                    @Override
                    public List<Pet> apply(List<Pet> v) {
                        return v;
                    }
                })
                .filter(new Predicate<Pet>() {//Checking objects for valid
                    @Override
                    public boolean test(Pet v) {
                        if (v.getPhotoUrls() != null && v.getPhotoUrls().size() != 0){
                            v.setPhotoUrlsRealm(listToRealmListString(v.getPhotoUrls()));
                        }

                        if (v.getTags() != null && v.getTags().size() != 0){
                            v.setTagsRealm(listToRealmListTag(v.getTags()));
                        }

                        return  v.getName() != null &&
                                v.getCategory() != null &&
                                v.getId() != 0L &&
                                v.getPhotoUrls() != null &&
                                v.getPhotoUrls().size() != 0 &&
                                v.getTags() != null &&
                                v.getTags().size() != 0 &&
                                isTagNotNull(v.getTags()) &&
                                v.getStatus() != null;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));

        //get PET status=pending
        compositeDisposable.add(requestInterface.getPetRx2()
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(new Function<List<Pet>, List<Pet>>() {
                    @Override
                    public List<Pet> apply(List<Pet> v) {
                        return v;
                    }
                })
                .filter(new Predicate<Pet>() {//Checking objects for valid
                    @Override
                    public boolean test(Pet v) {
                        if (v.getPhotoUrls() != null && v.getPhotoUrls().size() != 0){
                            v.setPhotoUrlsRealm(listToRealmListString(v.getPhotoUrls()));
                        }

                        if (v.getTags() != null && v.getTags().size() != 0){
                            v.setTagsRealm(listToRealmListTag(v.getTags()));
                        }

                        return  v.getName() != null &&
                                v.getCategory() != null &&
                                v.getId() != 0L &&
                                v.getPhotoUrls() != null &&
                                v.getPhotoUrls().size() != 0 &&
                                v.getTags() != null &&
                                v.getTags().size() != 0 &&
                                isTagNotNull(v.getTags()) &&
                                v.getStatus() != null;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));

        //get PET status=sold
        compositeDisposable.add(requestInterface.getPetRx3()
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(new Function<List<Pet>, List<Pet>>() {
                    @Override
                    public List<Pet> apply(List<Pet> v) {
                        return v;
                    }
                })
                .filter(new Predicate<Pet>() {//Checking objects for valid
                    @Override
                    public boolean test(Pet v) {
                        if (v.getPhotoUrls() != null && v.getPhotoUrls().size() != 0){
                            v.setPhotoUrlsRealm(listToRealmListString(v.getPhotoUrls()));
                        }

                        if (v.getTags() != null && v.getTags().size() != 0){
                            v.setTagsRealm(listToRealmListTag(v.getTags()));
                        }

                        return  v.getId() != 0L &&
                                v.getCategory() != null &&
                                v.getName() != null &&
                                v.getPhotoUrls() != null &&
                                v.getPhotoUrls().size() != 0 &&
                                v.getTags() != null &&
                                v.getTags().size() != 0 &&
                                isTagNotNull(v.getTags()) &&
                                v.getStatus() != null;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));

    }



    private void handleResponse(List<Pet> petsList) {

        realm.beginTransaction();



        realm.copyToRealmOrUpdate(petsList);
        realm.commitTransaction();
    }

    private void handleError(Throwable error) {
        Log.e("Throwable error", error.getLocalizedMessage());
        Toast.makeText(this, "Error "+error.getLocalizedMessage(), Toast.LENGTH_LONG).show();

    }

    private void setUpRecyclerView() {

        petsListRealm = realm.where(Pet.class)
                .notEqualTo("change", "deleted_by_me")
                .findAll();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerViewAdapter = new MyRealmRecyclerViewAdapter(getApplicationContext(), petsListRealm);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setHasFixedSize(true);
    }

    private void deleteOldDataFromRealm(){
        try {
            petsListRealm = realm.where(Pet.class).findAll();
            realm.beginTransaction();
            petsListRealm.deleteAllFromRealm();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> realmListToArrayListString(RealmList<RealmString> realmList) {

        ArrayList<String> arrayList = new ArrayList<>();

        for (RealmString realmStr : realmList) {
            arrayList.add(realmStr.getValue());
        }
        return arrayList;
    }

    private RealmList<RealmString> listToRealmListString(List<String> list) {
        RealmList<RealmString> realmList = new RealmList<>();
        for (String str : list) {
            RealmString realmStr = new RealmString();
            realmStr.setValue(str);
            realmList.add(realmStr);
        }
        return realmList;
    }

    private ArrayList<Tag> realmListToArrayListTag(RealmList<Tag> tagsRealm) {
        ArrayList<Tag> arrayListTags = new ArrayList<>();
        for (Tag tag:tagsRealm){
            Tag tagJson = new Tag();
            tagJson.setId(tag.getId());
            tagJson.setName(tag.getName());
            arrayListTags.add(tagJson);
        }
        return arrayListTags;
    }

    private boolean isTagNotNull(List<Tag> tags) {
        boolean notNull = false;
        for (Tag tag : tags) {
            if (tag != null){
                try {
                    tag.getId();
                    tag.getName();
                    notNull = true;
                } catch (Exception e) {
                    notNull = false;
                    break;
                }
            } else {
                notNull = false;
                break;
            }

        }
         return notNull;
    }

    private RealmList<Tag> listToRealmListTag(List<Tag> list) {
        RealmList<Tag> tagsRealmList = new RealmList<>();
        for (Tag tag : list) {
            if (tag == null){
                Tag tagNull = new Tag();
                tagNull.setId(0L);
                tagNull.setName("null");
                tagsRealmList.add(tagNull);
            } else {
                if (tag.getName() == null){
                    tag.setName("null");
                }
                tagsRealmList.add(tag);
            }
        }
        return null;
    }



    public static boolean hasConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()){
            return true;
        }
        return false;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        recyclerView.setAdapter(null);
        realm.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_create_new_pet:
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("request_type", "POST");
                startActivity(intent);
                break;
            case R.id.action_refresh:
                recyclerView.setAdapter(null);

                if (hasConnection(this)){//checking for an internet connection

                    progressDialog.show();//show ProgressDialog

                    sendNetworkRequestWithChangedData();//sending data created locally
                    deleteOldDataFromRealm();//delete old data from Realm
                    loadJsonToRealm();//get JSON from net and save to Realm

                    progressDialog.dismiss();//hide ProgressDialog
                }
                setUpRecyclerView();//get data from Realm

                Toast.makeText(this, "The data was refreshed", Toast.LENGTH_LONG).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
