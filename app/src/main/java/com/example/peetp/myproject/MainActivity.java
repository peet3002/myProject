package com.example.peetp.myproject;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.peetp.myproject.model.Posts;
import com.example.peetp.myproject.model.Users;
import com.example.peetp.myproject.videocall.BaseActivity;
import com.example.peetp.myproject.videocall.SinchService;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.SinchError;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import com.example.peetp.myproject.SearchToolbar.OnSearchToolbarQueryTextListner;

import java.util.HashMap;


public class MainActivity extends BaseActivity implements OnSearchToolbarQueryTextListner, SinchService.StartFailedListener{

    SearchToolbar searchToolbar;

    private FirebaseAuth mAuth;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private Toolbar mToolbar;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    private CircleImageView navProfileImage;
    private TextView navProfileUserName;

    private Users users;


    private String profileDefault = "https://firebasestorage.googleapis.com/v0/b/myproject-adc06.appspot.com/o/Profile%20Images%2Fprofile.png?alt=media&token=c5fb2184-5b8b-4e18-a791-95a52592f9ec";

    private DatabaseReference usersRef, postsRef;

    String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_PHONE_STATE},100);
        }

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("หน้าแรก");

        searchToolbar = new SearchToolbar(this, this, findViewById(R.id.search_layout));

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        postList = (RecyclerView) findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        navProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        navProfileUserName = (TextView) navView.findViewById(R.id.nav_user_full_name);
        invalidateOptionsMenu();


        checkUser();

        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(!dataSnapshot.hasChild("profileimage")){
                        usersRef.child(currentUserId).child("profileimage").setValue(profileDefault);
                    }
                    if(dataSnapshot.hasChild("username")){
                        String name = dataSnapshot.child("username").getValue().toString();
                        navProfileUserName.setText(name);

                    }
                    if(dataSnapshot.hasChild("profileimage")){
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(navProfileImage);
                    }
                    else {
                        //Toast.makeText(MainActivity.this, "Profile name do not exists...",Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });


        DisplsyAllUsersPost();


    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        getSinchServiceInterface().setStartListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private void checkUser() {
        final Menu nav_Menu = navigationView.getMenu();
        usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String usertype = dataSnapshot.child("usertype").getValue().toString();
                    setId(currentUserId);
                    if(usertype.equals("teacher")){
                        nav_Menu.findItem(R.id.nav_profile).setVisible(false);
                        nav_Menu.findItem(R.id.nav_teacher_profile).setVisible(true);
                        nav_Menu.findItem(R.id.nav_counsel).setVisible(false);
                        nav_Menu.findItem(R.id.nav_counsel_group).setVisible(true);
                        nav_Menu.findItem(R.id.nav_counsel_edited).setVisible(true);
                        nav_Menu.findItem(R.id.nav_counsel_not).setVisible(true);
                        nav_Menu.findItem(R.id.nav_find_friends).setVisible(false);
                        nav_Menu.findItem(R.id.nav_friends).setVisible(false);
                        nav_Menu.findItem(R.id.nav_static).setVisible(true);


                    }else{
                        nav_Menu.findItem(R.id.nav_profile).setVisible(true);
                        nav_Menu.findItem(R.id.nav_teacher_profile).setVisible(false);
                        nav_Menu.findItem(R.id.nav_counsel).setVisible(true);
                        nav_Menu.findItem(R.id.nav_counsel_group).setVisible(false);
                        nav_Menu.findItem(R.id.nav_counsel_edited).setVisible(false);
                        nav_Menu.findItem(R.id.nav_counsel_not).setVisible(false);
                        nav_Menu.findItem(R.id.nav_find_friends).setVisible(true);
                        nav_Menu.findItem(R.id.nav_friends).setVisible(true);
                        nav_Menu.findItem(R.id.nav_static).setVisible(false);


                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void DisplsyAllUsersPost() {

        Query sortPost = postsRef.orderByChild("counter");
        firebaseRecyclerAdapter(sortPost);
    }

    private void firebaseRecyclerAdapter(Query sortPost){
        FirebaseRecyclerOptions<Posts> options =
                new FirebaseRecyclerOptions.Builder<Posts>()
                        .setQuery(sortPost, Posts.class)
                        .build();

        FirebaseRecyclerAdapter<Posts,PostViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Posts, PostViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final PostViewHolder holder, int position, @NonNull Posts model) {
                        final String PostKey = getRef(position).getKey();

                        holder.postUsername.setText(model.getUsername());
                        holder.postDate.setText(model.getDate());
                        holder.postTime.setText(model.getTime());
                        holder.postHeader.setText(model.getPostheader());
                        holder.postType.setText(model.getPosttype());
                        holder.postDescription.setText(model.getDescription());
                        Picasso.get().load(model.getProfileimage()).into(holder.postProfileImage);
                        Picasso.get().load(model.getPostimage()).into(holder.postImage);


                        postsRef.child(PostKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(!dataSnapshot.hasChild("postimage")){
                                    holder.postImage.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                        holder.commentPostButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent commentIntent = new Intent(MainActivity.this, CommentsActivity.class);
                                commentIntent.putExtra("PostKey",PostKey);
                                startActivity(commentIntent);
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent clickPostIntent = new Intent(MainActivity.this, ClickPostActivity.class);
                                clickPostIntent.putExtra("PostKey",PostKey);
                                startActivity(clickPostIntent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_posts_layout, viewGroup , false);
                        PostViewHolder viewHolder = new PostViewHolder(view);
                        return viewHolder;
                    }
                };
        postList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    @Override
    public void onStartFailed(SinchError error) {

    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{
        ImageButton commentPostButton;
        CircleImageView postProfileImage;
        ImageView postImage;
        TextView postDate,postTime,postUsername,postDescription,postHeader,postType;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            commentPostButton = itemView.findViewById(R.id.post_comment_btn);
            postUsername = itemView.findViewById(R.id.post_user_name);
            postDate = itemView.findViewById(R.id.post_date);
            postTime = itemView.findViewById(R.id.post_time);
            postHeader = itemView.findViewById(R.id.post_all_header);
            postType = itemView.findViewById(R.id.post_all_type);
            postDescription = itemView.findViewById(R.id.post_all_description);
            postProfileImage = itemView.findViewById(R.id.post_profile_image);
            postImage = itemView.findViewById(R.id.post_image);

        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.ic_search:
                searchToolbar.openSearchToolbar();
                break;
            case R.id.ic_plus:
                sendUserToPostActivity();
                break;

        }
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onQueryTextSubmit(String query) {
        Toast.makeText(this, "User Query: "+query , Toast.LENGTH_SHORT).show();
        Intent findPostIntent = new Intent(MainActivity.this, FindPostActivity.class);
        findPostIntent.putExtra("queryText",query);
        startActivity(findPostIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

//        Query sortPost = postsRef.orderByChild("postheader").startAt(query).endAt(query + "\uf8ff");
//        firebaseRecyclerAdapter(sortPost);
    }

    @Override
    public void onQueryTextChange(String editable) {

    }

    private void UserMenuSelector(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_profile:
                sendUserToProfileActivity();
                break;
            case R.id.nav_teacher_profile:
                sendUserToTeacherProfileActivity();
                break;
            case R.id.nav_home:
                if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    this.drawerLayout.closeDrawer(GravityCompat.START);
                }
                break;

            case R.id.nav_friends:
                sendUserToAdviserProfileActivity();
                break;

            case R.id.nav_find_friends:
                sendUserToFindTeacherActivity();
                break;

            case R.id.nav_messages:
                sendUserToMesssageListActivity();
                break;

            case R.id.nav_logout:
                mAuth.signOut();
                sendUserToLoginActivity();
                break;

            case R.id.nav_post:
                sendUserToPostActivity();
                break;

            case R.id.nav_counsel:
                sendUserToCounselActivity();
                break;
            case R.id.nav_counsel_edited:
                sendUserToCounselEditedActivity();
                break;
            case R.id.nav_counsel_not:
                sendUserToCounselNotActivity();
                break;
            case R.id.nav_static:
                sendUserToStaticActivity();
                break;

        }

    }

    private void status(String onlineStatus){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlinestatus", onlineStatus);
        usersRef.child(currentUserId).updateChildren(hashMap);

    }

    private void setId(String cuid) {
            if (!getSinchServiceInterface().isStarted()) {
                getSinchServiceInterface().startClient(cuid);
            }
    }

    @Override
    public void onStarted() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        status("offline");
//    }


    @Override
    protected void onDestroy() {
        status("offline");
        super.onDestroy();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            sendUserToLoginActivity();
        }
        else{
            CheckUserExistence();
        }

    }

    private void CheckUserExistence() {
        final String current_user_id = mAuth.getCurrentUser().getUid();
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(current_user_id)){
                    sendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetUpActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void sendUserToPostActivity() {
        Intent addNewPostIntent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(addNewPostIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    private void sendUserToProfileActivity() {
        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(profileIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void sendUserToFindTeacherActivity() {
        Intent findTeacherIntent = new Intent(MainActivity.this, FindTeacherActivity.class);
        startActivity(findTeacherIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void sendUserToTeacherProfileActivity() {
        Intent teacherProfileIntent = new Intent(MainActivity.this, TeacherProfileActivity.class);
        startActivity(teacherProfileIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void sendUserToCounselActivity() {
        Intent counselIntent = new Intent(MainActivity.this, CounselListActivity.class);
        startActivity(counselIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void sendUserToCounselEditedActivity() {
        Intent counselIntent = new Intent(MainActivity.this, CounselEditedActivity.class);
        startActivity(counselIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void sendUserToCounselNotActivity() {
        Intent counselIntent = new Intent(MainActivity.this, CounselNotActivity.class);
        startActivity(counselIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void sendUserToAdviserProfileActivity() {
        Intent intent = new Intent(MainActivity.this, AdviserProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void sendUserToMesssageListActivity() {
        Intent intent = new Intent(MainActivity.this, MessageListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void sendUserToStaticActivity() {
        Intent intent = new Intent(MainActivity.this, StaticActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }






}
