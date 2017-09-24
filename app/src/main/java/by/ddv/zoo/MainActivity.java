package by.ddv.zoo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("request_type", "POST");
                startActivity(intent);
            }
        });

        realm = Realm.getDefaultInstance();

        compositeDisposable = new CompositeDisposable();

        //ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);

        loadData();

    }


    private void loadData() {

        progressDialog.show();//show ProgressDialog

        if (hasConnection(this)){//checking for an internet connection
            sendNetworkRequestWithChangedData();//sending data created locally
        }

        //get JSON from net and save to Realm
        RequestInterface requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RequestInterface.class);

        //get PET status=available
/*
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

                        v.setChange("from_net");

                        return  v.getName() != null &&
                                v.getCategory() != null &&
                                isCategoryFieldNotNull(v.getCategory()) &&
                                v.getPhotoUrls() != null &&
                                v.getPhotoUrls().size() != 0 &&
                                isPhotoUrlsNull(v.getPhotoUrls()) &&
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
                                isCategoryFieldNotNull(v.getCategory()) &&
                                v.getPhotoUrls() != null &&
                                v.getPhotoUrls().size() != 0 &&
                                isPhotoUrlsNull(v.getPhotoUrls()) &&
                                v.getTags() != null &&
                                v.getTags().size() != 0 &&
                                isTagNotNull(v.getTags()) &&
                                v.getStatus() != null;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
*/
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

                        if (v.getTags() != null && v.getTags().size() != 0 && isTagNotNull(v.getTags())){
                            v.setTagsRealm(listToRealmListTag(v.getTags()));
                        }

                        v.setChange("from_net");

                        return  v.getName() != null &&
                                v.getCategory() != null &&
                                isCategoryFieldNotNull(v.getCategory()) &&
                                v.getPhotoUrls() != null &&
                                v.getPhotoUrls().size() != 0 &&
                                isPhotoUrlsNull(v.getPhotoUrls()) &&
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

        deleteOldDataFromRealm();//delete old data from Realm

        //save data to Realm
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(petsList);
        realm.commitTransaction();

        setUpRecyclerView();//get data from Realm

        progressDialog.dismiss();//hide ProgressDialog
    }

    private void handleError(Throwable error) {
        Log.e("Throwable error", error.getLocalizedMessage());
        Toast.makeText(this, "Error "+error.getLocalizedMessage(), Toast.LENGTH_LONG).show();

        setUpRecyclerView();//get data from Realm

        progressDialog.dismiss();//hide ProgressDialog
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
                                    deleteOldPetFromRealm(value.getId());//delete old data from Realm
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
                                    deleteOldPetFromRealm(value.getId());
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
                                    Toast.makeText(MainActivity.this, "PET id=" + petJson.getId() + " deleted on the server", Toast.LENGTH_LONG).show();
                                    deleteOldPetFromRealm(petJson.getId());
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

    private void deleteOldPetFromRealm(long id) {
        petsListRealm = realm.where(Pet.class).equalTo("id", id).findAll();
        realm.beginTransaction();
        petsListRealm.deleteAllFromRealm();
        realm.commitTransaction();
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


    private void setUpRecyclerView() {

        petsListRealm = realm.where(Pet.class)
                .notEqualTo("change", "deleted_by_me")
                .findAll();

        petsListRealm = petsListRealm.sort("id");//Sort ascending

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

    private boolean isCategoryFieldNotNull(Category category) {
        boolean notNull;
        if (category != null){
            try {
                category.getId();
                category.getName();
                notNull = true;
            } catch (Exception e) {
                notNull = false;
            }
        } else notNull = false;
        return notNull;
    }

    private boolean isPhotoUrlsNull(List<String> photoUrls) {
        boolean notNull = false;

        for (String str : photoUrls){
            if (str.equals("") || str == null){
                notNull = false;
                break;
            } else notNull = true;
        }

        return notNull;
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
            tagsRealmList.add(tag);
        }
        return tagsRealmList;
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

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        
        return true;
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (recyclerViewAdapter != null) recyclerViewAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.action_refresh:
                recyclerView.setAdapter(null);

                loadData();

                Toast.makeText(this, "The data was refreshed", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_end_list:

                //auto scroll up RecyclerView
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        // Call smooth scroll
                        recyclerView.smoothScrollToPosition(recyclerViewAdapter.getItemCount());
                    }
                });

                break;

            case R.id.action_start_list:

                //auto scroll up RecyclerView
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        // Call smooth scroll
                        recyclerView.smoothScrollToPosition(0);
                    }
                });

                break;

            case R.id.action_info:
                String numberPETs = String.valueOf(recyclerViewAdapter.getItemCount());
                CustomDialogFragment dialog = new CustomDialogFragment();
                Bundle args = new Bundle();
                args.putString("number_pets", numberPETs);
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), "custom");

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
