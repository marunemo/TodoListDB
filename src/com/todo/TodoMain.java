package com.todo;

import java.io.File;
import java.util.Scanner;

import com.todo.dao.TodoList;
import com.todo.menu.Menu;
import com.todo.service.TodoUtil;

public class TodoMain {
	
	public static void start() {
	
		Scanner sc = new Scanner(System.in);
		TodoList l = new TodoList();
		boolean isList = false;
		boolean quit = false;
		
		if(new File("todolist.txt").exists())
			TodoUtil.loadList(l, "todolist.txt");
		Menu.displaymenu();
		do {
			Menu.prompt();
			isList = false;
			String choice = sc.next();
			switch (choice) {

			case "add":
				TodoUtil.createItem(l);
				break;
			
			case "del":
				TodoUtil.deleteItem(l);
				break;
				
			case "edit":
				TodoUtil.updateItem(l);
				break;
				
			case "ls":
				TodoUtil.listAll(l);
				break;

			case "ls_name_asc":
				l.sortByName();
				isList = true;
				break;

			case "ls_name_desc":
				l.sortByName();
				l.reverseList();
				isList = true;
				break;
				
			case "ls_date":
				l.sortByDate();
				isList = true;
				break;
				
			case "ls_date_desc":
				l.sortByDate();
				l.reverseList();
				isList = true;
				break;
				
			case "find":
				TodoUtil.findItem(l, sc.nextLine().trim());
				break;
				
			case "find_cate":
				TodoUtil.findCategory(l, sc.nextLine().trim());
				break;
				
			case "ls_cate":
				TodoUtil.listCategory(l);
				break;

			case "exit":
				quit = true;
				TodoUtil.saveList(l, "todolist.txt");
				break;
				
			case "help":
				Menu.displaymenu();
				break;

			default:
				System.out.println("해당하는 명령어가 없습니다! (help를 통해 명령어 확인)");
				break;
			}
			
			if(isList) l.listAll();
		} while (!quit);
	}
}
