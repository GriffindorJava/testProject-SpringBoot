package com.amigoscode.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.verify;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;
    @Mock
    private CustomerRepository customerRepository;


    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception{
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        underTest.selectAllCustomers();
        verify(customerRepository).findAll();
    }

    @Test
    void selectCustomerByID() {
        long id = 1L;
        underTest.selectCustomerByID(id);
        verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        Customer insertCustomer = new Customer(1L,11,"maria","mar@");
        underTest.insertCustomer(insertCustomer);
        verify(customerRepository).save(insertCustomer);
    }

    @Test
    void existsPersonWithEmail() {
        String email = "ddd";
        underTest.existsPersonWithEmail(email);
        verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void deleteCustomerById() {
        long id = 1L;
        underTest.deleteCustomerById(id);
        verify(customerRepository).deleteById(id);
    }

    @Test
    void existsPersonWithId() {
        long id = 1L;
        underTest.existsPersonWithId(id);
        verify(customerRepository).existsCustomerById(id);
    }

    @Test
    void updateCustomerById() {
        Customer updateCustomer = new Customer(1L,11,"maria","mar@");
        underTest.updateCustomerById(updateCustomer);
        verify(customerRepository).save(updateCustomer);
    }
}