package com.example.hello;

import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.hello.location.LocationHandle;
import com.example.hello.view.InstandView;

public class MainActivity extends Activity {
	private LocationManager mgr = null;
	private Button button1 = null;
	private Button button2 = null;
	private Button button3 = null;
	private TextView textViewMileage = null;
	private TextView textViewTotalTime = null;
	private TextView textViewAverageSpeed = null;
	private TextView textViewCurrentSpeed = null;
	private TextView textView3 = null;
	private TextView textView4 = null;
	private TextView textView5 = null;
	private TextView textviewResult = null;
	String fileName = "default";
	LocationHandle locationHandle = new LocationHandle();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_layout);

		button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(new ButtonListener());
		button2 = (Button) findViewById(R.id.button2);
		button2.setOnClickListener(new ButtonListener());
		button3 = (Button) findViewById(R.id.button3);
		button3.setOnClickListener(new ButtonListener());
		textViewMileage = (TextView) findViewById(R.id.textViewMileage);
		textViewTotalTime = (TextView) findViewById(R.id.textViewTotalTime);
		textViewAverageSpeed = (TextView) findViewById(R.id.textViewAverageSpeed);
		textViewCurrentSpeed = (TextView) findViewById(R.id.textViewCurrentSpeed);
		textView3 = (TextView) findViewById(R.id.textView3);
		textView4 = (TextView) findViewById(R.id.textView_Satellites);
		textView5 = (TextView) findViewById(R.id.textView5);
		textviewResult = (TextView) findViewById(R.id.textView_result);
		textView5.setText("正在获取GPS数据");
		// 获取系统的定位管理器
		mgr = (LocationManager) MainActivity.this
				.getSystemService(Context.LOCATION_SERVICE);
	}

	private class ButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.button1:
				fileName = "details" + (int) (Math.random() * 1000);
				mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
						10, locationListener);
				mgr.addGpsStatusListener(gpsStatusListener);
				break;
			case R.id.button2:
				mgr.removeUpdates(locationListener);
			case R.id.button3:
				gpsStatusListener
						.onGpsStatusChanged(GpsStatus.GPS_EVENT_SATELLITE_STATUS);
			default:
				break;
			}

		}
	}

	GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
		@Override
		public void onGpsStatusChanged(int event) {
			// TODO Auto-generated method stub
			switch (event) {
			// 第一次定位
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				textView5.setText("第一次定位");
				break;
			// 卫星状态改变
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				textView5.setText("卫星状态改变");
				// 获取当前状态
				GpsStatus gpsStatus = mgr.getGpsStatus(null);
				// 获取卫星颗数的默认最大值
				int maxSatellites = gpsStatus.getMaxSatellites();
				// 创建一个迭代器保存所有卫星
				Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
						.iterator();
				int count = 0;
				while (iters.hasNext() && count <= maxSatellites) {
					GpsSatellite s = iters.next();
					count++;
				}
				textView4.setText("搜索到：" + count + "颗卫星");
				break;
			// 定位启动
			case GpsStatus.GPS_EVENT_STARTED:
				textView5.setText("定位启动");
				break;
			// 定位结束
			case GpsStatus.GPS_EVENT_STOPPED:
				textView5.setText("定位结束");
				break;
			}
		}
	};
	// 步骤3：位置监听器LocationListener 的设置，当位置发生变化是触发onLocationChanged( )
	LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			// 将location传递到管理类
			InstandView instandView = locationHandle.getNewLocation(location);
			// 更新instandView
			textViewMileage.setText(instandView.mileage);
			textViewTotalTime.setText(instandView.totalTime);
			textViewAverageSpeed.setText(instandView.averageSpeed);
			textViewCurrentSpeed.setText(instandView.currentSpeed);
			textviewResult.setText(instandView.result);
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			// GPS状态为可见时
			case LocationProvider.AVAILABLE:
				textView5.setText("当前GPS状态为可见状态");
				break;
			// GPS状态为服务区外时
			case LocationProvider.OUT_OF_SERVICE:
				textView5.setText("当前GPS状态为服务区外状态");
				break;
			// GPS状态为暂停服务时
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				textView5.setText("当前GPS状态为暂停服务状态");
				break;
			}
		}

		public void onProviderEnabled(String provider) {
			textView5.setText("GPS enable");
		}

		public void onProviderDisabled(String provider) {
			textView5.setText("GPS disable");
		}

		private double getDistance(double lat1, double lon1, double lat2,
				double lon2) {
			float[] results = new float[1];
			Location.distanceBetween(lat1, lon1, lat2, lon2, results);
			return results[0];
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// 持续跟踪中的停止update：在本例中，我们要求不断获得位置更新，我们必须在人工进行removeUpdates()，否则即使应用中所有的Activity都关闭，App仍继续在不断更新位置信息，导致应用资源无法被回收
	protected void onPause() {
		super.onPause();
		// mgr.removeUpdates(locationListener);
	}

	// 步骤 2.1（1）
	// ：我们在onResume()中持续跟踪，相应的在onPause()中关闭出现跟踪。我们只在Activity运行时进行跟踪。具体见Activity的生命周期
	protected void onResume() {
		super.onResume();
		// 步骤2.1
		// （2）：由于人的位置是不断变化，我要设置一个位置变化的范围，包括同时满足最小的时间间隔和最小的位移变化，如果两个条件要同时满足，将位置监听器将被触发。实际上该方法有多个参数格式，特别是requestLocationUpdates
		// (long minTime, float minDistance, Criteria criteria,PendingIntent
		// intent)，当位置变化时可调用其他的Activity。 在本例中，我们制定用GPS，则在权限中必须要求精确定位许可。
		// 第二个参数单位为毫秒,第三个参数单位为米
		// mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10,
		// locationListener);
	}

}
