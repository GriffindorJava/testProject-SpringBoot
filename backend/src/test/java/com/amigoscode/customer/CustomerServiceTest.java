package com.amigoscode.customer;

import com.amigoscode.exception.DuplicateResourceException;
import com.amigoscode.exception.RequestValidationException;
import com.amigoscode.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    private CustomerService underTest;
    @Mock private CustomerDAO customerDAO;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDAO);
    }

    @Test
    void getAllCustomers() {
        underTest.getAllCustomers();
        verify(customerDAO).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        long id = 1L;
        Customer customer = new Customer(
                id,11,"maria","mar@"
        );
        when(customerDAO.selectCustomerByID(id)).thenReturn(Optional.of(customer));

        Customer actual = underTest.getCustomer(id);

        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willTrowWhenGetCustomerReturnEmptyOptional() {
        long id = 0L;

        when(customerDAO.selectCustomerByID(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(id));
    }

    @Test
    void addCustomer() {
        String email = "p@gmail.com";

        when(customerDAO.existsPersonWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                12,"petros", email
        );

        underTest.addCustomer(request);
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDAO).insertCustomer(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowWhenEmailExistsWhileAddingACustomer() {
        String email = "p@gmail.com";

        when(customerDAO.existsPersonWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                12,"petros", email
        );

        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        verify(customerDAO, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomerById() {
        long id = 0L;

        when(customerDAO.existsPersonWithId(id)).thenReturn(true);
        underTest.deleteCustomerById(id);

        verify(customerDAO).deleteCustomerById(id);
    }

    @Test
    void willThrowWhenIdNotExistsWhileDeletingCustomer() {
        long id = 0L;

        when(customerDAO.existsPersonWithId(id)).thenReturn(false);
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(id));

        verify(customerDAO, never()).deleteCustomerById(any());
    }

    @Test
    void canUpdateAllCustomersProperties() {
        long id = 0L;
        Customer customer = new Customer(
                id,11,"maria","mar@"
        );
        when(customerDAO.selectCustomerByID(id)).thenReturn(Optional.of(customer));

        String newEmail = "p@gmail.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                32,
                "petros",
                newEmail
        );
        when(customerDAO.existsPersonWithEmail(newEmail)).thenReturn(false);

        underTest.updateCustomer(request, id);
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDAO).updateCustomerById(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
    }

    @Test
    void canUpdateCustomersNameProperty() {
        long id = 0L;
        Customer customer = new Customer(
                id,11,"maria","mar@"
        );
        when(customerDAO.selectCustomerByID(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null,
                "petros",
                null
        );

        underTest.updateCustomer(request, id);
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDAO).updateCustomerById(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void canUpdateCustomersEmailProperty() {
        long id = 0L;
        Customer customer = new Customer(
                id,11,"maria","mar@"
        );
        when(customerDAO.selectCustomerByID(id)).thenReturn(Optional.of(customer));

        String newEmail = "p@gmail.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null,
                null,
                newEmail
        );
        when(customerDAO.existsPersonWithEmail(newEmail)).thenReturn(false);

        underTest.updateCustomer(request, id);
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDAO).updateCustomerById(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void willThrowWhenEmailExistsWhileUpdatingCustomer() {
        long id = 0L;
        Customer customer = new Customer(
                id,11,"maria","mar@"
        );
        when(customerDAO.selectCustomerByID(id)).thenReturn(Optional.of(customer));

        String newEmail = "p@gmail.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null,
                null,
                newEmail
        );
        when(customerDAO.existsPersonWithEmail(newEmail)).thenReturn(true);

        assertThatThrownBy(() -> underTest.updateCustomer(request, id))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        verify(customerDAO, never()).updateCustomerById(any());
    }

    @Test
    void canUpdateCustomersAgeProperty() {
        long id = 0L;
        Customer customer = new Customer(
                id,11,"maria","mar@"
        );
        when(customerDAO.selectCustomerByID(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                21,
                null,
                null
        );

        underTest.updateCustomer(request, id);
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDAO).updateCustomerById(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void willTrowWhenCustomerUpdateHasNoChanges() {
        long id = 0L;
        Customer customer = new Customer(
                id,11,"maria","mar@"
        );
        when(customerDAO.selectCustomerByID(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                customer.getAge(),
                customer.getName(),
                customer.getEmail()
        );

        assertThatThrownBy(() -> underTest.updateCustomer(request, id))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found");

        verify(customerDAO, never()).updateCustomerById(any());
    }
}