package com.example.imagetransferclient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.ParseException;

public class MainActivity extends AppCompatActivity {

    Button btnSend, btnConnect, btnBrowse;

    EditText edtIP, edtPort;

    ImageView imageView;

    private boolean imageFilled = false;
    private Uri imageUri = null;
    private Socket clientSocket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setupEvent();
    }

    private void setupEvent() {
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = String.valueOf(edtIP.getText());
                int port = Integer.parseInt(String.valueOf(edtPort.getText()));
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        clientSocket = clientConnect(ip, port);
                    }
                });

                thread.start();

            }
        });

        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        Context context = MainActivity.this.getApplicationContext();
                        int len;
                        byte buf[] = new byte[1024];
                        try {
                            OutputStream outputStream = clientSocket.getOutputStream();
                            ContentResolver cr = context.getContentResolver();
                            InputStream inputStream = null;
                            inputStream = cr.openInputStream(imageUri);

                            while ((len = inputStream.read(buf)) != -1) {
                                outputStream.write(buf, 0, len);
                            }
                            outputStream.close();
                            inputStream.close();
                            Log.e("notify", "Done file transfering!");
                        } catch (FileNotFoundException e) {
                            Log.e("error", "File not found");
                        } catch (IOException e) {
                            Log.e("error", "Unable to initialize outputStream");
                        }

                    }
                });

                thread.start();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            imageUri = data.getData();
            imageFilled = true;
            btnSend.setEnabled(true);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Socket clientConnect(String ip, int port) {
        Context context = this.getApplicationContext();
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect((new InetSocketAddress(ip, port)), 500);
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Socket init failed!", Toast.LENGTH_LONG).show();
        }
        return socket;
    }


    protected void initView() {
        btnConnect = findViewById(R.id.buttonConnect);
        btnBrowse = findViewById(R.id.buttonBrowse);
        btnSend = findViewById(R.id.buttonSend);
        edtIP = findViewById(R.id.editTextIP);
        edtPort = findViewById(R.id.editTextPort);
        imageView = findViewById(R.id.imageView);
        btnSend.setEnabled(false);

    }


}