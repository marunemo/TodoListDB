package com.todo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Scanner;

public class TodoUtil {
	private String dbFile;
	private String tableName;
	private Scanner scan = new Scanner(System.in);
	
	public TodoUtil (String dbName, String tableName) throws ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		
		this.dbFile = dbName;
		this.tableName = tableName;
	}
	
	public void createTodo() throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
		String title, desc, category, dueDate, currDate;
		int isRoutine, isRequired; 
		
		System.out.println("\n=== 데이터 추가 ===");
		System.out.print("제목 : ");
		title = scan.nextLine().trim();
		
		if(containsTitle(title) != -1) {
			System.err.println("이미 존재하는 제목입니다!!");
			return;
		}
		
		System.out.print("내용 : ");
		desc = scan.nextLine().trim();
		System.out.print("카테고리 : ");
		category = scan.nextLine().trim();
		System.out.print("매일 수행할 활동으로 설정하시겠습니까? (y/n) ");
		isRoutine = (scan.nextLine().trim().matches("[yY]")?1:0);
		if(isRoutine == 1)
			dueDate = "";
		else {
			System.out.print("마감일 : ");
			dueDate = scan.nextLine().trim();
		}
		System.out.print("중요 활동으로 설정하시겠습니까? (y/n) ");
		isRequired = (scan.nextLine().trim().matches("[yY]")?1:0);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		Date today = new Date();
		currDate = format.format(today);
		String createInsert = "insert into " + this.tableName + " (title, desc, category, dueDate, currDate, isCompleted, isRoutine, isRequired)"
				+ "values ('" + title + "', '" + desc + "', '" + category + "', '" + dueDate + "', '" + currDate + "', 0, " + isRoutine + ", " + isRequired +");";
		if(stat.executeUpdate(createInsert) > 0) {
			System.out.println("데이터가 추가되었습니다.");
			String createCateTable = "create table if not exists " + category
					+ " (title text, desc text, dueDate text,"
					+ " currDate text, isCompleted int, isRoutine int, isRequired int)";
			if(stat.executeUpdate(createCateTable) > 0) {
				String createCategory = "insert into " + category + " (title, desc, dueDate, currDate, isCompleted, isRoutine, isRequired)"
						+ "values ('" + title + "', '" + desc + "', '" + dueDate + "', '" + currDate + "', 0, " + isRoutine + ", " + isRequired +");";
				stat.executeUpdate(createCategory);
				System.out.println("카테고리 테이블이 추가되었습니다.");
			}
			else {
				System.err.println("카테고리 테이블 추가에 실패했습니다!");
			}
		}
		else
			System.err.println("데이터 추가에 실패했습니다!");
		
		stat.close();
		connect.close();
	}
	
	public void readTodo() throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
		
		System.out.println("\n=== 데이터 조회 ===");
		ResultSet count = stat.executeQuery("select count(*) from " + this.tableName);
		if(count.next())
			System.out.println("총 " + count.getInt(1) + "개의 항목을 발견했습니다.");
		
		String readSelect = "select * from " + this.tableName;
		ResultSet result = stat.executeQuery(readSelect);
		listAll(result);
		stat.close();
		connect.close();
	}
	
	public void updateTodo() throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
		
		System.out.println("\n=== 데이터 수정 ===");
		System.out.print("수정할 제목 : ");
		String target = scan.nextLine().trim();
		
		System.out.print("새 제목 : ");
		String title = scan.nextLine().trim();
		
		if(!target.equals(title) && containsTitle(title) != -1) {
			System.err.println("이미 존재하는 제목입니다!!");
			return;
		}
		
		System.out.print("새 내용 : ");
		String desc = scan.nextLine().trim();
		System.out.print("새 카테고리 : ");
		String category = scan.nextLine().trim();
		System.out.print("매일 수행할 활동으로 설정하시겠습니까? (y/n) ");
		int isRoutine = (scan.nextLine().trim().matches("[yY]")?1:0);
		String dueDate = "";
		if(isRoutine != 1) {			
			System.out.print("새 마감일 : ");
			dueDate = scan.nextLine().trim();
		}
		System.out.print("중요 활동으로 설정하시겠습니까? (y/n) ");
		int isRequired = (scan.nextLine().trim().matches("[yY]")?1:0);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		Date today = new Date();
		String currDate = format.format(today);
		String updateUpdate = "update " + this.tableName
				+ " set title = '" + title + "', desc = '" + desc
				+ "', category = '" + category + "', dueDate = '" + dueDate
				+ "', currDate = '" + currDate + "', isRoutine = " + isRoutine 
				+ ", isRequired = " + isRequired + " where title = '" + target + "';";
		if(stat.executeUpdate(updateUpdate) > 0) {
			String updateCategory = "update " + category
					+ " set title = '" + title + "', desc = '" + desc
					+ "', dueDate = '" + dueDate
					+ "', currDate = '" + currDate + "', isRoutine = " + isRoutine 
					+ ", isRequired = " + isRequired + " where title = '" + target + "';";
			stat.executeUpdate(updateCategory);
			System.out.println("데이터가 수정되었습니다.");
		}
		else
			System.err.println("데이터 수정에 실패했습니다!");
		
		stat.close();
		connect.close();
	}
	
	public void deleteTodo() throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
		
		System.out.println("\n=== 데이터 삭제 ===");
		System.out.print("삭제할 제목 : ");
		String title = scan.nextLine().trim();
		
		
		String deleteDelete = "delete from " + this.tableName + " where title = '" + title + "';";
		if(stat.executeUpdate(deleteDelete) > 0) {
			String category = getCategory(connect, stat, this.tableName, title);
			int dataCount = 0;
			ResultSet count = stat.executeQuery("select count(*) from " + category);
			if(count.next())
				dataCount = count.getInt(1);
			if(dataCount <= 1) {
				String deleteTable = "drop table " + category + ";";
				stat.executeUpdate(deleteTable);
			}
			else {
				String deleteCategory = "delete from " + category + " where title = '" + title + "';";
				stat.executeUpdate(deleteCategory);
			}
			System.out.println("데이터가 수정되었습니다.");
		}
		else
			System.err.println("데이터 수정에 실패했습니다!");
		
		stat.close();
		connect.close();
	}
	
	public void findTodo(String keyword) throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
		
		System.out.println("\n=== 키워드 검색 ===");
		ResultSet count = stat.executeQuery("select count(*) from " + this.tableName
				+ " where title like '%" + keyword + "%' or desc like '%" + keyword + "%'");
		if(count.next())
			System.out.println("키워드 <" + keyword + "> 를 포함한 총 " + count.getInt(1) + "개의 항목을 발견했습니다.");
		
		String readSelect = "select * from " + this.tableName
				+ " where title like '%" + keyword + "%' or desc like '%" + keyword + "%'";
		ResultSet result = stat.executeQuery(readSelect);
		listAll(result);
		stat.close();
		connect.close();
	}
	
	public void categoryList() throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
		
		ResultSet count = stat.executeQuery("select count(*) from sqlite_master where type = 'table' and not name = 'sqlite_sequence' and not name = 'TodoItem';");
		if(count.next())
			System.out.println("총 " + count.getInt(1) + "개의 카테고리를 발견했습니다.");
		
		String readSelect = "select name from sqlite_master where type = 'table' and not name = 'sqlite_sequence' and not name = 'TodoItem';";
		ResultSet result = stat.executeQuery(readSelect);		
		while(result.next())
			System.out.print("[" + result.getString(1) + "] ");
		System.out.println();
		
		stat.close();
		connect.close();
	}
	

	public void findCategory(String keyword) throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
		ArrayList<String> cateList = new ArrayList<String>();
		ArrayList<String> dataList = new ArrayList<String>();
		
		System.out.println("\n=== 카테고리 검색 ===");
		ResultSet count = stat.executeQuery("select name from sqlite_master where type = 'table' and not name = 'sqlite_sequence' and not name = 'TodoItem' and name like '%" + keyword + "%';");
		while(count.next())
			cateList.add(count.getString(1));
		
		System.out.println("카테고리 <" + keyword + "> 를 포함한 총 " + cateList.size() + "개의 항목을 발견했습니다.");		
		for(String category : cateList) {
			String readSelect = "select * from " + category;
			ResultSet result = stat.executeQuery(readSelect);
			listAll(result, category);
		}
		
		stat.close();
		connect.close();
	}
	
	public void nameList() throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
		
		System.out.println("\n=== 이름순 정렬 ===");
		String readSelect = "select * from " + this.tableName + " order by title";
		ResultSet result = stat.executeQuery(readSelect);
		
		if(result.isBeforeFirst())
			System.out.println("데이터가 정렬되었습니다.");
		else
			System.out.println("정렬할 데이터가 없습니다!");
		
		listAll(result);
		stat.close();
		connect.close();
	}
	
	public void nameList(boolean isAsc) throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
		
		if(isAsc)
			System.out.println("\n=== 이름순 정렬 ===");
		else
			System.out.println("\n=== 이름역순 정렬 ===");
		
		String readSelect = "select * from " + this.tableName + " order by title" + (isAsc?"":" desc");
		ResultSet result = stat.executeQuery(readSelect);
		
		if(result.isBeforeFirst())
			System.out.println("데이터가 정렬되었습니다.");
		else
			System.out.println("정렬할 데이터가 없습니다!");
		
		listAll(result);
		stat.close();
		connect.close();
	}
	
	public void dateList() throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
		
		System.out.println("\n=== 날짜순 정렬 ===");
		String readSelect = "select * from " + this.tableName + " order by dueDate";
		ResultSet result = stat.executeQuery(readSelect);
		
		if(result.isBeforeFirst())
			System.out.println("데이터가 정렬되었습니다.");
		else
			System.out.println("정렬할 데이터가 없습니다!");
		
		listAll(result);
		stat.close();
		connect.close();
	}
	
	public void dateList(boolean isAsc) throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
		
		if(isAsc)
			System.out.println("\n=== 날짜순 정렬 ===");
		else
			System.out.println("\n=== 날짜역순 정렬 ===");
		
		String readSelect = "select * from " + this.tableName + " order by dueDate" + (isAsc?"":" desc");
		ResultSet result = stat.executeQuery(readSelect);
		
		if(result.isBeforeFirst())
			System.out.println("데이터가 정렬되었습니다.");
		else
			System.out.println("정렬할 데이터가 없습니다!");
				
		listAll(result);
		stat.close();
		connect.close();
	}
	
	public void completeTodo(int index) throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
				
		String updateUpdate = "update " + this.tableName
				+ " set isCompleted = 1 where id = '" + index + "';";
		if(stat.executeUpdate(updateUpdate) > 0)
			System.out.println("항목이 완료되었습니다.");
		else
			System.err.println("완료 설정에 실패했습니다!");
		
		stat.close();
		connect.close();
	}
	
	public void completeList() throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
				
		ResultSet count = stat.executeQuery("select count(*) from " + this.tableName + " where isCompleted = 1");
		if(count.next())
			System.out.println("총 " + count.getInt(1) + "개의 항목을 발견했습니다.");
		
		String readSelect = "select * from " + this.tableName + " where isCompleted = 1";
		ResultSet result = stat.executeQuery(readSelect);
		listAll(result);
		
		stat.close();
		connect.close();
	}
	
	public int containsTitle(String title) throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
		int returnId = -1;
		
		ResultSet count = stat.executeQuery("select id from " + this.tableName + " where title = '" + title + "'");
		if(count.next()) returnId = count.getInt(1);
		
		stat.close();
		connect.close();
		
		return returnId;
	}
	
	private void listAll(ResultSet rs) throws SQLException {
		while(rs.next()) {
			String id = rs.getString("id");
			String title = rs.getString("title");
			String desc = rs.getString("desc");
			String category = rs.getString("category");
			String dueDate = rs.getString("dueDate");
			String currDate = rs.getString("currDate").replace('-', '/');
			int isCompleted = rs.getInt("isCompleted");
			int isRoutine = rs.getInt("isRoutine");
			int isRequired = rs.getInt("isRequired");
			if(isRoutine == 1) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
				Date today = new Date();
				currDate = format.format(today) + " 00:00:00";
				dueDate = format.format(today) + " 23:59:59";
			}
			System.out.println(String.format("%2s [%s] %s%s%s | %s - %s ~ %s", id, category, (isRequired==1?"★ ":""), title, (isCompleted==1?"[V]":""), desc, currDate, dueDate));
		}
	}
	
	private void listAll(ResultSet rs, String category) throws SQLException {
		while(rs.next()) {
			String id = rs.getString("id");
			String title = rs.getString("title");
			String desc = rs.getString("desc");
			String dueDate = rs.getString("dueDate");
			String currDate = rs.getString("currDate").replace('-', '/');
			int isCompleted = rs.getInt("isCompleted");
			int isRoutine = rs.getInt("isRoutine");
			int isRequired = rs.getInt("isRequired");
			if(isRoutine == 1) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
				Date today = new Date();
				currDate = format.format(today) + " 00:00:00";
				dueDate = format.format(today) + " 23:59:59";
			}
			System.out.println(String.format("%2s [%s] %s%s%s | %s - %s ~ %s", id, category, (isRequired==1?"★ ":""), title, (isCompleted==1?"[V]":""), desc, currDate, dueDate));
		}
	}
	
	private int getId(Connection connect, Statement stat, String tableName, String title) throws SQLException {
		int targetId = -1;
		ResultSet id = stat.executeQuery("select id from " + tableName + " where title = '" + title + "';");
		if(id.next())
			targetId = id.getInt(1);
		return targetId;
	}
	
	private String getCategory(Connection connect, Statement stat, String tableName, String title) throws SQLException {
		String targetCategory = "";
		ResultSet category = stat.executeQuery("select category from " + tableName + " where title = '" + title + "';");
		if(category.next())
			targetCategory = category.getString(1);
		return targetCategory;
	}
}