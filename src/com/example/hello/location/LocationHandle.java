package com.example.hello.location;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.location.Location;

import com.example.hello.view.InstandView;

public class LocationHandle {
	boolean isFirstLocation = true;
	private List<ExtendLocation> locationList = new ArrayList<ExtendLocation>();
	private long startTime;// ��ʼʱ��
	private double tmpLat1 = 0, tmpLon1 = 0;// ��ʱ����,��¼��һ�ξ�γ�ȵ���ֵ,���ڼ������
	private double mileage = 0;// ���,��λ:����
	private long totalTime = 0;// ��ʱ�� ��λ:��
	private int tmpi = 0;// ��ʱ����
	private double segmentDistance = 0;// ������ֶε���ر���
	private long segmentTime = 0;
	private int segmentI = 1;
	private float segmentSpeed = 0;

	public InstandView getNewLocation(Location l) {

		ExtendLocation extendLocation = new ExtendLocation();
		extendLocation.latitude = l.getLatitude();
		extendLocation.longitude = l.getLongitude();
		extendLocation.speed = l.getSpeed();
		extendLocation.currentTime = l.getTime();
		if (isFirstLocation) {
			extendLocation.distance = 0;
			startTime = extendLocation.currentTime;
			isFirstLocation = false;
		} else {
			float[] results = new float[1];
			Location.distanceBetween(tmpLat1, tmpLon1, extendLocation.latitude,
					extendLocation.longitude, results);
			extendLocation.distance = results[0];
		}

		// ��¼��γ�ȵ���ʱ����
		tmpLat1 = extendLocation.latitude;
		tmpLon1 = extendLocation.longitude;
		// �������ݵ��������
		mileage += extendLocation.distance;
		totalTime = (extendLocation.currentTime - startTime) / 1000;// ����ת������

		// ����ֶ�����
		if (mileage > 1000 * segmentI) {
			segmentSpeed = (float) (mileage - segmentDistance)
					/ (totalTime - segmentTime);
			segmentI++;
			segmentDistance = mileage;
			segmentTime = totalTime;
		}

		// �����б�
		locationList.add(extendLocation);

		// ����instandView
		InstandView instandView = new InstandView();
		instandView.mileage = String
				.valueOf(((float) (int) (mileage / 1000 * 100)) / 100);
		instandView.totalTime = secondsFormat(totalTime);
		instandView.averageSpeed = String.valueOf(((float) (int) (mileage
				/ totalTime * 3600 / 1000 * 100)) / 100);
		instandView.currentSpeed = String.valueOf(extendLocation.speed);
		// instandView.result = tmpi++ + "�ٶ�:" + extendLocation.speed + ":����:"
		// + extendLocation.distance + "\n";
		instandView.result = "�ٶ�:" + segmentSpeed;
		return instandView;
	}

	public void saveToFile(ArrayList<InstandView> l)
			throws FileNotFoundException, IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
				"d:/d.dat"));
		out.writeObject(l);
		out.flush();
		out.close();
	}

	private String secondsFormat(long a) {
		long hours = a / 3600;
		long seconds = a % 3600;
		long minutes = seconds / 60;
		seconds = seconds % 60;
		return (hours + ":" + minutes + ":" + seconds);
	}
}
