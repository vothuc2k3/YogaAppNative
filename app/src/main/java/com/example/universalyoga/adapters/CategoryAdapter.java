package com.example.universalyoga.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universalyoga.R;
import com.example.universalyoga.models.ClassCategoryModel;
import com.example.universalyoga.sqlite.DAO.CategoryDAO;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private final List<ClassCategoryModel> categoryList;
    private final Context context;
    private final CategoryDAO categoryDAO;

    public CategoryAdapter(List<ClassCategoryModel> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
        this.categoryDAO = new CategoryDAO(context);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        ClassCategoryModel category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getName());
        holder.tvCategoryDescription.setText(category.getDescription());

        holder.btnEditCategory.setOnClickListener(v -> showDialogEditCategory(category, position));
        holder.btnDeleteCategory.setOnClickListener(v -> showDialogDeleteCategory(category, position));
    }

    private void showDialogEditCategory(ClassCategoryModel category, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_edit_category, null);

        EditText nameInput = dialogView.findViewById(R.id.edit_text_category_name);
        EditText descriptionInput = dialogView.findViewById(R.id.edit_text_category_description);

        nameInput.setText(category.getName());
        descriptionInput.setText(category.getDescription());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        builder.setPositiveButton("Edit", (dialog, which) -> {
            if(!validateInput(nameInput)){
                return;
            }
            if(!validateDuplicateName(nameInput.getText().toString(), category.getName())){
                return;
            }

            String name = nameInput.getText().toString();
            String description = descriptionInput.getText().toString();
            categoryDAO.updateCategory(new ClassCategoryModel(category.getId(), name, description));
            categoryList.set(position, new ClassCategoryModel(category.getId(), name, description));
            notifyItemChanged(position);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showDialogDeleteCategory(ClassCategoryModel category, int position) {
        boolean isUsed = categoryDAO.isCategoryUsed(category.getId());
        if (isUsed) {
            Toast.makeText(context, "Category is used in classes", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = getBuilder(category, position);

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private AlertDialog.Builder getBuilder(ClassCategoryModel category, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Category");

        builder.setMessage("Are you sure want to delete this category?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            categoryDAO.deleteCategory(category.getId());
            FirebaseFirestore.getInstance().collection("categories").document(category.getId()).delete();
            Toast.makeText(context, "Category deleted", Toast.LENGTH_SHORT).show();
            notifyItemRemoved(position);
        });
        return builder;
    }

        private boolean validateInput(EditText nameInput) {
        String name = nameInput.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(context, "Name is required.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
        }
    
    private boolean validateDuplicateName(String newName, String oldName){
        if(newName.equals(oldName)){
            Toast.makeText(context, "Enter a different name.", Toast.LENGTH_SHORT).show();
            return false;
        }
        for(ClassCategoryModel category : categoryList){
            if(category.getName().equals(newName)){
                Toast.makeText(context, "Name is already taken.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCategoryName, tvCategoryDescription;
        public ImageButton btnEditCategory, btnDeleteCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvCategoryDescription = itemView.findViewById(R.id.tv_category_description);
            btnEditCategory = itemView.findViewById(R.id.btn_edit_category);
            btnDeleteCategory = itemView.findViewById(R.id.btn_delete_category);
        }
    }
}
