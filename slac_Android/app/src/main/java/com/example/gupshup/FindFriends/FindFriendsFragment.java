package com.example.gupshup.FindFriends;

import android.nfc.tech.Ndef;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gupshup.Common.Constants;
import com.example.gupshup.Common.NodesList;
import com.example.gupshup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class FindFriendsFragment extends Fragment
{

    private RecyclerView recyclerViewFindFriends;
    private FindFriendAdapter findFriendAdapter;
    private List<FindFriendModel> findFriendModelList;
    private TextView findFriendsText;
    private View progBar;
    private DatabaseReference databaseReference, databaseReferenceFriendRequests;
    private FirebaseUser currentUser;
    private LinearLayoutManager linearLayoutManager;
    public FindFriendsFragment()
    {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewFindFriends =  view.findViewById(R.id.recyclerViewFindFriends);
        progBar = view.findViewById(R.id.progBar1);
        findFriendsText = view.findViewById(R.id.emptyFriendsList);


        findFriendModelList = new ArrayList<>();
        findFriendAdapter = new FindFriendAdapter(getActivity(), findFriendModelList);


        recyclerViewFindFriends.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerViewFindFriends.setAdapter(findFriendAdapter);



        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        findFriendsText.setVisibility(View.VISIBLE);







        databaseReferenceFriendRequests = FirebaseDatabase.getInstance().getReference().child(NodesList.FRIEND_REQUESTS).child(currentUser.getUid());
        progBar.setVisibility(View.VISIBLE);


        DatabaseReference usersRef = databaseReference.child(NodesList.USERS);


        ValueEventListener eventListener= new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    final String userID = ds.getKey();
                    if(!userID.equals(currentUser.getUid()))
                    {
                        final String name = ds.child(NodesList.NAME).getValue(String.class);
                        final String photo = ds.child(NodesList.PHOTO).getValue().toString();


                        databaseReferenceFriendRequests.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if(dataSnapshot.exists())
                                {
                                    String requestType  =  dataSnapshot.child(NodesList.REQUEST_TYPE).getValue().toString();

                                    if(requestType.equals(Constants.REQUEST_STATUS_SENT))
                                    {
                                        findFriendModelList.add(new FindFriendModel(name,photo,userID,true));
                                        findFriendAdapter.notifyDataSetChanged();
                                    }
                                }
                                else
                                {
                                    findFriendModelList.add(new FindFriendModel(name,photo,userID,false));
                                    findFriendAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error)
                            {
                                progBar.setVisibility(View.GONE);
                            }
                        });


                        findFriendsText.setVisibility(View.GONE);
                        progBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progBar.setVisibility(View.GONE);
                Toast.makeText(getContext(),"Failed to get friends: "+error.getDetails(),Toast.LENGTH_SHORT).show();
            }
        };

        usersRef.addListenerForSingleValueEvent(eventListener);

//        Query query = databaseReference.orderByChild(NodesList.USERS);

       /* query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                findFriendModelList.clear();

                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String userID = ds.getKey();

                    System.out.println(userID);



                    if(userID.equals(currentUser.getUid()))
                    {
                        return;
                    }
                    if (ds.child(NodesList.NAME).getValue()!= null)
                    {


                        String name = ds.child(NodesList.NAME).getValue().toString();
                        String photo = ds.child(NodesList.PHOTO).getValue().toString();

                        findFriendModelList.add(new FindFriendModel(name,photo,userID,false));

                        findFriendAdapter.notifyDataSetChanged();

                        findFriendsText.setVisibility(View.GONE);
                        progBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                progBar.setVisibility(View.GONE);
                Toast.makeText(getContext(),"Failed to get friends: "+error.getDetails(),Toast.LENGTH_SHORT).show();
            }
        });*/

    }
}