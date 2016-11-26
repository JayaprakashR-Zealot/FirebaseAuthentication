package com.truedreamz.firebaseauthenticationapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import static android.R.string.cancel;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG="HomeActivity";
    private FirebaseUser existingUser;

    private EditText etxtDisplayName;
    private EditText etxtEmail;
    private EditText etxtNewPassword;
    private ImageView imageViewProfilePhoto;
    private Bitmap bitmapPhoto;

    private View mProgressView;
    private View mLoginFormView;

    private int PICK_IMAGE_REQUEST = 1;

    private FirebaseAuth authHome;
    private FirebaseAuth.AuthStateListener authListener;
    private boolean isSignOut=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        authHome=FirebaseAuth.getInstance();

        // this listener will be called when there is change in firebase user session
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    if(isSignOut){
                        // user auth state is changed - user is null
                        Toast.makeText(HomeActivity.this, "Sign out - Success", Toast.LENGTH_SHORT).show();
                        // launch login activity
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            }
        };

        etxtDisplayName=(EditText) findViewById(R.id.display_name);
        etxtEmail=(EditText) findViewById(R.id.home_email);
        etxtNewPassword=(EditText) findViewById(R.id.new_password);
        etxtNewPassword.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {

                if(s.toString().length()<=5){
                    etxtNewPassword.setError(getString(R.string.error_invalid_password));
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        imageViewProfilePhoto=(ImageView)findViewById(R.id.imagePhotoUrl);

        mLoginFormView = findViewById(R.id.home_form);
        mProgressView = findViewById(R.id.home_progress);

        imageViewProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });


        existingUser = FirebaseAuth.getInstance().getCurrentUser();
        if (existingUser!= null) {
            // Name, email address, and profile photo Url
            String name = existingUser.getDisplayName();
            String email = existingUser.getEmail();
            Uri photoUrl = existingUser.getPhotoUrl();

            if(name!=null)etxtDisplayName.setText(name);
            if(email!=null)etxtEmail.setText(email);
            if(photoUrl!=null){
                imageViewProfilePhoto.setBackgroundResource(0);
                imageViewProfilePhoto.setImageURI(photoUrl);
            }

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = existingUser.getUid();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            try {
                bitmapPhoto = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageViewProfilePhoto.setImageBitmap(bitmapPhoto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Uri bitmapToUriConverter(Bitmap mBitmap) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Calculate inSampleSize
            options.inSampleSize = 8000;

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, 200, 200,
                    true);
            File file = new File(this.getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");
            FileOutputStream out = this.openFileOutput(file.getName(),
                    Context.MODE_WORLD_READABLE);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //get absolute path
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        return uri;
    }

    public void onUpdateUserInfoListener(View v){

        showProgress(true);

        Uri photoUri=bitmapToUriConverter(bitmapPhoto);

        if(photoUri!=null){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(etxtDisplayName.getText().toString())
                    .setPhotoUri(photoUri)
                    .build();

            existingUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showProgress(false);
                                Log.d(TAG, "User profile updated.");
                                Toast.makeText(HomeActivity.this,"User info is updated.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else showProgress(false);
    }

    public void onUpdateCredentialsListener(View v){
        // Reset errors.
        etxtEmail.setError(null);
        etxtNewPassword.setError(null);

        boolean cancel = false;
        View focusView = null;

        String email=etxtEmail.getText().toString().trim();
        String password = etxtNewPassword.getText().toString();

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            etxtEmail.setError(getString(R.string.error_field_required));
            focusView = etxtEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            etxtEmail.setError(getString(R.string.error_invalid_email));
            focusView = etxtEmail;
            cancel = true;
        }

        /*// Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            etxtNewPassword.setError(getString(R.string.error_invalid_password));
            focusView = etxtNewPassword;
            cancel = true;
        }*/

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            existingUser.updateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            showProgress(false);
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User email address updated.");
                                Toast.makeText(HomeActivity.this,"User email address is updated.",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(HomeActivity.this,"Fail to update.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            if(password.length()>5){
                existingUser.updatePassword(password)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            showProgress(false);
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User password updated.");
                                Toast.makeText(HomeActivity.this,"User password is updated.",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(HomeActivity.this,"Fail to update.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }
        }

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }


    public void onSignoutListener(View v){
        isSignOut=true;
        authHome.signOut();
    }

    public void onDeleteUserListener(View v){

        if (existingUser != null) {
            existingUser.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(HomeActivity.this, "Your profile is deleted, Create a account now!", Toast.LENGTH_SHORT).show();
                            // launch login activity
                            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(HomeActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        //progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        authHome.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            authHome.removeAuthStateListener(authListener);
        }
    }

}
