package org.service.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "role")
    private String role;
}
