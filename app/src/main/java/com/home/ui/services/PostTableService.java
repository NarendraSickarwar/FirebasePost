package com.home.ui.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.AgroStarApplication;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.home.ui.activity.MainActivity;
import com.home.ui.tables.models.PostModel;
import com.home.ui.utils.NotifactionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.AgroStarApplication.printLog;

/**
 * Created by Narendra Singh on 3/10/17.
 */
public class PostTableService extends Service implements ChildEventListener, ValueEventListener, ServiceInterface {
    public static final String TAG = "POSTTABLESERVICE";
    public static final String TABLENAME = "post_table";
    public int isfirstime = 0;
    List<PostModel> postsList;
    List<PostModel> postListForward;
    IBinder iBinder = new LocalBinder();

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, PostTableService.class);
        return intent;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        postsList = new ArrayList<>();
        postListForward = new ArrayList<>();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AgroStarApplication.getDatabaseInstance().getReference().child(TABLENAME).orderByKey().addListenerForSingleValueEvent(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        isfirstime++;
        if (isfirstime <= 1) return;
        printLog(TAG, dataSnapshot.getChildrenCount() + "");
        PostModel postModel = dataSnapshot.getValue(PostModel.class);
        boolean isnewPost = true;
        for (PostModel postModel1 : postsList
                ) {
            if (postModel1.getTimestamp() == postModel.getTimestamp()) {
                isnewPost = false;
                break;
            }
        }
        if (isnewPost) {
            postsList.add(0, postModel);
            postListForward.add(0, postModel);
            if (isAppIsInBackground(this)) {
                Intent resultIntent;
                resultIntent = new Intent(this, MainActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                showNotificationMessage(this, "New Post Added By", postModel.getUser_name(), String.valueOf(postModel.getTimestamp()), resultIntent);
            } else {
                AgroStarApplication.getInstance().triggerPostAdded(postListForward);
            }
        }
    }

    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        NotifactionUtil notificationUtils = new NotifactionUtil(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        printLog(TAG, "onChild changed");
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        postsList.clear();
        postListForward.clear();
        printLog(TAG, dataSnapshot.getChildrenCount() + "on single time data change");
        Iterable iterable = dataSnapshot.getChildren();
        Iterator iterator = iterable.iterator();
        while (iterator.hasNext()) {
            DataSnapshot dataSnapshot1 = (DataSnapshot) iterator.next();
            HashMap hashMap = (HashMap) dataSnapshot1.getValue();
            String str = (String) hashMap.get(dataSnapshot1.getKey());
            PostModel postModel = new PostModel(hashMap);

            postsList.add(postModel);
        }
        postListForward.addAll(postsList);
        Collections.reverse(postsList);
        Collections.reverse(postListForward);
        AgroStarApplication.getInstance().triggerPostAdded(postListForward);
        AgroStarApplication.getDatabaseInstance().getReference().child(TABLENAME).addChildEventListener(this);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void insertRow(Object o) {
        PostModel postModel = (PostModel) o;
        if (postModel != null) {
            AgroStarApplication.getDatabaseInstance()
                    .getReference().child(TABLENAME).
                    child("" + postModel.getTimestamp())
                    .setValue(postModel);
        }
    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }

    public class LocalBinder extends Binder {
        public PostTableService getInstance() {
            return PostTableService.this;
        }
    }

}
