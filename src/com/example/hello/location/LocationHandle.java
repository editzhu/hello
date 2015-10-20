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
	private long startTime;// 开始时间
	private double tmpLat1 = 0, tmpLon1 = 0;// 临时变量,记录上一次经纬度的数值,用于计算距离
	private double mileage = 0;// 里程,单位:公里
	private long totalTime = 0;// 总时长 单位:秒
	private int tmpi = 0;// 临时变量
	private double segmentDistance = 0;// 按公里分段的相关变量
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

		// 记录经纬度到临时变量
		tmpLat1 = extendLocation.latitude;
		tmpLon1 = extendLocation.longitude;
		// 加入数据到总体变量
		mileage += extendLocation.distance;
		totalTime = (extendLocation.currentTime - startTime) / 1000;// 毫秒转化成秒

		// 处理分段数据
		if (mileage > 1000 * segmentI) {
			segmentSpeed = (float) (mileage - segmentDistance)
					/ (totalTime - segmentTime);
			segmentI++;
			segmentDistance = mileage;
			segmentTime = totalTime;
		}

		// 插入列表
		locationList.add(extendLocation);

		// 更新instandView
		InstandView instandView = new InstandView();
		instandView.mileage = String
				.valueOf(((float) (int) (mileage / 1000 * 100)) / 100);
		instandView.totalTime = secondsFormat(totalTime);
		instandView.averageSpeed = String.valueOf(((float) (int) (mileage
				/ totalTime * 3600 / 1000 * 100)) / 100);
		instandView.currentSpeed = String.valueOf(extendLocation.speed);
		// instandView.result = tmpi++ + "速度:" + extendLocation.speed + ":距离:"
		// + extendLocation.distance + "\n";
		instandView.result = "速度:" + segmentSpeed;
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
