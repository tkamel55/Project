package com.example.kamel.project;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kamel.project.Model.AdminQuestionModel;
import com.example.kamel.project.Adapter.AdminQuestionsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;

public class AdminQuestionActivity extends AppCompatActivity {
    public static List<AdminQuestionModel> list;
    private Button add,excelbtn;
    private RecyclerView recyclerView;
    private AdminQuestionsAdapter adapter;
    private Dialog loadingDialog;
    private DatabaseReference ref;
    public static final int CELL_COUNT=6;
    private int set;
   // TextView loadingText;
    String categoryName;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_question);


        ref = FirebaseDatabase.getInstance().getReference();

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        categoryName = getIntent().getStringExtra("category");
        set = getIntent().getIntExtra("setNo", 1);
        getSupportActionBar().setTitle(categoryName + "/set " + set);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        excelbtn = findViewById(R.id.excel_btn);

        add = findViewById(R.id.add_btn);
        recyclerView = findViewById(R.id.recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();
        adapter = new AdminQuestionsAdapter(list, categoryName, new AdminQuestionsAdapter.DeleteListener() {
            @Override
            public void onLongClick(final int position, final String id) {

                new AlertDialog.Builder(AdminQuestionActivity.this, R.style.DefaultAlertDialogTheme).setTitle("Delete Question")
                        .setMessage("Are you sure , you want to delete this Question").setPositiveButton("Delete"
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadingDialog.show();

                                ref.child("SETS").child(categoryName).child("questions").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            list.remove(position);
                                            adapter.notifyItemRemoved(position);

                                        } else {
                                            Toast.makeText(AdminQuestionActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();


                                        }
                                        loadingDialog.dismiss();
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", null)
                        .setIcon(android.R.drawable.ic_dialog_alert).show();


            }
        });
        recyclerView.setAdapter(adapter);


        getData(categoryName, set);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addquestion = new Intent(AdminQuestionActivity.this, addquestion.class);
                addquestion.putExtra("categoryName", categoryName);
                addquestion.putExtra("setNo", set);
                startActivity(addquestion);

            }
        });

        excelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(AdminQuestionActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    selectFile();
                }else {
                    ActivityCompat.requestPermissions(AdminQuestionActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},101);
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==101)
        {
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                selectFile();
            }else
            {
                Toast.makeText(getApplicationContext(),"please grant permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void selectFile()
    {

        String[] mimeTypes =
                {"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent,"select file"),102);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCode  && resultCode == RESULT_OK && null != data)
        {
            String FileName = data.getData().getLastPathSegment();
            String FilePath = data.getData().getPath();
            if(FilePath.endsWith(".xlsx"))
            {
                Toast.makeText(getApplicationContext(),"file was selected",Toast.LENGTH_LONG).show();
                readFile(data.getData());
            }else
            {
                Toast.makeText(getApplicationContext(),"file is not match",Toast.LENGTH_LONG).show();

            }

        }
    }

    private void readFile(final Uri fileUri)
    {
     //   loadingText.setText("Scaning questions");
        loadingDialog.show();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {


                //parent map
                final HashMap<String ,Object>parentMap=new HashMap<>();
                final List<AdminQuestionModel>tempuraryList=new ArrayList<>();
                try {
                    InputStream inputStream=getContentResolver().openInputStream(fileUri);
                    XSSFWorkbook workbook=new XSSFWorkbook(inputStream);
                    XSSFSheet sheet=workbook.getSheetAt(0);
                    FormulaEvaluator formulaEvaluator=workbook.getCreationHelper().createFormulaEvaluator();

                    int rowsCount=sheet.getPhysicalNumberOfRows();
                    if (rowsCount>0)
                    {

                        for (int r=0;r<rowsCount;r++)
                        {
                            Row row=sheet.getRow(r);

                            if (row.getPhysicalNumberOfCells()==CELL_COUNT)
                            {
                                String question=getcellData(row,0,formulaEvaluator);
                                String a=getcellData(row,1,formulaEvaluator);
                                String b=getcellData(row,2,formulaEvaluator);
                                String c=getcellData(row,3,formulaEvaluator);
                                String d=getcellData(row,4,formulaEvaluator);
                                String correctAns=getcellData(row,5,formulaEvaluator);
                                if (correctAns.equals(a)||correctAns.equals(b)||correctAns.equals(c)||correctAns.equals(d))
                                {
                                    HashMap<String ,Object>questionMap=new HashMap<>();
                                    questionMap.put("correctAns",correctAns);
                                    questionMap.put("optionA",a);
                                    questionMap.put("optionB",b);
                                    questionMap.put("optionC",c);
                                    questionMap.put("optionD",d);
                                    questionMap.put("questions",question);
                                    questionMap.put("setNo",set);

                                    //rando id for firebase where question ar uploaded
                                    String id= UUID.randomUUID().toString();
                                    parentMap.put(id,questionMap);
                                    tempuraryList.add(new AdminQuestionModel(id,question,a,b,c,d,correctAns,set));


                                }else
                                {
                                    final int finalR = r;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                           // loadingText.setText("Loading...");
                                         //   loadingDialog.dismiss();
                                            Toast.makeText(getApplicationContext(),"Row number" +(finalR +1)+ "has no correct option",Toast.LENGTH_LONG).show();
                                        }
                                    });

                                    return;
                                }
                            }else
                            {
                                final int finalR1 = r;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                  //      loadingText.setText("Loading...");
                                   //     loadingDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"Row number" +(finalR1 +1)+ "has incorrect data",Toast.LENGTH_LONG).show();
                                    }
                                });
                                return;
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                             //   loadingText.setText("Uploading...");
                                //queary
                                FirebaseDatabase.getInstance().getReference()
                                        .child("SETS").child(categoryName)
                                        .child("questions").updateChildren(parentMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    list.addAll(tempuraryList);
                                                    adapter.notifyDataSetChanged();
                                                }else
                                                {
                                                 //   loadingText.setText("Loading...");
                                                 //   loadingDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(),"Someting is wrong",Toast.LENGTH_LONG).show();
                                                }

                                                loadingDialog.dismiss();
                                            }
                                        });
                            }
                        });

                    }else
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                             //   loadingText.setText("Loading...");
                             //   loadingDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"file is empty",Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }

                } catch (final FileNotFoundException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                          //  loadingText.setText("Loading...");
                          //  loadingDialog.dismiss();
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });


                } catch (final IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           // loadingText.setText("Loading...");
                         //   loadingDialog.dismiss();
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });

    }

    private String  getcellData(Row row,int cellPosition,FormulaEvaluator formulaEvaluator)
    {
        String value="";
        Cell cell=row.getCell(cellPosition);
        switch (cell.getCellType())
        {
            case CELL_TYPE_BOOLEAN:
                return value+cell.getBooleanCellValue();
            case CELL_TYPE_NUMERIC:
                return value+cell.getNumericCellValue();
            case CELL_TYPE_STRING:
                return value+cell.getStringCellValue();
            default:
                return value;
        }

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData(String categoryname, final int setno)
    {

        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference()
                .child("SETS").child(categoryname).child("questions").orderByChild("setNo").equalTo(setno)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                        {
                            String id=dataSnapshot1.getKey();
                            String question=dataSnapshot1.child("questions").getValue().toString();
                            String a=dataSnapshot1.child("optionA").getValue().toString();
                            String b=dataSnapshot1.child("optionB").getValue().toString();
                            String c=dataSnapshot1.child("optionC").getValue().toString();
                            String d=dataSnapshot1.child("optionD").getValue().toString();
                            String correctAns=dataSnapshot1.child("correctAns").getValue().toString();

                            list.add(new AdminQuestionModel(id,question,a,b,c,d,correctAns,setno));
                        }
                        loadingDialog.dismiss();
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"something worng",Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    //when data upload in server refresh the recyclerview
    @Override
    protected void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }

}
