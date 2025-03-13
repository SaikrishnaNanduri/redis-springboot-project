package com.example.libraryApp.service;

import com.example.libraryApp.model.Book;
import com.example.libraryApp.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class BookService {

    private  static final String BOOK_CACHE_KEY ="BOOK_";

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RedisTemplate<String,Object>redisTemplate;

    public Book addBook(Book book){
        Book savedBook =bookRepository.save(book);
        redisTemplate.opsForValue().set(BOOK_CACHE_KEY+savedBook.getId(),savedBook,10, TimeUnit.MINUTES);
        return savedBook;
    }

    public Book getBookById(Long id){
        String key =BOOK_CACHE_KEY+id;
        if(redisTemplate.hasKey(key)){
            return (Book) redisTemplate.opsForValue().get(key);
        }

        Book book = bookRepository.findById(id).orElse(null);
        if(book != null){
            redisTemplate.opsForValue().set(BOOK_CACHE_KEY+book.getId(),book,10,TimeUnit.MINUTES);
        }
        return book;
    }

    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }
}
