package com.devnguyen.test_skill.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user")   // ← thêm cái này cho chắc
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String username;
    String password;
    String firstName;
    String lastName;

    LocalDate dob;

    // ================== FIX ROLES ==================
    @ManyToMany
    @JdbcTypeCode(SqlTypes.JSON)           // ← Quan trọng nhất
    @Column(name = "roles", columnDefinition = "JSON")
    Set<Role> roles = new HashSet<>();   // khởi tạo mặc định
    // ===============================================
}
