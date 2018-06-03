package data.model;

import java.time.LocalDate;

public class Patient {
    int id;
    String firstname;
    String middlename;
    String lastname;
    LocalDate birthdate;
    byte gender;
    String contactNumber;
    String address;
    String nationality;
    String religion;

    public Patient(String firstname, String middlename, String lastname, LocalDate birthdate, byte gender, String contactNumber, String address, String nationality, String religion) {
        this(0, firstname, middlename, lastname, birthdate, gender, contactNumber, address, nationality, religion);
    }

    public Patient(int id, String firstname, String middlename, String lastname, LocalDate birthdate, byte gender, String contactNumber, String address, String nationality, String religion) {
        this.id = id;
        this.firstname = firstname;
        this.middlename = middlename;
        this.lastname = lastname;
        this.birthdate = birthdate;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.address = address;
        this.nationality = nationality;
        this.religion = religion;
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

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public byte getGender() {
        return gender;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }
}
