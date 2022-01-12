package com.example.volumn.setting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.volumn.R;
import com.example.volumn.chat.ChatService;
import com.example.volumn.chat.Get_img_Request;
import com.example.volumn.chat.ImageUtil;
import com.example.volumn.chat.Upload_img_Request;
import com.example.volumn.include.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class settingActivity extends AppCompatActivity {

    Context context;

    String encodeImageString = null;

    ImageView img_back5;
    ImageView img_profile_img;
    TextView txt_profil_img;

    Button btn_setting_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        context = this;
        txt_profil_img = (TextView) findViewById(R.id.txt_profil_img);
        img_profile_img = (ImageView) findViewById(R.id.img_profile_img);
        btn_setting_save = (Button) findViewById(R.id.btn_setting_save);

        btn_setting_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기

                //비밀번호 변경



                if (encodeImageString != null) {

                    //데이터 베이스에 저장
                    Response.Listener<String> responseListner = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);


                                boolean success = jsonObject.getBoolean("success");

                                if (success) { // 이미지 업로드에 성공

                                    //메시지 보내기 서비스에 전달

                                        Toast.makeText(getApplicationContext(), "프로필 사진이 변경되었습니다．" , Toast.LENGTH_SHORT).show();



                                } else { // 이미지 업로드 실패
                                    //Toast.makeText(getApplicationContext()," ",Toast.LENGTH_SHORT).show();

                                    return;
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    };
                    //String userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기

                    Upload_ProfileIMG_Request upload_profileIMG_request = new Upload_ProfileIMG_Request(userEmail, encodeImageString, responseListner);
                    RequestQueue queue = Volley.newRequestQueue(context);
                    queue.add(upload_profileIMG_request);
                }

            }
        });

        txt_profil_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 11);
            }
        });


        img_back5 = (ImageView) findViewById(R.id.img_back5);
        img_back5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getProfileIMG();//프로필 받아오기
    }
    //프로필 사진 받아오기
    private void getProfileIMG()
    {
        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);


                    boolean success = jsonObject.getBoolean("success");

                    if (success) { // 이미지 받아오기 성공

                        //메시지 보내기 서비스에 전달
                        try {
                            String img = jsonObject.getString("img");


                            String data = img; //bitmap 변환
                           Bitmap bitmap = ImageUtil.convert(data);
                            img_profile_img.setImageBitmap(bitmap);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else { // 이미지 받아오기 실패
                        //Toast.makeText(getApplicationContext()," ",Toast.LENGTH_SHORT).show();

                        return ;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        String userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기

        get_ProfileIMG_Request get_profileIMG_request = new get_ProfileIMG_Request(userEmail, responseListner);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(get_profileIMG_request);


    }

    private void encodeUriToBitmap(Uri uri) throws FileNotFoundException {//최종 결과 encodeImageString 값에 이미지 String 값 전달됨

        InputStream inputStream = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        //encodeBitmapImage(bitmap);

        encodeImageString = ImageUtil.convert(bitmap);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 11 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Uri imageUri = data.getData();

                encodeUriToBitmap(imageUri);//최종 결과 encodeImageString 값에 이미지 String 값 전달됨


                Bitmap bitmap = ImageUtil.convert(encodeImageString);

                img_profile_img.setImageBitmap(bitmap);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

//        switch (requestCode) {
//            case 11:
//                if (resultCode == RESULT_OK) {
//
//
//                    //선택한 사진의 경로(Uri)객체 가져오기
//                    // Uri uri = data.getData();
//
//                    if (data != null) {
//                        if (data.getClipData() == null) {  // 이미지를 하나만 선택한 경우
//                            Log.e("single choice: ", String.valueOf(data.getData()));
//                            Uri imageUri = data.getData();
//
//                            //bitmap으로 변환
//
//                            try {
//                                encodeUriToBitmap(imageUri);//최종 결과 encodeImageString 값에 이미지 String 값 전달됨
//
//                                Bitmap bitmap = ImageUtil.convert(encodeImageString);
//
//                                img_profile_img.setImageBitmap(bitmap);
//
//                                //  Upload_img(encodeImageString);//이미지 DB에 저장
//
//                            } catch (FileNotFoundException e) {
//                                e.printStackTrace();
//                            }
//
//                            String imgPath = getRealPathFromUri(imageUri);
//
//                            //서버에 메시지 보내기
//
//
//                            //clientMsg_list.add( "이미지▶"+imageUri);
//                            // adapter.notifyDataSetChanged();
//
//
//                            //  rv_msgList.scrollToPosition(clientMsg_list.size()-1);
//
//
//                            Log.d("이미지 경로", "" + imageUri.toString() + "\n" + imgPath);
//
//                        }
//
//                        //이미지뷰의 변수명 . setImageURI(uri);
//
//                        //갤러리앱에서 관리하는 DB정보가 있는데 , 그것이 나온다 [실제 파일 경로가 아님]
//                        //얻어온 Uri는 Gallery앱의 DB번호임
//                        //업로드를 하려면 이미지의 절대경로(실제경로: file:// -----/aaa.png)필요함
//                        //Uri -->절대경로 (String)변환
//
//
//                        // adapter = new MsgAdapter(clientMsg_list);
//                        //Toast.makeText(context, "메시지", Toast.LENGTH_SHORT).show();
//
//
//                        //이미지 경로 uri 확인해보기
//                    } else {
//                        Toast.makeText(context, "이미지가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                break;
//
//            default:
//                break;
//        }

    }
}