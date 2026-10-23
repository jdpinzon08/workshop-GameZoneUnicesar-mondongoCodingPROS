package com.gamezone.service;

import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.model.Customer;
import com.gamezone.model.Person;
import com.gamezone.model.Seller;
import com.gamezone.persistence.PersonRepository;
import com.gamezone.validation.PersonValidator;

import java.util.ArrayList;
import java.util.List;

public class PersonService {

    private PersonRepository repository;
    private List<Person> people;

    public PersonService(PersonRepository repository) {
        this.repository = repository;
        this.people = repository.findAll();
    }

    /**
     * Validates and registers a customer, then persists the updated people list.
     *
     * @param customer customer to register
     * @throws InvalidDataException if the customer or required customer data is invalid
     * @throws com.gamezone.exceptions.BusinessRuleException if the identifier is already registered
     */
    public void registerCustomer(Customer customer) {
        if (customer == null) {
            throw new InvalidDataException("customer", "no puede ser nulo");
        }
        PersonValidator.validatePersonData(customer.getId(), customer.getName(),
                customer.getEmail(), customer.getPhoneNumber());
        PersonValidator.validateUniquePerson(people, customer.getId());
        this.people.add(customer);
        this.repository.saveAll(this.people);
    }

    /**
     * Validates and registers a seller, then persists the updated people list.
     *
     * @param seller seller to register
     * @throws InvalidDataException if the seller or required seller data is invalid
     * @throws com.gamezone.exceptions.BusinessRuleException if the identifier is already registered
     */
    public void registerSeller(Seller seller) {
        if (seller == null) {
            throw new InvalidDataException("seller", "no puede ser nulo");
        }
        PersonValidator.validatePersonData(seller.getId(), seller.getName(), seller.getPhoneNumber());
        PersonValidator.validateUniquePerson(people, seller.getId());
        this.people.add(seller);
        this.repository.saveAll(this.people);
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        for (Person person : people) {
            if (person instanceof Customer) {
                customers.add((Customer) person);
            }
        }
        return customers;
    }
    public List<Seller> getAllSellers() {
        List<Seller> sellers = new ArrayList<>();
        for (Person person : people) {
            if (person instanceof Seller) {
                sellers.add((Seller) person);
            }
        }
        return sellers;
    }

    public Customer findCustomerById(String id) {
        for (Customer customer : getAllCustomers()) {
            if (customer.getId().equals(id)) {
                return customer;
            }
        }
        return null;
    }

    public Seller findSellerById(String employeeId) {
        for (Seller seller : getAllSellers()) {
            if (seller.getEmployeeId().equals(employeeId)) {
                return seller;
            }
        }
        return null;
    }
}
