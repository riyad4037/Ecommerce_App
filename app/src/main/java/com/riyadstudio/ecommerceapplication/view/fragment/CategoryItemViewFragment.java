package com.riyadstudio.ecommerceapplication.view.fragment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.riyadstudio.ecommerceapplication.DataRepository.FirebaseAuthRepo;
import com.riyadstudio.ecommerceapplication.R;

import java.util.ArrayList;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

public class CategoryItemViewFragment extends Fragment {

    private String categoryTitle;
    private FirebaseDatabase firebaseDatabase;
    private FloatingActionButton addItemFloatingActionButton;
    private RecyclerView itemsRecyclerView;
    private String TAG = "Create Item View Fragment";

    public CategoryItemViewFragment(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = new FirebaseAuthRepo(getActivity()).getFirebaseDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_item_view, container, false);

        System.out.println(categoryTitle);

        itemsRecyclerView = view.findViewById(R.id.CategoryItemViewRecyclerView);
        addItemFloatingActionButton = view.findViewById(R.id.CategoryItemViewAddCategoryBtn);



        addItemFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> imageUrl = new ArrayList<>();
                List<Integer> colorList = new ArrayList<>();
                List<String> sizeList = new ArrayList<>();
                AlertDialog.Builder createItemAlertDialog = new AlertDialog.Builder(getActivity());
                View createItemAlertDialogView = getLayoutInflater().inflate(R.layout.create_item_alert_dialog_design,null);
                createItemAlertDialog.setView(createItemAlertDialogView);
                EditText title = createItemAlertDialogView.findViewById(R.id.ProductTitleInputEditText);
                EditText price = createItemAlertDialogView.findViewById(R.id.ProductPriceInputEditText);
                EditText description = createItemAlertDialogView.findViewById(R.id.ProductDescriptionEditText);
                Button addImage = createItemAlertDialogView.findViewById(R.id.ProductAddImageButton);
                Button addColor = createItemAlertDialogView.findViewById(R.id.ProductAddColorButton);
                Button addSize = createItemAlertDialogView.findViewById(R.id.ProductAddSizeButton);
                Button cancel = createItemAlertDialogView.findViewById(R.id.ProductItemCreateCancelButton);
                Button conform = createItemAlertDialogView.findViewById(R.id.ProductItemCreateButton);
                AlertDialog createAlertDialog = createItemAlertDialog.create();

                addImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                addColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int selected_color = ContextCompat.getColor(getActivity(),R.color.primary_Color);
                        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(getActivity(), selected_color, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                            @Override
                            public void onCancel(AmbilWarnaDialog dialog) {
                                Log.d(TAG, "Color Picker is onCancel");
                            }

                            @Override
                            public void onOk(AmbilWarnaDialog dialog, int color) {
                                colorList.add(color);
                            }
                        });
                    }
                });
                addSize.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder addSizeAlertDialog = new AlertDialog.Builder(getActivity());
                        View addSizeAlertView = getLayoutInflater().inflate(R.layout.add_size_alert_dialog_design, null);
                        addSizeAlertDialog.setView(addSizeAlertView);
                        AlertDialog addSizeDialog = addSizeAlertDialog.create();
                        TextView sizeText = addSizeAlertView.findViewById(R.id.AddSizeEditText);
                        Button addSize = addSizeAlertView.findViewById(R.id.AddSizeDialogBtn);
                        addSize.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sizeList.add(sizeText.getText().toString().trim());
                                addSizeDialog.dismiss();
                            }
                        });
                        addSizeDialog.show();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createAlertDialog.dismiss();
                    }
                });
                conform.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                createAlertDialog.show();

            }
        });

        return view;
    }
}