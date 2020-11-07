package com.example.gupshup.Chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gupshup.Common.Constants;
import com.example.gupshup.Common.Extras;
import com.example.gupshup.Common.Util;
import com.example.gupshup.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>
{
    private Context context;
    private List<ChatListModel> chatListModelList;

    public ChatListAdapter(Context context, List<ChatListModel> chatListModelList)
    {
        this.context = context;
        this.chatListModelList = chatListModelList;
    }


    @NonNull
    @Override
    public ChatListAdapter.ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = null;
        try {
           view = LayoutInflater.from(context).inflate(R.layout.chat_list_layout,parent,false);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListAdapter.ChatListViewHolder holder, int position)
    {
        final ChatListModel chatListModel = chatListModelList.get(position);

        holder.chatListUserNameTextVIew.setText(chatListModel.getUserName());




        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child(Constants.PROFILE_PICTURE+"/"+chatListModel.getPhotoName());

        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .error(R.drawable.user)
                        .placeholder(R.drawable.user)
                        .into(holder.chatListProfilePic);
            }
        });

        String lastMessage  = chatListModel.getLastMessage();
        lastMessage = lastMessage.length()>30?lastMessage.substring(0,30):lastMessage;
        holder.chatListLastMessage.setText(lastMessage);

        String lastMessageTime = chatListModel.getLastMessageTime();
        if(lastMessageTime==null) lastMessageTime="";
        if(!TextUtils.isEmpty(lastMessageTime))
            holder.chatListLastMessageTime.setText(Util.getTimeAgo(Long.parseLong(lastMessageTime)));


        if(!chatListModel.getUnreadMessageCount().equals("0"))
        {
            holder.chatListUnreadCount.setVisibility(View.VISIBLE);
            holder.chatListUnreadCount.setText(chatListModel.getUnreadMessageCount());
        }
        else
        {
            holder.chatListUnreadCount.setVisibility(View.GONE);
        }

        holder.linLayoutChatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(context, ChatActivity.class);
                intent.putExtra(Extras.USER_KEY,chatListModel.getUserID());
                intent.putExtra(Extras.USER_NAME,chatListModel.getUserName());
                intent.putExtra(Extras.PHOTO_NAME,chatListModel.getPhotoName());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatListModelList.size();
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView chatListProfilePic;
        private LinearLayout linLayoutChatList;
        private TextView chatListUserNameTextVIew, chatListLastMessage, chatListLastMessageTime, chatListUnreadCount;

        public ChatListViewHolder(@NonNull View itemView)
        {
            super(itemView);

            chatListProfilePic = itemView.findViewById(R.id.chatListProfilePic);
            linLayoutChatList = itemView.findViewById(R.id.linLayoutChatList);
            chatListUserNameTextVIew  = itemView.findViewById(R.id.chatListUserNameTextVIew);
            chatListLastMessage = itemView.findViewById(R.id.chatListLastMessage);
            chatListLastMessageTime = itemView.findViewById(R.id.chatListLastMessageTime);
            chatListUnreadCount = itemView.findViewById(R.id.chatListUnreadCount);
        }
    }
}

