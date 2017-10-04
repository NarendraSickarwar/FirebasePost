package com.home.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.home.ui.database.AppPrefs;
import com.home.ui.tables.models.UserModel;
import com.home.ui.utils.CircleTransformation;
import com.squareup.picasso.Picasso;

import io.github.froger.instamaterial.R;

/**
 * Created by Narendra Singh on 2/10/17.
 */
public class ProfileActivity extends BaseActivity implements View.OnClickListener {
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();
    private RelativeLayout mRlImagelay;
    private ImageView mIvProfilepic;
    private ProgressBar mPbIamge;
    private TextView mTvProfilepicStatus;
    private TextView mEtEmail;
    private TextView mEtFirstname;
    private TextView mEtMobilenumber;
    private TextView mEtLogout;

    public static void startUserProfileFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, ProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        findViews();
    }

    private void findViews() {
        mRlImagelay = (RelativeLayout) findViewById(R.id.rl_imagelay);
        mIvProfilepic = (ImageView) findViewById(R.id.iv_profilepic);
        mPbIamge = (ProgressBar) findViewById(R.id.pb_iamge);
        mTvProfilepicStatus = (TextView) findViewById(R.id.tv_profilepic_status);
        mEtEmail = (TextView) findViewById(R.id.et_email);
        mEtFirstname = (TextView) findViewById(R.id.et_firstname);
        mEtMobilenumber = (TextView) findViewById(R.id.et_mobilenumber);
        mEtLogout = (TextView) findViewById(R.id.et_logout);

        mEtLogout.setOnClickListener(this);
        setData();
    }

    private void setData() {
        UserModel userModel = AppPrefs.getUserModel(this);
        if (userModel != null) {
            mEtEmail.setText(userModel.getEmail());
            mEtFirstname.setText(userModel.getName());
            mEtMobilenumber.setText(userModel.getMobile_no());
            CircleTransformation circleTransformation = new CircleTransformation();
            Picasso.with(this).load(userModel.getImg_url()).transform(circleTransformation).into(mIvProfilepic);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_logout:
                // to do logout
                performLogout();
                break;
        }
    }

    private void performLogout() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
