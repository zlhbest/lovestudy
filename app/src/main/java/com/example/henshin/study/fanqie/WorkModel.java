package com.example.henshin.study.fanqie;

public class WorkModel {

	public int status = 0;
	
	public String workname = "工作中...";

	public int worklong = 30 * 60;

	public int foodlong = 5 * 60;

	public int currentlong = 0;

	public int gamelong = 20 * 60;

	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getWorkname() {
		return workname;
	}

	public void setWorkname(String workname) {
		this.workname = workname;
	}

	public int getWorklong() {
		return worklong;
	}

	public void setWorklong(int worklong) {
		this.worklong = worklong;
	}

	public int getFoodlong() {
		return foodlong;
	}

	public void setFoodlong(int foodlong) {
		this.foodlong = foodlong;
	}

	public int getGamelong() {
		return gamelong;
	}

	public void setGamelong(int gamelong) {
		this.gamelong = gamelong;
	}

	public int getCurrentlong() {
		return currentlong;
	}

	public void setCurrentlong(int currentlong) {
		this.currentlong = currentlong;
	}


}
