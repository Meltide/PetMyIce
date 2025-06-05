package com.furry.petmyice;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.furry.petmyice.databinding.ActivityGameBinding;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.Random;
import java.util.stream.Stream;
import android.os.Looper;
import android.os.Handler;

public class GameActivity extends AppCompatActivity {
    
    private ActivityGameBinding binding;
    
    private String name;
    private String age;
    
    private int blood = 100;
    private int food = 100;
    private int love = 0;
    private int count = 0;
    
    private TextView masterText;
    private TextView bloodText;
    private TextView foodText;
    private TextView loveText;
    private TextView countText;
    private TextView actionText;
    private TextView msgText;
    
    private CardView avatar;
    private ImageView avatarImg;
    
    private Button reburnBtn;
    private Button ruaBtn;
    private Button eatBtn;
    private Button playBtn;
    private Button fuckBtn;
    
    private SharedPreferences sharedPreferences;
    private boolean isNewGame;
    
    private Random rand = new Random();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        
        sharedPreferences = getSharedPreferences("PetMyIceSave", MODE_PRIVATE);
        isNewGame = getIntent().getBooleanExtra("IS_NEW_GAME", true);
        
        initViews(binding);
        setButtonEvents();
        
        if (isNewGame) {
            name = getIntent().getStringExtra("NAME");
            age = getIntent().getStringExtra("AGE");
            resetGame();
        } else {
            loadGame();
        }
        
        updateUI();
        msgText.setText(name + (isNewGame ? "，你就是我的主人了！" : "，欢迎回来！"));
        
        if (blood <= 0 || food <= 0) {
            msgText.setText("😵");
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.newBtn) {
            newGame();
        } else if (id == R.id.saveBtn) {
            saveGame();
            return true;
        } else if (id == R.id.loadBtn) {
            loadGameConfirmation();
            return true;
        } else if (id == R.id.delBtn) {
            delSave();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void resetGame() {
        blood = 100;
        food = 100;
        love = 0;
        count = 0;
        
        avatarImg.clearColorFilter();
        
        ruaBtn.setEnabled(true);
        eatBtn.setEnabled(true);
        playBtn.setEnabled(true);
        fuckBtn.setEnabled(true);
        
        avatarImg.setImageResource(R.drawable.outice);
    }
    
    private void newGame() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_login, null);
        TextInputEditText nameEdit = dialogView.findViewById(R.id.nameEditIn);
        TextInputEditText ageEdit = dialogView.findViewById(R.id.ageEditIn);
        
        new MaterialAlertDialogBuilder(this)
            .setTitle("新游戏")
            .setMessage("确定要加载新游戏？当前的游戏进度将会丢失！")
            .setPositiveButton("确定", (dialog, which) -> {
                MaterialAlertDialogBuilder innerDialogBuilder = new MaterialAlertDialogBuilder(this)
                    .setTitle("输入你的信息")
                    .setView(dialogView)
                    .setNegativeButton("取消", null);
                
                innerDialogBuilder.setPositiveButton("确定", null); // 先设置为null
                
                innerDialogBuilder.setNeutralButton("填入当前信息", null);
                
                AlertDialog innerDialog = innerDialogBuilder.create();
                
                innerDialog.show();
                
                Button positiveButton = innerDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(v -> {
                    String name = nameEdit.getText().toString().trim();
                    String age = ageEdit.getText().toString().trim();
                    
                    if (name.isEmpty()) {
                        Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (age.isEmpty()) {
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
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });
                
                Button neutralButton = innerDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                neutralButton.setOnClickListener(v -> {
                    nameEdit.setText(name);
                    ageEdit.setText(age);
                });
            })
            .setNegativeButton("取消", null)
            .show();
    }
    
    private void saveGame() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putInt("blood", blood);
        editor.putInt("food", food);
        editor.putInt("love", love);
        editor.putInt("count", count);
        editor.apply();
        
        binding.toolbar.setTitle("PetMyIce - " + name + "的存档");
        
        Toast.makeText(this, "游戏已保存", Toast.LENGTH_SHORT).show();
    }
    
    private void loadGame() {
        name = sharedPreferences.getString("name", "主人");
        blood = sharedPreferences.getInt("blood", 100);
        food = sharedPreferences.getInt("food", 100);
        love = sharedPreferences.getInt("love", 0);
        count = sharedPreferences.getInt("count", 0);
    }
    
    private void delSave() {
        if (!sharedPreferences.contains("name")) {
            Toast.makeText(this, "没有存档可删除", Toast.LENGTH_SHORT).show();
            return;
        }
        
        new MaterialAlertDialogBuilder(this)
            .setTitle("删除存档")
            .setMessage("确定要删除当前存档吗？此操作不可恢复！")
            .setPositiveButton("确定删除", (dialog, which) -> {
                sharedPreferences.edit().clear().apply();
                
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(this, "存档已删除", Toast.LENGTH_SHORT).show();
                finish();
            })
            .setNegativeButton("取消", null)
            .show();
    }
    
