package core.model;

import core.service.AccountService;
import core.util.NumberUtility;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class StaffInformation extends Model implements BatchSaveable{

    private final SimpleIntegerProperty id = new SimpleIntegerProperty(this, "id", 0);
    private final SimpleStringProperty firstname = new SimpleStringProperty(this, "firstname", "");
    private final SimpleStringProperty middlename = new SimpleStringProperty(this, "middlename", "");
    private final SimpleStringProperty lastname = new SimpleStringProperty(this, "lastname", "");
    private final SimpleObjectProperty<LocalDate> birthDate = new SimpleObjectProperty<>(this, "birthDate", LocalDate.now());
    private final SimpleObjectProperty<Gender> gender = new SimpleObjectProperty<>(this, "gender", Gender.None);
    private final SimpleStringProperty position = new SimpleStringProperty(this, "position", "");
    private final SimpleStringProperty expertise = new SimpleStringProperty(this, "expertise", "");
    private final SimpleStringProperty email = new SimpleStringProperty(this, "email", "");
    private final SimpleStringProperty contactNumber = new SimpleStringProperty(this, "contactNumber", "");
    private final SimpleStringProperty address = new SimpleStringProperty(this, "address", "");
    private final SimpleObjectProperty<Account> account = new SimpleObjectProperty<>(this, "account", null);

    final private ReadOnlyIntegerWrapper age = new ReadOnlyIntegerWrapper(this, "age");
    final private ReadOnlyStringWrapper fullname = new ReadOnlyStringWrapper(this, "fullname");

    public static StaffInformation NewInstance(SaveListener saveListener, ValidationListener validationListener, ErrorListener errorListener){
        StaffInformation s = new StaffInformation();
        s.setSaveListener(saveListener);
        s.setValidationListener(validationListener);
        s.setErrorListener(errorListener);
//        p.setDirty(true);
        s.isNew = true;
        return s;
    }

    public static StaffInformation FromDb(int id, String firstname, String middlename, String lastname, Gender gender, LocalDate birthdate, String position, String expertise, String contactNumber, String email, String address){
        StaffInformation s = new StaffInformation(id, firstname, middlename, lastname, gender, birthdate, position, expertise, contactNumber, email, address);
        s.isNew = false;
        return s;
    }

    public static StaffInformation FromDbPartial(int id, String firstname, String midlename, String lastname){
        StaffInformation s = new StaffInformation(id, firstname, midlename, lastname, null, null, null, null, null, null, null);
        s.isNew = false;
        return s;
    }

    private StaffInformation(int id, String firstname, String middlename, String lastname, Gender gender, LocalDate birthdate, String position, String expertise, String contactNumber, String email, String address){
        this();
        setId(id);
        setFirstname(firstname);
        setMiddlename(middlename);
        setLastname(lastname);
        setGender(gender);
        setBirthDate(birthdate);
        setPosition(position);
        setExpertise(expertise);
        setContactNumber(contactNumber);
        setEmail(email);
        setAddress(address);
    }


    private StaffInformation(){


        age.bind(Bindings.createIntegerBinding(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return Period.between(getBirthDate(), LocalDate.now()).getYears();
            }
        },birthDateProperty()));
        fullname.bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String fullname = "";
                fullname += firstname.getValue() + " ";
                if(middlename.getValue().length() > 0) fullname += middlename.getValue() + " ";
                fullname += lastname.getValue();
                return fullname;
            }
        }, firstnameProperty(), middlenameProperty(), lastnameProperty()));
        setRestoreBackupOnInvalid(false);
    }

    private boolean accountLoaded = false;

    private void loadAccount(){
        AccountService service = new AccountService();
        Account a = service.getStaffAccount(this);
        a.setStaffInformation(this);
        setAccount(a);
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getFirstname() {
        return firstname.get();
    }

    public SimpleStringProperty firstnameProperty() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname.set(firstname);
    }

    public String getMiddlename() {
        return middlename.get();
    }

    public SimpleStringProperty middlenameProperty() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename.set(middlename);
    }

    public String getLastname() {
        return lastname.get();
    }

    public SimpleStringProperty lastnameProperty() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname.set(lastname);
    }

    public LocalDate getBirthDate() {
        return birthDate.get();
    }

    public SimpleObjectProperty<LocalDate> birthDateProperty() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate.set(birthDate);
    }

    public Gender getGender() {
        return gender.get();
    }

    public SimpleObjectProperty<Gender> genderProperty() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender.set(gender);
    }

    public String  getPosition() {
        return position.get();
    }

    public SimpleStringProperty positionProperty() {
        return position;
    }

    public void setPosition(String position) {
        this.position.set(position);
    }

    public String getExpertise() {
        return expertise.get();
    }

    public SimpleStringProperty expertiseProperty() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise.set(expertise);
    }

    public String getEmail() {
        return email.get();
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getContactNumber() {
        return contactNumber.get();
    }

    public SimpleStringProperty contactNumberProperty() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber.set(contactNumber);
    }

    public String getAddress() {
        return address.get();
    }

    public SimpleStringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public int getAge() {
        return age.get();
    }

    public ReadOnlyIntegerProperty ageProperty() {
        return age.getReadOnlyProperty();
    }

    public String getFullname() {
        return fullname.get();
    }

    public ReadOnlyStringProperty fullnameProperty() {
        return fullname.getReadOnlyProperty();
    }

    public Account getAccount() {
        if(!accountLoaded){
            loadAccount();
            accountLoaded = true;
        }
        return account.get();
    }

    public SimpleObjectProperty<Account> accountProperty() {
        return account;
    }

    public void setAccount(Account account) {
        if(account == null) return;
        this.account.set(account);
        accountLoaded = true;
    }

    @Override
    protected void backupFields() {

    }

    @Override
    protected void restoreFields() {

    }

    @Override
    protected void clearBackup() {

    }

    @Override
    protected void saveMethod() {

    }

    @Override
    protected void updateMethod() {

    }

    @Override
    protected void deleteMethod() {

    }

    @Override
    protected void refreshMethod() {

    }

    @Override
    protected boolean isIdentifiable() {
        return getId() > 0;
    }

    @Override
    protected AbstractList<ValidationCriterion> createValidationCriteria() {
        List<ValidationCriterion> validationCriteria = new ArrayList<>();

        validationCriteria.add(new ValidationCriterion("Staff Id is required.") {
            @Override
            public boolean validate() {
                return isIdentifiable();
            }
        });

        validationCriteria.add(new ValidationCriterion("Staff firstname is required.") {
            @Override
            public boolean validate() {
                return getFirstname().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Staff lastname is required.") {
            @Override
            public boolean validate() {
                return getLastname().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Staff can't be born on a latter time.") {
            @Override
            public boolean validate() {
                LocalDate now = LocalDate.now();
                LocalDate birthdate = getBirthDate();
                return birthdate.isEqual(now) || birthdate.isBefore(now);
            }
        });

        validationCriteria.add(new ValidationCriterion("Staff gender is not supplied.") {
            @Override
            public boolean validate() {
                return getGender() != Gender.None;
            }
        });

        validationCriteria.add(new ValidationCriterion("Staff position is required.") {
            @Override
            public boolean validate() {
                return getPosition().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Email is required.") {
            @Override
            public boolean validate() {
                return getContactNumber().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Enter a valid email address.") {
            @Override
            public boolean validate() {
                return getEmail().contains("@") && getEmail().contains(".");
            }
        });

        validationCriteria.add(new ValidationCriterion("Staff contact number is required.") {
            @Override
            public boolean validate() {
                return getContactNumber().trim().length() > 0;
            }
        });

        validationCriteria.add(new ValidationCriterion("Contact number must be numeric.") {
            @Override
            public boolean validate() {
                return NumberUtility.isNumber(getContactNumber());
            }
        });

        validationCriteria.add(new ValidationCriterion("Contact number is too short.") {
            @Override
            public boolean validate() {
                return getContactNumber().trim().length() >= 7;
            }
        });

        return new AbstractList<ValidationCriterion>() {
            @Override
            public ValidationCriterion get(int index) {
                return validationCriteria.get(index);
            }

            @Override
            public int size() {
                return validationCriteria.size();
            }
        };
    }

    @Override
    public void onBatchSaved() {
        isNew = false;
    }
}
