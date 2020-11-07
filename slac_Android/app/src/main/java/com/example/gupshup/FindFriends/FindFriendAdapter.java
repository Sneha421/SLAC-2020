package com.example.gupshup.FindFriends;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gupshup.Common.Constants;
import com.example.gupshup.Common.NodesList;
import com.example.gupshup.Common.Util;
import com.example.gupshup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class FindFriendAdapter extends RecyclerView.Adapter<FindFriendAdapter.FindFriendsViewHolder>
{

    private Context context;
    private List<FindFriendModel> findFriendModelList;

    private DatabaseReference databaseReferenceFriends;
    private FirebaseUser currentUser;

    private String userId;


    public FindFriendAdapter(Context context, List<FindFriendModel> findFriendModelList)
    {
        this.context = context;
        this.findFriendModelList = findFriendModelList;
    }

    @NonNull
    @Override
    public FindFriendAdapter.FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.find_friends_layout,parent,false);

        return new FindFriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FindFriendAdapter.FindFriendsViewHolder holder, int position)
    {
        final FindFriendModel findFriendModel = findFriendModelList.get(position);

        holder.findFriendUserName.setText(findFriendModel.getUserName());

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        StorageReference spaceRef = storageRef.child("profilePics/"+findFriendModel.getUserName()+".jpg");

        spaceRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri uri)
            {
                Glide.with(context)
                        .load(uri)
                        .placeholder(R.drawable.user)
                        .error(R.drawable.user)
                        .into(holder.findFriendProfPic);
            }

        });

        databaseReferenceFriends = FirebaseDatabase.getInstance().getReference().child(NodesList.FRIEND_REQUESTS);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

//        System.out.println(currentUser.getDisplayName());


        if(findFriendModel.isRequestStatus())
        {
            holder.sendFriendRequestButton.setVisibility(View.GONE);
            holder.cancelFriendRequestButton.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.sendFriendRequestButton.setVisibility(View.VISIBLE);
            holder.cancelFriendRequestButton.setVisibility(View.GONE);
        }

        holder.sendFriendRequestButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                holder.sendFriendRequestButton.setVisibility(View.GONE);
                holder.friendRequestProgBar.setVisibility(View.VISIBLE);

                userId = findFriendModel.getUserID();

                databaseReferenceFriends.child(currentUser.getUid()).child(userId).child(NodesList.REQUEST_TYPE)
                .setValue(Constants.REQUEST_STATUS_SENT).addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            databaseReferenceFriends.child(userId).child(currentUser.getUid()).child(NodesList.REQUEST_TYPE)
                                    .setValue(Constants.REQUEST_STATUS_RECEIVED).addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(context, "Request sent successfully",Toast.LENGTH_SHORT).show();

                                        String title = "New Friend Request";
                                        String message = "Friend request from: "+currentUser.getDisplayName();

                                        Util.sendNotification(context, title, message, userId);

                                        holder.sendFriendRequestButton.setVisibility(View.GONE);
                                        holder.friendRequestProgBar.setVisibility(View.GONE);
                                        holder.cancelFriendRequestButton.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        Toast.makeText(context,"Request failed: "+
                                                task.getException(),Toast.LENGTH_SHORT).show();
                                        holder.sendFriendRequestButton.setVisibility(View.VISIBLE);
                                        holder.friendRequestProgBar.setVisibility(View.GONE);
                                        holder.cancelFriendRequestButton.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });


            }
        });





        holder.cancelFriendRequestButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                holder.cancelFriendRequestButton.setVisibility(View.GONE);
                holder.friendRequestProgBar.setVisibility(View.VISIBLE);

                userId = findFriendModel.getUserID();

                databaseReferenceFriends.child(currentUser.getUid()).child(userId).child(NodesList.REQUEST_TYPE)
                        .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            databaseReferenceFriends.child(userId).child(currentUser.getUid()).child(NodesList.REQUEST_TYPE)
                                    .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(context, "Request cancelled successfully",Toast.LENGTH_SHORT).show();
                                        holder.sendFriendRequestButton.setVisibility(View.VISIBLE);
                                        holder.friendRequestProgBar.setVisibility(View.GONE);
                                        holder.cancelFriendRequestButton.setVisibility(View.GONE);
                                    }
                                    else
                                    {
                                        Toast.makeText(context,"Request cancel failed: "+
                                                task.getException(),Toast.LENGTH_SHORT).show();
                                        holder.sendFriendRequestButton.setVisibility(View.GONE);
                                        holder.friendRequestProgBar.setVisibility(View.GONE);
                                        holder.cancelFriendRequestButton.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    }
                });


            }
        });






    }

    @Override
    public int getItemCount()
    {
        return findFriendModelList.size();
    }

    public class FindFriendsViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView findFriendProfPic;
        private TextView findFriendUserName;
        private Button sendFriendRequestButton, cancelFriendRequestButton;
        private ProgressBar friendRequestProgBar;


        public FindFriendsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            findFriendProfPic = itemView.findViewById(R.id.findFriendProfile);
            findFriendUserName = itemView.findViewById(R.id.findFriendUserName);
            sendFriendRequestButton = itemView.findViewById(R.id.sendFriendRequestButton);
            cancelFriendRequestButton = itemView.findViewById(R.id.cancelFriendRequestButton);
            friendRequestProgBar = itemView.findViewById(R.id.friendRequestProgBar);
        }
    }
}
