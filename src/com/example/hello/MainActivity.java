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
		textView5.setText("���ڻ�ȡGPS����");
		// ��ȡϵͳ�Ķ�λ������
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
			// ��һ�ζ�λ
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				textView5.setText("��һ�ζ�λ");
				break;
			// ����״̬�ı�
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				textView5.setText("����״̬�ı�");
				// ��ȡ��ǰ״̬
				GpsStatus gpsStatus = mgr.getGpsStatus(null);
				// ��ȡ���ǿ�����Ĭ�����ֵ
				int maxSatellites = gpsStatus.getMaxSatellites();
				// ����һ��������������������
				Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
						.iterator();
				int count = 0;
				while (iters.hasNext() && count <= maxSatellites) {
					GpsSatellite s = iters.next();
					count++;
				}
				textView4.setText("��������" + count + "������");
				break;
			// ��λ����
			case GpsStatus.GPS_EVENT_STARTED:
				textView5.setText("��λ����");
				break;
			// ��λ����
			case GpsStatus.GPS_EVENT_STOPPED:
				textView5.setText("��λ����");
				break;
			}
		}
	};
	// ����3��λ�ü�����LocationListener �����ã���λ�÷����仯�Ǵ���onLocationChanged( )
	LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			// ��location���ݵ�������
			InstandView instandView = locationHandle.getNewLocation(location);
			// ����instandView
			textViewMileage.setText(instandView.mileage);
			textViewTotalTime.setText(instandView.totalTime);
			textViewAverageSpeed.setText(instandView.averageSpeed);
			textViewCurrentSpeed.setText(instandView.currentSpeed);
			textviewResult.setText(instandView.result);
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			// GPS״̬Ϊ�ɼ�ʱ
			case LocationProvider.AVAILABLE:
				textView5.setText("��ǰGPS״̬Ϊ�ɼ�״̬");
				break;
			// GPS״̬Ϊ��������ʱ
			case LocationProvider.OUT_OF_SERVICE:
				textView5.setText("��ǰGPS״̬Ϊ��������״̬");
				break;
			// GPS״̬Ϊ��ͣ����ʱ
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				textView5.setText("��ǰGPS״̬Ϊ��ͣ����״̬");
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

	// ���������е�ֹͣupdate���ڱ����У�����Ҫ�󲻶ϻ��λ�ø��£����Ǳ������˹�����removeUpdates()������ʹӦ�������е�Activity���رգ�App�Լ����ڲ��ϸ���λ����Ϣ������Ӧ����Դ�޷�������
	protected void onPause() {
		super.onPause();
		// mgr.removeUpdates(locationListener);
	}

	// ���� 2.1��1��
	// ��������onResume()�г������٣���Ӧ����onPause()�йرճ��ָ��١�����ֻ��Activity����ʱ���и��١������Activity����������
	protected void onResume() {
		super.onResume();
		// ����2.1
		// ��2���������˵�λ���ǲ��ϱ仯����Ҫ����һ��λ�ñ仯�ķ�Χ������ͬʱ������С��ʱ��������С��λ�Ʊ仯�������������Ҫͬʱ���㣬��λ�ü���������������ʵ���ϸ÷����ж��������ʽ���ر���requestLocationUpdates
		// (long minTime, float minDistance, Criteria criteria,PendingIntent
		// intent)����λ�ñ仯ʱ�ɵ���������Activity�� �ڱ����У������ƶ���GPS������Ȩ���б���Ҫ��ȷ��λ��ɡ�
		// �ڶ���������λΪ����,������������λΪ��
		// mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10,
		// locationListener);
	}

}
