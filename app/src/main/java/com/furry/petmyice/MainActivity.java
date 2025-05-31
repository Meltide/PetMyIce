package com.furry.petmyice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.furry.petmyice.databinding.ActivityMainBinding;
import com.furry.petmyice.R;
import java.time.Instant;
import android.content.SharedPreferences;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
       
        setContentView(binding.getRoot());
        
        sharedPreferences = getSharedPreferences("PetMyIceSave", MODE_PRIVATE);

        binding.startBtn.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_login, null);
            TextInputLayout nameEdit = dialogView.findViewById(R.id.nameEdit);
            TextInputLayout ageEdit = dialogView.findViewById(R.id.ageEdit);
                
            new MaterialAlertDialogBuilder(this)
                .setTitle("输入你的信息")
                .setView(dialogView)
                .setPositiveButton("确定", (dialog, which) -> {
                    String name = nameEdit.getEditText().getText().toString().trim();
                    String age = ageEdit.getEditText().getText().toString().trim();
                        
                    if (name.isEmpty()) {
                        Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
                        return;
                    } if (age.isEmpty()) {
                        Toast.makeText(this, "请输入年龄", Toast.LENGTH_SHORT).show();
                        return;
                    }
                        
                    if (Long.parseLong(age) < 18) {
                        Toast.makeText(this, "未满18岁禁止游玩", Toast.LENGTH_SHORT).show();
                        return;
                    }
                        
                    Intent intent = new Intent(this, GameActivity.class);
                    intent.putExtra("NAME", name);
                    intent.putExtra("AGE", age);
                    intent.putExtra("IS_NEW_GAME", true);
                    startActivity(intent);
                })
                .setNegativeButton("取消", null)
                .show();
        });
        
        binding.loadBtn.setOnClickListener(v -> {
            if (!sharedPreferences.contains("name")) {
                Toast.makeText(this, "没有找到存档", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String savedName = sharedPreferences.getString("name", "");
            new MaterialAlertDialogBuilder(this)
                .setTitle("加载存档")
                .setMessage("是否加载" + savedName + "的存档？")
                .setPositiveButton("确定", (dialog, which) -> {
                    Intent intent = new Intent(this, GameActivity.class);
                    intent.putExtra("IS_NEW_GAME", false);
                    startActivity(intent);
                })
                .setNegativeButton("取消", null)
                .show();
        });
        
        binding.exitBtn.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}