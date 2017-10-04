package com.home.ui.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.AgroStarApplication;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.home.ui.activity.MainActivity;
import com.home.ui.tables.models.CommentsModel;
import com.home.ui.utils.NotifactionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.AgroStarApplication.printLog;

/**
 * Created by Narendra Singh on 3/10/17.
 */

public class CommentTableService extends Service implements ChildEventListener, ValueEventListener {
    public static final String TAG = "COMMENTTABLESERVICE";
    public static final String TABLENAME = "comment_table";
    public static long count = 0;
    List<CommentsModel> commentslist;

    public static Intent createIntent(Context context) {
        return new Intent(context, CommentTableService.class);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        commentslist = new ArrayList<>();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AgroStarApplication.getDatabaseInstance().getReference().child(TABLENAME).addListenerForSingleValueEvent(this);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        printLog(TAG, dataSnapshot.getChildrenCount() + "");
        CommentsModel commentsModel = dataSnapshot.getValue(CommentsModel.class);
        boolean isnewPost = true;
        for (CommentsModel commentsModel1 : commentslist
                ) {
            if (commentsModel.getTime_stamp() == commentsModel1.getTime_stamp()) {
                isnewPost = false;
                break;
            }
        }
        if (isnewPost) {
            commentslist.add(0, commentsModel);

            if (NotifactionUtil.isAppIsInBackground(this)) {
                Intent resultIntent;
                resultIntent = new Intent(this, MainActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                showNotificationMessage(this, "New comment post by ", commentsModel.getUser_name() + " : " + commentsModel.getComment(), String.valueOf(commentsModel.getTime_stamp()), resultIntent);
            } else {
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

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        commentslist.clear();
        printLog(TAG, dataSnapshot.getChildrenCount() + "on single time data change");
        Iterable iterable = dataSnapshot.getChildren();
        Iterator iterator = iterable.iterator();
        while (iterator.hasNext()) {
            DataSnapshot dataSnapshot1 = (DataSnapshot) iterator.next();
            HashMap hashMap = (HashMap) dataSnapshot1.getValue();
            CommentsModel commentsModel = new CommentsModel(hashMap);
            commentslist.add(commentsModel);
        }
        AgroStarApplication.getDatabaseInstance().getReference().child(TABLENAME).addChildEventListener(this);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
