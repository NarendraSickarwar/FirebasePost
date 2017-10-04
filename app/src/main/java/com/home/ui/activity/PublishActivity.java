package com.home.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.home.ui.database.AppPrefs;
import com.home.ui.tables.models.PostModel;
import com.home.ui.tables.models.UserModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import io.github.froger.instamaterial.R;

/**
 * Created by Narendra Singh on 26/9/17.
 */
public class PublishActivity extends BaseActivity {
    public static final String ARG_TAKEN_PHOTO_URI = "arg_taken_photo_uri";

    @BindView(R.id.tbFollowers)
    ToggleButton tbFollowers;
    @BindView(R.id.tbDirect)
    ToggleButton tbDirect;
    @BindView(R.id.ivPhoto)
    ImageView ivPhoto;
    @BindView(R.id.etDescription)
    EditText etDescription;
    FirebaseStorage storage;
    StorageReference storageRef;
    private boolean propagatingToggleState = false;
    private Uri photoUri;
    private int photoSize;
    private ProgressDialog progressDialog;

    public static void openWithPhotoUri(Activity openingActivity, Uri photoUri) {
        Intent intent = new Intent(openingActivity, PublishActivity.class);
        intent.putExtra(ARG_TAKEN_PHOTO_URI, photoUri);
        openingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_grey600_24dp);
        photoSize = getResources().getDimensionPixelSize(R.dimen.publish_photo_thumbnail_size);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("imgaes/" + System.currentTimeMillis() + ".jpg");
        if (savedInstanceState == null) {
            photoUri = getIntent().getParcelableExtra(ARG_TAKEN_PHOTO_URI);
        } else {
            photoUri = savedInstanceState.getParcelable(ARG_TAKEN_PHOTO_URI);
        }
        updateStatusBarColor();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Wait..");
        loadThumbnailPhoto();

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Utils.isAndroid5()) {
            getWindow().setStatusBarColor(0xff888888);
        }
    }

    private void loadThumbnailPhoto() {
        ivPhoto.setScaleX(0);
        ivPhoto.setScaleY(0);
        Picasso.with(this)
                .load(photoUri)
                .centerCrop()
                .resize(photoSize, photoSize)
                .into(ivPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        ivPhoto.animate()
                                .scaleX(1.f).scaleY(1.f)
                                .setInterpolator(new OvershootInterpolator())
                                .setDuration(400)
                                .setStartDelay(200)
                                .start();
                    }

                    @Override
                    public void onError() {

                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_publish) {
            // to do uploading i,age to firebase data base and give new post entry in database
            UploadFiles();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void bringMainActivityToTop() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(MainActivity.ACTION_SHOW_LOADING_ITEM);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_TAKEN_PHOTO_URI, photoUri);
    }

    @OnCheckedChanged(R.id.tbFollowers)
    public void onFollowersCheckedChange(boolean checked) {
        if (!propagatingToggleState) {
            propagatingToggleState = true;
            tbDirect.setChecked(!checked);
            propagatingToggleState = false;
        }
    }

    @OnCheckedChanged(R.id.tbDirect)
    public void onDirectCheckedChange(boolean checked) {
        if (!propagatingToggleState) {
            propagatingToggleState = true;
            tbFollowers.setChecked(!checked);
            propagatingToggleState = false;
        }
    }

    private void UploadFiles() {
        // Get the data from an ImageView as bytes
        progressDialog.show();
        ivPhoto.setDrawingCacheEnabled(true);
        ivPhoto.buildDrawingCache();
        Bitmap bitmap = ivPhoto.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                progressDialog.dismiss();
                exception.printStackTrace();
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                PostModel postModel =
                        setPostData(downloadUrl);
                MainActivity.getInstance().callAdPost(postModel);

                bringMainActivityToTop();
            }
        });

    }

    private PostModel setPostData(Uri downloadUrl) {
        UserModel userModel = AppPrefs.getUserModel(PublishActivity.this);
        PostModel postModel = new PostModel();
        postModel.setComments(0);
        postModel.setLike(0);
        postModel.setPost_image(downloadUrl.toString());
        postModel.setUser_img(userModel.getImg_url());
        postModel.setUser_name(userModel.getName());
        postModel.setTimestamp(System.currentTimeMillis());
        postModel.setPost_type(PostModel.POST_TYPE_IMG);
        postModel.setPost_text(etDescription.getText().toString());
        return postModel;
    }
}
