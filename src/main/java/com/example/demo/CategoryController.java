package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;


@Controller
public class CategoryController {
	
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@GetMapping("/categories")
	public String viewCategories(Model model) {
		List<Category>categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList",categoryList);
		return "view_categories";
	}
	@GetMapping("/categoryadd")
	public String addCategory(Model model) {
		model.addAttribute("category",new Category());
		return "add_category";
	}
	@PostMapping("/category/save")
	public String saveCategory(@Valid Category category,BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			return "add_category";
		}
		categoryRepository.save(category);
		return "redirect:/categories";
	}
	
	//edit
		@GetMapping("/categoryedit{id}")
		public String editCategory(@PathVariable("id")int id,Model model) {
			Category category = categoryRepository.getReferenceById(id);
			model.addAttribute("category",category);
			return "edit_category";
		}
		@PostMapping("/categoryedit{id}")
		public String saveUpdateCategory(@PathVariable("id")int id,Category category) {
			categoryRepository.save(category);
			return "redirect:/categories";
		}
		//delete
		@GetMapping("/category/delete/{id}")
		public String deleteCategory(@PathVariable("id")int id) {
			categoryRepository.deleteById(id);
			return "redirect:/categories";
		}
}
