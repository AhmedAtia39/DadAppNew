package net.senior.dadapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
@RuntimePermissions
public class Temp extends AppCompatActivity {
    TextView uploadeDone;
    RecyclerView rec;
    Model model;
    private UploadTask uploadTask;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    AlertDialog.Builder alert;
    List<Model> files;
    ModelAdapter modelAdapter;
    Model memory;

    private String photo;
    EditText searchName;
    public Button save;
    View alertView;
    DatabaseReference database;
    private SearchView searchView;
    ProgressBar progressBar;
    SharedPref sharedPref;
    ImageView imageView;
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        rec = findViewById(R.id.rec);
//        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.img);
        files = new ArrayList();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        model = new Model();
        sharedPref = new SharedPref(this);

        database = FirebaseDatabase.getInstance().getReference().child("memories");
        files = new ArrayList();
        database.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                files.clear();

                for (DataSnapshot d : snapshot.getChildren()) {
                    database.child(d.getValue(Model.class).getId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            files.add(snapshot.getValue(Model.class));
//                            modelAdapter = new ModelAdapter(files);
                            modelAdapter.setOnImgClicked(new ModelAdapter.OnImgClicked() {
                                @Override
                                public void onImgClicked(int pos) {
//                                    if (files.get(pos).getImg().contains(".jpg") || files.get(pos).getImg().contains(".png")) {
////                                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
////                                        shareIntent.setType("image/*");
////                                        shareIntent.putExtra(Intent.EXTRA_STREAM, files.get(pos).getImg());
////                                        startActivity(shareIntent);
////                                        progressBar.setVisibility(View.GONE);
//
//                                    } else {

//                                    MainActivityPermissionsDispatcher.picassoWithPermissionCheck(Temp.this, files.get(pos).getImg() + ".jpg");
//                                    }
                                }
                            });

                            rec.setLayoutManager(new LinearLayoutManager(Temp.this));
                            rec.setAdapter(modelAdapter);
//                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

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
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {


                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }


//        databaseReference.child("Files").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                 for(DataSnapshot d:dataSnapshot.getChildren()) {
//                    files.add(d.getValue(Model.class));
//                }
//                modelAdapter = new ModelAdapter(files);
//                rec.setLayoutManager(new LinearLayoutManager(Temp.this));
//                rec.setAdapter( modelAdapter);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//
//                // Failed to read value
//                Toast.makeText(Temp.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void picasso(String ImageUrl) {
//        progressBar.setVisibility(View.VISIBLE);
        removePreviousDownloadedImage();
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.browse, null);
        Picasso.get().load(ImageUrl).into(new Target() {
                                              @Override
                                              public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                  try {
                                                      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new ByteArrayOutputStream());
                                                      String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
                                                      uri = Uri.parse(path);
//
//                                                      Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                                                      shareIntent.setType("image/*");
//                                                      shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
//                                                      startActivity(shareIntent);
//
//                                                      progressBar.setVisibility(View.GONE);
//
//                                                      sharedPref.setUri(uri.toString());
                                                      AlertDialog builder = new AlertDialog.Builder(Temp.this).create();
//                                                      builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                                                      builder.getWindow().setBackgroundDrawable(
//                                                              new ColorDrawable(android.graphics.Color.TRANSPARENT));
//
//
//                                                      builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                                          @Override
//                                                          public void onDismiss(DialogInterface dialogInterface) {
//                                                              //nothing;
//                                                          }
//                                                      });



                                                      final AlertDialog dialogBuilder = new AlertDialog.Builder(Temp.this).create();
                                                      dialogBuilder.getWindow().setBackgroundDrawable(
                                                              new ColorDrawable(android.graphics.Color.TRANSPARENT));                                                      LayoutInflater inflater = Temp.this.getLayoutInflater();
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
                                                              shareIntent.setAction(Intent.ACTION_GET_CONTENT);

                                                              startActivity(shareIntent);

                                                              progressBar.setVisibility(View.GONE);

                                                              sharedPref.setUri(uri.toString());}});




