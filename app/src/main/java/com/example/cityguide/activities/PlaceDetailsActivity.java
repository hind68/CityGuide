package com.example.cityguide.activities;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.cityguide.R;
import com.example.cityguide.database.DatabaseHelper;
import com.example.cityguide.models.Place;
import com.example.cityguide.utils.Constants;
import com.example.cityguide.utils.ImageLoader;
import com.example.cityguide.utils.IntentUtils;
import com.example.cityguide.utils.SessionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PlaceDetailsActivity extends BaseActivity {

    private static final int CAMERA_REQUEST_CODE = 81;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 82;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private Place place;
    private String pendingPhotoPath;

    @Override
    protected int getPageTopInsetDp() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        if (savedInstanceState != null) {
            pendingPhotoPath = savedInstanceState.getString("pendingPhotoPath");
        }

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        int placeId = getIntent().getIntExtra(Constants.EXTRA_PLACE_ID, -1);
        place = databaseHelper.getPlaceById(placeId);
        if (place == null) {
            finish();
            return;
        }

        ImageLoader.load((ImageView) findViewById(R.id.imageHero), place.getImage());
        databaseHelper.addRecentView(getTrackingUserId(sessionManager), place.getId(), Constants.FAVORITE_PLACE);
        ((TextView) findViewById(R.id.textTitle)).setText(place.getName());
        ((TextView) findViewById(R.id.textSubtitle)).setText(place.getCity() + " - " + place.getCategory());
        ((TextView) findViewById(R.id.textDescription)).setText(place.getDescription());
        ((TextView) findViewById(R.id.textAddress)).setText(place.getAddress());
        ((TextView) findViewById(R.id.textMeta)).setText(getString(R.string.rating_phone_label,
                String.valueOf(place.getRating()), place.getPhone()));
        bindUserPhoto();

        findViewById(R.id.buttonOpenMap).setOnClickListener(v ->
                IntentUtils.openMap(this, place.getLatitude(), place.getLongitude(), place.getName()));
        findViewById(R.id.buttonCall).setOnClickListener(v -> IntentUtils.callPhone(this, place.getPhone()));
        findViewById(R.id.buttonTakePhoto).setOnClickListener(v -> startCameraFlow());
        findViewById(R.id.buttonSaveItinerary).setOnClickListener(v -> saveItinerary());
        findViewById(R.id.buttonFavorite).setOnClickListener(v -> {
            if (sessionManager.isGuest() || !sessionManager.isLoggedIn()) {
                Toast.makeText(this, R.string.sign_in_required_favorites, Toast.LENGTH_SHORT).show();
                return;
            }
            databaseHelper.addFavorite(sessionManager.getUserId(), place.getId(), Constants.FAVORITE_PLACE);
            Toast.makeText(this, R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.buttonViewMapScreen).setOnClickListener(v -> {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra(Constants.EXTRA_LATITUDE, place.getLatitude());
            intent.putExtra(Constants.EXTRA_LONGITUDE, place.getLongitude());
            intent.putExtra(Constants.EXTRA_LABEL, place.getName());
            startActivity(intent);
        });
    }

    private int getTrackingUserId(SessionManager sessionManager) {
        return sessionManager.isLoggedIn() ? sessionManager.getUserId() : 0;
    }

    private void bindUserPhoto() {
        ImageView imageUserPhoto = findViewById(R.id.imageUserPhoto);
        String imagePath = databaseHelper.getLatestUserPlacePhoto(getTrackingUserId(sessionManager), place.getId());
        if (imagePath == null || imagePath.trim().isEmpty()) {
            imageUserPhoto.setVisibility(View.GONE);
            return;
        }
        imageUserPhoto.setVisibility(View.VISIBLE);
        imageUserPhoto.setImageURI(Uri.fromFile(new File(imagePath)));
    }

    private void startCameraFlow() {
        if (sessionManager.isGuest() || !sessionManager.isLoggedIn()) {
            Toast.makeText(this, R.string.photo_sign_in_required, Toast.LENGTH_SHORT).show();
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            return;
        }
        openCamera();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, R.string.no_camera_available, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            File photoFile = createPhotoFile();
            Uri photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.setClipData(ClipData.newUri(getContentResolver(), "CityGuide place photo", photoUri));
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            grantCameraUriPermissions(intent, photoUri);
            try {
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            } catch (RuntimeException e) {
                revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Toast.makeText(this, R.string.camera_open_error, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException | IllegalArgumentException e) {
            pendingPhotoPath = null;
            openCameraForThumbnail();
        }
    }

    private void openCameraForThumbnail() {
        Intent fallbackIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(fallbackIntent, CAMERA_REQUEST_CODE);
        } catch (RuntimeException e) {
            Toast.makeText(this, R.string.camera_open_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void grantCameraUriPermissions(Intent intent, Uri photoUri) {
        List<ResolveInfo> cameraApps = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo cameraApp : cameraApps) {
            grantUriPermission(cameraApp.activityInfo.packageName, photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    private File createPhotoFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir == null) {
            storageDir = getFilesDir();
        }
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            throw new IOException("Photo directory was not created.");
        }
        File photoFile = File.createTempFile("cityguide_place_" + place.getId() + "_", ".jpg", storageDir);
        pendingPhotoPath = photoFile.getAbsolutePath();
        return photoFile;
    }

    private void saveItinerary() {
        if (sessionManager.isGuest() || !sessionManager.isLoggedIn()) {
            Toast.makeText(this, R.string.signin_to_save_itineraries, Toast.LENGTH_SHORT).show();
            return;
        }
        databaseHelper.addSavedItinerary(sessionManager.getUserId(), place.getId());
        Toast.makeText(this, R.string.itinerary_saved, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != CAMERA_REQUEST_CODE || resultCode != RESULT_OK) {
            return;
        }

        String imagePath = pendingPhotoPath;
        if ((imagePath == null || imagePath.trim().isEmpty()) && data != null && data.getExtras() != null) {
            Object thumbnail = data.getExtras().get("data");
            if (thumbnail instanceof Bitmap) {
                imagePath = saveThumbnailPhoto((Bitmap) thumbnail);
            }
        }

        if (imagePath != null && !imagePath.trim().isEmpty()) {
            databaseHelper.addUserPlacePhoto(sessionManager.getUserId(), place.getId(), imagePath);
            pendingPhotoPath = null;
            Toast.makeText(this, R.string.photo_added, Toast.LENGTH_SHORT).show();
            bindUserPhoto();
        } else {
            Toast.makeText(this, R.string.photo_not_saved, Toast.LENGTH_SHORT).show();
        }
    }

    private String saveThumbnailPhoto(Bitmap bitmap) {
        File storageDir = new File(getFilesDir(), "cityguide_photos");
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            return null;
        }
        File imageFile = new File(storageDir, "cityguide_place_" + place.getId() + "_" + System.currentTimeMillis() + ".jpg");
        try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, outputStream);
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("pendingPhotoPath", pendingPhotoPath);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
    }
}