    private void updateUI() {
        if (!isNewGame) {
            binding.toolbar.setTitle("PetMyIce - " + name + "的存档");
            binding.actionText.setText(blood <= 0 || food <= 0 ? "outice已经似了" : "回到了家中");
        }
        
        masterText.setText(name);
        bloodText.setText(String.valueOf(blood));
        foodText.setText(String.valueOf(food));
        loveText.setText(String.valueOf(love));
        countText.setText(String.valueOf(count));
        
        if (blood <= 0 || food <= 0) {
            die();
        }
    }
    
    private void loadGameConfirmation() {
        if (!sharedPreferences.contains("name")) {
            Toast.makeText(this, "没有存档可加载", Toast.LENGTH_SHORT).show();
            return;
        }
        
        new MaterialAlertDialogBuilder(this)
            .setTitle("加载游戏")
            .setMessage("加载游戏将丢失当前进度，是否继续？")
            .setPositiveButton("确定", (dialog, which) -> {
                loadGame();
                updateUI();
                Toast.makeText(this, "游戏已加载", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("取消", null)
            .show();
    }
    
    private void initViews(ActivityGameBinding binding) {
        masterText = binding.masterText;
        bloodText = binding.bloodText;
        foodText = binding.foodText;
        loveText = binding.loveText;
        countText = binding.countText;
        actionText = binding.actionText;
        msgText = binding.msgText;
        
        avatar = binding.avatar;
        avatarImg = binding.avatarImg;
        
        reburnBtn = binding.reburnBtn;
        ruaBtn = binding.ruaBtn;
        eatBtn = binding.eatBtn;
        playBtn = binding.playBtn;
        fuckBtn = binding.fuckBtn;
    }
    
    private void setButtonEvents() {
        avatar.setOnClickListener(v -> {
            String[] sayList = {"唔", "嗷", "喵", "💕"};
            love += rand.nextInt(7) + 4;
            loveText.setText(String.valueOf(love));
            actionText.setText("摸了摸outice的头");
            msgText.setText(sayList[rand.nextInt(sayList.length)]);
        });
        
        ruaBtn.setOnClickListener(v -> {
            avatarImg.setImageResource(R.drawable.outice);
                
            String[] sayList = {"唔", "嗷", "喵", "💕"};
            love += rand.nextInt(7) + 4;
            loveText.setText(String.valueOf(love));
            actionText.setText("rua了outice一下");
            msgText.setText(sayList[rand.nextInt(sayList.length)]);
        });
        
        eatBtn.setOnClickListener(v -> {
            avatarImg.setImageResource(R.drawable.outice);
                
            if ((180 - food) <= 10) {
                new MaterialAlertDialogBuilder(this)
                    .setTitle("注意")
                    .setMessage("outice快吃不下了，再吃就要撑死了")
                    .setPositiveButton("我不管！", (dialog, which) -> {
                        eat();
                    })
                    .setNeutralButton("带她散步", (dialog, which) -> {
                        play();
                    })
                    .setNegativeButton("好吧", null)
                    .show();
            } else {
                eat();
            }
        });
        
        playBtn.setOnClickListener(v -> {
            avatarImg.setImageResource(R.drawable.outice);
                
            if (food <= 25) {
                new MaterialAlertDialogBuilder(this)
                    .setTitle("注意")
                    .setMessage("outice很饿，快去喂她吃点东西吧")
                    .setPositiveButton("我不管！", (dialog, which) -> {
                        play();
                    })
                    .setNeutralButton("带她吃东西", (dialog, which) -> {
                        eat();
                    })
                    .setNegativeButton("好吧", null)
                    .show();
            } else {
                play();
            }
        });
        
        fuckBtn.setOnClickListener(v -> {
            if (love < 100) {
                 new MaterialAlertDialogBuilder(this)
                    .setTitle("注意")
                    .setMessage("好感度不足，无法推倒！")
                    .setPositiveButton("强上她！", (dialog, which) -> {
                        fuck(true);
                    })
                    .setNegativeButton("好吧", null)
                    .create()
                    .show();
            } else {
                fuck(false);
            }
        });
        
        reburnBtn.setOnClickListener(v -> {
            AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle("广告")
                .setView(getLayoutInflater().inflate(R.layout.dialog_ad, null))
                .setPositiveButton("关闭", null)
                .setNegativeButton("不看了", null)
                .create();
            
            dialog.show();
            
            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveButton.setEnabled(false);
            
            final int countDownTime = 10;
            final String originalText = "关闭";
            positiveButton.setText(originalText + "(" + countDownTime + ")");
            
            CountDownTimer countDownTimer = new CountDownTimer(countDownTime * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int secondsRemaining = (int) (millisUntilFinished / 1000);
                    positiveButton.setText(originalText + "(" + secondsRemaining + ")");
                }
                
                @Override
                public void onFinish() {
                    positiveButton.setText(originalText);
                    positiveButton.setEnabled(true);
                }
            }.start();
            
            positiveButton.setOnClickListener(v1 -> {
                countDownTimer.cancel();
                blood = 100;
                food = 100;
                
                bloodText.setText(String.valueOf(blood));
                foodText.setText(String.valueOf(food));
            
                avatarImg.setImageResource(R.drawable.outice);
                avatarImg.setColorFilter(null);
            
                avatar.setEnabled(true);
                ruaBtn.setEnabled(true);
                eatBtn.setEnabled(true);
                playBtn.setEnabled(true);
                fuckBtn.setEnabled(true);
                
                actionText.setText("看广告复活了outice");
                msgText.setText(name + "，我回来了");
                    
                reburnBtn.setVisibility(View.INVISIBLE);
                reburnBtn.setEnabled(false);
                
                dialog.dismiss();
            });
        });
    }
    
    private void die() {
        blood = 0;
        food = 0;
        bloodText.setText(String.valueOf(blood));
        foodText.setText(String.valueOf(food));
        
        avatarImg.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix() {{ setSaturation(0); }}));
        
        avatar.setEnabled(false);
        ruaBtn.setEnabled(false);
        eatBtn.setEnabled(false);
        playBtn.setEnabled(false);
        fuckBtn.setEnabled(false);
        
        reburnBtn.setVisibility(View.VISIBLE);
        reburnBtn.setEnabled(true);
    }
    
