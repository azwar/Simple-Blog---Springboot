package com.example.azwarakbar.blog.repository;

import com.example.azwarakbar.blog.model.Role;
import com.example.azwarakbar.blog.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
