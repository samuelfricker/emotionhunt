package ch.fhnw.ip5.emotionhunt.helpers;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import agency.tango.android.avatarview.views.AvatarView;
import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.models.User;

/**
 * This adapter class is required by the user list in the create experience view.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private final static String TAG = "UserAdapter";
    private List<User> mDataset;
    private List<User> mSelectedDataset;
    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout mLinearLayout;
        public TextView mTextView;
        public AvatarView mAvatarView;
        public CardView mCardView;
        public ViewHolder(LinearLayout v) {
            super(v);
            mLinearLayout = v;
            mTextView = (TextView) v.findViewById(R.id.txt_username);
            mCardView = (CardView) v.findViewById(R.id.card_view);
            mAvatarView = (AvatarView) v.findViewById(R.id.avatar_view);
        }
    }

    public void updateDataset(List<User> mDataset) {
        this.mDataset= mDataset;
        this.mSelectedDataset = new ArrayList<User>();
    }

    public List<User> getSelectedUsers() {
        return mSelectedDataset;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public UserAdapter(List<User> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final User user = mDataset.get(position);
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(user.name);
        Picasso.with(mContext).load(user.getAvatarURL(mContext)).into(holder.mAvatarView, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Successfully loaded image for user " + user.getAvatarURL(mContext));
            }
            @Override
            public void onError() {
                Log.v(TAG,"Could not fetch image from user " + user.getAvatarURL(mContext));
            }
        });
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mSelectedDataset.contains(user)) {
                    mSelectedDataset.add(user);
                    holder.mCardView.setBackgroundColor(Color.argb(20,0,0,0));
                } else {
                    holder.mCardView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                    try {
                        mSelectedDataset.remove(user);
                    } catch (Exception e) {
                        Log.e(TAG, "Could not remove user from recipients ... \n" + e.toString());
                    }
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
