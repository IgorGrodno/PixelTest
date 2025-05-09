package com.example.pixeltest.Utils;

import com.example.pixeltest.Models.Ntities.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class UserSpecification {

    public static Specification<User> hasDateOfBirthGreaterThan(Date dateOfBirth) {
        return (root, query, cb) -> dateOfBirth != null
                ? cb.greaterThan(root.get("dateOfBirth"), dateOfBirth)
                : cb.conjunction();
    }

    public static Specification<User> hasPhone(String phone) {
        return (root, query, cb) -> {
            if (phone != null && !phone.isEmpty()) {
                query.distinct(true);
                return cb.equal(root.join("phones").get("phone"), phone);
            }
            return cb.conjunction();
        };
    }

    public static Specification<User> hasNameLike(String name) {
        return (root, query, cb) -> name != null && !name.isEmpty()
                ? cb.like(root.get("name"), name + "%")
                : cb.conjunction();
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, cb) -> {
            if (email != null && !email.isEmpty()) {
                query.distinct(true);
                return cb.equal(root.join("emails").get("email"), email);
            }
            return cb.conjunction();
        };
    }
}
