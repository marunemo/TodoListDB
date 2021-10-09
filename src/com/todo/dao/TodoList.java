package com.todo.dao;

import java.util.*;

import com.todo.service.TodoSortByDate;
import com.todo.service.TodoSortByName;

public class TodoList {
	private List<TodoItem> list;

	public TodoList() {
		this.list = new ArrayList<TodoItem>();
	}

	public void addItem(TodoItem t) {
		list.add(t);
	}

	public void deleteItem(TodoItem t) {
		list.remove(t);
	}

	public void editItem(TodoItem t, TodoItem updated) {
		int index = list.indexOf(t);
		if(index == -1) return;
		
		list.remove(index);
		list.add(index, updated);
	}

	public ArrayList<TodoItem> getList() {
		return new ArrayList<TodoItem>(list);
	}

	public void sortByName() {
		Collections.sort(list, new TodoSortByName());

	}

	public void listAll() {
		System.out.println("\n정렬된 결과입니다.");
		for (TodoItem item : list) {
			System.out.println(String.format("[%s] %s | %s - %s ~ %s",
					item.getCategory(), item.getTitle(), item.getDesc(), item.getCurrent_date(),item.getDue_date()));
		}
	}
	
	public void reverseList() {
		Collections.reverse(list);
	}

	public void sortByDate() {
		Collections.sort(list, new TodoSortByDate());
	}

	public int indexOf(TodoItem t) {
		return list.indexOf(t);
	}

	public Boolean isDuplicate(String title) {
		for (TodoItem item : list) {
			if (title.equals(item.getTitle())) return true;
		}
		return false;
	}
}
