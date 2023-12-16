package rest.onlineShop.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import rest.onlineShop.models.Person;
import rest.onlineShop.repositories.PersonRepository;

import java.util.Optional;

@Component
public class PersonValidator implements Validator {
    private final PersonRepository personRepository;

    @Autowired
    public PersonValidator(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;
        Optional<Person> person1 = personRepository.findByUsername(person.getUsername());
        if(person1.isPresent()){
            errors.rejectValue("username", "401", "Person with such username already exists");
        }
        Optional<Person> person2 = personRepository.findByEmail(person.getEmail());
        if(person2.isPresent()){
            errors.rejectValue("email", "401", "Person with such email already exists");
        }

    }
}
