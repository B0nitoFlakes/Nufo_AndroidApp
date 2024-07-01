package com.example.nufo.Listeners;

import com.example.nufo.Models.IngredientDetailsResponse;
import com.example.nufo.Models.InstructionsResponse;

import java.util.List;

public interface IngredientDetailsListener {
    void didFetch(IngredientDetailsResponse response, String message);
    void didError(String message);
}
