package com.example.nufo.Listeners;

import com.example.nufo.Models.RecipeDetailsResponse;
import com.example.nufo.Models.SimilarRecipeResponse;

import java.util.List;

public interface SimilarRecipeListener {
    void didFetch(List<SimilarRecipeResponse> response, String message);
    void didError(String message);
}
