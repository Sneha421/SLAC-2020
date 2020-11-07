package com.example.gupshup.Chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
//import android.view.ActionMode;
import androidx.appcompat.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gupshup.Common.Constants;
import com.example.gupshup.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>
{
    private Context context;
    private List<MessageModel> messageModelList;
    private FirebaseAuth firebaseAuth;

    private ActionMode actionMode;
    private ConstraintLayout selectedView;


    public MessagesAdapter(Context context, List<MessageModel> messageModelList)
    {
        this.context = context;
        this.messageModelList = messageModelList;
    }

    @NonNull
    @Override
    public MessagesAdapter.MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.messages_layout, parent, false);
        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessagesAdapter.MessagesViewHolder holder, int position)
    {
        final MessageModel messageModel = messageModelList.get(position);

        firebaseAuth = FirebaseAuth.getInstance();
        String currentUserID = firebaseAuth.getCurrentUser().getUid();

        String fromUserID = messageModel.getMessageFrom();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyy HH:mm");
        String dateTime = simpleDateFormat.format(messageModel.getMessageTime());
        String [] splitTime = dateTime.split(" ");
        String messageTime = splitTime[1];

        if(fromUserID.equals(currentUserID))
        {

            if(messageModel.getMessageType().equals(Constants.MESSAGE_TYPE_TEXT))
            {
                holder.llSent.setVisibility(View.VISIBLE);
                holder.llSentImage.setVisibility(View.GONE);
            }
            else
            {
                holder.llSent.setVisibility(View.GONE);
                holder.llSentImage.setVisibility(View.VISIBLE);
            }


            holder.llReceived.setVisibility(View.GONE);
            holder.llReceivedImage.setVisibility(View.GONE);

            holder.tvSentMessage.setText(messageModel.getMessage());
            holder.tvSentMessageTime.setText(messageTime);
            holder.tvSentImageTime.setText(messageTime);
            Glide.with(context)
                    .load(messageModel.getMessage())
                    .placeholder(R.drawable.ic_image)
                    .into(holder.ivSent);
        }
        else
        {

            if(messageModel.getMessageType().equals(Constants.MESSAGE_TYPE_TEXT))
            {
                holder.llReceived.setVisibility(View.VISIBLE);
                holder.llReceivedImage.setVisibility(View.GONE);
            }
            else
            {
                holder.llReceived.setVisibility(View.GONE);
                holder.llReceivedImage.setVisibility(View.VISIBLE);
            }

            holder.llSent.setVisibility(View.GONE);
            holder.llReceived.setVisibility(View.VISIBLE);

            holder.tvReceivedMessage.setText(messageModel.getMessage());
            holder.tvReceivedMessageTime.setText(messageTime);
            holder.tvReceivedImageTime.setText(messageTime);
            Glide.with(context)
                    .load(messageModel.getMessage())
                    .placeholder(R.drawable.ic_image)
                    .into(holder.ivReceived);
        }

        holder.clMessage.setTag(R.id.TAG_MESSAGE, messageModel.getMessage());
        holder.clMessage.setTag(R.id.TAG_MESSAGE_ID, messageModel.getMessageId());
        holder.clMessage.setTag(R.id.TAG_MESSAGE_TYPE, messageModel.getMessageType());

        holder.clMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String messageType = view.getTag(R.id.TAG_MESSAGE_TYPE).toString();

                Uri uri =Uri.parse(view.getTag(R.id.TAG_MESSAGE).toString());

                if(messageType.equals(Constants.MESSAGE_TYPE_VIDEO))
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.setDataAndType(uri, "video/mp4");
                    context.startActivity(intent);
                }
                else if(messageType.equals(Constants.MESSAGE_TYPE_IMAGE))
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.setDataAndType(uri, "image/jpg");
                    context.startActivity(intent);
                }
            }
        });

        holder.clMessage.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                if(actionMode!=null)
                {
                    return false;
                }

                selectedView = holder.clMessage;

                actionMode = ((AppCompatActivity) context).startSupportActionMode(actionModeCallback);


                holder.clMessage.setBackgroundColor(context.getResources().getColor(R.color.selectedMsgColor));

                return true;
            }
        });


    }

    @Override
    public int getItemCount()
    {
        return messageModelList.size();
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder
    {
        private LinearLayout llSent, llReceived, llSentImage, llReceivedImage;
        private TextView tvSentMessage, tvReceivedMessage, tvSentMessageTime, tvReceivedMessageTime, tvSentImageTime, tvReceivedImageTime;
        private ConstraintLayout clMessage;
        private ImageView ivSent, ivReceived;

        public MessagesViewHolder(@NonNull View itemView)
        {
            super(itemView);

            llSent = itemView.findViewById(R.id.llSent);
            llReceived = itemView.findViewById(R.id.llReceived);

            llSentImage = itemView.findViewById(R.id.llSentImage);
            llReceivedImage = itemView.findViewById(R.id.llReceivedImage);

            tvSentMessage = itemView.findViewById(R.id.tvSentMessage);
            tvReceivedMessage = itemView.findViewById(R.id.tvReceivedMessage);

            tvSentImageTime = itemView.findViewById(R.id.tvSentImageTime);
            tvReceivedImageTime = itemView.findViewById(R.id.tvReceivedImageTime);

            tvSentMessageTime = itemView.findViewById(R.id.tvSentMessageTime);
            tvReceivedMessageTime = itemView.findViewById(R.id.tvReceivedMessageTime);

            ivSent = itemView.findViewById(R.id.ivSent);
            ivReceived = itemView.findViewById(R.id.ivReceived);

            clMessage = itemView.findViewById(R.id.clMessage);
        }
    }

    public  ActionMode.Callback actionModeCallback =  new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
        {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.menu_chat_options, menu);
            String selectedMessageType = String.valueOf(selectedView.getTag(R.id.TAG_MESSAGE_TYPE));
            if(selectedMessageType.equals(Constants.MESSAGE_TYPE_TEXT))
            {
                MenuItem itemDownload = menu.findItem(R.id.menuDownload);
                itemDownload.setVisible(false);
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem)
        {
            String selMsgID = (String) selectedView.getTag(R.id.TAG_MESSAGE_ID);
            String selMsgType = (String) selectedView.getTag(R.id.TAG_MESSAGE_TYPE);
            String selMsg = (String) selectedView.getTag(R.id.TAG_MESSAGE);
            int itemID = menuItem.getItemId();

            switch (itemID)
            {
                case R.id.menuDelete:

                    if(context instanceof  ChatActivity)
                    {
                        ((ChatActivity)context).deleteMessage(selMsgID, selMsgType);
                    }
                    actionMode.finish();
                    break;
                case R.id.menuDownload:
                    if(context instanceof  ChatActivity)
                    {
                        ((ChatActivity)context).downloadFile(selMsgID, selMsgType, false);
                    }
                    actionMode.finish();
                    break;
                case R.id.menuForward:
                    if(context instanceof  ChatActivity)
                    {
                        ((ChatActivity) context).forwardMessage(selMsgID, selMsg, selMsgType);
                    }                    actionMode.finish();
                    break;
                case R.id.menuShare:

                    if(selMsgType.equals(Constants.MESSAGE_TYPE_TEXT)){
                        Intent intentShare = new Intent();
                        intentShare.setAction(Intent.ACTION_SEND);
                        intentShare.putExtra(Intent.EXTRA_TEXT, selMsg);
                        intentShare.setType("text/plain");
                        context.startActivity(intentShare);
                    }
                    else
                    {
                        if(context instanceof  ChatActivity)
                        {
                            ((ChatActivity)context).downloadFile(selMsgID, selMsgType, true);
                        }
                    }
                    actionMode.finish();
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode)
        {
            actionMode = null;
            selectedView.setBackgroundColor(context.getResources().getColor(R.color.chat_background));
        }
    };

}
