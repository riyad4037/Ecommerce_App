package com.riyadstudio.ecommerceapplication.view.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.riyadstudio.ecommerceapplication.DataRepository.FirebaseAuthRepo;
import com.riyadstudio.ecommerceapplication.R;
import com.riyadstudio.ecommerceapplication.adapter.CategoryAdapter;
import com.riyadstudio.ecommerceapplication.model.Category;

import java.util.ArrayList;
import java.util.List;


public class CategoriesFragment extends Fragment {


    private FloatingActionButton addCategory;
    private RecyclerView categoryRecyclerView;
    private ImageView dialogImageView;
    private EditText altDialogTitle;
    private Uri imageUri;
    private FirebaseStorage firebaseStorage;
    private String TAG = "Category Fragment";
    private int drawableId;
    private FirebaseDatabase firebaseDatabase;
    private CategoryAdapter categoryAdapter;
    private Uri imageUriFromStorage;

    public CategoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseStorage = new FirebaseAuthRepo(getActivity()).getStorageInstance();
        firebaseDatabase = new FirebaseAuthRepo(getActivity()).getFirebaseDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        drawableId = R.drawable.baseline_image_24;
        uriReset(drawableId);

        categoryRecyclerView = view.findViewById(R.id.CategoryPageRecyclerView);
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        categoryAdapter = new CategoryAdapter();
        categoryRecyclerView.setAdapter(categoryAdapter);

        DatabaseReference catRef = firebaseDatabase.getReference().child("Categories");
        catRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Category> categoryList = new ArrayList<>();
                for (DataSnapshot catSnapshot :
                        snapshot.getChildren()) {
                    String c_title = catSnapshot.child("CategoryInfo").child("title").getValue(String.class);
                    String c_imUrl = catSnapshot.child("CategoryInfo").child("imgUrl").getValue(String.class);

                    Category new_Category = new Category(c_title, c_imUrl);
                    categoryList.add(new_Category);
                }
                if (categoryList != null) {
                    categoryAdapter.setCategoryList(categoryList);
                }

