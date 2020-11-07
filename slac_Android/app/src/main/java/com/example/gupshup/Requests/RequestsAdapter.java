package com.example.gupshup.Requests;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestViewHolder>
{
    private Context context;
    private List<RequestsModel> requestsModelList;
    private DatabaseReference databaseReferenceFriendRequests, databaseReferenceChats;
    private FirebaseUser currentUser;

    public RequestsAdapter(Context context, List<RequestsModel> requestsModelList)
    {
        this.context = context;
        this.requestsModelList = requestsModelList;
    }

    @NonNull
    @Override
    public RequestsAdapter.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_request_layout,parent,false);

        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RequestsAdapter.RequestViewHolder holder, int position)
    {
        final RequestsModel requestsModel = requestsModelList.get(position);

        holder.friendRequestName.setText(requestsModel.getUserName());

        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child(Constants.PROFILE_PICTURE+"/"+requestsModel.getPhotoName());

        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                .load(uri)
                .error(R.drawable.user)
                .placeholder(R.drawable.user)
                .into(holder.requestProfPic);

            }
        });

        databaseReferenceFriendRequests = FirebaseDatabase.getInstance().getReference().child(NodesList.FRIEND_REQUESTS);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReferenceChats = FirebaseDatabase.getInstance().getReference().child(NodesList.CHATS);


        holder.acceptRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                holder.requestProgBar.setVisibility(View.VISIBLE);
                holder.denyRequestButton.setVisibility(View.GONE);
                holder.acceptRequestButton.setVisibility(View.GONE);
                final String userID = requestsModel.getUserID();

                databaseReferenceChats.child(currentUser.getUid()).child(userID)
                        .child(NodesList.TIME_STAMP)
                        .setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                         databaseReferenceChats.child(userID).child(currentUser.getUid())
                                 .child(NodesList.TIME_STAMP)
                                 .setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>()
                         {
                             @Override
                             public void onComplete(@NonNull Task<Void> task)
                             {
                                 if(task.isSuccessful())
                                 {
                                     databaseReferenceFriendRequests.child(currentUser.getUid()).child(userID)
                                             .child(NodesList.REQUEST_TYPE)
                                             .setValue(Constants.REQUEST_STATUS_ACCEPTED).addOnCompleteListener(new OnCompleteListener<Void>()
                                     {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task)
                                         {
                                             if(task.isSuccessful())
                                             {
                                                 databaseReferenceFriendRequests.child(userID).child(currentUser.getUid())
                                                         .child(NodesList.REQUEST_TYPE)
                                                         .setValue(Constants.REQUEST_STATUS_ACCEPTED).addOnCompleteListener(new OnCompleteListener<Void>()
                                                 {
                                                     @Override
                                                     public void onComplete(@NonNull Task<Void> task)
                                                     {
                                                         if(task.isSuccessful())
                                                         {
                                                             String title = "Friend Request Accepted";
                                                             String message = "Friend request accepted by: "+currentUser.getDisplayName();

                                                             Util.sendNotification(context, title, message, userID);

                                                             holder.requestProgBar.setVisibility(View.GONE);
                                                             holder.denyRequestButton.setVisibility(View.VISIBLE);
                                                             holder.acceptRequestButton.setVisibility(View.VISIBLE);
                                                         }
                                                         else
                                                         {
                                                             handleException(holder,task.getException());
                                                         }
                                                     }
                                                 });
                                             }
                                             else
                                             {
                                                 handleException(holder,task.getException());
                                             }
                                         }
                                     });
                                 }
                                 else
                                 {
                                     handleException(holder,task.getException());
                                 }
                             }
                         });
                        }
                        else
                        {
                            handleException(holder,task.getException());
                        }
                    }
                });
            }
        });

        holder.denyRequestButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                holder.requestProgBar.setVisibility(View.VISIBLE);
                holder.denyRequestButton.setVisibility(View.GONE);
                holder.acceptRequestButton.setVisibility(View.GONE);


                final String userID = requestsModel.getUserID();

                databaseReferenceFriendRequests.child(currentUser.getUid()).child(userID)
                        .child(NodesList.REQUEST_TYPE).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            databaseReferenceFriendRequests.child(userID).child(currentUser.getUid())
                                    .child(NodesList.REQUEST_TYPE).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        String title = "Friend Request Denied";
                                        String message = "Friend request denied by: "+currentUser.getDisplayName();

                                        Util.sendNotification(context, title, message, userID);
                                        holder.requestProgBar.setVisibility(View.GONE);
                                        holder.denyRequestButton.setVisibility(View.VISIBLE);
                                        holder.acceptRequestButton.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        Toast.makeText(context,"Failed to deny request:"
                                                +task.getException(),Toast.LENGTH_SHORT).show();
                                        holder.requestProgBar.setVisibility(View.GONE);
                                        holder.denyRequestButton.setVisibility(View.VISIBLE);
                                        holder.acceptRequestButton.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(context,"Failed to deny request:"
                            +task.getException(),Toast.LENGTH_SHORT).show();
                            holder.requestProgBar.setVisibility(View.GONE);
                            holder.denyRequestButton.setVisibility(View.VISIBLE);
                            holder.acceptRequestButton.setVisibility(View.VISIBLE);
                        }
                    }
                });


            }
        });
    }

    private void handleException(RequestViewHolder holder,Exception exception)
    {
        Toast.makeText(context,"Failed to accept request:"
                +exception,Toast.LENGTH_SHORT).show();

        holder.requestProgBar.setVisibility(View.GONE);
        holder.denyRequestButton.setVisibility(View.VISIBLE);
        holder.acceptRequestButton.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return requestsModelList.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder
    {

        private ImageView requestProfPic;
        private TextView friendRequestName;
        private Button acceptRequestButton, denyRequestButton;
        private View requestProgBar;
        public RequestViewHolder(@NonNull View itemView)
        {
            super(itemView);

            requestProfPic = itemView.findViewById(R.id.requestProfPic);
            friendRequestName = itemView.findViewById(R.id.friendRequestName);
            acceptRequestButton = itemView.findViewById(R.id.acceptRequestButton);
            denyRequestButton = itemView.findViewById(R.id.denyRequestButton);
            requestProgBar = itemView.findViewById(R.id.requestProgBar);
        }
    }
}
