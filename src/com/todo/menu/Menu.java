package com.todo.menu;
public class Menu {

    public static void displaymenu() {
        System.out.println("\n=== Todo List 명령어 일람 ===");
        System.out.println("add : Todo 항목을 추가합니다.");
        System.out.println("del : Todo 항목을 삭제합니다.");
        System.out.println("edit : Todo 항목을 수정합니다.");
        System.out.println("ls : Todo 항목들을 조회합니다.");
        System.out.println("ls_name_asc : Todo 항목들을 제목순으로 정렬합니다.");
        System.out.println("ls_name_desc : Todo 항목들을 제목역순으로 정렬합니다.");
        System.out.println("ls_date : Todo 항목들을 날짜순으로 정렬합니다.");
        System.out.println("ls_date_desc : Todo 항목들을 날짜역순으로 정렬합니다.");
        System.out.println("find <키워드> : 제목이나 내용에서 <키워드>를 포함하고 있는 모든 Todo 항목을 출력합니다.");
        System.out.println("find_cate <키워드> : 카테고리에서 <키워드>를 포함하고 있는 모든 Todo 항목을 출력합니다.");
        System.out.println("ls_cate : 현재 Todo 항목에 등록된 모든 카테고리를 출력합니다.");
        System.out.println("exit : 프로그램을 종료합니다.");
    }
    
    public static void prompt() {
    	System.out.print("\n 명령어를 입력하세요 >>> ");
    }
}
