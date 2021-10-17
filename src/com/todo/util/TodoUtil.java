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
		TodoItem todo = new TodoItem();
		
		System.out.println("\n=== 데이터 추가 ===");
		System.out.print("제목 : ");
		todo.title = scan.nextLine().trim();
		
		if(containsTitle(todo.title) != -1) {
			System.err.println("이미 존재하는 제목입니다!!");
			return;
		}
		
		System.out.print("내용 : ");
		todo.desc = scan.nextLine().trim();
		System.out.print("카테고리 : ");
		todo.category = scan.nextLine().trim();
		System.out.print("매일 수행할 활동으로 설정하시겠습니까? (y/n) ");
		todo.isRoutine = (scan.nextLine().trim().matches("[yY]")?1:0);
		if(todo.isRoutine == 1)
			todo.dueDate = "";
		else {
			System.out.print("마감일 : ");
			todo.dueDate = scan.nextLine().trim();
		}
		System.out.print("중요 활동으로 설정하시겠습니까? (y/n) ");
		todo.isRequired = (scan.nextLine().trim().matches("[yY]")?1:0);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss");
		Date today = new Date();
		todo.currDate = format.format(today);
		String createInsert = "insert into " + this.tableName + " (title, desc, category, dueDate, currDate, isCompleted, isRoutine, isRequired)"
				+ "values ('" + todo.title + "', '" + todo.desc + "', '" + todo.category + "', '" + todo.dueDate + "', '" + todo.currDate + "', 0, " + todo.isRoutine + ", " + todo.isRequired +");";
		if(stat.executeUpdate(createInsert) > 0) {
			todo.id = "" + getId(connect, stat, this.tableName, todo.title);
			createCateTable(connect, stat, todo);
			System.out.println("데이터가 추가되었습니다.");
		}
		else
			System.err.println("데이터 추가에 실패했습니다!");
		
		stat.close();
		connect.close();
	}
	
	private void createCateTable(Connection connect, Statement stat, TodoItem item) throws SQLException {
		String createCateTable = "create table if not exists " + item.category
				+ " (id int, title text, desc text, dueDate text,"
				+ " currDate text, isCompleted int, isRoutine int, isRequired int);";
		stat.execute(createCateTable);
		
		String createCategory = "insert into " + item.category
				+ " (id, title, desc, dueDate, currDate, isCompleted, isRoutine, isRequired)"
				+ "values (" + item.id + ", '" + item.title + "', '" + item.desc + "', '"
				+ item.dueDate + "', '" + item.currDate + "', 0, " + item.isRoutine + ", "
				+ item.isRequired +");";
		stat.executeUpdate(createCategory);
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
		TodoItem todo = new TodoItem();
		
		System.out.println("\n=== 데이터 수정 ===");
		System.out.print("수정할 제목 : ");
		String target = scan.nextLine().trim();
		
		if(containsTitle(target) == -1) {
			System.err.println("존재하지 않는 제목입니다!!");
			return;
		}
		String targetCategory = getCategory(connect, stat, this.tableName, target);
		
		System.out.print("새 제목 : ");
		todo.title = scan.nextLine().trim();
		
		if(!target.equals(todo.title) && containsTitle(todo.title) != -1) {
			System.err.println("이미 존재하는 제목입니다!!");
			return;
		}
		
		System.out.print("새 내용 : ");
		todo.desc = scan.nextLine().trim();
		System.out.print("새 카테고리 : ");
		todo.category = scan.nextLine().trim();
		System.out.print("매일 수행할 활동으로 설정하시겠습니까? (y/n) ");
		todo.isRoutine = (scan.nextLine().trim().matches("[yY]")?1:0);
		todo.dueDate = "";
		if(todo.isRoutine != 1) {			
			System.out.print("새 마감일 : ");
			todo.dueDate = scan.nextLine().trim();
		}
		System.out.print("중요 활동으로 설정하시겠습니까? (y/n) ");
		todo.isRequired = (scan.nextLine().trim().matches("[yY]")?1:0);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss");
		Date today = new Date();
		todo.currDate = format.format(today);
		String updateUpdate = "update " + this.tableName
				+ " set title = '" + todo.title + "', desc = '" + todo.desc
				+ "', category = '" + todo.category + "', dueDate = '" + todo.dueDate
				+ "', currDate = '" + todo.currDate + "', isRoutine = " + todo.isRoutine 
				+ ", isRequired = " + todo.isRequired + " where title = '" + target + "';";
		if(stat.executeUpdate(updateUpdate) > 0) {
			deleteCateTable(connect, stat, targetCategory, target);
			todo.id = "" + getId(connect, stat, this.tableName, todo.title);
			createCateTable(connect, stat, todo);
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
		String category = getCategory(connect, stat, this.tableName, title);
		
		String deleteDelete = "delete from " + this.tableName + " where title = '" + title + "';";
		if(stat.executeUpdate(deleteDelete) > 0) {
			deleteCateTable(connect, stat, category, title);
			System.out.println("데이터가 수정되었습니다.");
		}
		else
			System.err.println("데이터 수정에 실패했습니다!");
		
		stat.close();
		connect.close();
	}
	
	public void deleteTodo(String multi_item) throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
		
		String[] items = multi_item.split(",");
		for(String item : items) {
			String title = item.trim();
			String category = getCategory(connect, stat, this.tableName, title);
			
			String deleteDelete = "delete from " + this.tableName + " where title = '" + title + "';";
			if(stat.executeUpdate(deleteDelete) > 0) {
				deleteCateTable(connect, stat, category, title);
			}
			else {
				System.err.println("데이터 수정에 실패했습니다! (error: " + title + ")");
				return;
			}
		}
		System.out.println("데이터가 수정되었습니다.");
		
		stat.close();
		connect.close();
	}
	
	private void deleteCateTable(Connection connect, Statement stat, String category, String title) throws SQLException {
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
		
		System.out.println("\n=== 카테고리 검색 ===");
		ResultSet count = stat.executeQuery("select name from sqlite_master where type = 'table' and not name = 'sqlite_sequence' and not name = 'TodoItem' and name like '%" + keyword + "%';");
		while(count.next())
			cateList.add(count.getString(1));
		
		System.out.println("키워드 <" + keyword + "> 를 포함한 총 " + cateList.size() + "개의 카테고리를 발견했습니다.");		
		for(String category : cateList) {
			String readSelect = "select * from " + category + " order by id";
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
	
	public void completeTodo(String multi_item) throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
				
		String[] items = multi_item.split(",");
		for(String item : items) {
			String title = item.trim();
			String updateUpdate = "update " + this.tableName
					+ " set isCompleted = 1 where title = '" + title + "';";
			if(stat.executeUpdate(updateUpdate) > 0) {
				String updateCategory = "update " + getCategory(connect, stat, this.tableName, title)
						+ " set isCompleted = 1 where title = '" + title + "';";
				stat.executeUpdate(updateCategory);
			}
			else {
				System.err.println("완료 설정에 실패했습니다!");
				return;
			}
		}
		System.out.println("항목이 완료되었습니다.");
		
		stat.close();
		connect.close();
	}
	
	public void completeList() throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
				
		ResultSet count = stat.executeQuery("select count(*) from " + this.tableName + " where isCompleted = 1");
		if(count.next())
			System.out.println("총 " + count.getInt(1) + "개의 완료 항목을 발견했습니다.");
		
		String readSelect = "select * from " + this.tableName + " where isCompleted = 1";
		ResultSet result = stat.executeQuery(readSelect);
		listAll(result);
		
		stat.close();
		connect.close();
	}
	
	public void setRequire(String multi_item) throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
				
		String[] items = multi_item.split(",");
		for(String item : items) {
			String title = item.trim();
			String updateUpdate = "update " + this.tableName
					+ " set isRequired = 1 where title = '" + title + "';";
			if(stat.executeUpdate(updateUpdate) > 0) {
				String updateCategory = "update " + getCategory(connect, stat, this.tableName, title)
						+ " set isRequired = 1 where title = '" + title + "';";
				stat.executeUpdate(updateCategory);
			}
			else {
				System.err.println("완료 설정에 실패했습니다!");
				return;
			}
		}
		System.out.println("항목이 완료되었습니다.");
		
		stat.close();
		connect.close();
	}
	
	public void requireList() throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
				
		ResultSet count = stat.executeQuery("select count(*) from " + this.tableName + " where isRequired = 1");
		if(count.next())
			System.out.println("총 " + count.getInt(1) + "개의 중요 항목을 발견했습니다.");
		
		String readSelect = "select * from " + this.tableName + " where isRequired = 1";
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
			TodoItem todo = new TodoItem(rs);
			System.out.println(todo.toString());
		}
	}
	
	private void listAll(ResultSet rs, String category) throws SQLException {
		while(rs.next()) {
			TodoItem todo = new TodoItem(rs, category);
			System.out.println(todo.toString());
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
	
	public void clearTodo() throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss");
		Date today = new Date();
		String currDate = format.format(today);
		
		String readSelect = "select * from " + this.tableName;
		ResultSet rs = stat.executeQuery(readSelect);
		
		while(rs.next()) {
			String title = rs.getString("title");
			String desc = rs.getString("desc");
			String category = rs.getString("category");
			String dueDate = rs.getString("dueDate");
			int isCompleted = rs.getInt("isCompleted");
			int isRoutine = rs.getInt("isRoutine");
			int isRequired = rs.getInt("isRequired");
			if(isRoutine != 1 && dueDate.compareTo(currDate) < 0) {
				if(isCompleted != 1) {
					if(isRequired != 1)
						deleteTodo(title);
					else {
						System.out.println("다음 항목의 마감 시간이 지났습니다.");
						System.out.println(String.format("[%s] %s | %s", category, title, desc));
						System.out.print("마감 시간을 연장하시겠습니까? (y/n) ");
						if(!scan.nextLine().trim().matches("[yY]"))
							deleteTodo(title);
						else {
							System.out.print("새로운 마감 시간을 작성해주세요 : ");
							String newDate = scan.nextLine().trim();
							String updateUpdate = "update " + this.tableName
									+ " set dueDate = '" + newDate + "' where title = '" + title + "';";
							if(stat.executeUpdate(updateUpdate) > 0) {
								String updateCategory = "update " + category
										+ " set dueDate = '" + newDate + "' where title = '" + title + "';";
								stat.executeUpdate(updateCategory);
								System.out.println("마감 시간이 수정되었습니다.");
							}
							else {
								System.err.println("수정 중 오류가 발생했습니다!");
							}
						}
					}
				}
			}
		}
		
		stat.close();
		connect.close();
	}
	
	public void exportTodo() throws SQLException {
		Connection connect = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile);
		Statement stat = connect.createStatement();
		
		System.out.println("\n=== json 출력 ===");
		ResultSet count = stat.executeQuery("select count(*) from " + this.tableName);
		if(count.next())
			System.out.println("총 " + count.getInt(1) + "개의 항목을 내보냅니다.");
		
		String readSelect = "select * from " + this.tableName;
		ResultSet rs = stat.executeQuery(readSelect);
		
		while(rs.next()) {
			TodoItem todo = new TodoItem(rs);
			System.out.println(todo.toString());
		}
		
		stat.close();
		connect.close();
	}
}