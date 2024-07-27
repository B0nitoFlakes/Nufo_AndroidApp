package com.example.nufo.Activities.FoodRecognition;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nufo.Activities.FoodDiary.FoodDiaryActivity;
import com.example.nufo.Activities.MainActivity;
import com.example.nufo.Activities.Recipes.SearchFoodActivity;
import com.example.nufo.Activities.Recipes.SearchFoodDetailsActivity;
import com.example.nufo.Adapters.DetectNamesAdapter;
import com.example.nufo.Helpers.DiaryHelperClass;
import com.example.nufo.Listeners.OnDishSelectedListener;
import com.example.nufo.Listeners.SearchIngredientListener;
import com.example.nufo.Models.Result;
import com.example.nufo.Models.SearchIngredientApiResponse;
import com.example.nufo.R;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nufo.RequestManager;
import com.example.nufo.databinding.ActivityFoodRecognitionBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class FoodRecognitionActivity extends AppCompatActivity implements Detector.DetectorListener {

    private String mealType;
    private Button buttonHome_foodRecognition, captureButton;
    private ActivityFoodRecognitionBinding binding;
    private final boolean isFrontCamera = false;
    private Preview preview;
    private ImageAnalysis imageAnalyzer;
    private Camera camera;
    private ProcessCameraProvider cameraProvider;
    private Detector detector;
    private ExecutorService cameraExecutor;
    private static final String TAG = "Camera";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    private Map<String, AtomicInteger> detectedClassCounts = new HashMap<>();
    private RequestManager requestManager;
    private DatabaseReference reference;
    private FirebaseDatabase database;
    private DetectNamesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFoodRecognitionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("Food Recognition");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        buttonHome_foodRecognition = findViewById(R.id.buttonHome_foodRecognition);
        mealType = getIntent().getStringExtra("mealType");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();

        // Initialize FirebaseDatabase
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users").child(uid);


        detector = new Detector(this, Constants.MODEL_PATH, Constants.LABELS_PATH, this);
        detector.setup();

        if (detector != null && allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        cameraExecutor = Executors.newSingleThreadExecutor();
        captureButton = findViewById(R.id.capture_button);
        captureButton.setOnClickListener(v -> captureDetectedObjects());

        buttonHome_foodRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodRecognitionActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startCamera() {
        ProcessCameraProvider.getInstance(this).addListener(() -> {
            try {
                cameraProvider = ProcessCameraProvider.getInstance(this).get();
                bindCameraUseCases();
            } catch (Exception e) {
                Log.e(TAG, "Camera initialization error", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }
    private void bindCameraUseCases() {
        if (cameraProvider == null) {
            Log.e(TAG, "Camera initialization failed.");
            return;
        }

        int rotation = binding.viewFinder.getDisplay().getRotation();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview = new Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(rotation)
                .build();

        imageAnalyzer = new ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetRotation(rotation)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build();

        imageAnalyzer.setAnalyzer(cameraExecutor, imageProxy -> {
            ByteBuffer buffer = imageProxy.getPlanes()[0].getBuffer();
            int width = imageProxy.getWidth();
            int height = imageProxy.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            imageProxy.close();

            Matrix matrix = new Matrix();
            matrix.postRotate(imageProxy.getImageInfo().getRotationDegrees());

            if (isFrontCamera) {
                matrix.postScale(-1f, 1f, width / 2f, height / 2f);
            }

            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

            detector.detect(rotatedBitmap);
        });

        cameraProvider.unbindAll();

        try {
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer);
            preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());
        } catch (Exception e) {
            Log.e(TAG, "Use case binding failed", e);
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    private final ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                if (permissions.getOrDefault(Manifest.permission.CAMERA, false)) {
                    startCamera();
                } else {
                    // Handle permission denial, e.g., show a message to the user
                    Log.e(TAG, "Camera permission denied.");
                }
            });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
        detector.clear(); // Clear any resources held by your detector
        cameraExecutor.shutdown(); // Shutdown the executor service

    }
    @Override
    public void onBackPressed() {
        // Stop image analysis and release resources
        if (imageAnalyzer != null) {
            imageAnalyzer.clearAnalyzer(); // Custom method to clear the image analyzer
        }

        super.onBackPressed();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Stop image analysis and release resources
            if (imageAnalyzer != null) {
                imageAnalyzer.clearAnalyzer(); // Custom method to clear the image analyzer
            }

            finish(); // Optional: Finish the activity immediately
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void captureDetectedObjects() {
        showBottomSheet(new ArrayList<>(detectedClassCounts.keySet())); // Show the bottom sheet with detected objects
    }
    private void showBottomSheet(List<String> classNames) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout_recognition, null);


        requestManager = new RequestManager(this);
        // Create a list to collect all matched results
        List<Result> matchedNames = new ArrayList<>();

        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.recycler_view_labels);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DetectNamesAdapter(FoodRecognitionActivity.this, matchedNames, requestManager, detectedClassCounts);
        recyclerView.setAdapter(adapter);

        for (String classname : classNames) {
            requestManager.searchIngredient(new SearchIngredientListener() {
                @Override
                public void didFetch(SearchIngredientApiResponse response, String message) {
                    // Add all matched names to the list
                    matchedNames.addAll(response.results);
                    adapter.notifyDataSetChanged();

                    bottomSheetView.findViewById(R.id.button_detected_logFood).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            logSelectedDishes(mealType);
                            Intent intent = new Intent(FoodRecognitionActivity.this, FoodDiaryActivity.class);
                            startActivity(intent);
                        }
                    });
                }
                @Override
                public void didError(String message) {
                    Toast.makeText(FoodRecognitionActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }, classname);
        }
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS);
        }
    }
    @Override
    public void onEmptyDetect() {
        runOnUiThread(() -> binding.overlay.invalidate());
    }
    @Override
    public void onDetect(List<BoundingBox> boundingBoxes, long inferenceTime) {
        detectedClassCounts.clear();

        for (BoundingBox box : boundingBoxes) {
            String className = box.getClsName();
            if (detectedClassCounts.containsKey(className)) {
                detectedClassCounts.get(className).incrementAndGet();
            } else {
                detectedClassCounts.put(className, new AtomicInteger(1));
            }
        }

        runOnUiThread(() -> {
            binding.inferenceTime.setText(inferenceTime + "ms");
            binding.overlay.setResults(boundingBoxes);
            binding.overlay.invalidate();
        });
    }
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
    private void logSelectedDishes(String mealType) {
        List<DiaryHelperClass> selectedDishes = adapter.getSelectedDishes();

        DatabaseReference foodLogRef = reference.child("foodLog").child(mealType).child(getCurrentDate());

        for (DiaryHelperClass dish : selectedDishes) {
            DatabaseReference newFoodRef = foodLogRef.push();
            String foodName = dish.getFoodName();
            newFoodRef.setValue(dish).addOnSuccessListener(aVoid->
            {
                Toast.makeText(this, foodName + " logged for " + mealType, Toast.LENGTH_SHORT).show();
            });
        }
    }

}
