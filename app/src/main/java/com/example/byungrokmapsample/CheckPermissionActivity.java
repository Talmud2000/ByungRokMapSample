package com.example.byungrokmapsample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class CheckPermissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_permission);
    }

    /*
    권한 체크 방법
    1. 사용자의 스마트폰이 마시멜로우 이상 버전인지 이하버전인지 체크
        -> 마시멜로우 이상 버전일때는 2번부터 진행
        -> 마시멜로우 이하 버전일때는 7번부터 진행
    2. 사용자가 해당 권한에 대해 거부를 했는지 있는지 확인(현재 상태)
        -> 권한 수락을 했다면 MainActivity 호출 (7번 진행)
        -> 권한을 수락하지 않았다면(거부되어 있는 상태라면) 3번부터 진행
    3. 사용자가 권한에 대해 거부를 한 이력이 있는지 확인
        -> 거부한 적이 있다면 최초 실행이 아님 4번부터 진행
        -> 거부를 한 적이 없다면 최초 실행 5번부터 진행
    4. 사용자가 최초실행이 아니고 권한에 대해 거부했다면
       개발자는 사용자에게 이 권한이 왜 필요한지에 대한 자세한 설명 제공 후 5번 진행
    5. requestPermissions라는 메서드를 통해서 권한 체크 다이얼러그 실행
    6. onRequestPermissionsResult()를 통해 권한 체크 여부 확인
        -> 권한 수락이 이루어지면 7번 실행
        -> 권한 거부가 이루어지면 앱을 종료, 그 기능을 못쓰게끔 하고 앱 실행
    7. 앱 실행

     */

    @Override
    protected void onResume() {
        super.onResume();


        //사용자의 OS 버전이 마시멜로우(23)이상인지 판별
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            //사용자의 단말에 권한 중 위치 가져오기(ACCESS_FINE_LOCATION)의 권한 허가 여부를 가져온다.
            //허가 -> PERMISSION_GRANTED
            //거부 -> PERMISSION_DENIED
            int permissionCheck=checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

            //현재 어플리케이션이 권한에 대해 거부되었는지 확인
            if(permissionCheck== PackageManager.PERMISSION_DENIED){

                //권한을 거부한적이 있으면 true-> 최초실행 아님;
                //권한을 거부한적이 없으면 false -> 최초실행;
                if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1000);
                }else{
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1000);
                }
            }else{
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        }else{
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //요청에 대한 응답을 처리하는 부분
    //@param requestCode : 요청코드(1000)
    //@param permissions : 사용자가 요청한 권한들(개발자) String배열
    //@param grandResults : 권한에 대한 응답들(인덱스 별로 매칭)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_DENIED) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "권한 요청을 거부하였습니다", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
