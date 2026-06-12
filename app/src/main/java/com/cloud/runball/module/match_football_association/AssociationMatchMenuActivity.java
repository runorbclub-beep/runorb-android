package com.cloud.runball.module.match_football_association;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cloud.runball.R;

public class AssociationMatchMenuActivity extends AppCompatActivity {

  public static void startAction(Context context) {
    Intent intent = new Intent(context, AssociationMatchMenuActivity.class);
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_association_match_menu);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

  }

}
