package com.example.nufo;

import android.content.Context;

import com.example.nufo.Listeners.IngredientDetailsListener;
import com.example.nufo.Listeners.InstructionsListener;
import com.example.nufo.Listeners.RandomRecipeResponseListener;
import com.example.nufo.Listeners.RecipeDetailsListener;
import com.example.nufo.Listeners.SearchIngredientListener;
import com.example.nufo.Listeners.SimilarRecipeListener;
import com.example.nufo.Models.IngredientDetailsResponse;
import com.example.nufo.Models.InstructionsResponse;
import com.example.nufo.Models.RandomRecipeApiResponse;
import com.example.nufo.Models.RecipeDetailsResponse;
import com.example.nufo.Models.SearchIngredientApiResponse;
import com.example.nufo.Models.SimilarRecipeResponse;
import com.google.android.material.shape.ShapePath;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class RequestManager {
    Context context;
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public RequestManager(Context context){
        this.context =  context;
    }

    public void getRandomRecipe(RandomRecipeResponseListener listener, List<String> tags){
        CallRandomRecipes callRandomRecipes = retrofit.create(CallRandomRecipes.class);
        Call<RandomRecipeApiResponse> call = callRandomRecipes.callRandomRecipe(context.getString(R.string.api_key), "12", tags);
        call.enqueue(new Callback<RandomRecipeApiResponse>() {
            @Override
            public void onResponse(Call<RandomRecipeApiResponse> call, Response<RandomRecipeApiResponse> response) {
                if(!response.isSuccessful())
                {
                    listener.didError(response.message() +"cannot");
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<RandomRecipeApiResponse> call, Throwable t) {
                listener.didError(t.getMessage() + "cannot");
            }
        });
    }

    public void getRecipeDetails(RecipeDetailsListener listener, int id, boolean includeNutrition)
    {
        CallRecipeDetails callRecipeDetails = retrofit.create(CallRecipeDetails.class);
        Call<RecipeDetailsResponse> call = callRecipeDetails.callRecipeDetails(id, context.getString(R.string.api_key), includeNutrition);
        call.enqueue(new Callback<RecipeDetailsResponse>() {
            @Override
            public void onResponse(Call<RecipeDetailsResponse> call, Response<RecipeDetailsResponse> response) {
                if(!response.isSuccessful())
                {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<RecipeDetailsResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void getSimilarRecipes(SimilarRecipeListener listener, int id)
    {
        CallSimilarRecipes callSimilarRecipes = retrofit.create(CallSimilarRecipes.class);
        Call<List<SimilarRecipeResponse>> call = callSimilarRecipes.callSimilarRecipe(id, "5", context.getString(R.string.api_key));
        call.enqueue(new Callback<List<SimilarRecipeResponse>>() {
            @Override
            public void onResponse(Call<List<SimilarRecipeResponse>> call, Response<List<SimilarRecipeResponse>> response) {
                if(!response.isSuccessful())
                {
                    listener.didError(response.message() + "cannot");
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<List<SimilarRecipeResponse>> call, Throwable t) {
                listener.didError(t.getMessage() + "cannot");
            }
        });
    }

    public void getInstructions(InstructionsListener listener, int id)
    {
        CallInstructions callInstructions = retrofit.create(CallInstructions.class);
        Call<List<InstructionsResponse>> call = callInstructions.callInstructions(id, context.getString(R.string.api_key));
        call.enqueue(new Callback<List<InstructionsResponse>>() {
            @Override
            public void onResponse(Call<List<InstructionsResponse>> call, Response<List<InstructionsResponse>> response) {
                if(!response.isSuccessful())
                {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<List<InstructionsResponse>> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });

    }

    public void searchIngredient(SearchIngredientListener listener, String query)
    {
        CallSearchIngredients callSearchIngredients = retrofit.create(CallSearchIngredients.class);
        Call<SearchIngredientApiResponse> call = callSearchIngredients.callSearchIngredients(context.getString(R.string.api_key), "1", query);
        call.enqueue(new Callback<SearchIngredientApiResponse>() {
            @Override
            public void onResponse(Call<SearchIngredientApiResponse> call, Response<SearchIngredientApiResponse> response) {
                if(!response.isSuccessful())
                {
                    listener.didError(response.message() +"cannot");
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<SearchIngredientApiResponse> call, Throwable t) {
                listener.didError(t.getMessage() + "cannot");
            }
        });
    }

    public void getIngredientDetails(IngredientDetailsListener listener, int id)
    {
        CallIngredientDetails callIngredientDetails = retrofit.create(CallIngredientDetails.class);
        Call<IngredientDetailsResponse> call = callIngredientDetails.callIngredientDetails(id,"1", context.getString(R.string.api_key));
        call.enqueue(new Callback<IngredientDetailsResponse>() {
            @Override
            public void onResponse(Call<IngredientDetailsResponse> call, Response<IngredientDetailsResponse> response) {
                if(!response.isSuccessful())
                {
                    listener.didError(response.message() +"cannot");
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<IngredientDetailsResponse> call, Throwable t) {
                listener.didError(t.getMessage() + "cannot");
            }
        });
    }


    private interface CallRandomRecipes{
        @GET("/recipes/random")
        Call<RandomRecipeApiResponse> callRandomRecipe(
                @Query("apiKey") String apiKey,
                @Query("number") String number,
                @Query("tags") List<String> tags
        );
    }

    private interface CallRecipeDetails{
        @GET("recipes/{id}/information")
        Call<RecipeDetailsResponse> callRecipeDetails(
                @Path("id") int id,
                @Query("apiKey") String apiKey,
                @Query("includeNutrition") boolean includeNutrition
        );
    }

    private interface CallSimilarRecipes{
        @GET("recipes/{id}/similar")
        Call<List<SimilarRecipeResponse>> callSimilarRecipe(
                @Path("id") int id,
                @Query("number") String number,
                @Query("apiKey") String apiKey

        );
    }

    private interface CallInstructions{
        @GET("recipes/{id}/analyzedInstructions")
        Call<List<InstructionsResponse>> callInstructions(
                @Path("id") int id,
                @Query("apiKey") String apiKey
        );
    }

    private interface CallSearchIngredients{
        @GET("food/ingredients/search")
        Call<SearchIngredientApiResponse> callSearchIngredients(
                @Query("apiKey") String apiKey,
                @Query("number") String number,
                @Query("query") String query
        );

    }

    private interface CallIngredientDetails{
        @GET("food/ingredients/{id}/information")
        Call<IngredientDetailsResponse> callIngredientDetails(
                @Path("id") int id,
                @Query("amount") String amount,
                @Query("apiKey") String apiKey
        );
    }


}
