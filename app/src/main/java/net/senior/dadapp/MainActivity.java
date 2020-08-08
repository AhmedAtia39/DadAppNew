package net.senior.dadapp;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

public class MainActivity extends AppCompatActivity {

    DatabaseReference database;
    private SearchView searchView;
ArrayList<String> foldersModel;
    private FolderAdapter foldersAdapter;
    HomeFragment home;
    Bundle bundle;
    private UploadTask uploadTask;
    Model memory;

    private String photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database=FirebaseDatabase.getInstance().getReference().child("Memories");
        foldersModel=new ArrayList();
         bundle = new Bundle();

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foldersModel.clear();
                for(DataSnapshot d:snapshot.getChildren())
                {
                    foldersModel.add(d.getKey());
                foldersAdapter=new FolderAdapter(foldersModel);
                    bundle.putSerializable("list", (Serializable) foldersModel);
                    home = new HomeFragment();
                    home.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,home).commitAllowingStateLoss();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {


                handleSendImage(intent); // Handle single image being sent
            }

        }

    }


    void handleSendImage(Intent intent) {
        final Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog, null);
            final EditText editText = dialogView.findViewById(R.id.searchName);
            Button button1 = dialogView.findViewById(R.id.save);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(editText.getText())) {
                        Toast.makeText(MainActivity.this, "enter name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(editText.getText().toString() + imageUri.getLastPathSegment());
                    uploadTask = storageReference.putFile(imageUri);
                    Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            return storageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri image_uri = task.getResult();
                                photo = image_uri.toString();
                                memory = new Model(editText.getText().toString());
                                memory.setImg(photo + "");
                                memory.setId(database.getRef().push().getKey());
                                memory.setText(editText.getText().toString());
                                database.child(editText.getText().toString()).child(memory.getId()).setValue(memory).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(MainActivity.this, "تم الحفظ بنجاح", Toast.LENGTH_SHORT).show();
                                        dialogBuilder.dismiss();

                                    }
                                });
                            }
                        }
                    });

                }


            });


            dialogBuilder.setView(dialogView);
            dialogBuilder.show();


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filter, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (foldersAdapter != null)
                    foldersAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:
                break;
            case R.id.action_edit:
                break;
            case R.id.action_add:
                final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog, null);
                final EditText editText = dialogView.findViewById(R.id.searchName);
                Button button1 = dialogView.findViewById(R.id.save);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(editText.getText())) {
                            Toast.makeText(MainActivity.this, "enter name", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        database.child(editText.getText().toString()).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(MainActivity.this, "تم انشاء المجلد بنجاح", Toast.LENGTH_SHORT).show();
                                dialogBuilder.dismiss();
                                getSupportFragmentManager().beginTransaction().replace(R.id.container,home).commit();


                            }

                        });

                    }


                });


                dialogBuilder.setView(dialogView);
                dialogBuilder.show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

}