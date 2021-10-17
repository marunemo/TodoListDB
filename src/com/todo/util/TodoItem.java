package com.todo.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TodoItem {
	public String id;
	public String title; 
	public String desc;
	public String category;
	public String dueDate;
	public String currDate;
	public int isCompleted;
	public int isRoutine;
	public int isRequired; 
	SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
	
	public TodoItem() {}
	
	public TodoItem(ResultSet rs) {
		try {
			this.id = rs.getString("id");
			this.title = rs.getString("title");
			this.desc = rs.getString("desc");
			this.category = rs.getString("category");
			int isRoutine = rs.getInt("isRoutine");
			if(isRoutine == 1) {
				Date today = new Date();
				this.currDate = format.format(today) + " 00:00:00";
				this.dueDate = format.format(today) + " 23:59:59";
			}
			else {
				this.dueDate = rs.getString("dueDate");
				this.currDate = rs.getString("currDate").replace('-', '/');
			}
			this.isCompleted = rs.getInt("isCompleted");
			this.isRequired = rs.getInt("isRequired");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public TodoItem(ResultSet rs, String category) {
		try {
			this.id = rs.getString("id");
			this.title = rs.getString("title");
			this.desc = rs.getString("desc");
			this.category = category;
			int isRoutine = rs.getInt("isRoutine");
			if(isRoutine == 1) {
				Date today = new Date();
				this.currDate = format.format(today) + " 00:00:00";
				this.dueDate = format.format(today) + " 23:59:59";
			}
			else {
				this.dueDate = rs.getString("dueDate");
				this.currDate = rs.getString("currDate").replace('-', '/');
			}
			this.isCompleted = rs.getInt("isCompleted");
			this.isRequired = rs.getInt("isRequired");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return String.format("%2s [%s] %s%s%s | %s - %s ~ %s", id, category, (isRequired==1?"★ ":""), title, (isCompleted==1?"[V]":""), desc, currDate, dueDate);
	}
}
