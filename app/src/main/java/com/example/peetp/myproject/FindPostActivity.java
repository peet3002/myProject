package com.example.peetp.myproject;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.peetp.myproject.model.Posts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class FindPostActivity extends AppCompatActivity  {


    private RelativeLayout searchLayout;
    private EditText searchEditText;
    private TextView noData;
    private ImageButton arrowBackBtn, clearBtn, micBtn;
    private InputMethodManager imm;
    private RecyclerView postList;

    private DatabaseReference postsRef;

    private String queryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_post);

        searchLayout = (RelativeLayout) findViewById(R.id.search_layout);
        searchEditText = (EditText) findViewById(R.id.searchEditText);
        arrowBackBtn = (ImageButton) findViewById(R.id.ic_arrowBack);
        clearBtn = (ImageButton) findViewById(R.id.ic_clear);
        micBtn = (ImageButton) findViewById(R.id.ic_mic);
        noData = (TextView) findViewById(R.id.no_data);
        queryText = getIntent().getExtras().get("queryText").toString();

        postList = (RecyclerView) findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        init();
        noData.setVisibility(View.GONE);
        DisplsyPost();


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>0)
                {
                    micBtn.setVisibility(View.GONE);
                    clearBtn.setVisibility(View.VISIBLE);
                }
                else
                {
                    clearBtn.setVisibility(View.GONE);
                }

            }
        });
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if(textView.getText().toString().length()<1)
                {
                    return true;
                }else{
                    onQueryTextSubmit(textView.getText().toString());
                    return true;
                }
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText(null);
            }
        });

        arrowBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void DisplsyPost() {
        Query sortPost = postsRef.orderByChild("postheader").startAt(queryText).endAt(queryText + "\uf8ff");
        adapter(sortPost);
        sortPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    noData.setVisibility(View.VISIBLE);
                }
                else{
                    noData.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void adapter(Query sortPost){
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
                                Intent commentIntent = new Intent(FindPostActivity.this, CommentsActivity.class);
                                commentIntent.putExtra("PostKey",PostKey);
                                startActivity(commentIntent);
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent clickPostIntent = new Intent(FindPostActivity.this, ClickPostActivity.class);
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

    private void openKeyboard()
    {
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left , R.anim.slide_out_right);
    }

    public void onQueryTextSubmit(String query) {
        Query sortPost = postsRef.orderByChild("postheader").startAt(query).endAt(query + "\uf8ff");
        adapter(sortPost);
        sortPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    noData.setVisibility(View.VISIBLE);
                }else{
                    noData.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init(){
        searchLayout.setVisibility(View.VISIBLE);
        searchEditText.setText(queryText);
        micBtn.setVisibility(View.GONE);
        clearBtn.setVisibility(View.VISIBLE);
        searchEditText.requestFocus();
        openKeyboard();
    }
}
