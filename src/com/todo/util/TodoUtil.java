package com.todo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
		
		System.out.println("\n=== 데이터 추가 ===");
		System.out.print("제목 : ");
		String title = scan.nextLine().trim();
		System.out.print("내용 : ");
		String desc = scan.nextLine().trim();
		System.out.print("카테고리 : ");
		String category = scan.nextLine().trim();
		System.out.print("마감일 : ");
		String dueDate = scan.nextLine().trim();
		
		String createInsert = "insert into " + this.tableName + " (title, desc, category, dueDate, currDate)"
				+ "values ('" + title + "', '" + desc + "', '" + category + "', '" + dueDate + "', datetime('now', 'localtime'));";
		if(stat.executeUpdate(createInsert) > 0)
			System.out.println("데이터가 추가되었습니다.");
		else
			System.err.println("데이터 추가에 실패했습니다!");
		
		stat.close();
		connect.close();
	}
	
	public void readTodo() throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
		
		System.out.println("\n=== 데이터 조회 ===");
		String readSelect = "select * from " + tableName;
		ResultSet result = stat.executeQuery(readSelect);
		
		System.out.println("총 " + result.getRow() + "개의 항목을 발견했습니다.");
		while(result.next()) {
			String id = result.getString("id");
			String title = result.getString("title");
			String desc = result.getString("desc");
			String category = result.getString("category");
			String dueDate = result.getString("dueDate");
			String currDate = result.getString("currDate");
			System.out.println(String.format("%2s [%s] %s | %s - %s ~ %s", id, category, title, desc, currDate, dueDate));
		}
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
		System.out.print("새 내용 : ");
		String desc = scan.nextLine().trim();
		System.out.print("새 카테고리 : ");
		String category = scan.nextLine().trim();
		System.out.print("새 마감일 : ");
		String dueDate = scan.nextLine().trim();
		
		String updateUpdate = "update " + this.tableName
				+ " set title = '" + title + "', desc = '" + desc
				+ "', category = '" + category + "', dueDate = '" + dueDate
				+ "', currDate = datetime('now', 'localtime') where title = '" + target + "';";
		if(stat.executeUpdate(updateUpdate) > 0)
			System.out.println("데이터가 수정되었습니다.");
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
		if(stat.executeUpdate(deleteDelete) > 0)
			System.out.println("데이터가 수정되었습니다.");
		else
			System.err.println("데이터 수정에 실패했습니다!");
		
		stat.close();
		connect.close();
	}
	
	public void findTodo() throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
		
		System.out.println("\n=== 키워드 검색 ===");
		System.out.print("검색할 키워드 : ");
		String keyword = scan.nextLine().trim();
		
		String readSelect = "select * from " + this.tableName
				+ " where title like '" + keyword + "' or desc like '" + keyword + "'";
		ResultSet result = stat.executeQuery(readSelect);
		
		System.out.println("총 " + result.getRow() + "개의 항목을 발견했습니다.");
		while(result.next()) {
			String id = result.getString("id");
			String title = result.getString("title");
			String desc = result.getString("desc");
			String category = result.getString("category");
			String dueDate = result.getString("dueDate");
			String currDate = result.getString("currDate");
			System.out.println(String.format("%2s [%s] %s | %s - %s ~ %s", id, category, title, desc, currDate, dueDate));
		}
		
		stat.close();
		connect.close();
	}
	
	public void categoryList() throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
		
		String readSelect = "select distinct category from " + this.tableName;
		ResultSet result = stat.executeQuery(readSelect);
		
		System.out.println("\n총 " + result.getRow() + "개의 카테고리를 발견했습니다.");
		while(result.next())
			System.out.println(result.getString(0) + " ");
		
		stat.close();
		connect.close();
	}
	

	public void findCategory() throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
		
		System.out.println("\n=== 카테고리 검색 ===");
		System.out.print("검색할 카테고리 : ");
		String keyword = scan.nextLine().trim();
		
		String readSelect = "select * from " + this.tableName + " where category like '" + keyword + "'";
		ResultSet result = stat.executeQuery(readSelect);
		
		System.out.println("총 " + result.getRow() + "개의 항목을 발견했습니다.");
		while(result.next()) {
			String id = result.getString("id");
			String title = result.getString("title");
			String desc = result.getString("desc");
			String category = result.getString("category");
			String dueDate = result.getString("dueDate");
			String currDate = result.getString("currDate");
			System.out.println(String.format("%2s [%s] %s | %s - %s ~ %s", id, category, title, desc, currDate, dueDate));
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
		
		while(result.next()) {
			String id = result.getString("id");
			String title = result.getString("title");
			String desc = result.getString("desc");
			String category = result.getString("category");
			String dueDate = result.getString("dueDate");
			String currDate = result.getString("currDate");
			System.out.println(String.format("%2s [%s] %s | %s - %s ~ %s", id, category, title, desc, currDate, dueDate));
		}
		
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
		
		while(result.next()) {
			String id = result.getString("id");
			String title = result.getString("title");
			String desc = result.getString("desc");
			String category = result.getString("category");
			String dueDate = result.getString("dueDate");
			String currDate = result.getString("currDate");
			System.out.println(String.format("%2s [%s] %s | %s - %s ~ %s", id, category, title, desc, currDate, dueDate));
		}
		
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
		
		while(result.next()) {
			String id = result.getString("id");
			String title = result.getString("title");
			String desc = result.getString("desc");
			String category = result.getString("category");
			String dueDate = result.getString("dueDate");
			String currDate = result.getString("currDate");
			System.out.println(String.format("%2s [%s] %s | %s - %s ~ %s", id, category, title, desc, currDate, dueDate));
		}
		
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
		
		while(result.next()) {
			String id = result.getString("id");
			String title = result.getString("title");
			String desc = result.getString("desc");
			String category = result.getString("category");
			String dueDate = result.getString("dueDate");
			String currDate = result.getString("currDate");
			System.out.println(String.format("%2s [%s] %s | %s - %s ~ %s", id, category, title, desc, currDate, dueDate));
		}
		
		stat.close();
		connect.close();
	}
}