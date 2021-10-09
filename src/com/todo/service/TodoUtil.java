package com.todo.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.todo.dao.TodoItem;
import com.todo.dao.TodoList;

public class TodoUtil {
	
	public static void createItem(TodoList list) {
		
		String title, desc, category, due_date;
		Scanner sc = new Scanner(System.in);
		
		System.out.println("추가할 Todo 항목을 입력하세요.");
		System.out.print("제목 >>> ");
		title = sc.nextLine();
		if (list.isDuplicate(title)) {
			System.out.println("이미 존재하는 제목입니다!");
			return;
		}

		System.out.print("내용 >>> ");
		desc = sc.nextLine();
		
		System.out.print("카테고리 >>> ");
		category = sc.nextLine();
		
		System.out.print("마감일 >>> ");
		due_date = sc.nextLine();

		TodoItem t = new TodoItem(title, desc, category, due_date);
		list.addItem(t);
	}

	public static void deleteItem(TodoList l) {
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("삭제할 Todo 항목의 번호를 입력하세요.");
		System.out.print(" >>> ");
		int index = sc.nextInt();
		sc.nextLine();

		TodoItem item = l.getList().get(index-1);
		System.out.println(String.format("%d. [%s] %s | %s - %s ~ %s",
				index, item.getCategory(), item.getTitle(), item.getDesc(), item.getCurrent_date(),item.getDue_date()));
		System.out.print("이 항목을 삭제하시겠습니까? (y/n)");
		String yn = sc.nextLine();
		if(yn.equals("y") || yn.equals("Y")) {
			l.deleteItem(item);
			System.out.println("항목을 삭제하였습니다.");
			return;
		}
		System.out.println("취소하였습니다.");
	}


	public static void updateItem(TodoList list) {
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("변경할 Todo 항목의 번호를 입력하세요.");
		System.out.print(" >>> ");
		int index = sc.nextInt();
		TodoItem item = list.getList().get(index-1);
		System.out.println(String.format("%d. [%s] %s | %s - %s ~ %s",
				index, item.getCategory(), item.getTitle(), item.getDesc(), item.getCurrent_date(),item.getDue_date()));

		System.out.println("새로 추가할 Todo 항목을 입력하세요.");
		sc.nextLine();
		System.out.print("새 제목 >>> ");
		String new_title = sc.nextLine().trim();
		if (!item.getTitle().equals(new_title) && list.isDuplicate(new_title)) {
			System.out.println("이미 존재하는 제목입니다!");
			return;
		}
		System.out.print("새 내용 >>> ");
		String new_description = sc.nextLine().trim();
		System.out.print("새 카테고리 >>> ");
		String new_category = sc.nextLine().trim();
		System.out.print("새 마감일 >>> ");
		String new_due_date = sc.nextLine().trim();
		list.editItem(item, new TodoItem(new_title, new_description, new_category, new_due_date));
		System.out.println("항목이 변경되었습니다.");
	}

	public static void listAll(TodoList l) {
		ArrayList<TodoItem> list = l.getList();
		int len = list.size();
		System.out.println("총 " + len + "개의 todo 항목이 있습니다.");
		for(int i = 0; i < len; i++) {
			TodoItem item = list.get(i);
			System.out.println(String.format("%d. [%s] %s | %s - %s ~ %s",
					i+1, item.getCategory(), item.getTitle(), item.getDesc(), item.getCurrent_date(),item.getDue_date()));
		}
	}
	
	public static void findItem(TodoList l, String keyword) {
		int count = 0;		
		ArrayList<TodoItem> list = l.getList();
		int len = list.size();
		for(int i = 0; i < len; i++) {
			TodoItem item = list.get(i);
			if(item.getTitle().contains(keyword) || item.getDesc().contains(keyword)) {
				System.out.println(String.format("%d. [%s] %s | %s - %s ~ %s",
						i+1, item.getCategory(), item.getTitle(), item.getDesc(), item.getCurrent_date(),item.getDue_date()));
				count++;
			}
		}
		System.out.println("<" + keyword + "> 키워드를 지닌 총 " + count + "개의 todo 항목을 찾았습니다.");
	}
	
	public static void findCategory(TodoList l, String keyword) {
		int count = 0;
		ArrayList<TodoItem> list = l.getList();
		int len = list.size();
		for(int i = 0; i < len; i++) {
			TodoItem item = list.get(i);
			if(item.getCategory().contains(keyword)) {
				System.out.println(String.format("%d. [%s] %s | %s - %s ~ %s",
						i+1, item.getCategory(), item.getTitle(), item.getDesc(), item.getCurrent_date(),item.getDue_date()));
				count++;
			}
		}
		System.out.println("<" + keyword + "> 키워드를 지닌 총 " + count + "개의 todo 항목을 찾았습니다.");
	}
	
	public static void listCategory(TodoList l) {
		HashSet<String> s = new HashSet<String>();
		ArrayList<TodoItem> list = l.getList();
		int count = 0;
		String categories = "";
		
		for(TodoItem item : list)
			s.add(item.getCategory());
		
		for(String category : s) {
			categories += category + " / ";
			count++;
		}
		
		System.out.println(categories.substring(0, categories.length() - 3));
		System.out.println("총 " + count + "개의 카테고리가 등록되어 있습니다.");
	}
	
	public static void saveList(TodoList l, String filename) {
		try {
			FileWriter writer = new FileWriter(filename);
			
			for (TodoItem item : l.getList())
				writer.write(item.toSaveString());
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadList(TodoList l, String filename) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			String readStr = reader.readLine();
			while(readStr != null) {
				StringTokenizer strtok = new StringTokenizer(readStr, "##");
				String title = strtok.nextToken();
				String desc = strtok.nextToken();
				String date = strtok.nextToken();
				String cate = strtok.nextToken();
				String due = strtok.nextToken();
				
				TodoItem item = new TodoItem(title, desc, cate, due);
				item.setCurrent_date(date);
				l.addItem(item);
				
				readStr = reader.readLine();
			}
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
