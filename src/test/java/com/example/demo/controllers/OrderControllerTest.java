package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class OrderControllerTest {

    @Autowired
    private OrderController orderController;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private OrderRepository orderRepository;

    @Test
    public void submitOrderTest() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");

        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setPrice(new BigDecimal("2.99"));
        item.setDescription("A widget that is round");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(Collections.singletonList(item));
        cart.setTotal(new BigDecimal("2.99"));
        cart.setUser(user);

        user.setCart(cart);

        when(userRepository.findByUsername("test")).thenReturn(user);

        final ResponseEntity<UserOrder> response = orderController.submit("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder userOrder = response.getBody();
        assertNotNull(userOrder);
        assertEquals("test", userOrder.getUser().getUsername());
    }

    @Test
    public void getOrdersForUserTest() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");

        List<UserOrder> userOrders = new ArrayList<>();
        for (Long i = 0L; i < 3L; i++) {
            UserOrder order = new UserOrder();
            order.setId(i);
            order.setUser(user);

            List<Item> items = new ArrayList<Item>();
            for (long j = 0L; j < 3L; j++) {
                Item item = new Item();
                item.setId(j);
                item.setDescription("test");
                item.setName("test");
                item.setPrice(new BigDecimal("2.99"));
                items.add(item);
            }
            order.setItems(items);

            order.setTotal(new BigDecimal("8.97"));

            userOrders.add(order);
        }

        when(userRepository.findByUsername("test")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(userOrders);
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> actualOrders = response.getBody();
        assertNotNull(actualOrders);
        assertEquals(new BigDecimal("8.97"), actualOrders.get(1).getTotal());
    }
}
