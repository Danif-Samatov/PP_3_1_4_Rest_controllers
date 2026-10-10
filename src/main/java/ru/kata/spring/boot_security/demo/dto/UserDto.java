package ru.kata.spring.boot_security.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private Integer age;
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private List<Long> roleIds = new ArrayList<>();
    private String rolesAsString;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getRolesAsString() {
        return rolesAsString;
    }

    public void setRolesAsString(String rolesAsString) {
        this.rolesAsString = rolesAsString;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UserDto)) {
            return false;
        }
        UserDto userDto = (UserDto) other;
        return Objects.equals(getId(), userDto.getId())
                && Objects.equals(getEmail(), userDto.getEmail())
                && Objects.equals(getRolesAsString(), userDto.getRolesAsString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail(), getRolesAsString());
    }
}
