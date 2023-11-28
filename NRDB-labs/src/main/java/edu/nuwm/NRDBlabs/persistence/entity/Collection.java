package edu.nuwm.NRDBlabs.persistence.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Collection {
    private String code;
    private String name;

    public Collection(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        System.out.println("im here");
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Collection that = (Collection) o;
        System.out.println(that.name);
        return new EqualsBuilder().append(code, that.code).append(name, that.name).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(code).append(name).toHashCode();
    }
}
