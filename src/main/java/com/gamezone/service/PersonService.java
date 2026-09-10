package com.gamezone.service;

import com.gamezone.model.Customer;
import com.gamezone.model.Person;
import com.gamezone.model.Seller;
import com.gamezone.persistence.PersonRepository;

import java.util.ArrayList;
import java.util.List;

public class PersonService {

    private PersonRepository repository;
    private List<Person> people;

    public PersonService(PersonRepository repository) {
        this.repository = repository;
        this.people = repository.findAll();
    }

    public void registerCustomer(Customer customer) {
        if(customer != null){
            this.people.add(customer);
            this.repository.saveAll(this.people);
        }
    }

    public void registerSeller(Seller seller) {
        if(seller != null){
            this.people.add(seller);
            this.repository.saveAll(this.people);
        }
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
