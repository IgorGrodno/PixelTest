package com.example.pixeltest.Utils;

import com.example.pixeltest.Models.Ntities.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> hasDateOfBirthGreaterThan(java.util.Date dateOfBirth) {
        return (root, query, criteriaBuilder) -> {
            if (dateOfBirth != null) {
                return criteriaBuilder.greaterThan(root.get("dateOfBirth"), dateOfBirth);
            }
            return null;
        };
    }

    public static Specification<User> hasPhone(String phone) {
        return (root, query, criteriaBuilder) -> {
            if (phone != null && !phone.isEmpty()) {
                return criteriaBuilder.equal(root.join("phones").get("phone"), phone);
            }
            return null;
        };
    }

    public static Specification<User> hasNameLike(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name != null && !name.isEmpty()) {
                return criteriaBuilder.like(root.get("name"), name + "%");
            }
            return null;
        };
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email != null && !email.isEmpty()) {
                return criteriaBuilder.equal(root.join("emails").get("email"), email);
            }
            return null;
        };
    }
}

