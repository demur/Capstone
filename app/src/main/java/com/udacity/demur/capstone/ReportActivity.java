package com.udacity.demur.capstone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.udacity.demur.capstone.database.FirebaseReport;
import com.udacity.demur.capstone.databinding.ActivityReportBinding;

import java.io.IOException;

public class ReportActivity extends AppCompatActivity {
    ActivityReportBinding mReportBinding;

    private Uri selectedImageUri;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReportsDBRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mReportsStorageRef;
    private FirebaseUser mUser;

    private static final int RC_PHOTO_PICKER = 407;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReportBinding = DataBindingUtil.setContentView(this, R.layout.activity_report);
        setSupportActionBar(mReportBinding.toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (null != actionbar) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        mReportsDBRef = mFirebaseDatabase.getReference().child("reports");
        mReportsStorageRef = mFirebaseStorage.getReference().child("reports");

        mReportBinding.btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.chooser_title)), RC_PHOTO_PICKER);
            }
        });

        mReportBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReport();
            }
        });
    }

    private void submitReport() {
        if (null != selectedImageUri) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.progress_dialog_uploading));
            progressDialog.show();

            final StorageReference photoRef = mReportsStorageRef.child(selectedImageUri.getLastPathSegment());
            photoRef.putFile(selectedImageUri).addOnProgressListener(this, new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                            .getTotalByteCount());
                    progressDialog.setMessage(getString(R.string.progress_dialog_update, (int) progress));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ReportActivity.this, getString(R.string.report_submit_error, e.getMessage()), Toast.LENGTH_LONG).show();
                }
            }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return photoRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        FirebaseReport firebaseReport = new FirebaseReport((null != mUser ? mUser.getUid() : null), mReportBinding.etDesc.getText().toString(), downloadUri.toString());
                        mReportsDBRef.push().setValue(firebaseReport);
                        Toast.makeText(getApplicationContext(), R.string.report_submit_success, Toast.LENGTH_LONG).show();
                        ReportActivity.this.finish();
                    } else {
                        Toast.makeText(ReportActivity.this, R.string.report_submit_failed, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            if (null != selectedImageUri) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    mReportBinding.ivPreview.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mReportBinding.btnSubmit.setEnabled(true);
            }

        } else {
            mReportBinding.btnSubmit.setEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUser = mFirebaseAuth.getCurrentUser();
        if (null == mUser) {
            mFirebaseAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mUser = mFirebaseAuth.getCurrentUser();
                            }
                        }
                    });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}