                System.out.println("imgUrl0: " + categoryList.get(0).getImgUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });

        categoryAdapter.setOnItemClickListener(new CategoryAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Category singel_category) {
                CategoryItemViewFragment categoryItemViewFragment = new CategoryItemViewFragment(singel_category.getTitle());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.FrameLayoutInMainActivity, categoryItemViewFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        categoryAdapter.setOnItemLongClickListener(new CategoryAdapter.onItemLongClickListener() {
            @Override
            public void onItemLongClick(Category singel_category) {
                AlertDialog.Builder CategoryInfoEdit = new AlertDialog.Builder(getActivity());
                View categoryInfoView = getLayoutInflater().inflate(R.layout.category_info_alert_dialog_design, null);
                CategoryInfoEdit.setView(categoryInfoView);
                AlertDialog categoryEditDialog = CategoryInfoEdit.create();

                categoryInfoView.findViewById(R.id.CategoryInfoEditButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        categoryEditDialog.dismiss();
                        AlertDialog.Builder category_edit_portal = new AlertDialog.Builder(getActivity());
                        View category_edit_portal_view = getLayoutInflater().inflate(R.layout.category_creation_aleart_dialog_design, null);
                        category_edit_portal.setView(category_edit_portal_view);
                        AlertDialog category_edit_dialog = category_edit_portal.create();

                        EditText editText = category_edit_portal_view.findViewById(R.id.alertTitle);
                        ImageView imageView = category_edit_portal_view.findViewById(R.id.DialogImage);
                        Button conformBtn = category_edit_portal_view.findViewById(R.id.alertConformBtn);
                        conformBtn.setText("Update");
                        Glide.with(getActivity()).load(singel_category.getImgUrl()).into(imageView);
                        editText.setText(singel_category.getTitle());

                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    getContent.launch("image/*");
                                } else {
                                    requestStoragePermission();
                                }
                            }
                        });

                        category_edit_portal_view.findViewById(R.id.alertCancelBtn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                category_edit_dialog.dismiss();
                            }
                        });

                        conformBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String cat_title = editText.getText().toString();
                                showConformationDialog(cat_title, imageUri);
                                category_edit_dialog.dismiss();
                            }
                        });

                        category_edit_dialog.show();

                    }
                });

                categoryInfoView.findViewById(R.id.CategoryInfoDeleteButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        categoryEditDialog.dismiss();
                        AlertDialog.Builder deleteConformation = new AlertDialog.Builder(getActivity());
                        View deleteConformationView = getLayoutInflater().inflate(R.layout.make_category_conformation_dialog_design, null);
                        deleteConformation.setView(deleteConformationView);
                        AlertDialog deleteConformationDialog = deleteConformation.create();
                        TextView title = deleteConformationView.findViewById(R.id.CategoryCreateConformationDialogTextView);
                        Button btnCancel = deleteConformationView.findViewById(R.id.CategoryCreateConformationDialogCancelBtn);
                        Button btnConform = deleteConformationView.findViewById(R.id.CategoryCreateConformationDialogConformBtn);
                        btnConform.setText("Delete");
                        title.setText("This will delete all the items and its information.\n Are you sure?");
                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteConformationDialog.dismiss();
                            }
                        });
                        btnConform.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                removeCategoryFromRealTimeDatabase(singel_category);
                                deleteConformationDialog.dismiss();
                            }
                        });

                        deleteConformationDialog.show();

                        Toast.makeText(getActivity(), "This button Clicked!", Toast.LENGTH_SHORT).show();
                    }
                });

                try{
                    categoryEditDialog.show();
                }catch (Exception e){
                    Toast.makeText(getActivity(), "seeing delete conformation msg: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }


            }

        });




        addCategory = view.findViewById(R.id.CategoryPageAddCategoryBtn);

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder createCategoryDialog = new AlertDialog.Builder(getActivity());
                View createCategoryView = getLayoutInflater().inflate(R.layout.category_creation_aleart_dialog_design, null);
                createCategoryDialog.setView(createCategoryView);
                AlertDialog alertDialog = createCategoryDialog.create();
                altDialogTitle = createCategoryView.findViewById(R.id.alertTitle);
                createCategoryView.findViewById(R.id.DialogImage).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogImageView = view.findViewById(R.id.DialogImage);
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            getContent.launch("image/*");
                        } else {
                            requestStoragePermission();
                        }
                    }
                });

                createCategoryView.findViewById(R.id.alertCancelBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                createCategoryView.findViewById(R.id.alertConformBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String cat_title = altDialogTitle.getText().toString();
                        showConformationDialog(cat_title, imageUri);
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });
        return view;
    }

    private void removeCategoryFromRealTimeDatabase(Category singelCategory) {
        Log.d(TAG, "Delete Method Called!!");
        DatabaseReference removeCategoryReference = firebaseDatabase.getReference().child("Categories");
        removeCategoryReference.child(singelCategory.getTitle()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getActivity(), singelCategory.getTitle()+" category deleted successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), singelCategory.getTitle()+" category delete unsuccessful.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uriReset(int drawableId) {
        String uriString = "android.resource://" + getContext().getPackageName() + "/" + drawableId;
        Uri uri = Uri.parse(uriString);
        imageUri = uri;
    }

    private void showConformationDialog(String catTitle, Uri imageUri) {
        AlertDialog.Builder conformationDialog = new AlertDialog.Builder(getActivity());
        View createCategoryConformationView = getLayoutInflater().inflate(R.layout.make_category_conformation_dialog_design, null);
        conformationDialog.setView(createCategoryConformationView);
        AlertDialog conformationAlertDialog = conformationDialog.create();
        createCategoryConformationView.findViewById(R.id.CategoryCreateConformationDialogConformBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeImageInFirebaseStorage(catTitle, imageUri);
                uriReset(drawableId);
                conformationAlertDialog.dismiss();
            }
        });

        createCategoryConformationView.findViewById(R.id.CategoryCreateConformationDialogCancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conformationAlertDialog.dismiss();
            }
        });
        conformationAlertDialog.show();
    }

    private void storeCategoryInRealTimeDatabase(String catTitle, Uri imgUrlFromFirebase) {
        DatabaseReference dataRef = firebaseDatabase.getReference();
        String url = imgUrlFromFirebase.toString();
        if (url.isEmpty()) {
            System.out.println("Image did not inserted.");
        }
        Category newCategory = new Category(catTitle, url);
        dataRef.child("Categories").child(catTitle).child("CategoryInfo").setValue(newCategory).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getActivity(), catTitle + " category created successfully.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Exception: " + e.getLocalizedMessage());
                Toast.makeText(getActivity(), catTitle + " category creation unsuccessful !", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void storeImageInFirebaseStorage(String title, Uri ImageUri) {

        StorageReference imgRef = firebaseStorage.getReference().child("Category").child(title).child("Category_Image").child(title);
        imgRef.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        storeCategoryInRealTimeDatabase(title, uri);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Exception in getting download uri: " + e.getLocalizedMessage());
            }
        });
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 40);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 40 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getContent.launch("image/*");
        } else {
            Toast.makeText(getActivity(), "Permission denied!", Toast.LENGTH_SHORT).show();
        }
    }

    ActivityResultLauncher<String> getContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    imageUri = result;
                    try {
                        Glide.with(getActivity())
                                .load(result)
                                .error(R.drawable.baseline_image_24)
                                .into(dialogImageView);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Exception: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
}