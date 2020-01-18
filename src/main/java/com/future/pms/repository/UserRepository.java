package com.future.pms.repository;

import com.future.pms.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository public interface UserRepository extends MongoRepository<User, String> {
    List<User> findAll();

    Page<User> findAllByRoleAndEmailContainingAllIgnoreCaseAndIdUserIsNot(String role, String email,
        Pageable pageable, String idUser);

    User findByEmail(String email);

    User findByIdUser(String idUser);

    Integer countByEmail(String email);
}
