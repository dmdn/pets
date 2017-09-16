package by.ddv.zoo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;


import by.ddv.zoo.models.Category;
import by.ddv.zoo.models.Pet;
import by.ddv.zoo.models.RealmString;
import by.ddv.zoo.models.Tag;
import by.ddv.zoo.network.RequestInterface;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class EditActivity extends AppCompatActivity {

    private Realm realm;

    private long petId;
    private String petName, petStatus, petPhotoUrls;

    EditText etId, etName;

    Spinner spinnerStatus;

    Button btnSave;
    ImageButton btnAddPhoto, btnDeletePhoto;

    TextView tvPhotoUri;

    ImageView ivPhoto;

    private String REQUEST_TYPE;

    static final int GALLERY_REQUEST = 1;

    private ArrayList<String> petUrlsArrayList;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        REQUEST_TYPE = getIntent().getExtras().getString("request_type");
        realm = Realm.getDefaultInstance();

        etId = (EditText) findViewById(R.id.et_edit_id);
        etName = (EditText) findViewById(R.id.et_edit_name);
        tvPhotoUri = (TextView) findViewById(R.id.tv_edit_photo_url);

        ivPhoto = (ImageView) findViewById(R.id.iv_edit_photo);

        spinnerStatus = (Spinner)findViewById(R.id.spinner_status);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.pet_list_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        petUrlsArrayList = new ArrayList<>();

        btnSave = (Button) findViewById(R.id.btn_save);

        if (REQUEST_TYPE.equals("PUT")){

            btnSave.setText("Save Changes");

            petId = getIntent().getExtras().getLong("pet_id");
            petName = getIntent().getExtras().getString("pet_name");
            petStatus = getIntent().getExtras().getString("pet_status");

            petPhotoUrls = getIntent().getExtras().getString("pet_photo_urls");

            if (!petPhotoUrls.equals("")){
                petUrlsArrayList.addAll(Arrays.asList(petPhotoUrls.split(",")));

                Picasso
                        .with(this)
                        .load((petUrlsArrayList.get(0)))
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.no_image)
                        .error(R.drawable.no_image)
                        .into(ivPhoto);
            }


            tvPhotoUri.setText(petPhotoUrls);

            etId.setText(Long.toString(petId));
            etName.setText(petName);

            spinnerStatus.setSelection(setPetStatus(petStatus));


        } else {

            btnSave.setText("Create new PET");
            etId.setText("");
            tvPhotoUri.setText("");

        }



        btnAddPhoto = (ImageButton)findViewById(R.id.btn_add_photo);
        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });

        btnDeletePhoto = (ImageButton)findViewById(R.id.btn_delete_photo);
        btnDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (petUrlsArrayList.size() > 1){

                    int lastIndex = petUrlsArrayList.size() - 1;
                    petUrlsArrayList.remove(lastIndex);

                    tvPhotoUri.setText(arrayListToSring(petUrlsArrayList));
                    Picasso
                            .with(v.getContext())
                            .load(petUrlsArrayList.get(lastIndex - 1))
                            .fit()
                            .centerCrop()
                            .placeholder(R.drawable.no_image)
                            .error(R.drawable.no_image)
                            .into(ivPhoto);
                } else {
                    petUrlsArrayList.clear();
                    ivPhoto.setImageResource(R.drawable.no_image);
                    tvPhotoUri.setText("");
                }



            }
        });




        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(ProgressBar.VISIBLE);

                if (petUrlsArrayList.size() == 0){
                    petUrlsArrayList.add("string");
                }

                if (Long.valueOf(etId.getText().toString()) != petId && REQUEST_TYPE.equals("PUT")){
                    Toast.makeText(getApplicationContext(), "The id=" + petId + " in the pet was erroneously changed", Toast.LENGTH_LONG).show();
                    return;
                }

                if (hasConnection(getApplicationContext())){
                    sendNetworkRequest(REQUEST_TYPE);
                }

                saveToRealm(REQUEST_TYPE);

                progressBar.setVisibility(ProgressBar.INVISIBLE);

                Toast.makeText(EditActivity.this, "PET id=" + etId.getText() + " " + REQUEST_TYPE + " successfully :)", Toast.LENGTH_LONG).show();

                if (REQUEST_TYPE.equals("POST")){
                    clearAllEditText();
                } else {
                    //finish();
                    onBackPressed();
                }

            }
        });


    }



    private void sendNetworkRequest(String requestType) {

        Pet petJson = new Pet();

        if (requestType.equals("POST")){
            petJson.setId(Long.valueOf(etId.getText().toString()));
        } else {
            petJson.setId(petId);
        }

        petJson.setName(etName.getText().toString());

        Category category = new Category();
        category.setId(0L);
        category.setName("string");
        petJson.setCategory(category);

        petJson.setPhotoUrls(petUrlsArrayList);

        ArrayList<Tag> tags = new ArrayList<>();
        Tag tag = new Tag();
        tag.setId(0L);
        tag.setName("string");
        tags.add(tag);
        petJson.setTags(tags);

        petJson.setStatus(getStatusFromSpinner());


        //create retrofit instance
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(MainActivity.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        //get client & call object for the request
        RequestInterface client = retrofit.create(RequestInterface.class);

        Call<Pet> call;
        if (requestType.equals("POST")){
            call = client.createPet(petJson);
        } else call = client.updatePet(petJson);

        call.enqueue(new Callback<Pet>() {
            @Override
            public void onResponse(Call<Pet> call, Response<Pet> response) {
                Toast.makeText(EditActivity.this, "PET id=" + petJson.getId() + " " + requestType + " successfully :)", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Pet> call, Throwable t) {
                Toast.makeText(EditActivity.this, "Something went wrong :(", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void saveToRealm(final String requestType) {

        final RealmResults<Pet> results = realm.where(Pet.class)
                .equalTo("id", petId)
                .findAll();

        if (requestType.equals("PUT")){

            realm.beginTransaction();
            Pet petForDelete = results.get(0);
            petForDelete.deleteFromRealm();
            realm.commitTransaction();

        } else {

            if (results.size() != 0){
                Toast.makeText(this, "PET with id=" + etId.getText().toString() + " exist", Toast.LENGTH_LONG).show();
                return;
            }
        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Pet pet = realm.createObject(Pet.class);

                if (hasConnection(getApplicationContext())){
                    pet.setChange("from_net");
                } else {
                    if (requestType.equals("POST")){
                        long id = Long.valueOf(etId.getText().toString());
                        pet.setId(id);
                        pet.setChange("created_by_me");
                    } else {
                        pet.setId(petId);
                        pet.setChange("changed_by_me");
                    }
                }

                Category category = realm.createObject(Category.class);
                category.setId(0L);
                category.setName("string");
                pet.setCategory(category);

                pet.setName(etName.getText().toString());

                pet.setPhotoUrlsRealm(arrayListToRealmList(petUrlsArrayList));

                Tag tag = realm.createObject(Tag.class);
                tag.setId(0L);
                tag.setName("string");
                RealmList<Tag> tagsRealm = new RealmList<>();
                tagsRealm.add(tag);
                pet.setTagsRealm(tagsRealm);

                pet.setStatus(getStatusFromSpinner());

            }
        });

    }


    private RealmList<RealmString> arrayListToRealmList(ArrayList<String> arrayList) {

        RealmList<RealmString> realmList = new RealmList<>();

        for (String str : arrayList) {
            RealmString realmString = realm.createObject(RealmString.class);
            realmString.setValue(str);
            realmList.add(realmString);
        }
        return realmList;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }



    private String getStatusFromSpinner() {
        String selected = spinnerStatus.getSelectedItem().toString();
        return selected;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);


        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();

                    Picasso
                            .with(this)
                            .load(selectedImage.toString())
                            .fit()
                            .centerCrop()
                            .placeholder(R.drawable.no_image)
                            .error(R.drawable.no_image)
                            .into(ivPhoto);

                    petUrlsArrayList.add(getRealPathFromUri(this, Uri.parse(selectedImage.toString())));
                    tvPhotoUri.setText(arrayListToSring(petUrlsArrayList));
                }
        }

    }

    private String arrayListToSring(ArrayList<String> arrayList) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < arrayList.size(); i++) {
            if (i < arrayList.size() - 1){
                sb.append(arrayList.get(i)).append(", ");
            } else sb.append(arrayList.get(i));
        }
        return sb.toString();
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return "file://" + cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    private void clearAllEditText() {
        etId.setText("");
        etName.setText("");
        tvPhotoUri.setText("");
        ivPhoto.setImageResource(R.drawable.no_image);
        spinnerStatus.setSelection(0);
    }


    private int setPetStatus(String str) {
        int i = 0;
        switch(str) {
            case "available":
                i = 0;
                break;
            case "pending":
                i = 1;
                break;
            case "sold":
                i = 2;
                break;
            case "test":
                i = 3;
                break;
        }
        return i;
    }


    public static boolean hasConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()){
            return true;
        }
        return false;
    }





}
