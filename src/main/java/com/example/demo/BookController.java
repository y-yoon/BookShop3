package com.example.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import jakarta.validation.Valid;

@Controller
public class BookController {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private CartItemRepository cartItemRepository;

	@GetMapping("/book")
	public String viewBooks(Model model, Principal principal) {
		if(principal!=null) {
			MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			int loggedInMemberId = loggedInMember.getMember().getId();

			List<CartItem> cartItemList = cartItemRepository.findAllByMemberId(loggedInMemberId);

			int count = 0;
			for (CartItem cartItem : cartItemList) {

				count++;
			}

			model.addAttribute("count", count);
		}
		List<Book> bookList = bookRepository.findAll();
		model.addAttribute("bookList", bookList);
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		return "view_books";
	}

	@GetMapping("/bookadd")
	public String addBook(Model model) {
		model.addAttribute("book", new Book());
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		return "add_book";
	}

	@PostMapping("/booksave")
	public String saveBook(@Valid Book book, BindingResult bindingResult,
			@RequestParam("bookImage") MultipartFile imgFile, Model model) {
		if (bindingResult.hasErrors()) {
			List<Category> categoryList = categoryRepository.findAll();
			model.addAttribute("categoryList", categoryList);
			return "add_book";
		}

		String imageName = imgFile.getOriginalFilename();
		// set the image name in item object
		book.setImgName(imageName);
		// save the item obj to the db
		Book savedBook = bookRepository.save(book);
		try {
			// prepare the directory path
			String uploadDir = "uploads/books/" + savedBook.getId();
			Path uploadPath = Paths.get(uploadDir);
			// check if the upload path exists, if not it will be created
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			// prepare path for file
			Path fileToCreatePath = uploadPath.resolve(imageName);
			System.out.println("File path: " + fileToCreatePath);
			// copy file to the upload location
			Files.copy(imgFile.getInputStream(), fileToCreatePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "redirect:/book";
	}

	@GetMapping("/book{id}")
	public String viewSingleBook(@PathVariable("id") int id, Model model, Principal principal) {
		if(principal!=null) {
			MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			int loggedInMemberId = loggedInMember.getMember().getId();

			List<CartItem> cartItemList = cartItemRepository.findAllByMemberId(loggedInMemberId);

			int count = 0;
			for (CartItem cartItem : cartItemList) {

				count++;
			}

			model.addAttribute("count", count);
		}
		Book book = bookRepository.getReferenceById(id);
		model.addAttribute("book", book);
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		return "view_single_book";
	}

	// edit
	@GetMapping("/bookedit{id}")
	public String editBook(@PathVariable("id") int id, Model model) {
		Book book = bookRepository.getReferenceById(id);
		model.addAttribute("book", book);
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		return "edit_book";
	}

	@PostMapping("/bookedit{id}")
	public String saveUpdatedBook(@PathVariable("id") Integer id, Book book,
			@RequestParam("bookImage") MultipartFile imgFile) {
		String imageName = imgFile.getOriginalFilename();

		// get existing item from the database
		Book editBook = bookRepository.getReferenceById(id);

		if (imageName.isEmpty()) {
			// No new image selected, use the existing image name
			imageName = editBook.getImgName();
		}

		// set the image name in item object
		book.setImgName(imageName);

		// save the item obj to the db
		Book savedBook = bookRepository.save(book);
		try {
			// prepare the directory path
			String uploadDir = "uploads/books/" + savedBook.getId();
			Path uploadPath = Paths.get(uploadDir);

			// check if the upload path exists, if not it will be created
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			// prepare path for file
			Path fileToCreatePath = uploadPath.resolve(imageName);

			// copy file to the upload location if a new image is provided
			if (!imgFile.isEmpty()) {
				Files.copy(imgFile.getInputStream(), fileToCreatePath, StandardCopyOption.REPLACE_EXISTING);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "redirect:/book";
	}

	// delete
	@GetMapping("/book/delete/{id}")
	public String deleteBook(@PathVariable("id") int id) {
		bookRepository.deleteById(id);
		return "redirect:/book";
	}

	@GetMapping("/all{id}")
	public String viewBooksByCategory(@PathVariable("id") int id, Model model, Principal principal) {
		if(principal!=null) {
			MemberDetails loggedInMember = (MemberDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			int loggedInMemberId = loggedInMember.getMember().getId();

			List<CartItem> cartItemList = cartItemRepository.findAllByMemberId(loggedInMemberId);

			int count = 0;
			for (CartItem cartItem : cartItemList) {

				count++;
			}

			model.addAttribute("count", count);
		}
		
		List<Book> bookList = bookRepository.findAllByCategoryId(id);
		model.addAttribute("bookList", bookList);
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		return "view_books_categories";
	}

}
