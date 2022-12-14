package com.example.simpleinstagram;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;


public class MoreFragment extends Fragment {
    public static final String TAG="MoreFragment";
    public static  final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE=42;
    EditText etDescription;
    Button btnCaptureImage;
    ImageView ivPostImage;
    Button btnSubmit;
    public File photoFile;
    public String photoFileName = "photo.jpg";
    public ProgressBar progressLoading ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etDescription=view.findViewById(R.id.etDescription);
        btnCaptureImage=view.findViewById(R.id.btnCaptureImage);
        ivPostImage=view.findViewById(R.id.ivPostImage);
        btnSubmit=view.findViewById(R.id.btnSubmit);
        progressLoading=view.findViewById(R.id.pLoading);
        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                launchCamera();
                Toast.makeText(getContext(), "Take", Toast.LENGTH_SHORT).show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description=etDescription.getText().toString();
                if(description.isEmpty()){
                    Toast.makeText(getContext(),"Description cannot be empty",Toast.LENGTH_SHORT).show();
                    return;

                }
                ParseUser currentUser=ParseUser.getCurrentUser();
                savePost(description,currentUser,photoFile);

            }
        });



    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
//        See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.example.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                // Load the image located at photoUri into selectedImage
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                // Load the selected image into a preview
                btnSubmit.setVisibility(View.VISIBLE);
                btnCaptureImage.setVisibility(View.INVISIBLE);
                etDescription.setVisibility(View.VISIBLE);
                ivPostImage.setVisibility(View.VISIBLE);
                ivPostImage.setImageBitmap(takenImage);
            } else {
                Toast.makeText(getContext(), "Picture wasn't taken", Toast.LENGTH_SHORT).show();
            }

        }
    }
    private void savePost(String description, ParseUser currentUser, File photoFile) {
        progressLoading.setVisibility(ProgressBar.VISIBLE);
        Post post = new Post();
        post.setDescription(description);
        post.setImage(new ParseFile(photoFile));
        post.setUser(currentUser);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG,"Error while saving !", e);
                    Toast.makeText(getContext(),"Error while saving", Toast.LENGTH_SHORT).show();
                    progressLoading.setVisibility(ProgressBar.INVISIBLE);
                    return;
                }
                if (photoFile == null || ivPostImage.getDrawable() == null) {
                    Toast.makeText(getContext(), "No image", Toast.LENGTH_SHORT).show();
                    progressLoading.setVisibility(ProgressBar.INVISIBLE);
                    return;
                }
                Log.i(TAG,"Post save was successful!");
                etDescription.setText("");
                ivPostImage.setImageResource(0);
                progressLoading.setVisibility(ProgressBar.INVISIBLE);
                btnSubmit.setVisibility(View.INVISIBLE);
                btnCaptureImage.setVisibility(View.VISIBLE);
                etDescription.setVisibility(View.INVISIBLE);
                ivPostImage.setVisibility(View.INVISIBLE);
            }
        });
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }
    }
