package com.home.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.AgroStarApplication;
import com.Utils;
import com.home.UiInterface;
import com.home.ui.adapter.CommentsAdapter;
import com.home.ui.database.AppPrefs;
import com.home.ui.tables.CommentTable;
import com.home.ui.tables.models.CommentsModel;
import com.home.ui.tables.models.PostModel;
import com.home.ui.view.SendCommentButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.github.froger.instamaterial.R;

/**
 * Created by Narendra Singh on 26/9/17.
 */
public class CommentsActivity extends BaseDrawerActivity implements SendCommentButton.OnSendClickListener, UiInterface {
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";

    @BindView(R.id.contentRoot)
    LinearLayout contentRoot;
    @BindView(R.id.rvComments)
    RecyclerView rvComments;
    @BindView(R.id.llAddComment)
    LinearLayout llAddComment;
    @BindView(R.id.etComment)
    EditText etComment;
    @BindView(R.id.btnSendComment)
    SendCommentButton btnSendComment;
    CommentTable commentTable;
    private CommentsAdapter commentsAdapter;
    private int drawingStartLocation;
    private long post_id;
    private List<CommentsModel> commentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            post_id = bundle.getLong("post_id");
        }
        AgroStarApplication.getInstance().addUiInterface(this);
        commentTable = new CommentTable(post_id);
        setupComments();
        setupSendCommentButton();

        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        if (savedInstanceState == null) {
            contentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    contentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }
    }

    private void setupComments() {
        commentsList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setHasFixedSize(true);

        commentsAdapter = new CommentsAdapter(this, commentsList);
        rvComments.setAdapter(commentsAdapter);
        rvComments.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvComments.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    commentsAdapter.setAnimationsLocked(true);
                }
            }
        });
    }

    private void setupSendCommentButton() {
        btnSendComment.setOnSendClickListener(this);
    }

    private void startIntroAnimation() {
        ViewCompat.setElevation(getToolbar(), 0);
        contentRoot.setScaleY(0.1f);
        contentRoot.setPivotY(drawingStartLocation);
        llAddComment.setTranslationY(200);

        contentRoot.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewCompat.setElevation(getToolbar(), Utils.dpToPx(8));
                        animateContent();
                    }
                })
                .start();
    }

    private void animateContent() {
        // commentsAdapter.updateItems();
        llAddComment.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
    }

    @Override
    public void onBackPressed() {
        ViewCompat.setElevation(getToolbar(), 0);
        contentRoot.animate()
                .translationY(Utils.getScreenHeight(this))
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        CommentsActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }

    @Override
    public void onSendClickListener(View v) {
        if (validateComment()) {
            CommentsModel commentsModel = new CommentsModel();
            commentsModel.setComment(etComment.getText().toString());
            commentsModel.setPost_id(post_id);
            commentsModel.setTime_stamp(System.currentTimeMillis());
            commentsModel.setUser_img(AppPrefs.getUserModel(this).getImg_url());
            commentsModel.setUser_name(AppPrefs.getUserModel(this).getName());
            commentsList.add(commentsModel);
            commentsAdapter.notifyDataSetChanged();
            commentTable.insertRow(commentsModel);
            commentsAdapter.setAnimationsLocked(false);
            commentsAdapter.setDelayEnterAnimation(false);
            //   rvComments.smoothScrollBy(0, rvComments.getChildAt(0).getHeight() * commentsAdapter.getItemCount());
            etComment.setText(null);
            btnSendComment.setCurrentState(SendCommentButton.STATE_DONE);
        }
    }

    private boolean validateComment() {
        if (TextUtils.isEmpty(etComment.getText())) {
            btnSendComment.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }

        return true;
    }

    @Override
    public void onPostAdded(List<PostModel> postsList) {

    }

    @Override
    public void onNewUserAdded() {

    }

    @Override
    public void onCommentAdded(List<CommentsModel> commentsModelList) {
        this.commentsList.clear();
        this.commentsList.addAll(commentsModelList);
        commentsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPostLiked() {

    }

    @Override
    public void onPostDisLiked() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AgroStarApplication.getInstance().removeUiListeners(this);
    }
}
