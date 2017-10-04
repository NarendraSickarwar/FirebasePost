package com.home.ui.tables;

import com.AgroStarApplication;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.home.ui.tables.models.CommentsModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Narendra Singh on 2/10/17.
 */

public class CommentTable extends BaseTable implements ChildEventListener, ValueEventListener {
    public static final String TAG = "COMMENTTABLE";
    public static final String TABLENAME = "comment_table";
    public static CommentTable instance;
    public int isFirstTime = 0;
    List<CommentsModel> commentslist;
    private long post_id;

    public CommentTable(long post_id) {
        instance = this;
        this.post_id = post_id;
        commentslist = new ArrayList<>();
        AgroStarApplication.getDatabaseInstance().getReference().child(TABLENAME).addChildEventListener(this);
        AgroStarApplication.getDatabaseInstance().getReference().child(TABLENAME).orderByChild("post_id").equalTo(post_id).addListenerForSingleValueEvent(this);
    }

    public static CommentTable getInstance() {
        return instance;
    }

    @Override
    public void insertRow(Object o) {
        super.insertRow(o);
        CommentsModel commentsModel = (CommentsModel) o;
        if (commentsModel != null) {
            AgroStarApplication.getDatabaseInstance()
                    .getReference().child(TABLENAME).
                    child(commentsModel.getTime_stamp() + "").
                    setValue(commentsModel);
        }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        isFirstTime++;
        if (isFirstTime <= 1) return;

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
        AgroStarApplication.getInstance().triggerCommentAdded(commentslist);

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
