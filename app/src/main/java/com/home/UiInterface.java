package com.home;

import com.home.ui.tables.models.CommentsModel;
import com.home.ui.tables.models.PostModel;

import java.util.List;

/**
 * Created by Narendra Singh on 29/9/17.
 */

public interface UiInterface {
    void onPostAdded(List<PostModel> postsList);

    void onNewUserAdded();

    void onCommentAdded(List<CommentsModel> commentsModelList);

    void onPostLiked();

    void onPostDisLiked();
}