                                                      dialogBuilder.setView(dialogView);
                                                      dialogBuilder.show();











//                                                      return false;
//                                                  }
//                                              };
                                                  } catch (Exception e) {
                                                      progressBar.setVisibility(View.GONE);
                                                  }
                                              }

                                              @Override
                                              public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                                  progressBar.setVisibility(View.GONE);
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
//                if (modelAdapter != null)
//                    modelAdapter.getFilter().filter(newText);
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
                            Toast.makeText(Temp.this, "enter name", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        database.child(searchName.getText().toString()).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(Temp.this, "تم انشء المجلد بنجاح", Toast.LENGTH_SHORT).show();
                                dialogBuilder.dismiss();


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

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
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
                        Toast.makeText(Temp.this, "enter name", Toast.LENGTH_SHORT).show();
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
                                database.child(memory.getId()).setValue(memory).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(Temp.this, "تم الحفظ بنجاح", Toast.LENGTH_SHORT).show();
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


    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }
    }


//    public void upload(View view) {
//attatch();    }
//
//
//    public void attatch() {
//        String options[] = {"Image", "PDF File", "Word File"};
//        final AlertDialog.Builder builder = new AlertDialog.Builder(Temp.this);
//
//        builder.setItems(options, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (which == 0) {
//                    check = "img";
//                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON_TOUCH).setAspectRatio(1, 1).start(Temp.this);
//
//                }
//                if (which == 1) {
//                    check = "pdf";
//                    Intent i = new Intent();
//                    i.setType("application/pdf");
//                    i.setAction("android.intent.action.GET_CONTENT");
//                    startActivityForResult(i, 384);
//                }
//                if (which == 2) {
//                    check = "doc";
//                    Intent i = new Intent();
//                    i.setType("application/msword");
//                    i.setAction("android.intent.action.GET_CONTENT");
//
//                    startActivityForResult(Intent.createChooser(i, "select doc"), 384);
//
//
//                }
//
//            }
//
//        });
//        builder.create();
//        builder.show();
//
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 13 && resultCode == RESULT_OK) {
//            Bitmap inImage = (Bitmap) data.getExtras().get("data");
//            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//            String path = MediaStore.Images.Media.insertImage(Temp.this.getContentResolver(), inImage, "Title", null);
//            photopath = Uri.parse(path);
//            uploadImage();
//        }
//
//
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
//            if (check.equals("img")) {
//                CropImage.ActivityResult result = CropImage.getActivityResult(data);
//                photopath = result.getUri();
//
//                myUrl = photopath.toString() + ".jpg";
//                file_url = myUrl;
//                model.setPath(myUrl);
//                AlertDialog.Builder alert = new AlertDialog.Builder(Temp.this,R.style.MyDialogTheme);
//                View alertView = getLayoutInflater().inflate(R.layout.dialog, null);
//                final EditText name = alertView.findViewById(R.id.name);
//                final Button save = alertView.findViewById(R.id.save);
//                save.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        model.setText(name.getText().toString());
//                        uploadImage();
//                        saveFile();
//
//                    }
//                });
//                final AlertDialog alertDialog = alert.show();
//                alertDialog.setCancelable(true);
//
//                alertDialog.setCanceledOnTouchOutside(true);
//
//
//                uploadeDone.setText("donee");
////                SaveMessageInfoToDatabase();
////alertDialog.dismiss();
//            }
//        }
//        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
//            List<String> results = data.getStringArrayListExtra(
//                    RecognizerIntent.EXTRA_RESULTS);
//            String spokenText = results.get(0);
//            file_url = spokenText;
////            SaveMessageInfoToDatabase();
//        }
//
//
//        if (requestCode == 384 && resultCode == RESULT_OK && data != null) {
//            if (check.equals("pdf")) {
//                pdfUri = data.getData();
//                final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("files pdf").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(pdfUri.getLastPathSegment());
//                uploadTask = storageReference.putFile(pdfUri);
//
//                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                    public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
//
//                        return storageReference.getDownloadUrl();
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    public void onComplete(Task<Uri> task) {
//                        if (task.isSuccessful()) {
//                            Uri file_uri = task.getResult();
//                            file_url = file_uri + toString() + ".pdf";
//                            if (file_url != null) {
//
//
//                                Cursor returnCursor =
//                                        getContentResolver().query(pdfUri, null, null, null, null);
//                                /*
//                                 * Get the column indexes of the data in the Cursor,
//                                 * move to the first row in the Cursor, get the data,
//                                 * and display it.
//                                 */
//                                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
//                                returnCursor.moveToFirst();
////                                SaveMessageInfoToDatabase();
//
//                            }
//                            return;
//                        }
//                    }
//                });
//
//            } else {
//                pdfUri = data.getData();
//                final StorageReference storage = FirebaseStorage.getInstance().getReference().child("files doc").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(pdfUri.getLastPathSegment());
//                uploadTask = storage.putFile(pdfUri);
//
//                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                    public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
//
//                        return storage.getDownloadUrl();
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    public void onComplete(Task<Uri> task) {
//                        if (task.isSuccessful()) {
//                            Uri file_uri = task.getResult();
//                            file_url = file_uri + toString() + ".doc";
//                            if (file_url != null) {
//                                Cursor returnCursor =
//                                        getContentResolver().query(pdfUri, null, null, null, null);
//                                /*
//                                 * Get the column indexes of the data in the Cursor,
//                                 * move to the first row in the Cursor, get the data,
//                                 * and display it.
//                                 */
//                                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
//                                returnCursor.moveToFirst();
////                                SaveMessageInfoToDatabase();
////                                uploadeDone.setText("File uploaded successfully");
//
//                            }
//                            return;
//                        }
//                    }
//                });
//
//            }
//        }
//
//    }
//
//    private void saveFile() {
//                String msgKey = databaseReference.child("Files").push().getKey();
//        databaseReference.child("Files").child(msgKey).setValue(model);
//
//    }
//
//    private void uploadImage() {
//        UploadTask uploadTask;
//        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + photopath.getLastPathSegment());
//        uploadTask = storageReference.putFile(photopath);
//
//        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//            @Override
//            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                return storageReference.getDownloadUrl();
//            }
//        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                if (task.isSuccessful()) {
//                    Uri image_uri = task.getResult();
//                    file_url = image_uri.toString() + ".jpg";
//                    model.setImg(file_url);
//                    uploadeDone.setText("File uploaded successfully");
//
////                    SaveMessageInfoToDatabase();
//
//                }
//            }
//        });
//    }
//    private void SaveMessageInfoToDatabase() {
//        String msgKey = databaseReference.child("Files").push().getKey();
//        if (file_url == null ) {
//            model = new Model(msg, name, myimg, getuID(), getIntent().getStringExtra("to"));
//        } else {
//            chatModel = new ChatModel(msg, name, myimg, getuID(), getIntent().getStringExtra("to"), file_url);
//        }
//
//        databaseReference.child("Chats").child(msgKey).setValue(chatModel);
//
//
//        databaseReference.child("Recent Chats").child(getuID()).child(getIntent().getStringExtra("to")).setValue(chatModel);
//
//        chat_field.setText("");
//        rec.scrollToPosition(chatList.size() - 1);
//        NotificationCompat.Builder builder = null;
//
//        Intent intent = new Intent(
//                LiveChat.this, LiveChat.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent =
//                PendingIntent.getActivity(LiveChat.this,
//                        0, intent, 0);
//        builder = new NotificationCompat.Builder(
//                LiveChat.this, "id1")
//                .setSmallIcon(android.R.drawable.ic_notification_overlay)
//                .setContentTitle("My notification")
//                .setContentText("message sent")
//                .setContentIntent(pendingIntent)
//                .setPriority(NotificationCompat.PRIORITY_HIGH);
//
//
//        Uri soundUri = RingtoneManager.
//                getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        builder.setSound(soundUri);
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(LiveChat.this);
//        notificationManager.notify(1, builder.build());
//    }


//    private void showFileChooser() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        try {
//            startActivityForResult(
//                    Intent.createChooser(intent, "Select a File to Upload"),
//                    1);
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(Temp.this, "Please install a File Manager.",
//                    Toast.LENGTH_SHORT).show();
//        }
//    }

//
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//
//            if (data != null && data.getData() != null && resultCode == Temp.RESULT_OK) {
//
//                boolean isImageFromGoogleDrive = false;
//
//                Uri uri = data.getData();
//
//                if (DocumentsContract.isDocumentUri(Temp.this, uri)) {
//                    if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
//                        String docId = DocumentsContract.getDocumentId(uri);
//                        String[] split = docId.split(":");
//                        String type = split[0];
//
//                        if ("primary".equalsIgnoreCase(type)) {
//                            realPath_1 = Environment.getExternalStorageDirectory() + "/" + split[1];
//                        } else {
//                            Pattern DIR_SEPORATOR = Pattern.compile("/");
//                            Set<String> rv = new HashSet<>();
//                            String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
//                            String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
//                            String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
//                            if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
//                                if (TextUtils.isEmpty(rawExternalStorage)) {
//                                    rv.add("/storage/sdcard0");
//                                } else {
//                                    rv.add(rawExternalStorage);
//                                }
//                            } else {
//                                String rawUserId;
//                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                                    rawUserId = "";
//                                } else {
//                                    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
//                                    String[] folders = DIR_SEPORATOR.split(path);
//                                    String lastFolder = folders[folders.length - 1];
//                                    boolean isDigit = false;
//                                    try {
//                                        Integer.valueOf(lastFolder);
//                                        isDigit = true;
//                                    } catch (NumberFormatException ignored) {
//                                    }
//                                    rawUserId = isDigit ? lastFolder : "";
//                                }
//                                if (TextUtils.isEmpty(rawUserId)) {
//                                    rv.add(rawEmulatedStorageTarget);
//                                } else {
//                                    rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
//                                }
//                            }
//                            if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
//                                String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
//                                Collections.addAll(rv, rawSecondaryStorages);
//                            }
//                            String[] temp = rv.toArray(new String[rv.size()]);
//                            for (int i = 0; i < temp.length; i++) {
//                                File tempf = new File(temp[i] + "/" + split[1]);
//                                if (tempf.exists()) {
//                                    realPath_1 = temp[i] + "/" + split[1];
//                                }
//                            }
//                        }
//                    } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
//                        String id = DocumentsContract.getDocumentId(uri);
//                        Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
//
//                        Cursor cursor = null;
//                        String column = "_data";
//                        String[] projection = {column};
//                        try {
//                            cursor = getContentResolver().query(contentUri, projection, null, null,
//                                    null);
//                            if (cursor != null && cursor.moveToFirst()) {
//                                int column_index = cursor.getColumnIndexOrThrow(column);
//                                realPath_1 = cursor.getString(column_index);
//                            }
//                        } finally {
//                            if (cursor != null)
//                                cursor.close();
//                        }
//                    } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
//                        String docId = DocumentsContract.getDocumentId(uri);
//                        String[] split = docId.split(":");
//                        String type = split[0];
//
//                        Uri contentUri = null;
//                        if ("image".equals(type)) {
//                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                        } else if ("video".equals(type)) {
//                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                        } else if ("audio".equals(type)) {
//                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                        }
//
//                        String selection = "_id=?";
//                        String[] selectionArgs = new String[]{split[1]};
//
//                        Cursor cursor = null;
//                        String column = "_data";
//                        String[] projection = {column};
//
//                        try {
//                            cursor = getContentResolver().query(contentUri, projection, selection, selectionArgs, null);
//                            if (cursor != null && cursor.moveToFirst()) {
//                                int column_index = cursor.getColumnIndexOrThrow(column);
//                                realPath_1 = cursor.getString(column_index);
//                            }
//                        } finally {
//                            if (cursor != null)
//                                cursor.close();
//                        }
//                    } else if ("com.google.android.apps.docs.storage".equals(uri.getAuthority())) {
//                        isImageFromGoogleDrive = true;
//                    }
//                } else if ("content".equalsIgnoreCase(uri.getScheme())) {
//                    Cursor cursor = null;
//                    String column = "_data";
//                    String[] projection = {column};
//
//                    try {
//                        cursor = getContentResolver().query(uri, projection, null, null, null);
//                        if (cursor != null && cursor.moveToFirst()) {
//                            int column_index = cursor.getColumnIndexOrThrow(column);
//                            realPath_1 = cursor.getString(column_index);
//                        }
//                    } finally {
//                        if (cursor != null)
//                            cursor.close();
//                    }
//                } else if ("file".equalsIgnoreCase(uri.getScheme())) {
//                    realPath_1 = uri.getPath();
//                }
//
//                try {
//                    file_1 = realPath_1.substring(realPath_1.lastIndexOf('/') + 1, realPath_1.length());
//                    Log.i("File Name 1 ", file_1);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        MainActivityPermissionsDispatcher.onRequestPermissionsResult(Temp.this, requestCode, grantResults);

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

}

