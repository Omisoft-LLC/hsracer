package com.omisoft.hsracer.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.adapters.callbacks.VideoViewInterface;
import com.omisoft.hsracer.adapters.classes.UploadItemState;
import java.util.ArrayList;

/**
 * Created by developer on 07.11.17.
 */

public class VideoUploadAdapter extends RecyclerView.Adapter<VideoUploadAdapter.VideoHolder> {

  private VideoViewInterface videoViewInterface;
  private ArrayList<UploadItemState> videos;
  private int color;

  public VideoUploadAdapter(VideoViewInterface videoViewInterface,
      ArrayList<UploadItemState> videos) {
    this.videoViewInterface = videoViewInterface;
    this.videos = videos;
  }

  @Override
  public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    color = parent.getContext().getResources().getColor(R.color.colorHomeButtonShadowPressed);
    View view = inflater.inflate(R.layout.fragment_video_names, parent, false);
    return new VideoHolder(view);
  }

  @Override
  public void onBindViewHolder(VideoHolder holder, int position) {
    holder.mVideoName.setText(videos.get(position).getVideoName());
    holder.mCheckedVideo.setChecked(videos.get(position).isChecked());

    if (videos.get(position).isPlaying()) {
      holder.mClickedVideo.setBackgroundColor(color);
    } else {
      holder.mClickedVideo.setBackgroundColor(0);
    }
    if (videos.get(position).isUploading()) {
      holder.mCheckedVideo.setEnabled(false);
      holder.mImageUploaded.setImageResource(R.drawable.ic_check_circle);
    }

  }

  @Override
  public int getItemCount() {
    return videos.size();
  }

  class VideoHolder extends ViewHolder {

    private CheckBox mCheckedVideo;
    private TextView mVideoName;
    private RelativeLayout mClickedVideo;
    private ImageView mImageUploaded;

    private VideoHolder(View itemView) {
      super(itemView);

      mCheckedVideo = itemView.findViewById(R.id.checked_video);
      mVideoName = itemView.findViewById(R.id.video_name);
      mClickedVideo = itemView.findViewById(R.id.clicked_video_layout);
      mImageUploaded = itemView.findViewById(R.id.upload_check);
      mClickedVideo.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          videoViewInterface.onItemClicked(v, getAdapterPosition());
        }
      });
      mCheckedVideo.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
          videoViewInterface.selectedItem(getAdapterPosition(), isChecked);
        }
      });
    }
  }

  public ArrayList<UploadItemState> getVideos() {
    return videos;
  }

  public void setVideos(ArrayList<UploadItemState> videos) {
    this.videos = videos;
  }
}
