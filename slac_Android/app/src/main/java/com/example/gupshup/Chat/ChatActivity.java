package com.example.gupshup.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gupshup.Common.Constants;
import com.example.gupshup.Common.Extras;
import com.example.gupshup.Common.NodesList;
import com.example.gupshup.Common.Util;
import com.example.gupshup.R;
import com.example.gupshup.SelectFriend.SelectFriendActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout linLayoutSendMessage;
    private EditText editTextSendMessage;
    private ImageView chatAttachment, send, ivProfile;

    private TextView tvUser,tvUserStatus;

    private DatabaseReference mRootRef;

    private FirebaseAuth firebaseAuth;

    private RecyclerView rvMessage;

    private SwipeRefreshLayout srlMessage;

    private MessagesAdapter messagesAdapter;

    private List<MessageModel> messageModelList;

    private int currentPage = 1;

    private static final int RECORD_PER_PAGE = 30;

    private static final int REQUEST_CODE_FORWARD_MESSAGE = 104;

    private DatabaseReference databaseReferenceMessages;

    private ChildEventListener childEventListener;

    private LinearLayout llProgress;

    private String currentUserID, chatUserID, userName, photoName;

    private BottomSheetDialog bottomSheetDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setContentView(R.layout.activity_chat);


        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("");

            ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_action_bar, null);

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setElevation(0);

            actionBar.setCustomView(actionBarLayout);


            actionBar.setDisplayOptions(actionBar.getDisplayOptions() | ActionBar.DISPLAY_SHOW_CUSTOM);
        }
        linLayoutSendMessage = findViewById(R.id.linLayoutSendMessage);
        llProgress = findViewById(R.id.llProgress);
        editTextSendMessage = findViewById(R.id.editTextSendMessage);
        chatAttachment = findViewById(R.id.chatAttachment);
        send = findViewById(R.id.send);
        ivProfile = findViewById(R.id.ivProfile);
        tvUser = findViewById(R.id.tvUser);

        tvUserStatus = findViewById(R.id.tvUserStatus);

        send.setOnClickListener(this);
        chatAttachment.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        currentUserID = firebaseAuth.getCurrentUser().getUid();

        if (getIntent().hasExtra(Extras.USER_KEY)) {
            chatUserID = getIntent().getStringExtra(Extras.USER_KEY);
        }
        if (getIntent().hasExtra(Extras.USER_NAME)) {
            userName = getIntent().getStringExtra(Extras.USER_NAME);
        }
        if (getIntent().hasExtra(Extras.PHOTO_NAME)) {
            photoName = getIntent().getStringExtra(Extras.PHOTO_NAME);
        }
        System.out.println(userName);

        tvUser.setText(userName);

        if (!TextUtils.isEmpty(photoName)) {
            StorageReference photoRef = FirebaseStorage.getInstance().getReference().child(Constants.PROFILE_PICTURE).child(photoName);
            photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(ChatActivity.this)
                            .load(uri)
                            .placeholder(R.drawable.user)
                            .error(R.drawable.user)
                            .into(ivProfile);

                }
            });
        }


        rvMessage = findViewById(R.id.rvMessages);
        srlMessage = findViewById(R.id.srlMessages);

        messageModelList = new ArrayList<>();

        messagesAdapter = new MessagesAdapter(this, messageModelList);

        rvMessage.setLayoutManager(new LinearLayoutManager(this));
        rvMessage.setAdapter(messagesAdapter);

        bottomSheetDialog = new BottomSheetDialog(ChatActivity.this);
        View view = getLayoutInflater().inflate(R.layout.chat_file_options, null);
        view.findViewById(R.id.llCamera).setOnClickListener(this);
        view.findViewById(R.id.llGallery).setOnClickListener(this);
        view.findViewById(R.id.llVideo).setOnClickListener(this);
        view.findViewById(R.id.ivClose).setOnClickListener(this);
        bottomSheetDialog.setContentView(view);

        loadMessages();

        mRootRef.child(NodesList.CHATS).child(currentUserID).child(chatUserID).child(NodesList.UNREAD_COUNT).setValue(0);

        rvMessage.scrollToPosition(messageModelList.size() - 1);

        srlMessage.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage++;
                loadMessages();
            }
        });

        if(getIntent().hasExtra(Extras.MESSAGE) && getIntent().hasExtra(Extras.MESSAGE_ID) && getIntent().hasExtra(Extras.MESSAGE_TYPE) )
        {
            String message = getIntent().getStringExtra(Extras.MESSAGE);
            String messageId = getIntent().getStringExtra(Extras.MESSAGE_ID);
            final String messageType = getIntent().getStringExtra(Extras.MESSAGE_TYPE);

            DatabaseReference messageRef = mRootRef.child(NodesList.MESSAGES).child(currentUserID).child(chatUserID).push();
            final String newMessageId = messageRef.getKey();

            if(messageType.equals(Constants.MESSAGE_TYPE_TEXT)) {
                SendMessage(message, messageType, newMessageId);
            }
            else{
                StorageReference rootRef = FirebaseStorage.getInstance().getReference();
                String folder = messageType.equals( Constants.MESSAGE_TYPE_VIDEO)? Constants.MESSAGES_VIDEOS:Constants.MESSAGES_IMAGES;
                String oldFileName = messageType.equals( Constants.MESSAGE_TYPE_VIDEO)?messageId + ".mp4": messageId+".jpeg";
                String newFileName = messageType.equals( Constants.MESSAGE_TYPE_VIDEO)?newMessageId + ".mp4": newMessageId+".jpeg";

                final String localFilePath = getExternalFilesDir(null).getAbsolutePath() + "/" + oldFileName;
                final File localFile = new File(localFilePath);

                final StorageReference newFileRef = rootRef.child(folder).child(newFileName);
                rootRef.child(folder).child(oldFileName).getFile(localFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                UploadTask uploadTask = newFileRef.putFile(Uri.fromFile(localFile));
                                uploadProgress(uploadTask, newFileRef, newMessageId, messageType);
                            }
                        });
            }

        }

        DatabaseReference databaseReferenceUsers = mRootRef.child(NodesList.USERS).child(chatUserID);
        databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = "";

                if(snapshot.child(NodesList.STATUS).getValue()!=null)
                {
                    status = snapshot.child(NodesList.STATUS).getValue().toString();
                }

                if(status.equals("true"))
                {
                    tvUserStatus.setText("Online");
                }
                else
                {
                    tvUserStatus.setText("Offline");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        editTextSendMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                DatabaseReference currentUserRef = mRootRef.child(NodesList.CHATS).child(currentUserID).child(chatUserID);
                if(editable.toString().matches(""))
                {
                    currentUserRef.child(NodesList.TYPING).setValue(Constants.TYPING_STOPPED);
                }
                else
                {
                    currentUserRef.child(NodesList.TYPING).setValue(Constants.TYPING_STARTED);
                }
            }
        });

        DatabaseReference chatUserRef = mRootRef.child(NodesList.CHATS).child(chatUserID).child(currentUserID);
        chatUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(NodesList.TYPING).getValue()!=null)
                {
                    String typingStatus = snapshot.child(NodesList.TYPING).getValue().toString();

                    String typing = typingStatus.equals(Constants.TYPING_STARTED)?Constants.STATUS_TYPING:Constants.STATUS_ONLINE;

                    tvUserStatus.setText(typing);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void SendMessage(final String msg, final String msgType, String pushID)
    {
        try {
            if (!msg.equals("")) {
                HashMap messageMap = new HashMap();
                messageMap.put(NodesList.MESSAGE_ID, pushID);
                messageMap.put(NodesList.MESSAGE, msg);
                messageMap.put(NodesList.MESSAGE_TYPE, msgType);
                messageMap.put(NodesList.MESSAGE_FROM, currentUserID);
                messageMap.put(NodesList.MESSAGE_TIME, ServerValue.TIMESTAMP);

                String currentUserRef = NodesList.MESSAGES + "/" + currentUserID + "/" + chatUserID;

                String chatUserRef = NodesList.MESSAGES + "/" + chatUserID + "/" + currentUserID;

                HashMap messageUserMap = new HashMap();

                messageUserMap.put(currentUserRef + "/" + pushID, messageMap);

                messageUserMap.put(chatUserRef + "/" + pushID, messageMap);

                editTextSendMessage.setText("");

                mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error != null)
                        {
                            Toast.makeText(ChatActivity.this, "Failed to send message: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String title = "New Message";

                            String lastMessage = !msgType.equals(Constants.MESSAGE_TYPE_TEXT)?"New media":msg;

                            Toast.makeText(ChatActivity.this, "Message sent successfully", Toast.LENGTH_SHORT).show();

                            Util.sendNotification(ChatActivity.this,title,msg,chatUserID);
                            Util.updateChatDetails(ChatActivity.this,currentUserID,chatUserID,lastMessage);
                        }
                    }
                });

            }
        } catch (Exception e) {
            Toast.makeText(ChatActivity.this, "Failed to send message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMessages()
    {
        messageModelList.clear();
        databaseReferenceMessages = mRootRef.child(NodesList.MESSAGES).child(currentUserID).child(chatUserID);

        Query messageQuery = databaseReferenceMessages.limitToLast(currentPage * RECORD_PER_PAGE);

        if (childEventListener != null) {
            messageQuery.removeEventListener(childEventListener);
        }

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MessageModel message = snapshot.getValue(MessageModel.class);

                messageModelList.add(message);
                messagesAdapter.notifyDataSetChanged();
                rvMessage.scrollToPosition(messageModelList.size() - 1);
                srlMessage.setRefreshing(false);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot)
            {
                loadMessages();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                srlMessage.setRefreshing(false);
            }
        };

        messageQuery.addChildEventListener(childEventListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                DatabaseReference userMessagePush = mRootRef.child(NodesList.MESSAGES).child(currentUserID).child(chatUserID).push();
                String pushID = userMessagePush.getKey();
                SendMessage(editTextSendMessage.getText().toString().trim(), Constants.MESSAGE_TYPE_TEXT, pushID);

                break;
            case R.id.chatAttachment:
                bottomSheetDialog.show();
                System.out.println("Opening attachment options window...");
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                break;

            case R.id.llCamera:
                Intent intentCamera = new Intent(ACTION_IMAGE_CAPTURE);
                startActivityForResult(intentCamera, 102);
                bottomSheetDialog.dismiss();
                break;

            case R.id.llGallery:
                System.out.println("Opening gallery app...");
                bottomSheetDialog.dismiss();
                System.out.println("Bottom sheet dialogue dismissed...");
                Intent intentGallery = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentGallery, 101);
                break;

            case R.id.llVideo:
                bottomSheetDialog.dismiss();
                Intent intentVideo = new Intent(Intent.ACTION_PICK,
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentVideo, 103);
                break;
            case R.id.ivClose:
                bottomSheetDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("Inside activity result");

        if (resultCode == RESULT_OK)
        {

            switch (requestCode)
            {
                case 102: //Camera
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    uploadBytes(bytes, Constants.MESSAGE_TYPE_IMAGE);
                    break;
                case 101: //Gallery
                    try {
                        System.out.println("Starting on result activity...");
                        InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                        System.out.println("Collected input stream...");
                        uploadFile(inputStream, Constants.MESSAGE_TYPE_IMAGE);
                        System.out.println("File uploaded and uploadFile() function exited...");

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case 103: //Video
                    try {
                        InputStream inputStream1 = this.getContentResolver().openInputStream(data.getData());
                        uploadFile(inputStream1, Constants.MESSAGE_TYPE_VIDEO);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    break;
                case REQUEST_CODE_FORWARD_MESSAGE:
                    Intent intent = new Intent( this, ChatActivity.class);
                    intent.putExtra(Extras.USER_KEY, data.getStringExtra(Extras.USER_KEY));
                    intent.putExtra(Extras.USER_NAME, data.getStringExtra(Extras.USER_NAME));
                    intent.putExtra(Extras.PHOTO_NAME, data.getStringExtra(Extras.PHOTO_NAME));

                    intent.putExtra(Extras.MESSAGE, data.getStringExtra(Extras.MESSAGE));
                    intent.putExtra(Extras.MESSAGE_ID, data.getStringExtra(Extras.MESSAGE_ID));
                    intent.putExtra(Extras.MESSAGE_TYPE, data.getStringExtra(Extras.MESSAGE_TYPE));

                    startActivity(intent);
                    finish();
                    break;
            }
        }
    }

    private void uploadFile(InputStream inputStream, String messageType) {
        System.out.println("Inside uploadFile() function...");

        DatabaseReference databaseReference = mRootRef.child(NodesList.MESSAGES).child(currentUserID).child(chatUserID).push();
        System.out.println("Database reference obtained...");

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        System.out.println("Root Storage reference obtained...");
        String pushID = databaseReference.getKey();
        System.out.println("Push Key obtained...");


        String folderName = messageType.equals(Constants.MESSAGE_TYPE_VIDEO) ? Constants.MESSAGES_VIDEOS : Constants.MESSAGES_IMAGES;
        String fileName = messageType.equals(Constants.MESSAGE_TYPE_VIDEO) ? pushID + ".mp4" : pushID + ".jpeg";
        System.out.println("File name and Folder name generated...");

        StorageReference fileRef = storageReference.child(folderName).child(fileName);
        System.out.println("Reference for file name generated...");

        UploadTask uploadTask = fileRef.putStream(inputStream);
        System.out.println("File uploading...");

        uploadProgress(uploadTask, fileRef, pushID, messageType);
    }

    private void uploadProgress(final UploadTask task, final StorageReference filePath, final String pushID, final String messageType) {
        final View view = getLayoutInflater().inflate(R.layout.file_progress, null);
        final TextView tvFileProgress = view.findViewById(R.id.tvFileProgress);
        final ProgressBar pbProgress = view.findViewById(R.id.pbProgress);
        final ImageView ivPause = view.findViewById(R.id.ivPause);
        final ImageView ivPlay = view.findViewById(R.id.ivPlay);
        ImageView ivStop = view.findViewById(R.id.ivStop);

        ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.pause();
                ivPlay.setVisibility(View.VISIBLE);
                ivPause.setVisibility(View.GONE);

            }
        });

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.resume();
                ivPlay.setVisibility(View.GONE);
                ivPause.setVisibility(View.VISIBLE);
            }
        });

        ivStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.cancel();
            }
        });

        llProgress.addView(view);
        tvFileProgress.setText(getString(R.string.upload_progress, messageType, "0"));

        task.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * ((int) taskSnapshot.getBytesTransferred())) / (int) taskSnapshot.getTotalByteCount();
                pbProgress.setProgress((int) progress);
                tvFileProgress.setText(getString(R.string.upload_progress, messageType, String.valueOf(pbProgress.getProgress())));
            }
        });

        task.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                llProgress.removeView(view);

                if (task.isSuccessful()) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUri = uri.toString();
                            SendMessage(downloadUri, messageType, pushID);
                        }
                    });
                }
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, getString(R.string.upload_failed, e.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadBytes(ByteArrayOutputStream bytes, String messageType) {
        DatabaseReference databaseReference = mRootRef.child(NodesList.MESSAGES).child(currentUserID).child(chatUserID).push();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        String pushID = databaseReference.getKey();

        String folderName = messageType.equals(Constants.MESSAGE_TYPE_VIDEO) ? Constants.MESSAGES_VIDEOS : Constants.MESSAGES_IMAGES;
        String fileName = messageType.equals(Constants.MESSAGE_TYPE_VIDEO) ? pushID + ".mp4" : pushID + ".jpeg";

        StorageReference fileRef = storageReference.child(folderName).child(fileName);

        UploadTask uploadTask = fileRef.putBytes(bytes.toByteArray());
        uploadProgress(uploadTask, fileRef, pushID, messageType);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();

        switch (itemID) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteMessage(final String messageId, final String messageType) {

        DatabaseReference databaseReference = mRootRef.child(NodesList.MESSAGES)
                .child(currentUserID).child(chatUserID).child(messageId);

        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    DatabaseReference databaseReferenceChatUser = mRootRef.child(NodesList.MESSAGES)
                            .child(chatUserID).child(currentUserID).child(messageId);

                    databaseReferenceChatUser.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), R.string.message_deleted_successfully, Toast.LENGTH_SHORT).show();
                                if (!messageType.equals(Constants.MESSAGE_TYPE_TEXT)) {
                                    StorageReference rootRef = FirebaseStorage.getInstance().getReference();
                                    String folder = messageType.equals(Constants.MESSAGE_TYPE_VIDEO) ? Constants.MESSAGES_VIDEOS : Constants.MESSAGES_IMAGES;
                                    String fileName = messageType.equals(Constants.MESSAGE_TYPE_VIDEO) ? messageId + ".mp4" : messageId + ".jpg";
                                    StorageReference fileRef = rootRef.child(folder).child(fileName);

                                    fileRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(),
                                                        getString(R.string.failed_to_delete_file, task.getException()), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.failed_to_delete_message, task.getException()),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.failed_to_delete_message, task.getException()),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public  void  downloadFile(String messageId, final String messageType, final boolean isShare)
    {
        String folderName = messageType.equals(Constants.MESSAGE_TYPE_VIDEO)?Constants.MESSAGES_VIDEOS : Constants.MESSAGES_IMAGES;
        String fileName = messageType.equals(Constants.MESSAGE_TYPE_VIDEO)?messageId + ".mp4": messageId + ".jpeg";

        StorageReference fileRef= FirebaseStorage.getInstance().getReference().child(folderName).child(fileName);
        final String localFilePath = getExternalFilesDir(null).getAbsolutePath() + "/" + fileName;

        File localFile = new File(localFilePath);

        try {
            if(localFile.exists() || localFile.createNewFile())
            {
                final FileDownloadTask downloadTask =  fileRef.getFile(localFile);

                final View view = getLayoutInflater().inflate(R.layout.file_progress, null);
                final ProgressBar pbProgress = view.findViewById(R.id.pbProgress);
                final TextView tvProgress = view.findViewById(R.id.tvFileProgress);
                final ImageView ivPlay = view.findViewById(R.id.ivPlay);
                final ImageView ivPause = view.findViewById(R.id.ivPause);
                ImageView ivCancel = view.findViewById(R.id.ivStop);

                ivPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        downloadTask.pause();
                        ivPlay.setVisibility(View.VISIBLE);
                        ivPause.setVisibility(View.GONE);
                    }
                });

                ivPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        downloadTask.resume();
                        ivPause.setVisibility(View.VISIBLE);
                        ivPlay.setVisibility(View.GONE);
                    }
                });

                ivCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        downloadTask.cancel();
                    }
                });

                llProgress.addView(view);
                tvProgress.setText(getString(R.string.download_progress, messageType, "0"));

                downloadTask.addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        pbProgress.setProgress((int) progress);
                        tvProgress.setText(getString(R.string.download_progress, messageType, String.valueOf(pbProgress.getProgress())));
                    }
                });

                downloadTask.addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        llProgress.removeView(view);
                        if (task.isSuccessful())
                        {
                             if(isShare)
                                {
                                    Intent intentShare = new Intent();
                                    intentShare.setAction(Intent.ACTION_SEND);
                                    intentShare.putExtra(Intent.EXTRA_STREAM, Uri.parse(localFilePath));
                                    if(messageType.equals(Constants.MESSAGE_TYPE_VIDEO))
                                        intentShare.setType("video/mp4");
                                    if(messageType.equals(Constants.MESSAGE_TYPE_IMAGE))
                                        intentShare.setType("image/jpg");
                                    startActivity(Intent.createChooser(intentShare, "Share wtih..."));

                                }
                                else
                                {
                                    Snackbar snackbar = Snackbar.make(llProgress, "File downloaded successfully", Snackbar.LENGTH_INDEFINITE);

                                    snackbar.setAction(R.string.view, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Uri uri = Uri.parse(localFilePath);
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            if (messageType.equals(Constants.MESSAGE_TYPE_VIDEO))
                                                intent.setDataAndType(uri, "video/mp4");
                                            else if (messageType.equals(Constants.MESSAGE_TYPE_IMAGE))
                                                intent.setDataAndType(uri, "image/jpg");

                                            startActivity(intent);
                                        }
                                    });


                                    snackbar.show();

                                }


                        }
                    }
                });

                downloadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        llProgress.removeView(view);
                        Toast.makeText(getApplicationContext(), getString(R.string.failed_to_download, e.getMessage()), Toast.LENGTH_SHORT).show();
                    }
                });

            }
            else
            {
                Toast.makeText(getApplicationContext(), R.string.failed_to_store_file, Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception ex){
            Toast.makeText(ChatActivity.this, getString(R.string.failed_to_download, ex.getMessage()), Toast.LENGTH_SHORT).show();
        }
    }
    public void forwardMessage(String selectedMessageId, String selectedMessage, String selectedMessageType) {
        Intent intent = new Intent(this, SelectFriendActivity.class);
        intent.putExtra(Extras.MESSAGE, selectedMessage);
        intent.putExtra(Extras.MESSAGE_ID, selectedMessageId);
        intent.putExtra(Extras.MESSAGE_TYPE, selectedMessageType);
        startActivityForResult(intent , REQUEST_CODE_FORWARD_MESSAGE);
    }

    @Override
    public void onBackPressed()
    {
        mRootRef.child(NodesList.CHATS).child(currentUserID).child(chatUserID).child(NodesList.UNREAD_COUNT).setValue(0);

        super.onBackPressed();
    }
}