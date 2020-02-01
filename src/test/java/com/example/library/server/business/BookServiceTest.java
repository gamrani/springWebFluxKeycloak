package com.example.library.server.business;

import com.example.library.server.dataaccess.BookRepository;
import com.example.library.server.dataaccess.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.example.library.server.dataaccess.Book;
import reactor.test.StepVerifier;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.when;

@DisplayName("Verify that book service")
@SpringJUnitConfig
class BookServiceTest {

    @InjectMocks private BookService bookService;
    @Mock private UserRepository userRepository;
    @Mock private BookRepository bookRepository;

    @DisplayName("grants access to create a book for role 'LIBRARY_CURATOR'")
    @Test
    @WithMockUser(roles="LIBRARY_CURATOR")
    public void verifyCreateAccessIsGrantedForCurator(){
        when(bookRepository.insert(Mockito.<Mono<Book>>any())).thenReturn(Flux.just(new Book()));
        StepVerifier.create(
                bookService.create(
                        Mono.just(
                                new Book(
                                        UUID.randomUUID(),
                                        "123456789",
                                        "title",
                                        "description",
                                        Collections.singletonList("author"),
                                        false,
                                        null))))
                .verifyComplete();

    }

    @DisplayName("denies access to create a book for roles 'LIBRARY_USER' and 'LIBRARY_ADMIN'")
    @Test
    @WithMockUser(roles = {"LIBRARY_USER", "LIBRARY_ADMIN"})
    public void verifyCreateAccessIsDeniedForUserAndAdmin(){
        when(bookRepository.insert(Mockito.<Mono<Book>>any())).thenReturn(Flux.just(new Book()));
        StepVerifier.create(
                bookService.create(
                        Mono.just(
                                new Book(
                                        UUID.randomUUID(),
                                        "123456789",
                                        "title",
                                        "description",
                                        Collections.singletonList("author"),
                                        false,
                                        null)))
        ).verifyError(AccessDeniedException.class);
    }

    @DisplayName("denies access to create a book for anonymous user")
    @Test
    void verifyCreateAccessIsDeniedForUnauthenticated() {
        when(bookRepository.insert(Mockito.<Mono<Book>>any())).thenReturn(Flux.just(new Book()));

        StepVerifier.create(
                bookService.create(
                        Mono.just(
                                new Book(
                                        UUID.randomUUID(),
                                        "123456789",
                                        "title",
                                        "description",
                                        Collections.singletonList("author"),
                                        false,
                                        null))))
                .verifyError(AccessDeniedException.class);
    }

}