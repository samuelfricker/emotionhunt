package ch.fhnw.ip5.emotionhunt.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import agency.tango.android.avatarview.views.AvatarView;
import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.activities.ExperienceDetailActivity;
import ch.fhnw.ip5.emotionhunt.models.Emotion;
import ch.fhnw.ip5.emotionhunt.models.Experience;
import ch.fhnw.ip5.emotionhunt.models.ReceivedExperience;
import ch.fhnw.ip5.emotionhunt.models.SentExperience;
import ch.fhnw.ip5.emotionhunt.models.User;

/**
 * Created by dimitri on 09.12.2016.
 */

public class ExperienceListAdapter extends RecyclerView.Adapter<ExperienceListAdapter.MyViewHolder> {
    private ArrayList experienceList;
    private static final String TAG = ExperienceListAdapter.class.getSimpleName();
    private Experience experience;
    private Context mContext;
    private boolean isSent = false;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = MyViewHolder.class.getSimpleName();
        public Experience currentExperience;
        public CardView mCardView;
        public TextView mTitle;
        public TextView mText;
        public TextView mNew;
        public TextView mDate;
        public ImageView mImgAvatar;
        public ImageView mImgPublicPrivate;
        public ImageView mImgGps;
        public AvatarView mAvatarView;
        private Context mContext;

        public MyViewHolder(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Log.d("MyViewHolder", "clicked to View");
                    Log.d(TAG, "ExperienceID: "+currentExperience.id);
                    Intent intent = new Intent(mContext, ExperienceDetailActivity.class);
                    intent.putExtra(ExperienceDetailActivity.EXTRA_EXPERIENCE_ID, currentExperience.id);
                    mContext.startActivity(intent);
                }
            });

            mCardView = (CardView) v.findViewById(R.id.card_view);
            mTitle = (TextView) v.findViewById(R.id.tv_title);
            mText = (TextView) v.findViewById(R.id.tv_text);
            mNew = (TextView) v.findViewById(R.id.tv_new);
            mDate = (TextView) v.findViewById(R.id.tv_date);
            mImgAvatar = (ImageView) v.findViewById(R.id.iv_image);
            mImgPublicPrivate = (ImageView) v.findViewById(R.id.iv_public_private);
            mImgGps = (ImageView) v.findViewById(R.id.iv_gps);
            mAvatarView = (AvatarView) v.findViewById(R.id.avatar_view);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ExperienceListAdapter(ArrayList<?> experienceList, Context mContext, boolean isSent) {
        this.experienceList = experienceList;
        this.mContext = mContext;
        this.isSent = isSent;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ExperienceListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_experience_list, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        experience = (Experience) experienceList.get(position);
        holder.currentExperience = (Experience) experienceList.get(position);
        holder.mContext = mContext;
        holder.mText.setText(experience.text);
        holder.mDate.setText(experience.getCreatedAt());
        holder.mTitle.setText(isSent ? mContext.getString(R.string.me) : experience.senderName);
        holder.mNew.setVisibility(!experience.isRead ? View.VISIBLE : View.GONE);

        if (isSent && experience.getExpectedEmotion() != null) {
            Log.d(TAG, "Set expected emotion as avatar img");
            holder.mImgAvatar.setImageResource(experience.getExpectedEmotion().getResourceId());
        }

        if (!isSent) {
            holder.mImgAvatar.setVisibility(View.GONE);
            holder.mAvatarView.setVisibility(View.VISIBLE);
            String avatarUrl = User.getAvatarURLByUserId(mContext, experience.senderId);
            Log.d(TAG, "Load avatar from " + avatarUrl);
            //load avatar
            Picasso.with(mContext).load(avatarUrl).into(holder.mAvatarView, new Callback() {
                @Override
                public void onSuccess() {
                            Log.d(TAG, "Successfully loaded image");
                        }
                @Override
                public void onError() {
                            Log.v(TAG,"Could not fetch image");
                        }
            });
        }

        if (experience.isPublic) {
            holder.mImgGps.setImageResource(R.drawable.ic_public);
        } else {
            holder.mImgGps.setImageResource(R.drawable.ic_private);
        }
        if (experience.isLocationBased) {
            holder.mImgGps.setImageResource(R.drawable.ic_gps_location_enabled);
        } else {
            holder.mImgGps.setImageResource(R.drawable.ic_gps_location_disabled);
        }

        Log.d(TAG, "Image width " + holder.mImgAvatar.getWidth());
    }

    @Override
    public int getItemCount() {
        return experienceList.size();
    }
}

