package edu.nuwm.NRDBlabs.persistence.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashSet;
import java.util.Set;

public class Subcategory {
    private String name;
    private String code;
    private String access;
    private Set<Collection> collections = new HashSet<>();

    public Subcategory(String name, String code, String access) {
        this.name = name;
        this.code = code;
        this.access = access;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhone() {
        return access;
    }

    public void setPhone(String access) {
        this.access = access;
    }

    public Set<Collection> getCollections() {
        return collections;
    }

    public void setSpecialities(Set<Collection> collections) {
        this.collections = collections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Subcategory chair = (Subcategory) o;

        return new EqualsBuilder()
                .append(name, chair.name)
                .append(code, chair.code)
                .append(access, chair.access)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name).append(code).append(access).toHashCode();
    }
}
