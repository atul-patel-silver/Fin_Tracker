package org.service.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_users")
@Getter
@Setter
public class UserModel extends BaseModel{

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email_id",nullable = false,unique = true)
    private String emailId;

    @Column(name = "mobile_number",nullable = false,unique = true)
    private String mobileNumber;

    @Column(name = "user_name",nullable = false,unique = true)
    private String userName;

    @Column(name = "password",nullable = false)
    private String password;

    @Column(name = "is_active", nullable = false)
    boolean isActive = true;

    @Column(name = "is_deleted", nullable = false)
    boolean isDeleted = false;

//    @Column(name = "role")
//    private String role;

    private String sub;

    @Column(name = "login_type")
    private String loginType;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
