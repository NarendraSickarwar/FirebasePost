package com.home.ui.tables;

import com.AgroStarApplication;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.home.ui.tables.models.PostModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Narendra Singh on 29/9/17.
 */
public class PostTable extends BaseTable implements ChildEventListener, ValueEventListener {
    public static final String TAG = "POSTTABLE";
    public static final String TABLENAME = "post_table";
    public static PostTable instance;
    public int isfirstime = 0;
    List<PostModel> postsList;

    public PostTable() {
        instance = this;
        postsList = new ArrayList<>();
        AgroStarApplication.getDatabaseInstance().getReference().child(TABLENAME).addChildEventListener(this);
        AgroStarApplication.getDatabaseInstance().getReference().child(TABLENAME).orderByKey().addListenerForSingleValueEvent(this);

    }

    public static PostTable getInstance() {
        return instance;
    }

    @Override
    public void insertRow(Object o) {
        super.insertRow(o);
        PostModel postModel = (PostModel) o;
        if (postModel != null) {
            AgroStarApplication.getDatabaseInstance()
                    .getReference().child(TABLENAME).
                    child("" + postModel.getTimestamp())
                    .setValue(postModel);
        }
    }


    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        isfirstime++;
        if (isfirstime <= 1) return;
        printLog(TAG, dataSnapshot.getChildrenCount() + "");
        PostModel postModel = dataSnapshot.getValue(PostModel.class);
        postsList.add(0, postModel);
        AgroStarApplication.getInstance().triggerPostAdded(postsList);

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        printLog(TAG, dataSnapshot.getChildrenCount() + "on child changed");

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        printLog(TAG, dataSnapshot.getChildrenCount() + "on child removed");

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        printLog(TAG, dataSnapshot.getChildrenCount() + "on child moved");

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        postsList.clear();
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
        AgroStarApplication.getInstance().triggerPostAdded(postsList);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        printLog(TAG, "on data canceled");

    }
}

