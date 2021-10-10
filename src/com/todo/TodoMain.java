package com.todo;

import java.util.Scanner;

import com.todo.menu.Menu;
import com.todo.util.TodoUtil;

public class TodoMain {
	
	public static void start() {	
		Scanner scan = new Scanner(System.in);
		String choice;
		try {
			TodoUtil todolist = new TodoUtil("todolist.db", "TodoItem");
			
			Menu.displaymenu();
			do {
				Menu.prompt();
				choice = scan.next().trim();
				switch (choice) {
					case "add":
						todolist.createTodo();
						break;
					case "del":
						todolist.deleteTodo();
						break;
					case "edit":
						todolist.updateTodo();
						break;
					case "ls":
						todolist.readTodo();
						break;
					case "ls_name_asc":
						todolist.nameList();
						break;
					case "ls_name_desc":
						todolist.nameList(false);
						break;
					case "ls_date":
						todolist.dateList();
						break;
					case "ls_date_desc":
						todolist.dateList(false);
						break;
					case "find":
						todolist.findTodo(scan.nextLine().trim());
						break;
					case "find_cate":
						todolist.findCategory(scan.nextLine().trim());
						break;
					case "ls_cate":
						todolist.categoryList();
						break;
					case "comp":
						todolist.completeTodo(Integer.parseInt(scan.nextLine().trim()));
						break;
					case "ls_comp":
						todolist.completeList();
						break;
					case "exit":
						break;
					case "help":
						Menu.displaymenu();
						break;	
					default:
						System.out.println("해당하는 명령어가 없습니다! (help를 통해 명령어 확인)");
						break;
				}
			} while(!choice.equals("exit"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("종료되었습니다.");
		scan.close();
	}
}
