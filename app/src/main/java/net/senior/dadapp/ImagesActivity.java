package net.senior.dadapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;

public class ImagesActivity extends AppCompatActivity {
    List<String> imgs;
    RecyclerView rec;
    ModelAdapter modelAdapter;
    private Uri uri;
    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        rec = findViewById(R.id.rec);
        imgs = new ArrayList();
        sharedPref = new SharedPref(this);

        rec.setLayoutManager(new GridLayoutManager(this, 2));
        FirebaseDatabase.getInstance().getReference().child("Memories").child(getIntent().getStringExtra("name")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

for(DataSnapshot d:snapshot.getChildren()) {
    Model m=d.getValue(Model.class);



        imgs.add(m.getImg());
    modelAdapter = new ModelAdapter(imgs);
    modelAdapter.setOnImgClicked(new ModelAdapter.OnImgClicked() {
        @Override
        public void onImgClicked(int pos) {
            picasso(imgs.get(pos));
        }
    });
    rec.setAdapter(modelAdapter);}
}



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void picasso(String ImageUrl) {
//        progressBar.setVisibility(View.VISIBLE);
        removePreviousDownloadedImage();
//        LayoutInflater inflater = this.getLayoutInflater();
//        final View dialogView = inflater.inflate(R.layout.browse, null);
        Picasso.get().load(ImageUrl).into(new Target() {
                                              @Override
                                              public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                  try {
                                                      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new ByteArrayOutputStream());
                                                      String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
                                                      uri = Uri.parse(path);


                                                      final AlertDialog dialogBuilder = new AlertDialog.Builder(ImagesActivity.this).create();
                                                      dialogBuilder.getWindow().setBackgroundDrawable(
                                                              new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                                      dialogBuilder.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                                                      dialogBuilder.getWindow().getAttributes().gravity = Gravity.TOP;
                                                      LayoutInflater inflater = ImagesActivity.this.getLayoutInflater();
                                                      View dialogView = inflater.inflate(R.layout.browse, null);
                                                      ImageView imageView = dialogView.findViewById(R.id.img);
                                                      Button button1 = dialogView.findViewById(R.id.share);
                                                      imageView.setImageURI(uri);
                                                      button1.setOnClickListener(new View.OnClickListener() {
                                                          @Override
                                                          public void onClick(View view) {
                                                              Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                                              shareIntent.setType("image/*");
                                                              shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                                              shareIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                                                              shareIntent.setAction(Intent.ACTION_GET_CONTENT);

                                                              startActivity(Intent.createChooser(shareIntent, "Share Image"));


                                                              sharedPref.setUri(uri.toString());
                                                          }
                                                      });


                                                      dialogBuilder.setView(dialogView);
                                                      dialogBuilder.show();

                                                  } catch (Exception e) {
                                                  }
                                              }

                                              @Override
                                              public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                              }

                                              @Override
                                              public void onPrepareLoad(Drawable placeHolderDrawable) {
                                              }

                                          }
        );
    }

    private void removePreviousDownloadedImage() {
        try {
            if (sharedPref.getUri() != null) {
                File file = new File(getRealPathFromURI(Uri.parse(sharedPref.getUri())));
                if (file.exists())
                    getContentResolver().delete(Uri.parse(sharedPref.getUri()), null, null);
            }
        } catch (Exception e) {
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filter, menu);

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
                            Toast.makeText(ImagesActivity.this, "enter name", Toast.LENGTH_SHORT).show();
                            return;
                        }

                                  dialogBuilder.dismiss();


                            }




                });


                dialogBuilder.setView(dialogView);
                dialogBuilder.show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

}