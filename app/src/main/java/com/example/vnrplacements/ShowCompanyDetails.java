package com.example.vnrplacements;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class ShowCompanyDetails extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCompanyDatabaseReference, mFilterDatabaseReference;
    private FirebaseStorage storage;
    private ChildEventListener mChildEventListener;
    private StorageReference storageReference;
    TextView cname, jd, ec;
    Button downid, downiq, piq, pid;
    String Detailsurl, Questionsurl;
    FirebaseAuth mAuth;
    String iq,id,FilenameOnDevice;

    HashMap<String,String> jdhm=new HashMap<>();
    HashMap<String,String> echm=new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_company_details);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

            /* perform your actions here*/


        } else {
            signInAnonymously();
        }
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCompanyDatabaseReference = mFirebaseDatabase.getReference().child("Company");
        mFilterDatabaseReference = mFirebaseDatabase.getReference().child("Filter");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        requestAppPermissions();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        cname = (TextView) findViewById(R.id.CompanyName);
        cname.setPaintFlags(cname.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        final TextView jdhead = (TextView) findViewById(R.id.JobDescription);
        final TextView echead = (TextView) findViewById(R.id.EligibityCriteria);

        TextView scrollanother = (TextView) findViewById(R.id.scrollanother);
        scrollanother.setSelected(true);
        jd = (TextView) findViewById(R.id.JobDescriptionDetails);
        ec = (TextView) findViewById(R.id.EligibityCriteriaDetails);
        downid = (Button) findViewById(R.id.downloadinterviewdetails);
        downiq = (Button) findViewById(R.id.downloadinterviewquestions);
        pid = (Button) findViewById(R.id.previewinterviewdetails);
        piq = (Button) findViewById(R.id.previewinterviewquestions);
        Intent intent = getIntent();
        final String companyName = intent.getStringExtra("companyName");
        iq=companyName+"_questions.pdf";
        id=companyName+"_details.pdf";
        StorageReference anotherref = storageReference.child("details").child(id);
        anotherref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Detailsurl = uri.toString();
            }
        });
        StorageReference ref = storageReference.child("questions").child(iq);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Questionsurl = uri.toString();
            }
        });
        Query query = mCompanyDatabaseReference.child(companyName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CompanyDetails cd = dataSnapshot.getValue(CompanyDetails.class);
                cname.setText(cd.companyName);
                jdhm=cd.jd;
                echm=cd.ec;
                if (jdhm!=null ) {
                    if (!jdhm.isEmpty()) {
                        for (Map.Entry mapElement : jdhm.entrySet()) {
                            String key = (String) mapElement.getKey();
                            String value = (String) mapElement.getValue();
                            jd.setText(jd.getText() + key + " : " + value + "\n");
                        }
                    }
                } else {
                    jdhead.setVisibility(View.GONE);
                }
                if (echm!=null) {
                    if (!echm.isEmpty()) {
                        for (Map.Entry mapElement : echm.entrySet()) {
                            String key = (String) mapElement.getKey();
                            String value = (String) mapElement.getValue();
                            ec.setText(ec.getText() + key + " : " + value + "\n");
                        }
                    }
                } else {
                    echead.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        pid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Detailsurl != null) {
                    String pdfUrl = Uri.encode(Detailsurl);
                    String url = "http://docs.google.com/gview?url=" + pdfUrl + "&embedded=true";
                    WebView webView = (WebView) findViewById(R.id.dontirritate);
                    webView.getSettings().setSupportZoom(true);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadUrl(url);
                } else {
                    Toast.makeText(ShowCompanyDetails.this, "File not found!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        piq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Questionsurl != null) {
                    String pdfUrl = Uri.encode(Questionsurl);
                    String url = "http://docs.google.com/gview?url=" + pdfUrl + "&embedded=true";
                    WebView webView = (WebView) findViewById(R.id.dontirritate);
                    webView.getSettings().setSupportZoom(true);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadUrl(url);
                } else {
                    Toast.makeText(ShowCompanyDetails.this, "File not found!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        downid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAppPermissions();
                if (!(hasReadPermissions() && hasWritePermissions())) {
                    Toast.makeText(ShowCompanyDetails.this, "Grant Access to external storage", Toast.LENGTH_SHORT).show();
                }
                StorageReference ref = storageReference.child("details").child(id);
                String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Placements/"+companyName+"/";
                FilenameOnDevice=companyName+"_Interview_Details.pdf";
                download(ref, directory_path, FilenameOnDevice);

            }
        });
        downiq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAppPermissions();
                if (!(hasReadPermissions() && hasWritePermissions())) {
                    Toast.makeText(ShowCompanyDetails.this, "Grant Access to external storage", Toast.LENGTH_SHORT).show();
                }
                StorageReference ref =storageReference.child("questions").child(iq);
                String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Placements/"+companyName+"/";
                FilenameOnDevice=companyName+"_Interview_Questions.pdf";
                download(ref, directory_path, FilenameOnDevice);
            }
        });
    }

    private void requestAppPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (hasReadPermissions() && hasWritePermissions()) {
            return;
        }

        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 112); // your request code
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    public void download(StorageReference ref, String directory_path, String Filename) {
        requestAppPermissions();
        if (!(hasReadPermissions() && hasWritePermissions())) {
            Toast.makeText(ShowCompanyDetails.this, "Grant Access to external storage", Toast.LENGTH_SHORT).show();
        }
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Downloading...");
        progressDialog.show();
        File fileNameOnDevice = new File(directory_path + "/" + Filename);
        ref.getFile(fileNameOnDevice).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(ShowCompanyDetails.this, "File has been Downloaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ShowCompanyDetails.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
            }
        });
    }

    public void view(StorageReference ref, String Filename) {
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Placements/";
        File pdfFile = new File(directory_path + "/" + Filename);
        if (pdfFile.exists()) {
            Uri excelPath;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                excelPath = FileProvider.getUriForFile(ShowCompanyDetails.this, "com.example.vnrplacements", pdfFile);
            } else {
                excelPath = Uri.fromFile(pdfFile);
            }
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(excelPath, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            try {
                startActivity(pdfIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(ShowCompanyDetails.this, "No Application available to view Pdf\nInstall Pdf Reader", Toast.LENGTH_SHORT).show();
            }
        } else {
            download(ref, directory_path, Filename);
        }
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                /* perform your actions here*/
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("MainActivity", "signFailed****** ", exception);
                    }
                });
    }
}
