package com.example.demo;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

@Controller
public class CartItemController {
	@Autowired
	private CartItemRepository cartItemRepo;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private MemberRepository memberRepo;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@GetMapping("/cart")
	public String showCart(Model model, Principal principal) {

		// Get currently logged in user
		MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInMemberId = loggedInMember.getMember().getId();

		// Get shopping cart items added by this user
		// *Hint: You will need to use the method we added in the CartItemRepository

		List<CartItem> cartItemList = cartItemRepo.findAllByMemberId(loggedInMemberId);

		// Add the shopping cart items to the model

		model.addAttribute("cartItemList", cartItemList);

		// Calculate the total cost of all items in the shopping cart

		double total = 0;
		int count = 0;
		for (CartItem cartItem : cartItemList) {
			cartItem.setSubtotal(cartItem.getBook().getPrice() * cartItem.getQuantity());
			total += cartItem.getSubtotal();
			count++;
		}

		/*
		 * item1 => cartitem.item.price*cartitem.quantity = 123*2=246 item2 =>
		 * cartitem.item.price*cartitem.quantity = 123*4=492 total => 738
		 */

		// Add the shopping cart total to the model

		model.addAttribute("total", total);
		model.addAttribute("count", count);
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);

		return "cart";
	}

	@PostMapping("/cart/add/{bookId}")
	public String addToCart(@PathVariable("bookId") int bookId, @RequestParam("quantity") int quantity,
			Principal principal) {

		if (quantity > 0) {
			// Get currently logged in user

			MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			int loggedInMemberId = loggedInMember.getMember().getId();

			// Check in the cartItemRepo if item was previously added by user.
			// *Hint: we will need to write a new method in the CartItemRepository

			List<CartItem> cartItemList = cartItemRepo.findAllByMemberId(loggedInMemberId);
			boolean flag = false;
			CartItem cItem = new CartItem();

			for (CartItem cartItem : cartItemList) {
				if (cartItem.getBook().getId() == bookId) {
					cItem = cartItem;
					flag = true;
					break;
				}
			}

			// if the item was previously added, then we get the quantity that was
			// previously added and increase that
			// Save the CartItem object back to the repository

			if (flag) {
				cItem.setQuantity(cItem.getQuantity() + quantity);
				cartItemRepo.save(cItem);
			}

			// if the item was NOT previously added,
			// then prepare the item and member objects

			else {
				// quantity/item/member
				Book book = bookRepository.getReferenceById(bookId);
				Member member = memberRepo.getReferenceById(loggedInMemberId);

				// Create a new CartItem object

				CartItem cartItem = new CartItem();
				cartItem.setQuantity(quantity);
				cartItem.setBook(book);
				cartItem.setMember(member);

				// Save the new CartItem object to the repository
				cartItemRepo.save(cartItem);
			}
		}

		// Set the item and member as well as the new quantity in the new CartItem
		// object

		return "redirect:/cart";
	}

	@PostMapping("/cart/update/{id}")
	public String updateCart(@PathVariable("id") int cartItemId, @RequestParam("qty") int qty) {

		if (qty > 0) {
			// Get cartItem object by cartItem's id
			CartItem cartItem = cartItemRepo.getReferenceById(cartItemId);

			// Set the quantity of the carItem object retrieved
			cartItem.setQuantity(qty);

			// Save the cartItem back to the cartItemRepo
			cartItemRepo.save(cartItem);

		}

		return "redirect:/cart";
	}

	@GetMapping("/cart/remove/{id}")
	public String removeFromCart(@PathVariable("id") int cartItemId) {

		// Remove item from cartItemRepo
		cartItemRepo.deleteById(cartItemId);

		return "redirect:/cart";
	}

	@PostMapping("/cartprocess_order")
	public String processOrder(Model model, @RequestParam("cartTotal") double cartTotal,
			@RequestParam("memberId") String memberId, @RequestParam("orderId") String orderId,
			@RequestParam("transactionId") String transactionId) {
		
		LocalDate date = LocalDate.now();
		// Get currently logged in user
		MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		int loggedInMemberId = loggedInMember.getMember().getId();

		// Retrieve cart items purchased
		List<CartItem> cartItemList = cartItemRepo.findAllByMemberId(loggedInMemberId);

		// Get member object
		Member member = memberRepo.getReferenceById(loggedInMemberId);

		// Loop to iterate through all cart items
		for (int i = 0; i < cartItemList.size(); i++) {

			// Retrieve details about current cart item
			CartItem cartItem = cartItemList.get(i);
			int qty = cartItem.getQuantity();
			double subtotal = cartItem.getBook().getPrice() * qty;
			cartItem.setSubtotal(subtotal);

			// Update item table
			Book book = bookRepository.getReferenceById(cartItem.getBook().getId());
			book.setQuantity(book.getQuantity() - qty);
			bookRepository.save(book);

			// Add item to order table
			OrderItem orderItem = new OrderItem();
			orderItem.setBook(book);
			orderItem.setMember(member);
			orderItem.setOrderId(orderId);
			orderItem.setQuantity(qty);
			orderItem.setSubtotal(subtotal);
			orderItem.setTransactionId(transactionId);
			orderItem.setDate(date);
			orderItemRepository.save(orderItem);

		}

		// clear cart items belonging to member
		// cartItemRepo.deleteAllByMemberId(loggedInMember);
		cartItemRepo.deleteAll(cartItemList);

		// Pass info to view to display success page
		model.addAttribute("cartItemList", cartItemList);
		model.addAttribute("orderId", orderId);
		model.addAttribute("transactionId", transactionId);
		model.addAttribute("success", "THANK YOU FOR YOR ORDER");

		// Send email

		String subject = "Order is confirmed!";

		String body = "Thank you for your order!\n" + "Order ID: " + orderId + "\n" + "Transaction ID: "
				+ transactionId;

		String to = member.getEmail();

		sendEmail(to, subject, body);

		return "success";
	}

	public void sendEmail(String to, String subject, String body) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(to);
		msg.setSubject(subject);
		msg.setText(body);

		javaMailSender.send(msg);
	}

}
