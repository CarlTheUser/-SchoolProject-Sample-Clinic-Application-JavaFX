package data.model;

public class ContactPerson {
    int id;
    int patientId;
    String name;
    String contactNumber;
    String address;
    String relation;

    public ContactPerson() {}

    public ContactPerson(int patientId, String name, String contactNumber, String address, String relation) {
        this(0, patientId, name, contactNumber, address, relation);
    }

    public ContactPerson(int id, int patientId, String name, String contactNumber, String address, String relation) {
        this.id = id;
        this.patientId = patientId;
        this.name = name;
        this.contactNumber = contactNumber;
        this.address = address;
        this.relation = relation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
}
