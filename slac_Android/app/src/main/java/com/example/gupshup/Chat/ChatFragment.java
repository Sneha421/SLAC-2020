package com.example.gupshup.Chat;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gupshup.Common.NodesList;
import com.example.gupshup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment
{
    private TextView emptyChatsList;
    private View chatListProgBar;
    private RecyclerView recyclerViewChats;
    private ChatListAdapter chatListAdapter;
    private List<ChatListModel> chatListModelList;

    private DatabaseReference databaseReferenceChats, databaseReferenceUsers;
    private FirebaseUser currentUser;

    private List<String> userIDs;

    private ChildEventListener childEventListener;
    private Query query;


    public ChatFragment()
    {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        emptyChatsList = view.findViewById(R.id.emptyChatsList);
        chatListProgBar = view.findViewById(R.id.chatListProgBar);
        recyclerViewChats = view.findViewById(R.id.recyclerViewChats);

        userIDs = new ArrayList<>();

        chatListModelList = new ArrayList<>();

        chatListAdapter = new ChatListAdapter(getActivity(), chatListModelList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewChats.setLayoutManager(linearLayoutManager);

        recyclerViewChats.setAdapter(chatListAdapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child(NodesList.USERS);

        databaseReferenceChats = FirebaseDatabase.getInstance().getReference().child(NodesList.CHATS).child(currentUser.getUid());




        query = databaseReferenceChats.orderByChild(NodesList.TIME_STAMP);

        emptyChatsList.setVisibility(View.VISIBLE);
        chatListProgBar.setVisibility(View.GONE);
        childEventListener = new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName)
            {
                updateList(dataSnapshot, true, dataSnapshot.getKey());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName)
            {
                updateList(dataSnapshot, false, dataSnapshot.getKey());


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
            {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName)
            {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        };

        query.addChildEventListener(childEventListener);
        chatListProgBar.setVisibility(View.VISIBLE);
        emptyChatsList.setVisibility(View.VISIBLE);
    }

    private void updateList(DataSnapshot dataSnapshot, final boolean isNew, final String userID)
    {
//        chatListModelList.clear();

        chatListProgBar.setVisibility(View.GONE);
        emptyChatsList.setVisibility(View.GONE);

        final String lastMessage, unreadMessageCount, lastMessageTime;

        if(dataSnapshot.child(NodesList.LAST_MESSAGE).getValue()!=null)
            lastMessage = dataSnapshot.child(NodesList.LAST_MESSAGE).getValue().toString();
        else
            lastMessage = "";

        if(dataSnapshot.child(NodesList.LAST_MESSAGE_TIME).getValue()!=null)
            lastMessageTime = dataSnapshot.child(NodesList.LAST_MESSAGE_TIME).getValue().toString();
        else
            lastMessageTime="";
        unreadMessageCount = dataSnapshot.child(NodesList.UNREAD_COUNT).getValue()==null
                ?"0"
                 : dataSnapshot.child(NodesList.UNREAD_COUNT).getValue().toString();

        databaseReferenceUsers.child(userID).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String userName = dataSnapshot.child(NodesList.NAME).getValue()!=null?
                        dataSnapshot.child(NodesList.NAME).getValue().toString(): "";

                String photoName = dataSnapshot.child(NodesList.PHOTO).getValue()!=null?
                        dataSnapshot.child(NodesList.PHOTO).getValue().toString(): "";

                ChatListModel chatListModel = new ChatListModel(userID,userName,lastMessage,photoName,unreadMessageCount,lastMessageTime);

                if(isNew)
                {
                    chatListModelList.add(chatListModel);
                    userIDs.add(userID);
                }
                else
                {
                    int indexOfClickedUser = userIDs.indexOf(userID);
                    chatListModelList.set(indexOfClickedUser,chatListModel);
                }
                chatListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(getContext(),"Failed to get chat list"
                +error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        query.removeEventListener(childEventListener);
    }
}