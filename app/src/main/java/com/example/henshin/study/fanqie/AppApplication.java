package com.example.henshin.study.fanqie;

import android.app.Application;

public class AppApplication extends Application {

	public static AppApplication instances = null;
	
	private WorkModel work = new WorkModel();
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	public static AppApplication getInstances(){
		if(instances == null){
			instances = new AppApplication();
		}
		return instances;
	}

	public WorkModel getWork() {
		return work;
	}

	public void setWork(WorkModel work) {
		this.work = work;
	}

	public void InitWork(){
		this.work = new WorkModel();
	}
}
