package com.master.demo;

import com.master.demo.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderProductE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @LocalServerPort
    private int port;

    private String vendorToken;
    private String customerToken;

    @BeforeAll
    void setupTestData() {
        System.out.println("✅ Setting up test data...");
        employeeRepository.deleteAll();
        productRepository.deleteAll();
        orderRepository.deleteAll();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Employee vendor = new Employee("vendorTest", "Sales", encoder.encode("vendor123"), "vendor");
        Employee customer = new Employee("customerTest", "Support", encoder.encode("customer123"), "customer");
        employeeRepository.save(vendor);
        employeeRepository.save(customer);

        this.vendorToken = loginAndGetToken("vendorTest", "vendor123");
        this.customerToken = loginAndGetToken("customerTest", "customer123");

        System.out.println("✅ vendorToken = " + vendorToken);
        System.out.println("✅ customerToken = " + customerToken);
    }

    @AfterAll
    void cleanup() {
        System.out.println("🧹 Cleaning up test data...");
        orderRepository.deleteAll();
        productRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    private String loginAndGetToken(String username, String password) {
        ResponseEntity<Map> response = restTemplate.postForEntity(
                url("/api/auth/login?username=" + username + "&password=" + password),
                null,
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody().get("token").toString();
    }

    private String url(String uri) {
        return "http://localhost:" + port + uri;
    }

    @Test
    void testVendorCanCreateProduct() {
        Product p = new Product("TestProduct", 500.0, 10);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(vendorToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Product> request = new HttpEntity<>(p, headers);

        ResponseEntity<Product> response = restTemplate.postForEntity(url("/api/products"), request, Product.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("TestProduct");
    }

    @Test
    void testCustomerCannotCreateProduct() {
        Product p = new Product("ShouldFail", 100.0, 5);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(customerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Product> request = new HttpEntity<>(p, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url("/api/products"), request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void testCustomerCanPlaceOrder() {
        // Create product first
        Product p = new Product("OrderItem", 150.0, 10);

        HttpHeaders vendorHeaders = new HttpHeaders();
        vendorHeaders.setBearerAuth(vendorToken);
        vendorHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Product> vendorRequest = new HttpEntity<>(p, vendorHeaders);

        ResponseEntity<Product> productResponse = restTemplate.postForEntity(url("/api/products"), vendorRequest, Product.class);
        Long productId = productResponse.getBody().getId();

        // Place order as customer
        OrderEntity order = new OrderEntity(List.of(productId), 150.0, null);

        HttpHeaders custHeaders = new HttpHeaders();
        custHeaders.setBearerAuth(customerToken);
        custHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderEntity> custRequest = new HttpEntity<>(order, custHeaders);

        ResponseEntity<OrderEntity> orderResp = restTemplate.postForEntity(url("/api/orders"), custRequest, OrderEntity.class);

        assertThat(orderResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(orderResp.getBody().getId()).isNotNull();
        assertThat(orderResp.getBody().getCustomerId()).isNotNull();
    }

    @Test
    void testVendorCannotPlaceOrder() {
        OrderEntity order = new OrderEntity(List.of(1L), 100.0, null);

        HttpHeaders headers = new HttpHeaders();
        System.out.println(vendorToken);
        headers.setBearerAuth(vendorToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<OrderEntity> request = new HttpEntity<>(order, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url("/api/orders"), request, String.class);

        System.out.println("DEBUG status = " + response.getStatusCode());
        System.out.println("DEBUG body = " + response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void testCustomerCanSeeOwnOrders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(customerToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/orders/my"),
                HttpMethod.GET,
                request,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testEveryoneCanListProducts() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(customerToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/products"),
                HttpMethod.GET,
                request,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
