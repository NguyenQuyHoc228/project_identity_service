package com.devnguyen.test_skill.repository;

import com.devnguyen.test_skill.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    //
    boolean existsByUsername(String username);
}
