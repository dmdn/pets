package by.ddv.zoo;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import by.ddv.zoo.models.Category;
import by.ddv.zoo.models.Pet;
import by.ddv.zoo.models.RealmString;
import by.ddv.zoo.models.Tag;
import by.ddv.zoo.network.RequestInterface;
import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.OrderedRealmCollectionSnapshot;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



import android.widget.Filter;
import android.widget.Filterable;



public class MyRealmRecyclerViewAdapter extends RealmRecyclerViewAdapter<Pet, MyRealmRecyclerViewAdapter.ViewHolder> implements Filterable {

    final Context context;

    private OrderedRealmCollection<Pet> mArrayList;
    private OrderedRealmCollection<Pet> mFilteredList;


    public MyRealmRecyclerViewAdapter(Context context, OrderedRealmCollection<Pet> data) {
        super(data, true);
        this.context = context;
        mArrayList = data;
        mFilteredList = data;
        //setHasStableIds(true);//support for animation insert / change / delete one element
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Pet pet = getItem(position);

        final String petListPhotoUrlsString;

        final long petId = pet.getId();
        final String petName = pet.getName();
        final String petStatus = pet.getStatus();

        Log.e("^^^^^", "id= "+petId);
        Log.e("^^^^^", "name= "+petName);


        RealmList<RealmString> realmStrings = pet.getPhotoUrlsRealm();
        ArrayList<String> petListPhotoUrls = new ArrayList<>();

        if (realmStrings.size() == 0){
            petListPhotoUrlsString = "";
        } else {

            for (RealmString realmStr:realmStrings) {
                petListPhotoUrls.add(realmStr.getValue());
            }
            petListPhotoUrlsString = realmListToSring(petListPhotoUrls);

            Picasso
                    .with(context)
                    .load((petListPhotoUrls.get(0)))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(holder.ivPhoto);

        }


        holder.tvId.setText(String.format("id: %s", petId));
        holder.tvName.setText(String.format("name: %s", petName));
        holder.tvPhotoUrls.setText(String.format("photoUrls: %s", petListPhotoUrlsString));
        holder.tvStatus.setText(String.format("status: %s", petStatus));


        holder.btnEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), EditActivity.class);
                intent.putExtra("pet_id", petId);
                intent.putExtra("pet_name", petName);
                intent.putExtra("pet_photo_urls", petListPhotoUrlsString);
                intent.putExtra("pet_status", petStatus);
                intent.putExtra("request_type", "PUT");
                v.getContext().startActivity(intent);

            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl(MainActivity.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());

                Retrofit retrofit = builder.build();
                RequestInterface client = retrofit.create(RequestInterface.class);
                Call<Void> call = client.deletePet(petId);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(context, "PET id=" + petId +" is DELETED successfully :)", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Something went wrong :(", Toast.LENGTH_LONG).show();
                    }
                });



                final Realm realm = Realm.getDefaultInstance();
                final RealmResults<Pet> results = realm.where(Pet.class)
                        .equalTo("id", petId)
                        .findAll();

                if (hasConnection(context)){

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            Pet petForDelete = results.get(0);
                            petForDelete.deleteFromRealm();

                            Toast.makeText(context, "PET id=" + petId +" is DELETED successfully :)", Toast.LENGTH_LONG).show();
                        }
                    });

                } else {

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            Pet petForDelete = results.get(0);


                            long id = petForDelete.getId();

                            long categoryId = petForDelete.getId();
                            String categoryName = petForDelete.getName();

                            String name = petForDelete.getName();

                            RealmList<RealmString> realmListString = new RealmList<>();
                            for (RealmString realmStrDelete : petForDelete.getPhotoUrlsRealm()){
                                RealmString realmStr = realm.createObject(RealmString.class);
                                realmStr.setValue(realmStrDelete.getValue());
                                realmListString.add(realmStr);
                            }

                            RealmList<Tag> realmListTag = new RealmList<>();
                            for (Tag tagDelete : petForDelete.getTagsRealm()){
                                Tag tag = realm.createObject(Tag.class);
                                tag.setId(tagDelete.getId());
                                tag.setName(tagDelete.getName());
                                realmListTag.add(tag);
                            }

                            String status = petForDelete.getStatus();


                            petForDelete.deleteFromRealm();


                            Pet pet = realm.createObject(Pet.class);
                            pet.setId(id);
                            Category category = realm.createObject(Category.class);
                            category.setId(categoryId);
                            category.setName(categoryName);
                            pet.setCategory(category);

                            pet.setName(name);
                            pet.setPhotoUrlsRealm(realmListString);
                            pet.setTagsRealm(realmListTag);
                            pet.setStatus(status);
                            pet.setChange("deleted_by_me");


                            Toast.makeText(context, "PET id=" + petId +" is DELETED successfully :)", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                realm.close();



            }

        });

    }



    //animation
    /*
    @Override
    public long getItemId(int index) {//Return the stable ID for the item at position.
        //noinspection ConstantConditions
        return getItem(index).getId();
    }
*/



    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName, tvId, tvStatus, tvPhotoUrls;
        private ImageButton btnEdit, btnDelete;
        private ImageView ivPhoto;

        public ViewHolder(View view) {
            super(view);

            tvName = (TextView)view.findViewById(R.id.tv_name);
            tvId = (TextView)view.findViewById(R.id.tv_id);
            tvPhotoUrls = (TextView)view.findViewById(R.id.tv_photoUrls);
            tvStatus = (TextView)view.findViewById(R.id.tv_status);

            btnEdit = (ImageButton) view.findViewById(R.id.img_btn_edit);
            btnDelete = (ImageButton) view.findViewById(R.id.img_btn_delete);

            ivPhoto = (ImageView) view.findViewById(R.id.iv_photo);


        }
    }




    private String realmListToSring(ArrayList<String> list) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            if (i < list.size() - 1){
                sb.append(list.get(i)).append(", ");
            } else sb.append(list.get(i));
        }
        return sb.toString();

    }



    public static boolean hasConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()){
            return true;
        }
        return false;
    }




    //SearchView with RecyclerView in Realm
    public Filter getFilter() {

        PetFilter filter = new PetFilter(this);
        return filter;
    }

    private class PetFilter
            extends Filter {
        private final MyRealmRecyclerViewAdapter adapter;

        private PetFilter(MyRealmRecyclerViewAdapter adapter) {
            super();
            this.adapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filterResults(constraint.toString());

        }
    }

    //filter results by name
    public void filterResults(String text) {
        text = text == null ? null : text.toLowerCase().trim();
        final Realm realm = Realm.getDefaultInstance();
        if(text == null || "".equals(text)) {
            updateData(realm.where(Pet.class).findAll());
        } else {
            updateData(realm.where(Pet.class)
                    .contains("name", text, Case.INSENSITIVE)
                    .findAll());
        }
        realm.close();
    }




}
