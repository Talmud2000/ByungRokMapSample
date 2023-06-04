package com.example.byungrokmapsample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    LinearLayout linMapView;
    EditText edtSearch;
    Button btnSearch, btnZoomIn, btnZoomOut, btnMyLocation;
    TMapView tmapView;
    TMapData tMapData;

    // 검색 결과를 출력하기 위해 만든 리스트
    ArrayList<TMapPOIItem> poiResult;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initInstance();
        eventListener();

    }

    @Override
    // 허가 수신
    protected void onResume() {
        super.onResume();
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                            1000, 1, locationListener);
        } catch (SecurityException e){

        }
    }

    // 위젯들 초기화 함수
    public void initView(){
        linMapView = findViewById(R.id.linMapView);
        edtSearch = findViewById(R.id.edtSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnZoomIn = findViewById(R.id.btnZoomIn);
        btnZoomOut = findViewById(R.id.btnZoomOut);
        btnMyLocation = findViewById(R.id.btnMyLocation);
    }

    // 필요 인스턴스 초기화 함수
    public void initInstance(){
        // 위치정보 객체
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // tMap 정보 객체
        tmapView = new TMapView(this);
        tmapView.setSKTMapApiKey("5MSMlqKawqaoI8j0jn2zk6Wp8pGDfZQQHiCDQmw5");
        linMapView.addView(tmapView);


        tMapData = new TMapData(this);
        poiResult = new ArrayList<>();
    }

    public void eventListener(){
        btnSearch.setOnClickListener(listener);
        btnZoomIn.setOnClickListener(listener);
        btnZoomOut.setOnClickListener(listener);
        btnMyLocation.setOnClickListener(listener);
    }

    // searchPOI 메소드 수정
    public void searchPOI(String strData) {
        tMapData.findAllPOI(strData, new TMapData.FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {
                // 이전에 추가한 마커들 제거
                tmapView.removeAllMarkerItem();

                // 검색 결과 중 첫 번째 위치에 마커 추가
                if (!arrayList.isEmpty()) {
                    TMapPOIItem firstPOI = arrayList.get(0);
                    double latitude = firstPOI.getPOIPoint().getLatitude();
                    double longitude = firstPOI.getPOIPoint().getLongitude();
                    String title = firstPOI.getPOIName();
                    addSearchLocationMarker(latitude, longitude, title);

                    tmapView.setCenterPoint(longitude, latitude);
                }
            }
        });
    }


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();

            // 현재 위치에 마커 추가
            addCurrentLocationMarker(lat, lon);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            LocationListener.super.onStatusChanged(provider, status, extras);
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            LocationListener.super.onProviderEnabled(provider);
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            LocationListener.super.onProviderDisabled(provider);
        }



    };

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnSearch:
                    String strData = edtSearch.getText().toString();
                    if(!strData.equals("")){
                        // 티맵을 통해 검색
                        searchPOI(strData);
                    } else{
                        Toast.makeText(getApplicationContext(), "검색어 입력 필수!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btnZoomIn:
                    btnZoomInClick();
                    break;
                case R.id.btnZoomOut:
                    btnZoomOutClick();
                    break;
                case R.id.btnMyLocation:
                    btnMyLocationClick();
                    break;
            }
        }
    };

    public void btnZoomInClick(){
        tmapView.MapZoomIn();
    };

    public void btnZoomOutClick(){
        tmapView.MapZoomOut();
    }

    // btnMyLocation 클릭 이벤트 핸들러
    public void btnMyLocationClick() {
        try {
            // GPS 프로바이더를 통해 현재 위치 가져오기
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                double lat = lastKnownLocation.getLatitude();
                double lon = lastKnownLocation.getLongitude();

                // 현재 위치를 지도 중심으로 설정
                tmapView.setCenterPoint(lon, lat);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    // 현재 위치에 마커 추가
    public void addCurrentLocationMarker(double latitude, double longitude) {
        TMapPoint currentLocation = new TMapPoint(latitude, longitude);
        TMapMarkerItem markerItem = new TMapMarkerItem();
        markerItem.setTMapPoint(currentLocation);
        markerItem.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_tmapmarker)); // 마커 아이콘 설정
        tmapView.addMarkerItem("currentLocationMarker", markerItem);
    }

    // 검색한 위치에 마커 추가
    public void addSearchLocationMarker(double latitude, double longitude, String title) {
        TMapPoint searchLocation = new TMapPoint(latitude, longitude);
        TMapMarkerItem markerItem = new TMapMarkerItem();
        markerItem.setTMapPoint(searchLocation);
        markerItem.setCalloutTitle(title); // 마커 클릭 시 표시될 제목 설정
        tmapView.addMarkerItem("searchLocationMarker", markerItem);
    }

    // 이부분은 아직 실행 못함
    // 길 안내를 시작하는 메소드
//    public void startNavigation(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
//        TMapPoint startPoint = new TMapPoint(startLatitude, startLongitude);
//        TMapPoint endPoint = new TMapPoint(endLatitude, endLongitude);
//
//        TMapData tMapData = new TMapData();
//        tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startPoint, endPoint, new TMapData.FindPathDataListenerCallback() {
//            @Override
//            public void onFindPathData(TMapPolyLine path) {
//                // 경로 정보를 받아와 지도에 그리기
//                tmapView.addTMapPath(path);
//
//                // 길 안내 시작
//                tmapView.setTrackingMode(true);
//            }
//        });
//    }





}