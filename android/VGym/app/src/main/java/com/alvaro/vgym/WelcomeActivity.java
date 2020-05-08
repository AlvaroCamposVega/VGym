package com.alvaro.vgym;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity
{
  TextView unsplashAttribution;

  @BindView(R.id.welcomeLoginBtn)
  Button loginBtn;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);
    // Enlazar la actividad con ButterKnife
    ButterKnife.bind(this);
    // Hacer el link del TextView Clickable
    unsplashAttribution = findViewById(R.id.welcomeUnsplashAttribution);
    unsplashAttribution.setMovementMethod(LinkMovementMethod.getInstance());

    loginBtn.setOnClickListener(v -> {
      Intent loginIntent = new Intent(this, LoginActivity.class);
      startActivity(loginIntent);
    });
  }
}
