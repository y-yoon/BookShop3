package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Integer>{

	List<CartItem> findAllByMemberId(int loggedInMemberId);
	CartItem findByBookId(int bookId);
}
