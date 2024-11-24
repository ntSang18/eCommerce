package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @MockBean
    private BCryptPasswordEncoder encoder;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void createUserTest() throws Exception {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals("test", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void findUserByNameTest() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");

        when(userRepository.findByUsername("test")).thenReturn(user);

        final ResponseEntity<User> response = userController.findByUserName("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User responseUser = response.getBody();
        assertNotNull(responseUser);
        assertEquals("test", responseUser.getUsername());
    }

    @Test
    public void findUserByIdTest() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        final ResponseEntity<User> response = userController.findById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User responseUser = response.getBody();
        assertNotNull(responseUser);
        assertEquals(user.getUsername(), responseUser.getUsername());
    }
}
