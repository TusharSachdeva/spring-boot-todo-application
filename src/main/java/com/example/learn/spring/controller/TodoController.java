package com.example.learn.spring.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.learn.spring.model.Todo;
import com.example.learn.spring.service.TodoService;


@Controller
public class TodoController {

	@Autowired
	private TodoService service;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, false));
	}
	
	@GetMapping("/list-todos")
	public String showTodos(ModelMap model) {
		String name = getLoggedinUserName();
		model.put("todos", service.retrieveTodos(name));
		return "list-todos";
	}

	private String getLoggedinUserName() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if(principal instanceof UserDetails) {
			return ((UserDetails)principal).getUsername();
		}
		
		return principal.toString();
	}
	
	@GetMapping("/add-todo")
	public String showAddTodo(ModelMap model) {
		model.addAttribute("todo", new Todo(0, getLoggedinUserName(), "Default Desc",
				new Date(), false));
		return "todo";
	}
	
	
	@PostMapping("/add-todo")
	public String addTodos(ModelMap model, @Valid Todo todo, BindingResult result) {
		
		if(result.hasErrors()) {
			return "todo";
		}
		
		try {
			service.addTodo(getLoggedinUserName(), todo.getDesc(), todo.getTargetDate(), false);
			System.out.println("Todo Added Successfully");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/list-todos";
	}
	
	@GetMapping("/delete-todo")
	public String deleteTodo(@RequestParam int id) {
//		if(id ==1) {
//			throw new RuntimeException("Something Went Wrong");
//		}
		service.deleteTodo(id);
		return "redirect:/list-todos";
	}
	
	@GetMapping("/update-todo")
	public String showUpdateTodo(@RequestParam int id, ModelMap model) {
		Todo todo = service.retrieveTodos(id);
		model.put("todo", todo);
		return "todo";
	}
	
	@PostMapping("/update-todo")
	public String updateTodo(ModelMap model, @Valid Todo todo, BindingResult result) {
		
		if (result.hasErrors()) {
			return "todo";
		}
		
		todo.setUser(getLoggedinUserName());
		service.updateTodo(todo);
		return "redirect:/list-todos";
	}
}

