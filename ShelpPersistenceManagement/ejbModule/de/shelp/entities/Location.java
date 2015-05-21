package de.shelp.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Location {

    @Id
    @GeneratedValue
    private long id;
    private String description;
    private String zipcode;

    public Location(String description, String zipcode) {
	this.description = description;
	this.zipcode = zipcode;
    }

    public Location() {
    }

    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getZipcode() {
	return zipcode;
    }

    public void setZipcode(String zipcode) {
	this.zipcode = zipcode;
    }

    @Override
    public String toString() {
        return description + "(" + zipcode + ")";
    }
    
}
