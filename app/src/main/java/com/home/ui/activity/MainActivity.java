package com.home.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.AgroStarApplication;
import com.Utils;
import com.home.UiInterface;
import com.home.ui.adapter.FeedAdapter;
import com.home.ui.adapter.FeedItemAnimator;
import com.home.ui.services.PostTableService;
import com.home.ui.tables.models.CommentsModel;
import com.home.ui.tables.models.PostModel;
import com.home.ui.view.FeedContextMenu;
import com.home.ui.view.FeedContextMenuManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.froger.instamaterial.R;

/**
 * Created by Narendra Singh on 26/9/17.
 */
public class MainActivity extends BaseDrawerActivity implements FeedAdapter.OnFeedItemClickListener,
        FeedContextMenu.OnFeedContextMenuItemClickListener, UiInterface {
    public static final String ACTION_SHOW_LOADING_ITEM = "action_show_loading_item";
    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;
    public static MainActivity instance;
    boolean mBounded;
    PostTableService postTableService;
    @BindView(R.id.rvFeed)
    RecyclerView rvFeed;
    @BindView(R.id.btnCreate)
    FloatingActionButton fabCreate;
    @BindView(R.id.content)
    CoordinatorLayout clContent;
    Bundle savedInstanceState;
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            printLog(PostTableService.TAG, "Service is connected");
            mBounded = true;
            PostTableService.LocalBinder mLocalBinder = (PostTableService.LocalBinder) service;
            postTableService = mLocalBinder.getInstance();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBounded = false;
            postTableService = null;
            printLog(PostTableService.TAG, "service disconnected");
        }
    };
    private FeedAdapter feedAdapter;
    private boolean pendingIntroAnimation;
    private List<PostModel> postList;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        this.savedInstanceState = savedInstanceState;


    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = PostTableService.createIntent(this);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        startService(intent);
        AgroStarApplication.getInstance().addUiInterface(this);
        setupFeed();
        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        } else {
            feedAdapter.updateItems(false);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public void callAdPost(Object o) {
        PostModel postModel = (PostModel) o;
        if (postModel != null && mBounded) {
            postTableService.insertRow(postModel);
        }
    }

    private void setupFeed() {
        postList = new ArrayList<>();


        postList.clear();
        WrapContentLinearLayoutManager wrapContentLinearLayoutManager = new WrapContentLinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvFeed.setLayoutManager(wrapContentLinearLayoutManager);
        feedAdapter = new FeedAdapter(this, postList);
        feedAdapter.setOnFeedItemClickListener(this);
        rvFeed.setAdapter(feedAdapter);
        rvFeed.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
            }
        });
        rvFeed.setItemAnimator(new FeedItemAnimator());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (ACTION_SHOW_LOADING_ITEM.equals(intent.getAction())) {
            showFeedLoadingItemDelayed();
        }
    }

    private void showFeedLoadingItemDelayed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rvFeed.smoothScrollToPosition(0);
                feedAdapter.showLoadingView();
            }
        }, 500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_inbox) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startIntroAnimation() {
        fabCreate.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));

        int actionbarSize = Utils.dpToPx(56);
        getToolbar().setTranslationY(-actionbarSize);
        getIvLogo().setTranslationY(-actionbarSize);
        getInboxMenuItem().getActionView().setTranslationY(-actionbarSize);

        getToolbar().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        getIvLogo().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);
        getInboxMenuItem().getActionView().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }
                })
                .start();
    }

    private void startContentAnimation() {
        fabCreate.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(ANIM_DURATION_FAB)
                .start();
        feedAdapter.updateItems(true);
    }

    @Override
    public void onCommentsClick(View v, int position) {
        final Intent intent = new Intent(this, CommentsActivity.class);
        intent.putExtra("post_id", postList.get(position).getTimestamp());
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onMoreClick(View v, int itemPosition) {
        FeedContextMenuManager.getInstance().toggleContextMenuFromView(v, itemPosition, this);
    }

    @Override
    public void onProfileClick(View v) {
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        startingLocation[0] += v.getWidth() / 2;
        // ProfileActivity.startUserProfileFromLocation(startingLocation, this);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onReportClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onSharePhotoClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCopyShareUrlClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCancelClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @OnClick(R.id.btnCreate)
    public void onTakePhotoClick() {
        int[] startingLocation = new int[2];
        fabCreate.getLocationOnScreen(startingLocation);
        startingLocation[0] += fabCreate.getWidth() / 2;
        TakePhotoActivity.startCameraFromLocation(startingLocation, this);
        overridePendingTransition(0, 0);
    }

    public void showLikedSnackbar() {
        Snackbar.make(clContent, "Liked!", Snackbar.LENGTH_SHORT).show();
    }

    public void showDisLikedSnackbar() {
        Snackbar.make(clContent, "Disliked!", Snackbar.LENGTH_SHORT).show();
    }


    @Override
    public void onPostAdded(List<PostModel> postsList) {
        this.postList.clear();
        postList.addAll(postsList);
        feedAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNewUserAdded() {

    }

    @Override
    public void onCommentAdded(List<CommentsModel> commentsModelList) {

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
        if (mBounded) {
            unbindService(serviceConnection);
            mBounded = false;
        }
    }

    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        //... constructor
        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("probe", "meet a IOOBE in RecyclerView");
            }
        }
    }
}