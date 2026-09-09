package com.gamezone.persistence;

import com.gamezone.model.Person;

import java.sql.SQLOutput;
import java.util.List;
import java.io.*;
import java.util.ArrayList;

public class PersonRepository {

    private String filePath;

    public PersonRepository(String filePath) {
        this.filePath = filePath;
    }

    public void saveAll(List<Person> people){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))){
            oos.writeObject(people);
        } catch (IOException e){
            System.out.println("Error saving people data: " + e.getMessage());
        }
    }
    public List<Person> findAll(){
        List<Person> people = new ArrayList<>();
        File file = new File(filePath);

        if (file.exists()){
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
                people = (List<Person>) ois.readObject();
            } catch (IOException | ClassNotFoundException e){
                System.out.println("Error loading people data: " + e.getMessage());
            }
        }
        return people;
    }
}
