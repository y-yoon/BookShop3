package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
	
	@Autowired
	private BookRepository bookRepository;
	
	@GetMapping("/search")
	public List<Book> searchProducts(@RequestParam("keyword") String keyword){
		List<Book> bList = bookRepository.findAll();
		List<Book> bookList=new ArrayList<Book>();
		for(Book book :bList) {
			if(book.getTitle().contains(keyword)) {
				bookList.add(book);
			}
		}
		
		return bookList;
	}

}
