package com.example.nufo.Listeners;

import com.example.nufo.Models.RecipeDetailsResponse;
import com.example.nufo.Models.SearchIngredientApiResponse;

public interface SearchIngredientListener {
    void didFetch(SearchIngredientApiResponse response, String message);
    void didError(String message);

}
