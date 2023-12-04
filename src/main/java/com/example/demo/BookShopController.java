package com.example.demo;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookShopController {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private CartItemRepository cartItemRepo;
	
	@GetMapping("/about")
	public String about(Model model, Principal principal) {
		if(principal!=null) {
			MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			int loggedInMemberId = loggedInMember.getMember().getId();

			List<CartItem> cartItemList = cartItemRepo.findAllByMemberId(loggedInMemberId);

			int count = 0;
			for (CartItem cartItem : cartItemList) {

				count++;
			}

			model.addAttribute("count", count);
		}
		
		
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		return "about";
	}

	@GetMapping("/add_feedback")
	public String contact(Model model, Principal principal) {
		if(principal!=null) {
			MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			int loggedInMemberId = loggedInMember.getMember().getId();

			List<CartItem> cartItemList = cartItemRepo.findAllByMemberId(loggedInMemberId);

			int count = 0;
			for (CartItem cartItem : cartItemList) {

				count++;
			}

			model.addAttribute("count", count);
		}
		
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		return "add_feedback";
	}

	
	@GetMapping("/login")
	public String login(Model model) {
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		return "login";
	}
	

}
