package com.quantumluke.quantumshops.services.order;

import com.quantumluke.quantumshops.dto.OrderDto;
import com.quantumluke.quantumshops.dto.OrderItemDto;
import com.quantumluke.quantumshops.enums.OrderStatus;
import com.quantumluke.quantumshops.exceptions.ResourceNotFoundException;
import com.quantumluke.quantumshops.models.Cart;
import com.quantumluke.quantumshops.models.Order;
import com.quantumluke.quantumshops.models.OrderItem;
import com.quantumluke.quantumshops.models.Product;
import com.quantumluke.quantumshops.repository.OrderRepository;
import com.quantumluke.quantumshops.repository.ProductRepository;
import com.quantumluke.quantumshops.services.cart.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public Order createOrder(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);

        Order order = createOrder(cart);
        List<OrderItem> orderItemList = createOrderItems(order, cart);

        order.setOrderItems(new HashSet<>(orderItemList));
        order.setOrderTotalPrice(calculateTotalPrice(orderItemList));
        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(cart.getId());

        return savedOrder;
    }

    private Order createOrder(Cart cart){
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
        return order;
    }

    private List<OrderItem> createOrderItems(Order order, Cart cart){
        return cart.getCartItems()
                .stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();
                    product.setInventory(product.getInventory() - cartItem.getQuantity());
                    productRepository.save(product);
                    return new OrderItem(
                            order,
                            product,
                            cartItem.getQuantity(),
                            cartItem.getUnitPrice()
                    );
                })
                .toList();
    }

    private BigDecimal calculateTotalPrice(List<OrderItem> orderItemList) {
        return orderItemList
                .stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }


    @Override
    public OrderDto getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    @Override
    public List<OrderDto> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    private OrderDto convertToDto(Order order) {
        return modelMapper.map(order, OrderDto.class);
    }
}
