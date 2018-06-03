package data.model;

public class LaboratoryTest {
    int id;
    String name;
    String description;
    double fee;

    public LaboratoryTest(String name, String description, double fee) {
        this(0, name, description, fee);
    }

    public LaboratoryTest(int id, String name, String description, double fee) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.fee = fee;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }
}
