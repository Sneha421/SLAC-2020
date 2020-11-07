package com.example.gupshup.Requests;

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

import com.example.gupshup.Common.Constants;
import com.example.gupshup.Common.NodesList;
import com.example.gupshup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestsFragment extends Fragment
{
    private RecyclerView recyclerViewRequests;
    private View reqProgBar;
    private TextView emptyRequestsList;
    private RequestsAdapter requestsAdapter;
    private List<RequestsModel> requestsModelList;

    private DatabaseReference databaseReferenceRequests, databaseReferenceUsers;
    private FirebaseUser currentUser;


    public RequestsFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewRequests = view.findViewById(R.id.recyclerViewRequests);
        reqProgBar = view.findViewById(R.id.reqProgBar);
        emptyRequestsList = view.findViewById(R.id.emptyRequestsList);

        recyclerViewRequests.setLayoutManager(new LinearLayoutManager(getActivity()));
        requestsModelList = new ArrayList<>();
        requestsAdapter = new RequestsAdapter(getActivity(), requestsModelList);

        recyclerViewRequests.setAdapter(requestsAdapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child(NodesList.USERS);

        databaseReferenceRequests = FirebaseDatabase.getInstance().getReference().child(NodesList.FRIEND_REQUESTS).child(currentUser.getUid());

        emptyRequestsList.setVisibility(View.VISIBLE);
        reqProgBar.setVisibility(View.VISIBLE);

        databaseReferenceRequests.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                reqProgBar.setVisibility(View.GONE);
                requestsModelList.clear();


                for(DataSnapshot ds:snapshot.getChildren())
                {
                    if(ds.exists())
                    {
                        String requestType = ds.child(NodesList.REQUEST_TYPE).getValue().toString();

                        if (requestType.equals(Constants.REQUEST_STATUS_RECEIVED))
                        {
                            final String userID = ds.getKey();
                            databaseReferenceUsers.child(userID).addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot)
                                {
                                    String userName = snapshot.child(NodesList.NAME).getValue().toString();

                                    String photoName = "";

                                    if(snapshot.child(NodesList.PHOTO).getValue().toString()!=null)
                                    {
                                        photoName = snapshot.child(NodesList.PHOTO).getValue().toString();
                                    }

                                    RequestsModel requestsModel = new RequestsModel(userID,userName,photoName);

                                    requestsModelList.add(requestsModel);

                                    requestsAdapter.notifyDataSetChanged();

                                    emptyRequestsList.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error)
                                {
                                    Toast.makeText(getContext(),"Failed to get requests: "
                                    +error.getMessage(),Toast.LENGTH_SHORT).show();
                                    reqProgBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(getContext(),"Failed to get requests: "
                        +error.getMessage(),Toast.LENGTH_SHORT).show();
                reqProgBar.setVisibility(View.GONE);
            }
        });

    }
}
