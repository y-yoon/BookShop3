package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer>{

	List<Book> findAllByCategoryId(int id);

	public List<Book> findByTitleContaining(String keyword);

	
}
