package by.ddv.zoo.network;

import java.util.List;

import by.ddv.zoo.models.Pet;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;



public interface RequestInterface {

    //access Pets by rxJava
    @GET("pet/findByStatus?status=pending")
    Observable<List<Pet>> getPetRx1();

    //access Pets by rxJava
    @GET("pet/findByStatus?status=available")
    Observable<List<Pet>> getPetRx2();

    //access Pets by rxJava
    @GET("pet/findByStatus?status=sold")
    Observable<List<Pet>> getPetRx3();

    //create new Pet by rxJava
    @POST("pet")
    Observable<Pet> createPetRx(@Body Pet pet);

    //update existing Pet by rxJava
    @PUT("pet")
    Observable<Pet> updatePetRx(@Body Pet pet);

    //delete Pet by rxJava
    @DELETE("pet/{petId}")
    Observable<Void> deletePetRx(@Path("petId") long petId);


    //create new Pet
    @POST("pet")
    Call<Pet> createPet(@Body Pet pet);

    //update existing Pet
    @PUT("pet")
    Call<Pet> updatePet(@Body Pet pet);

    //delete Pet
    @DELETE("pet/{petId}")
    Call<Void> deletePet(@Path("petId") long petId);

}
