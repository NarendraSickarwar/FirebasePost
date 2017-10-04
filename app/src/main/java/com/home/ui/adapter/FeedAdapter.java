package com.home.ui.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.home.ui.activity.MainActivity;
import com.home.ui.tables.models.PostModel;
import com.home.ui.utils.CircleTransformation;
import com.home.ui.view.LoadingFeedItemView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.froger.instamaterial.R;

/**
 * Created by Narendra Singh on 26/9/17.
 */
public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String ACTION_LIKE_BUTTON_CLICKED = "action_like_button_button";
    public static final String ACTION_LIKE_IMAGE_CLICKED = "action_like_image_button";

    public static final int VIEW_TYPE_DEFAULT = 1;
    public static final int VIEW_TYPE_LOADER = 2;

    private final List<PostModel> postList;

    private Context context;
    private OnFeedItemClickListener onFeedItemClickListener;

    private boolean showLoadingView = false;


    public FeedAdapter(Context context, List<PostModel> postModelList) {
        this.context = context;
        this.postList = postModelList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DEFAULT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);
            CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view);
            setupClickableViews(view, cellFeedViewHolder);
            return cellFeedViewHolder;
        } else if (viewType == VIEW_TYPE_LOADER) {
            LoadingFeedItemView view = new LoadingFeedItemView(context);
            view.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            );
            return new LoadingCellFeedViewHolder(view);
        }
        return null;
    }

    private void setupClickableViews(final View view, final CellFeedViewHolder cellFeedViewHolder) {
        cellFeedViewHolder.btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onCommentsClick(view, cellFeedViewHolder.getAdapterPosition());
            }
        });
        cellFeedViewHolder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onMoreClick(v, cellFeedViewHolder.getAdapterPosition());
            }
        });
        cellFeedViewHolder.ivFeedCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                if (!postList.get(adapterPosition).isLiked()) {
                    postList.get(adapterPosition).setLiked(true);
                    postList.get(adapterPosition).setLike((postList.get(adapterPosition).getLike()) + 1);
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).showLikedSnackbar();
                    }
                } else {
                    postList.get(adapterPosition).setLiked(false);

                    postList.get(adapterPosition).setLike((postList.get(adapterPosition).getLike()) - 1);
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).showDisLikedSnackbar();
                    }

                }
                MainActivity.getInstance().callAdPost(postList.get(adapterPosition));

                notifyItemChanged(adapterPosition, ACTION_LIKE_BUTTON_CLICKED);
            }
        });
        cellFeedViewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = cellFeedViewHolder.getAdapterPosition();

                if (!postList.get(adapterPosition).isLiked()) {
                    postList.get(adapterPosition).setLiked(true);
                    postList.get(adapterPosition).setLike((postList.get(adapterPosition).getLike()) + 1);
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).showLikedSnackbar();
                    }
                } else {
                    postList.get(adapterPosition).setLiked(false);
                    postList.get(adapterPosition).setLike((postList.get(adapterPosition).getLike()) - 1);
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).showDisLikedSnackbar();
                    }

                }
                MainActivity.getInstance().callAdPost(postList.get(adapterPosition));
                notifyItemChanged(adapterPosition, ACTION_LIKE_BUTTON_CLICKED);

            }
        });
        cellFeedViewHolder.ivUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onProfileClick(view);
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((CellFeedViewHolder) viewHolder).bindView(postList.get(position));

        if (getItemViewType(position) == VIEW_TYPE_LOADER) {
            bindLoadingFeedItem((LoadingCellFeedViewHolder) viewHolder);
        }
    }

    private void bindLoadingFeedItem(final LoadingCellFeedViewHolder holder) {
        holder.loadingFeedItemView.setOnLoadingFinishedListener(new LoadingFeedItemView.OnLoadingFinishedListener() {
            @Override
            public void onLoadingFinished() {
                showLoadingView = false;
                notifyItemChanged(0);
            }
        });
        holder.loadingFeedItemView.startLoading();
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoadingView && position == 0) {
            return VIEW_TYPE_LOADER;
        } else {
            return VIEW_TYPE_DEFAULT;
        }
    }

    @Override
    public int getItemCount() {
        if (postList == null) {
            return 0;
        }
        return postList.size();
    }

    public void updateItems(boolean animated) {
        if (animated) {
            notifyItemRangeInserted(0, postList.size());
        } else {
            notifyDataSetChanged();
        }
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {
        this.onFeedItemClickListener = onFeedItemClickListener;
    }

    public void showLoadingView() {
        showLoadingView = true;
        notifyItemChanged(0);
    }

    public interface OnFeedItemClickListener {
        void onCommentsClick(View v, int position);

        void onMoreClick(View v, int position);

        void onProfileClick(View v);
    }

    public class CellFeedViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivFeedCenter)
        ImageView ivFeedCenter;
        @BindView(R.id.ivFeedBottom)
        TextView ivFeedBottom;
        @BindView(R.id.btnComments)
        ImageButton btnComments;
        @BindView(R.id.btnLike)
        ImageButton btnLike;
        @BindView(R.id.btnMore)
        ImageButton btnMore;
        @BindView(R.id.vBgLike)
        View vBgLike;
        @BindView(R.id.ivLike)
        ImageView ivLike;
        @BindView(R.id.tsLikesCounter)
        TextSwitcher tsLikesCounter;
        @BindView(R.id.ivUserProfile)
        ImageView ivUserProfile;
        @BindView(R.id.vImageRoot)
        FrameLayout vImageRoot;
        @BindView(R.id.txt_username)
        TextView txt_username;

        PostModel postModel;

        public CellFeedViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bindView(PostModel postModel) {
            this.postModel = postModel;
            if (postModel != null) {
                int adapterPosition = getAdapterPosition();
                CircleTransformation circleTransformation = new CircleTransformation();
                Picasso.with(context).load(postModel.getUser_img()).transform(circleTransformation).into(ivUserProfile);
                txt_username.setText(postModel.getUser_name());
                Picasso.with(context).load(postModel.getPost_image()).into(ivFeedCenter);
                ivFeedBottom.setText(postModel.getPost_text());
                btnLike.setImageResource(postModel.isLiked() ? R.drawable.ic_heart_red : R.drawable.ic_heart_outline_grey);
                tsLikesCounter.setCurrentText(vImageRoot.getResources().getQuantityString(
                        R.plurals.likes_count, (int) postModel.getLike(), (int) postModel.getLike()
                ));
            }
        }

        public PostModel getFeedItem() {
            return postModel;
        }
    }

    public class LoadingCellFeedViewHolder extends CellFeedViewHolder {

        LoadingFeedItemView loadingFeedItemView;

        public LoadingCellFeedViewHolder(LoadingFeedItemView view) {
            super(view);
            this.loadingFeedItemView = view;
        }

        @Override
        public void bindView(PostModel postModel) {
            super.bindView(postModel);
        }
    }
}
