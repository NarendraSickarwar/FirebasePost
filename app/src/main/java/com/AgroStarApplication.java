package com;

import android.app.Application;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.home.UiInterface;
import com.home.ui.services.CommentTableService;
import com.home.ui.tables.models.CommentsModel;
import com.home.ui.tables.models.PostModel;

import java.util.ArrayList;
import java.util.List;

import io.github.froger.instamaterial.R;
import timber.log.Timber;

/**
 * Created by Narendra Singh on 26/9/17.
 */
public class AgroStarApplication extends Application {
    public static final List<UiInterface> uiListeners = new ArrayList<>();
    public static AgroStarApplication instance;
    static FirebaseDatabase database;

    public static AgroStarApplication getInstance() {
        return instance;
    }

    public static FirebaseDatabase getDatabaseInstance() {
        return database;
    }

    public static void printLog(String tag, String msg) {
        Log.e(tag, msg);
    }

    public void addUiInterface(UiInterface uiInterface) {
        uiListeners.add(uiInterface);
    }

    public void removeUiListeners(UiInterface uiInterface) {
        uiListeners.remove(uiInterface);
    }

    public void triggerPostAdded(List<PostModel> postList) {
        if (uiListeners != null && uiListeners.size() > 0) {
            for (UiInterface uiInterface : uiListeners
                    ) {
                uiInterface.onPostAdded(postList);

            }
        }
    }

    public void triggerCommentAdded(List<CommentsModel> commentList) {
        if (uiListeners != null && uiListeners.size() > 0) {
            for (UiInterface uiInterface : uiListeners
                    ) {
                uiInterface.onCommentAdded(commentList);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        FirebaseApp.initializeApp(this);
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));
        FacebookSdk.sdkInitialize(this);
        instance = this;
        database = FirebaseDatabase.getInstance();

        startService(CommentTableService.createIntent(this));

    }
}
