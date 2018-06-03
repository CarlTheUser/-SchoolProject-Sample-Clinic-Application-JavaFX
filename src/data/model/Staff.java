package data.model;

import java.time.LocalDate;

public class Staff {

    int id;
    String firstname;
    String middlename;
    String lastname;
    byte gender;
    LocalDate birthdate;
    String position;
    String expertise;
    String contactNumber;
    String email;
    String address;

    public Staff() {}

    public Staff(int id,
                 String firstname,
                 String middlename,
                 String lastname,
                 byte gender,
                 LocalDate birthdate,
                 String position,
                 String expertise,
                 String contactNumber,
                 String email,
                 String address) {
        this(firstname, middlename, lastname, gender, birthdate, position, expertise, contactNumber, email, address);
        this.id = id;
    }

    public Staff(String firstname,
                 String middlename,
                 String lastname,
                 byte gender,
                 LocalDate birthdate,
                 String position,
                 String expertise,
                 String contactNumber,
                 String email,
                 String address) {
        this.firstname = firstname;
        this.middlename = middlename;
        this.lastname = lastname;
        this.gender = gender;
        this.birthdate = birthdate;
        this.position = position;
        this.expertise = expertise;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public byte getGender() {
        return gender;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
