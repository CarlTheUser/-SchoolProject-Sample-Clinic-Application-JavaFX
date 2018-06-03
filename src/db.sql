
CREATE TABLE IF NOT EXISTS Account(
	Id INT(15) PRIMARY KEY,
	Username VARCHAR(50),
	Password VARCHAR(75),
	AccountType TINYINT(1),
	IsActive TINYINT(1)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Staff(
	Id INT(15) PRIMARY KEY,
	Firstname VARCHAR(100),
	Middlename VARCHAR(100),
	Lastname VARCHAR(100),
	Gender TINYINT(1),
	Birthdate DATE,
	Position VARCHAR(50),
	Expertise VARCHAR(50),
	ContactNumber VARCHAR(13),
	Email VARCHAR(75),
	Address VARCHAR(100)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Patient(
	Id INT(11) PRIMARY KEY AUTO_INCREMENT,
	Firstname VARCHAR(100),
	Middlename VARCHAR(100),
	Lastname VARCHAR(100),
	Gender TINYINT(1),
	Birthdate DATE,
	ContactNumber VARCHAR(13),
	Address VARCHAR(100),
	Nationality VARCHAR(15),
	Religion VARCHAR(20)
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Visit(
	Id INT(11) PRIMARY KEY AUTO_INCREMENT,
	PatientId INT(11),
	Notes VARCHAR(255),
	VisitDate DATETIME
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Vitals(
	PatientId INT(11) PRIMARY KEY ,
	BloodPressure VARCHAR(15),
	RespiratoryRate VARCHAR(15),
	Weight VARCHAR(15),
	Height VARCHAR(15),
	Temperature VARCHAR(15),
	DateTaken DATETIME
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS ContactPerson(
	Id INT(11) PRIMARY KEY AUTO_INCREMENT,
	PatientId INT(11),
	Name VARCHAR(100),
	ContactNumber VARCHAR(13),
	Address VARCHAR(100),
	Relation VARCHAR(20)
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS PatientCondition(
	Id INT(11) PRIMARY KEY AUTO_INCREMENT,
	PatientId INT(11),
	Name VARCHAR(100),
	Description VARCHAR(100),
	Status VARCHAR(20)
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Admission(
	VisitId INT(11) PRIMARY KEY AUTO_INCREMENT,
	InitialFindings VARCHAR(200),
	FinalFindings VARCHAR(200),
	AdmissionDate DATETIME,
	DischargeDate DATETIME
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Room(
	Id INT(11) PRIMARY KEY AUTO_INCREMENT,
	RoomType TINYINT(1),
	HourlyRate DECIMAL(7, 2),
	BedCount INT(3),
	LocationDetails VARCHAR(150)
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS LaboratoryTest(
	Id INT(11) PRIMARY KEY AUTO_INCREMENT, 
	Name VARCHAR(30),
	Description VARCHAR(150),
	Fee DECIMAL(11, 2)
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Service(
	Id INT(11) PRIMARY KEY AUTO_INCREMENT,
	VisitId INT(11),
	ServiceType TINYINT(1),
	Description VARCHAR(150),
	ServiceId INT(11),
	Fee DECIMAL(11, 2),
	Payment DECIMAL(11, 2),
	ServiceDate DATETIME
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS RoomService(
	Id INT(11) PRIMARY KEY AUTO_INCREMENT,
	RoomId INT(11),
	DateIn DATETIME,
	DateOut DATETIME
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS OperationService(
	Id INT(11) PRIMARY KEY AUTO_INCREMENT,
	Notes VARCHAR(200)
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS LaboratoryService(
	Id INT(11) PRIMARY KEY AUTO_INCREMENT,
	LaboratoryTestId INT(11),
	Findings VARCHAR(255),
	ResultDate DATE
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS ConsultationService(
	Id INT(11) PRIMARY KEY AUTO_INCREMENT,
	DoctorId INT(15),
	Symptoms VARCHAR(150),
	Diagnosis VARCHAR(100),
	Notes VARCHAR(150)
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS MedicineEquipmentServices(
	Id INT(11) PRIMARY KEY AUTO_INCREMENT,
	Item VARCHAR(50),
	Quantity INT(3)
)ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS OperationDoctor(
	OperationId INT(11),
	DoctorId INT(15)
)ENGINE=InnoDB;


ALTER TABLE Staff
ADD CONSTRAINT fk_staff_id_account_id
FOREIGN KEY (Id)
REFERENCES Account(Id);


ALTER TABLE Vitals
ADD CONSTRAINT fk_vitals_patientid_patient_id
FOREIGN KEY (PatientId)
REFERENCES Patient(Id);

ALTER TABLE Visit
ADD CONSTRAINT fk_visit_patientid_patient_id
FOREIGN KEY (PatientId)
REFERENCES Patient(Id);

ALTER TABLE Admission
ADD CONSTRAINT fk_admission_visitid_visit_id
FOREIGN KEY (VisitId)
REFERENCES Visit(Id);


ALTER TABLE ContactPerson
ADD CONSTRAINT fk_contactperson_patientid_patient_id
FOREIGN KEY (PatientId)
REFERENCES Patient(Id);


ALTER TABLE PatientCondition
ADD CONSTRAINT fk_patientcondition_patientid_patient_id
FOREIGN KEY (PatientId)
REFERENCES Patient(Id);


ALTER TABLE Service
ADD CONSTRAINT fk_service_visitid_visit_id
FOREIGN KEY (VisitId)
REFERENCES Visit(Id);


ALTER TABLE RoomService
ADD CONSTRAINT fk_roomservice_roomid_room_id
FOREIGN KEY (RoomId)
REFERENCES Room(Id);


ALTER TABLE LaboratoryService
ADD CONSTRAINT fk_laboratoryservice_labpratorytestid_laboratorytest_id
FOREIGN KEY (LaboratoryTestId)
REFERENCES LaboratoryTest(Id);


ALTER TABLE OperationDoctor
ADD CONSTRAINT fk_operationdoctor_operationid_operationservice_id
FOREIGN KEY (OperationId)
REFERENCES OperationService(Id);

ALTER TABLE OperationDoctor
ADD CONSTRAINT fk_operationdoctor_doctorid_staff_id
FOREIGN KEY (DoctorId)
REFERENCES Staff(Id);


ALTER TABLE account
ADD CONSTRAINT uc_account_username
UNIQUE(username);





































