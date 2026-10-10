package ru.kata.spring.boot_security.demo.dto;

import java.util.Objects;

public class RoleDto {

    private Long id;
    private String name;
    private String shortName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RoleDto)) {
            return false;
        }
        RoleDto roleDto = (RoleDto) other;
        return Objects.equals(getId(), roleDto.getId())
                && Objects.equals(getName(), roleDto.getName())
                && Objects.equals(getShortName(), roleDto.getShortName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getShortName());
    }
}
