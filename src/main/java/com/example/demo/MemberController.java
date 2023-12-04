package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class MemberController {
	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CategoryRepository categoryRepository;
	
	
	@GetMapping("/memberadd")
	public String addMember(Model model) {
		model.addAttribute("member", new Member());
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		return "add_member";
	}

	@PostMapping("/member/save")
	public String saveMember(@Valid Member member, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "add_member";
		}

		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		String encodePassword = bCryptPasswordEncoder.encode(member.getPassword());
		member.setPassword(encodePassword);

		if (member.getRole() == null) {
			member.setRole("ROLE_USER");
		}

		memberRepository.save(member);
		member.setRole("ROLE_USER");
		redirectAttributes.addFlashAttribute("success", "Member Registerd!");

		return "redirect:/member";
	}

	@GetMapping("/member")
	public String viewMember(Model model) {
		List<Member> memberList = memberRepository.findAll();
		model.addAttribute("memberList", memberList);
		return "view_member";
	}

	@GetMapping("/memberedit{id}")
	public String editMember(@PathVariable("id") int id, Model model) {
		Member member = memberRepository.getReferenceById(id);
		model.addAttribute("member", member);
		return "edit_member";
	}

	@PostMapping("/memberedit{id}")
	public String saveUpdateMember(@PathVariable("id") int id, Member member) {

		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		String encodePassword = bCryptPasswordEncoder.encode(member.getPassword());
		member.setPassword(encodePassword);
		memberRepository.save(member);
		return "redirect:/member";
	}

	@GetMapping("/memberdelete{id}")
	public String deleteMember(@PathVariable("id") int id) {
		memberRepository.deleteById(id);
		return "redirect:/member";
	}

}
