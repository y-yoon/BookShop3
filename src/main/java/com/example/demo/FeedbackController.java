package com.example.demo;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class FeedbackController {

	@Autowired
	private FeedbackRepository feedbackRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired(required = true)
	private JavaMailSender javaMailSender;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private MemberRepository memberRepository;

	@GetMapping("/feedback")
	public String viewFeedback(Model model) {
		List<Feedback> feedbackList = feedbackRepository.findAll();
		model.addAttribute("feedbackList", feedbackList);

		return "view_feedback";
	}

	@GetMapping("/feedback/add")
	public String addFeedback(Model model, Principal principal) {
		if (principal != null) {
			MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			Member member = loggedInMember.getMember();
			model.addAttribute("member", member);
			int loggedInMemberId = loggedInMember.getMember().getId();

			List<CartItem> cartItemList = cartItemRepository.findAllByMemberId(loggedInMemberId);

			int count = 0;
			for (CartItem cartItem : cartItemList) {

				count++;
			}

			model.addAttribute("count", count);
		}

		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		model.addAttribute("feedback", new Feedback());
		return "add_feedback";
	}

	@PostMapping("/feedback/save")
	public String saveFeedback(Model model, @Valid Feedback feedback, BindingResult bindingResult, Principal principal,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "add_feedback";
		}

		if (principal != null) {
			MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			Member member = loggedInMember.getMember();
			feedback.setMember(member);
		}
		feedback.setStatus(0);
		feedbackRepository.save(feedback);
		redirectAttributes.addFlashAttribute("success", "THANK YOU FOR YOR FEEDBACK");
		return "redirect:/feedback/add";
	}

	@GetMapping("/feedback/delete/{id}")
	public String deleteFeedback(@PathVariable("id") int id) {
		feedbackRepository.deleteById(id);
		return "redirect:/feedback";
	}

	@GetMapping("/reply{id}")
	public String replyBack(@PathVariable("id") int id) {

		Member member = memberRepository.getReferenceById(id);
		// Send email

		// String subject = "Order is confirmed!";

		String from = "shweyoon439@gmail.com";
		String body = "Thank you for your feedback!\n";
		String to = member.getEmail();

		sendEmail(to, body, from);
		List<Feedback> feedBackList = feedbackRepository.findAllByMemberId(member.getId());
		for (Feedback feedback : feedBackList) {
			feedback.setStatus(1);
			feedbackRepository.save(feedback);
		}

		return "redirect:/feedback";
	}

	public void sendEmail(String to, String body, String from) {
		SimpleMailMessage msg = new SimpleMailMessage();

		msg.setText(body);
		msg.setTo(to);
		msg.setFrom(from);

		javaMailSender.send(msg);
	}
}
