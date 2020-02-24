package com.example.vnrplacements;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShowCategories extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseStorage storage;
    private ChildEventListener mChildEventListener;
    private StorageReference storageReference;
    ArrayList<String> list = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    Button go;
    String item,resumeurl;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_categories);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

            /* perform your actions here*/


        } else {
            signInAnonymously();
        }
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        TextView scroll=(TextView)findViewById(R.id.scroll);
        scroll.setSelected(true);
        StorageReference ref = storageReference.child("sampleresume.pdf");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                resumeurl=uri.toString();
            }
        });
        Button alumniview=(Button) findViewById(R.id.alumniview);
        Button alumnidownload=(Button) findViewById(R.id.alumnidownload);
        Button listofcompanyview=(Button) findViewById(R.id.companyview);
        Button listofcompanydownload=(Button) findViewById(R.id.companydownload);
        Button resumeview=(Button) findViewById(R.id.resumeview);
        Button resumedownload=(Button) findViewById(R.id.resumedownload);
        alumnidownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference ref = storageReference.child("AlumniDetails.xlsx");
                download(ref,"AlumniDetails.xlsx",1);
            }
        });
        alumniview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference ref = storageReference.child("AlumniDetails.xlsx");
                view(ref,"AlumniDetails.xlsx",1);
            }
        });
        listofcompanyview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference ref = storageReference.child("listofcompanies.xlsx");
                view(ref,"listofcompanies.xlsx",2);
            }
        });
        listofcompanydownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference ref = storageReference.child("listofcompanies.xlsx");
                download(ref,"listofcompanies.xlsx",2);
            }
        });
        resumeview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resumeurl!=null)
                {
                    String pdfUrl = Uri.encode(resumeurl);
                    String url = "http://docs.google.com/gview?url=" + pdfUrl + "&embedded=true";
                    WebView webView = (WebView)findViewById(R.id.dontirritate);
                    webView.getSettings().setSupportZoom(true);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadUrl(url);
                }
                else
                {
                    Toast.makeText(ShowCategories.this,"File not found!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        resumedownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAppPermissions();
                if (!(hasReadPermissions() && hasWritePermissions())) {
                    Toast.makeText(ShowCategories.this,"Grant Access to external storage",Toast.LENGTH_SHORT).show();
                }
                StorageReference ref = storageReference.child("sampleresume.pdf");
                download(ref,"sampleresume.pdf",3);
            }
        });
        final List<String> categories = new ArrayList<String>();
        categories.add("Select");
        categories.add("Core");
        categories.add("Software and Service");
        categories.add("Software and Product");

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(0);
        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        go=(Button) findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item!=categories.get(0))
                {
                    Intent i=new Intent(ShowCategories.this,ShowCompany.class);
                    i.putExtra("Filter",item);
                    startActivity(i);
                    Toast.makeText(ShowCategories.this, "Selected Category: " + item, Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(ShowCategories.this,"Select a valid field",Toast.LENGTH_SHORT).show();
                }
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
    public void download(StorageReference ref, String filename, final int flag)
    {
        requestAppPermissions();
        if (!(hasReadPermissions() && hasWritePermissions())) {
            Toast.makeText(ShowCategories.this,"Grant Access to external storage",Toast.LENGTH_SHORT).show();
        }
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Placements/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Downloading...");
        progressDialog.show();
        File fileNameOnDevice = new File(directory_path+"/"+filename);
        ref.getFile(fileNameOnDevice).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                if(flag==1 || flag==2)
                {
                    Toast.makeText(ShowCategories.this,"File has been Downloaded\nClick on view now",Toast.LENGTH_SHORT).show();
                }
                else if(flag==3)
                {
                    Toast.makeText(ShowCategories.this,"File has been Downloaded",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ShowCategories.this, "Download Failed! Retry again", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
            }
        });
    }
    public void view(StorageReference ref,String filename,int flag)
    {
        requestAppPermissions();
        if (!(hasReadPermissions() && hasWritePermissions())) {
            Toast.makeText(ShowCategories.this,"Grant Access to external storage",Toast.LENGTH_SHORT).show();
        }
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Placements/";
        File pdfFile = new File(directory_path+"/"+filename);
        if (pdfFile.exists()) {
            Uri excelPath;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                excelPath = FileProvider.getUriForFile(ShowCategories.this, "com.example.vnrplacements", pdfFile);
            } else {
                excelPath = Uri.fromFile(pdfFile);
            }
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(excelPath, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            try {
                startActivity(pdfIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(ShowCategories.this, "No Application available to view Excel\nInstall Excel Reader", Toast.LENGTH_SHORT).show();
            }
        } else {
            download(ref,filename,flag);
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
