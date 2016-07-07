package com.example.amapm;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;

public class MainActivity extends Activity implements LocationSource, AMapLocationListener,GeocodeSearch.OnGeocodeSearchListener {
	private AMap aMap;
	private MapView mapView;
	// 声明AMapLocationClient类对象
	public AMapLocationClient mLocationClient = null;
	// 声明mLocationOption对象
	public AMapLocationClientOption mLocationOption = null;
	private OnLocationChangedListener mListener = null;// 定位监听器
	boolean isFirst = true;

	private TextView tv_des;
	String cityCode;
	//实时定位的经纬度
	public static double LA;
	public static double LO;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mapView = (MapView) findViewById(R.id.map);
		tv_des = (TextView) findViewById(R.id.tv_des);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		// 设置定位监听
		aMap = mapView.getMap();
		aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
		aMap.setLocationSource(this);
		// 设置定位层可用
		aMap.setMyLocationEnabled(true);
		
		UiSettings mUisSettings = aMap.getUiSettings();
		mUisSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
		// 初始化定位
		mLocationClient = new AMapLocationClient(getApplication());
		// 初始化定位参数
		mLocationOption = new AMapLocationClientOption();
		// 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		// 设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setNeedAddress(true);
		// 设置是否只定位一次,默认为false
		mLocationOption.setOnceLocation(false);
		// 设置是否强制刷新WIFI，默认为强制刷新
		mLocationOption.setWifiActiveScan(true);
		// 设置是否允许模拟位置,默认为false，不允许模拟位置
		mLocationOption.setMockEnable(false);
		// 设置定位间隔,单位毫秒,默认为2000ms
		mLocationOption.setInterval(2000);
		// 给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
		// 声明和设置定位回调监听器
		mLocationClient.setLocationListener(this);

		// 启动定位
		mLocationClient.startLocation();
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_red));
		myLocationStyle.radiusFillColor(android.R.color.transparent);
		myLocationStyle.strokeColor(android.R.color.transparent);
		aMap.setMyLocationStyle(myLocationStyle);
		
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		// deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		if (null != mLocationClient) {
			mLocationClient.onDestroy();
			mLocationClient = null;
		}
	}


	// 逆地理
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		// TODO Auto-generated method stub
		if (rCode == 1000) {
			//stopProgressDialog();
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				String addressName = result.getRegeocodeAddress()
						.getNeighborhood();

				tv_des.setText(addressName);

				cityCode = result.getRegeocodeAddress().getCityCode();
				// Log.e("当前位置", addressName);

			} else {

			}
		} else {

		}
	}
	// 地理搜索
	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		// TODO Auto-generated method stub
		if (rCode == 1000) {
			if (result != null && result.getGeocodeAddressList() != null
					&& result.getGeocodeAddressList().size() > 0) {
				GeocodeAddress address = result.getGeocodeAddressList().get(0);
				// aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
				// AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));

				String addressName = "经纬度值:" + address.getLatLonPoint()
						+ "\n位置描述:" + address.getFormatAddress();

				Log.e("搜索的位置定位", addressName);
				// ToastUtil.show(GeocoderActivity.this, addressName);
			} else {
				// /ToastUtil.show(GeocoderActivity.this, R.string.no_result);
			}
		} else {
			// ToastUtil.showerror(this, rCode);
		}
	}



	@Override
	public void onLocationChanged(AMapLocation amapLocation) {

		// TODO Auto-generated method stub
		if (amapLocation != null && amapLocation.getErrorCode() == 0) {
			// 定位成功回调信息，设置相关消息
			int s = amapLocation.getLocationType();// 获取当前定位结果来源，如网络定位结果，详见定位类型表
			double d1 = amapLocation.getLatitude();// 获取纬度
			double d2 = amapLocation.getLongitude();// 获取经度

			LA = d1;
			LO = d2;

			double d3 = amapLocation.getAccuracy();// 获取精度信息
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date(amapLocation.getTime());
			String Stime = df.format(date);// 定位时间
			String s1 = amapLocation.getAddress();// 地址，如果option中设置isNeedAddress为false，则没有此结果
			String s2 = amapLocation.getCountry();// 国家信息
			String s3 = amapLocation.getProvince();// 省信息
			String s4 = amapLocation.getCity();// 城市信息
			String s5 = amapLocation.getDistrict();// 城区信息
			String s6 = amapLocation.getRoad();// 街道信息
			String s7 = amapLocation.getCityCode();// 城市编码
			String s8 = amapLocation.getAdCode();// 地区编码
			String s9 = amapLocation.getLocationDetail();
			tv_des.setText(s1);
			// 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
			if (isFirst) {
				// 设置缩放级别
				//aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
				// 将地图移动到定位点
				aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(
						amapLocation.getLatitude(), amapLocation.getLongitude())));
				// 点击定位按钮 能够将地图的中心移动到定位点
				mListener.onLocationChanged(amapLocation);
				isFirst = false;
			}

		}
	
		
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		// TODO Auto-generated method stub
		mListener = listener;
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		mListener = null;
	}

	
}