    private void eat() {
        String[] foodList = {"猫粮", "罐头", "猫草"};
        String[] sayList = {"好吃😋", "美味", "好！"};

        if (blood > 0 || food > 0) {
            blood += rand.nextInt(3) + 3;
            food += rand.nextInt(5) + 6;
            love += rand.nextInt(9) + 8;

            bloodText.setText(String.valueOf(blood));
            foodText.setText(String.valueOf(food));
            loveText.setText(String.valueOf(love));

            actionText.setText("喂outice吃了点" + foodList[rand.nextInt(foodList.length)]);
            msgText.setText(sayList[rand.nextInt(sayList.length)]);
        }
        
        if (food >= 180) {
            actionText.setText("outice被撑死了");
            msgText.setText("😵");
            die();
        }
    }
    
    private void play() {
        String[] playList = {"鬼屋", "游乐园"};
        String[] sayList;

        String choice = playList[rand.nextInt(playList.length)];
        switch (choice) {
            case "鬼屋":
                sayList = new String[]{"😱", "唔...好可怕", "啊啊啊啊啊"};
                food -= rand.nextInt(25 - 15 + 1) + 15;
                foodText.setText(String.valueOf(food));
                break;
            case "游乐园":
                sayList = new String[]{"😄开心", "真好玩！"};
                food -= rand.nextInt(15 - 5 + 1) + 5;
                foodText.setText(String.valueOf(food));
                break;
            default:
                sayList = new String[]{"今天玩得很开心"};
                break;
        }

        love += rand.nextInt(31) + 10;
        loveText.setText(String.valueOf(love));

        actionText.setText("带outice去了" + choice);
        msgText.setText(sayList[rand.nextInt(sayList.length)]);
            
        if (food <= 0) {
            actionText.setText("把outice饿死了");
            msgText.setText("😵");
            die();
        }
    }
    
    private void fuck(boolean force) {
        avatarImg.setImageResource(R.drawable.outice_bed);
        
        String[] sayList = {"💕唔...", "嗷", "🥵"};
        String[] sayListForce = {"😭👊补药", "😡😡😡"};
        
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
            .setView(R.layout.dialog_loading)
            .setCancelable(false);

        AlertDialog loadingDialog = builder.show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                int duration = rand.nextInt(2000) + 1000;
                Thread.sleep(duration);

                runOnUiThread(() -> {
                    loadingDialog.dismiss();

                    if (force) {
                        love = Math.max(0, love - 50);
                        blood -= rand.nextInt(60 - 30 + 1) + 30;
                        food -= rand.nextInt(80 - 50 + 1) + 50;
                        count += 1;
                        
                        bloodText.setText(String.valueOf(blood));
                        foodText.setText(String.valueOf(food));
                        loveText.setText(String.valueOf(love));
                        countText.setText(String.valueOf(count));
                        
                        actionText.setText("强上了outice");
                        msgText.setText(sayListForce[rand.nextInt(sayListForce.length)]);
                    } else {
                        love += rand.nextInt(20) + 10;
                        blood -= rand.nextInt(30 - 20 + 1) + 20;
                        food -= rand.nextInt(60 - 40 + 1) + 40;
                        count += 1;

                        bloodText.setText(String.valueOf(blood));
                        foodText.setText(String.valueOf(food));
                        loveText.setText(String.valueOf(love));
                        countText.setText(String.valueOf(count));
                        
                        loveText.setText(String.valueOf(love));
                        actionText.setText("超了outice");
                        msgText.setText(sayList[rand.nextInt(sayList.length)]);
                    }
                    
                    if (blood <= 0 || food <= 0) {
                        blood = 0;
                        food = 0;
                        
                        bloodText.setText(String.valueOf(blood));
                        foodText.setText(String.valueOf(food));
                        
                        actionText.setText("把outice超市了");
                        msgText.setText("😵");
                        die();
                    }
                });

            } catch (InterruptedException e) {
                runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    Toast.makeText(GameActivity.this, "666被打断施法了", Toast.LENGTH_SHORT).show();
                });
                Thread.currentThread().interrupt();
            }
        }, 100);
    }
}
