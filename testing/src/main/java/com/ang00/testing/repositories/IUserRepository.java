package com.ang00.testing.repositories;

import com.ang00.testing.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<UserModel, Long> {

    UserModel findByToken(String token);

    UserModel findByEmail(String email);

    UserModel findByEmailAndPassword(String email, String password);

}
