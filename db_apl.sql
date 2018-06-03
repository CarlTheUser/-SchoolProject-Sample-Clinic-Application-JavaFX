-- phpMyAdmin SQL Dump
-- version 4.7.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: May 15, 2018 at 07:54 PM
-- Server version: 5.7.19
-- PHP Version: 7.1.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_apl`
--

-- --------------------------------------------------------

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
CREATE TABLE IF NOT EXISTS `account` (
  `Id` int(15) NOT NULL,
  `Username` varchar(50) DEFAULT NULL,
  `Password` varchar(75) DEFAULT NULL,
  `AccountType` tinyint(1) DEFAULT NULL,
  `IsActive` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `uc_account_username` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `account`
--

INSERT INTO `account` (`Id`, `Username`, `Password`, `AccountType`, `IsActive`) VALUES
(1, 'admin', 'password', 1, 1),
(1827492, 'julianbitara', 'password', 2, 1),
(19738826, 'doctorstrange', 'password', 3, 1),
(199163754, 'cashier', 'password', 5, 1),
(199270126, 'receptionist', 'password', 2, 1);

-- --------------------------------------------------------

--
-- Table structure for table `admission`
--

DROP TABLE IF EXISTS `admission`;
CREATE TABLE IF NOT EXISTS `admission` (
  `VisitId` int(11) NOT NULL AUTO_INCREMENT,
  `InitialFindings` varchar(200) DEFAULT NULL,
  `FinalFindings` varchar(200) DEFAULT NULL,
  `AdmissionDate` datetime DEFAULT NULL,
  `DischargeDate` datetime DEFAULT NULL,
  PRIMARY KEY (`VisitId`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `admission`
--

INSERT INTO `admission` (`VisitId`, `InitialFindings`, `FinalFindings`, `AdmissionDate`, `DischargeDate`) VALUES
(2, 'Choked, in critical condition', 'patient choked on hotdogs.', '2018-05-02 00:26:35', '2018-05-09 16:36:47'),
(10, 'high fever', NULL, '2018-05-08 03:56:09', NULL),
(11, 'Heart ache : (', NULL, '2018-05-08 04:00:40', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `consultationservice`
--

DROP TABLE IF EXISTS `consultationservice`;
CREATE TABLE IF NOT EXISTS `consultationservice` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `DoctorId` int(15) DEFAULT NULL,
  `Symptoms` varchar(150) DEFAULT NULL,
  `Diagnosis` varchar(100) DEFAULT NULL,
  `Notes` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `consultationservice`
--

INSERT INTO `consultationservice` (`Id`, `DoctorId`, `Symptoms`, `Diagnosis`, `Notes`) VALUES
(2, 19738826, 'Headache, runny nose, weakness.', 'Cold with flu.', 'Rest a lot, take biogesic 4x a day.'),
(3, 19738826, 'red eyes, eyes hurt', 'Sore eyes', 'Dont watch too much bold.'),
(4, 19738826, 'Ant bite, swollen arm', 'skin infection', 'take cream and apply daily'),
(5, 19738826, 'Frequent tiredness and stressed', 'tired lol', 'take stress tabs, rest a lot.'),
(6, 19738826, 'Eye hurts', 'eye infection', 'apply blahblah blah'),
(7, 19738826, 'headache', 'fever', 'rest well, take medicine.'),
(8, 19738826, 'headache			', 'Flu', 'take bioflu 4x a day after every meal.'),
(9, 19738826, 'Forgetfulness', 'memory L O S S : (', 'take vitamins daily though it wont change anything : (');

-- --------------------------------------------------------

--
-- Table structure for table `contactperson`
--

DROP TABLE IF EXISTS `contactperson`;
CREATE TABLE IF NOT EXISTS `contactperson` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `PatientId` int(11) DEFAULT NULL,
  `Name` varchar(100) DEFAULT NULL,
  `ContactNumber` varchar(13) DEFAULT NULL,
  `Address` varchar(100) DEFAULT NULL,
  `Relation` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `fk_contactperson_patientid_patient_id` (`PatientId`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `contactperson`
--

INSERT INTO `contactperson` (`Id`, `PatientId`, `Name`, `ContactNumber`, `Address`, `Relation`) VALUES
(1, 5, 'Jobert Bitara', '09351226377', 'Quezon City, Cavite', 'Spouse'),
(2, 1, 'John Michael Bitara', '09326778910', 'Caloocan City, Cavite', 'Thesis Partner'),
(3, 4, 'Nancy Bitara', '09275379862', 'Korea, Cavite', 'Espouse'),
(4, 4, 'Kimuel Bitara', '09665378964', 'Negros Oriental, Cavite', 'Kapatid'),
(5, 7, 'Jan Mark Andal', '09378865931', 'Spi, Cavite', 'Boss'),
(6, 4, 'Ramon Bitara', '09267345682', 'Vito Cruz, Cavite', 'erpats'),
(7, 9, 'Mafelyn Bitara', '09368443107', 'Dagupan, Cavite', 'Classmate sa Cavsu'),
(8, 11, 'Diendo Bitara', '09665887621', 'Kawayan, Cavite', 'Classmate'),
(9, 2, 'Johny Bitara', '09275886394', 'Dublin, Cavite', 'Friend'),
(10, 3, 'Jayvee Bitara', '09329954545', 'Caloocan City, Cavite', 'spouse'),
(11, 3, 'Frederick Bitara', '09167363883', 'Paris, Cavite', 'Neighbour'),
(12, 3, 'Mark Bitara', '09254789364', 'Cordillera, Cavite', 'Mortal Enemy');

-- --------------------------------------------------------

--
-- Table structure for table `laboratoryservice`
--

DROP TABLE IF EXISTS `laboratoryservice`;
CREATE TABLE IF NOT EXISTS `laboratoryservice` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `LaboratoryTestId` int(11) DEFAULT NULL,
  `Findings` varchar(255) DEFAULT NULL,
  `ResultDate` date DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `fk_laboratoryservice_labpratorytestid_laboratorytest_id` (`LaboratoryTestId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `laboratorytest`
--

DROP TABLE IF EXISTS `laboratorytest`;
CREATE TABLE IF NOT EXISTS `laboratorytest` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(30) DEFAULT NULL,
  `Description` varchar(150) DEFAULT NULL,
  `Fee` decimal(11,2) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `medicineequipmentservices`
--

DROP TABLE IF EXISTS `medicineequipmentservices`;
CREATE TABLE IF NOT EXISTS `medicineequipmentservices` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Item` varchar(50) DEFAULT NULL,
  `Quantity` int(3) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `medicineequipmentservices`
--

INSERT INTO `medicineequipmentservices` (`Id`, `Item`, `Quantity`) VALUES
(1, 'Biogesic', 3),
(2, 'IV Fluids', 1),
(3, 'IV Fluid', 2),
(4, 'Paracetamol', 3),
(5, 'Pain killer', 2);

-- --------------------------------------------------------

--
-- Table structure for table `operationdoctor`
--

DROP TABLE IF EXISTS `operationdoctor`;
CREATE TABLE IF NOT EXISTS `operationdoctor` (
  `OperationId` int(11) DEFAULT NULL,
  `DoctorId` int(15) DEFAULT NULL,
  KEY `fk_operationdoctor_operationid_operationservice_id` (`OperationId`),
  KEY `fk_operationdoctor_doctorid_staff_id` (`DoctorId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `operationservice`
--

DROP TABLE IF EXISTS `operationservice`;
CREATE TABLE IF NOT EXISTS `operationservice` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Notes` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `patient`
--

DROP TABLE IF EXISTS `patient`;
CREATE TABLE IF NOT EXISTS `patient` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Firstname` varchar(100) DEFAULT NULL,
  `Middlename` varchar(100) DEFAULT NULL,
  `Lastname` varchar(100) DEFAULT NULL,
  `Gender` tinyint(1) DEFAULT NULL,
  `Birthdate` date DEFAULT NULL,
  `ContactNumber` varchar(13) DEFAULT NULL,
  `Address` varchar(100) DEFAULT NULL,
  `Nationality` varchar(15) DEFAULT NULL,
  `Religion` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `patient`
--

INSERT INTO `patient` (`Id`, `Firstname`, `Middlename`, `Lastname`, `Gender`, `Birthdate`, `ContactNumber`, `Address`, `Nationality`, `Religion`) VALUES
(1, 'John Alvin', '', 'Bitara', 1, '2011-05-11', '09356351995', 'Imus City, Cavite', 'Filipino', 'Christian'),
(2, 'Jon Heron', '', 'Bitara', 2, '2003-05-07', '09359906391', 'Imus Complex, Cavite', 'Volleyballese', 'Volleyball'),
(3, 'Marvin John', '', 'Bitara', 1, '1996-05-14', '09174836284', 'Caloocan City, Imus', 'Filipino', 'Catholic'),
(4, 'Kim Nomar', '', 'Bitara', 1, '2012-05-01', '09352276949', 'Makati St, Imus', 'Filipino', 'Catholic'),
(5, 'Kimuel', '', 'Bitara', 1, '1988-05-12', '09274117305', 'Dagupan City, Cavite', 'Password', 'Catholic'),
(6, 'Joanna', '', 'Bitara', 2, '1999-05-20', '09275213439', 'California, Cavite', 'Filipino', 'Catholic'),
(7, 'Clouvis', 'Denosta', 'Bitara', 1, '1993-09-09', '09367724395', 'Marinduue, Cavite', 'Filipino', 'Basketball'),
(8, 'Gezelle', '', 'Bitara', 2, '1998-05-20', '09668117722', 'Palawan, Cavite', 'Filipinese', 'Makeup'),
(9, 'Shylyn', '', 'Bitara', 2, '1994-05-12', '09269914731', 'Paris, Cavite City', 'Filipino', 'Kpop'),
(10, 'Julian', '', 'Bitara', 2, '2004-05-06', '09351127948', 'El Nido, Cavite', 'Filipino', 'Muslim'),
(11, 'Janicka', '', 'Bitara', 2, '1996-05-10', '09347769982', 'United States of Cavite', 'Filipino', '');

-- --------------------------------------------------------

--
-- Table structure for table `patientcondition`
--

DROP TABLE IF EXISTS `patientcondition`;
CREATE TABLE IF NOT EXISTS `patientcondition` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `PatientId` int(11) DEFAULT NULL,
  `Name` varchar(100) DEFAULT NULL,
  `Description` varchar(100) DEFAULT NULL,
  `Status` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `fk_patientcondition_patientid_patient_id` (`PatientId`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `patientcondition`
--

INSERT INTO `patientcondition` (`Id`, `PatientId`, `Name`, `Description`, `Status`) VALUES
(1, 1, 'Scoliosis', 'description', 'untreated'),
(2, 4, 'Tobolculosis', 'sigarelyosis', 'Early stage'),
(3, 6, 'Scoliosis', '', 'untreated'),
(4, 7, 'Basketbaliosis', 'excessive balling', 'Stage IV'),
(5, 2, 'Breast Cancer', '', 'Stage IV'),
(6, 9, 'Cancer', 'oops', 'Stage II'),
(7, 4, 'Gangrene', 'flesh eating : (', 'stage I'),
(8, 7, 'cancer', 'early boi', 'stage I'),
(9, 7, 'Elephantiasis', 'right foot', 'big foot');

-- --------------------------------------------------------

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
CREATE TABLE IF NOT EXISTS `room` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `RoomType` tinyint(1) DEFAULT NULL,
  `HourlyRate` decimal(7,2) DEFAULT NULL,
  `BedCount` int(3) DEFAULT NULL,
  `LocationDetails` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `room`
--

INSERT INTO `room` (`Id`, `RoomType`, `HourlyRate`, `BedCount`, `LocationDetails`) VALUES
(1, 3, '50.00', 15, 'Ground Floor next to Emergency Room that is next to Morgue'),
(2, 4, '40.00', 6, '4th Floor 2nd room from the stairs.'),
(3, 2, '50.00', 8, 'Ground Floor next to Morgue'),
(4, 3, '50.00', 12, 'Ground Floor next to Emergency Room that is next to Morgue'),
(5, 4, '50.00', 12, '3rd Floor right corner'),
(6, 6, '30.00', 20, 'Ground Floor near laboratory'),
(7, 3, '56.70', 4, '3rd Floor, left corner'),
(8, 6, '40.00', 12, '2nd Floor right corner'),
(9, 4, '50.00', 5, '2nd Floor');

-- --------------------------------------------------------

--
-- Table structure for table `roomservice`
--

DROP TABLE IF EXISTS `roomservice`;
CREATE TABLE IF NOT EXISTS `roomservice` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `RoomId` int(11) DEFAULT NULL,
  `DateIn` datetime DEFAULT NULL,
  `DateOut` datetime DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `fk_roomservice_roomid_room_id` (`RoomId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `roomservice`
--

INSERT INTO `roomservice` (`Id`, `RoomId`, `DateIn`, `DateOut`) VALUES
(1, 1, '2018-05-02 00:26:35', '2018-05-09 16:33:16'),
(2, 2, '2018-05-08 03:56:09', NULL),
(3, 2, '2018-05-08 04:00:40', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `service`
--

DROP TABLE IF EXISTS `service`;
CREATE TABLE IF NOT EXISTS `service` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `VisitId` int(11) DEFAULT NULL,
  `ServiceType` tinyint(1) DEFAULT NULL,
  `Description` varchar(150) DEFAULT NULL,
  `ServiceId` int(11) DEFAULT NULL,
  `Fee` decimal(11,2) DEFAULT NULL,
  `Payment` decimal(11,2) DEFAULT NULL,
  `ServiceDate` datetime DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `fk_service_visitid_visit_id` (`VisitId`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `service`
--

INSERT INTO `service` (`Id`, `VisitId`, `ServiceType`, `Description`, `ServiceId`, `Fee`, `Payment`, `ServiceDate`) VALUES
(1, 2, 1, 'Admission room', 1, '0.00', '10000.00', '2018-05-02 00:26:35'),
(2, 3, 4, 'Patient Consultation', 2, '300.00', NULL, '2018-05-05 02:53:23'),
(3, 4, 4, 'Patient Consultation', 3, '250.00', NULL, '2018-05-05 03:19:14'),
(4, 5, 4, 'Patient Consultation', 4, '300.00', NULL, '2018-05-05 03:43:10'),
(5, 7, 4, 'Patient Consultation', 5, '300.00', NULL, '2018-05-05 20:22:54'),
(6, 6, 4, 'Patient Consultation', 6, '300.00', NULL, '2018-05-06 01:43:32'),
(7, 8, 4, 'Patient Consultation', 7, '300.00', NULL, '2018-05-06 01:44:56'),
(8, 9, 4, 'Patient Consultation', 8, '300.00', NULL, '2018-05-07 04:24:24'),
(9, 10, 1, 'Admission room', 2, '0.00', NULL, '2018-05-08 03:56:09'),
(10, 11, 1, 'Admission room', 3, '0.00', NULL, '2018-05-08 04:00:40'),
(11, 2, 5, 'biogesic medicine for headache', 1, '15.00', '100.00', '2018-05-09 01:08:37'),
(12, 2, 5, 'IV Fluid for dehydration', 2, '80.50', '80.50', '2018-05-09 01:10:28'),
(13, 11, 5, 'For dehydration', 3, '140.00', NULL, '2018-05-09 03:03:33'),
(14, 11, 5, 'Paracetamol for fever', 4, '18.00', NULL, '2018-05-09 03:05:53'),
(15, 11, 5, 'Pain killer for patient chest pain.', 5, '20.00', NULL, '2018-05-09 03:06:30'),
(16, 12, 4, 'Patient Consultation', 9, '0.00', '0.00', '2018-05-09 16:41:07');

-- --------------------------------------------------------

--
-- Table structure for table `staff`
--

DROP TABLE IF EXISTS `staff`;
CREATE TABLE IF NOT EXISTS `staff` (
  `Id` int(15) NOT NULL,
  `Firstname` varchar(100) DEFAULT NULL,
  `Middlename` varchar(100) DEFAULT NULL,
  `Lastname` varchar(100) DEFAULT NULL,
  `Gender` tinyint(1) DEFAULT NULL,
  `Birthdate` date DEFAULT NULL,
  `Position` varchar(50) DEFAULT NULL,
  `Expertise` varchar(50) DEFAULT NULL,
  `ContactNumber` varchar(13) DEFAULT NULL,
  `Email` varchar(75) DEFAULT NULL,
  `Address` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `staff`
--

INSERT INTO `staff` (`Id`, `Firstname`, `Middlename`, `Lastname`, `Gender`, `Birthdate`, `Position`, `Expertise`, `ContactNumber`, `Email`, `Address`) VALUES
(1, 'Juan', '', 'Delacruz', 1, '1993-02-17', 'Doctor', 'none', '09138482775', 'staff@hospital.com', 'bacoor city'),
(1827492, 'Julian', 'Aragones', 'Bitara', 1, '1995-05-11', 'Nurse', 'patient care', '09165314437', 'jbitara@yahoo.com', 'Mandaluyong, Vacite'),
(19738826, 'Mark', '', 'Tapulayan', 1, '2018-04-30', 'Doctor', 'Operations', '09354491772', 'marktapz@gmail.com', 'Naic, Cavite'),
(199163754, 'Ricky', '', 'Lopez', 1, '1996-05-10', 'Cashier', '', '09357711552', 'rlopez64@yahoo.com', 'Marikina City'),
(199270126, 'Kevin', '', 'Santiago', 1, '1995-10-11', 'Boi Nurse', 'customer care', '09279956775', 'kevins175@gmail.com', 'Makati City');

-- --------------------------------------------------------

--
-- Table structure for table `visit`
--

DROP TABLE IF EXISTS `visit`;
CREATE TABLE IF NOT EXISTS `visit` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `PatientId` int(11) DEFAULT NULL,
  `Notes` varchar(255) DEFAULT NULL,
  `VisitDate` datetime DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `fk_visit_patientid_patient_id` (`PatientId`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `visit`
--

INSERT INTO `visit` (`Id`, `PatientId`, `Notes`, `VisitDate`) VALUES
(1, 1, 'headache with fever', '2018-05-01 04:48:45'),
(2, 2, 'Choking', '2018-05-02 00:26:23'),
(3, 7, 'Fever', '2018-05-04 20:13:56'),
(4, 4, 'Sore eyes', '2018-05-05 03:18:32'),
(5, 3, 'ant bite', '2018-05-05 03:40:38'),
(6, 8, 'Eye infection', '2018-05-05 03:42:49'),
(7, 3, 'doctor checkup', '2018-05-05 20:22:00'),
(8, 3, 'sick', '2018-05-06 01:43:06'),
(9, 11, 'Headache', '2018-05-07 04:23:51'),
(10, 1, 'High Fever\n', '2018-05-08 03:55:29'),
(11, 6, 'Heart ache : (\r\n\r\n', '2018-05-08 03:59:43'),
(12, 9, 'Forgot wallet from last time', '2018-05-09 16:39:51');

-- --------------------------------------------------------

--
-- Table structure for table `vitals`
--

DROP TABLE IF EXISTS `vitals`;
CREATE TABLE IF NOT EXISTS `vitals` (
  `PatientId` int(11) NOT NULL AUTO_INCREMENT,
  `BloodPressure` varchar(15) DEFAULT NULL,
  `RespiratoryRate` varchar(15) DEFAULT NULL,
  `Weight` varchar(15) DEFAULT NULL,
  `Height` varchar(15) DEFAULT NULL,
  `Temperature` varchar(15) DEFAULT NULL,
  `DateTaken` datetime DEFAULT NULL,
  PRIMARY KEY (`PatientId`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `vitals`
--

INSERT INTO `vitals` (`PatientId`, `BloodPressure`, `RespiratoryRate`, `Weight`, `Height`, `Temperature`, `DateTaken`) VALUES
(3, '90/20', '120/120', '120 kg', '170 cm', '37 C', '2018-05-05 03:43:08'),
(4, '98/114', '33/33', '140 kg', '170 cm', '36 C', '2018-05-05 03:18:59'),
(7, '90/100 BP', '30/30 ', '122 kgs', '168 cm', '30 C', '2018-05-05 02:39:13'),
(8, '90/90', '90/90', '120 kgs', '167 cm', '34 C', '2018-05-06 01:43:31'),
(9, '90/90', '60', '120 kgs', '134 cm', '30 C', '2018-05-09 16:40:57'),
(11, '90/90', '110/110', '125 kg', '165 cm', '36 C', '2018-05-07 04:24:20');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `admission`
--
ALTER TABLE `admission`
  ADD CONSTRAINT `fk_admission_visitid_visit_id` FOREIGN KEY (`VisitId`) REFERENCES `visit` (`Id`);

--
-- Constraints for table `contactperson`
--
ALTER TABLE `contactperson`
  ADD CONSTRAINT `fk_contactperson_patientid_patient_id` FOREIGN KEY (`PatientId`) REFERENCES `patient` (`Id`);

--
-- Constraints for table `laboratoryservice`
--
ALTER TABLE `laboratoryservice`
  ADD CONSTRAINT `fk_laboratoryservice_labpratorytestid_laboratorytest_id` FOREIGN KEY (`LaboratoryTestId`) REFERENCES `laboratorytest` (`Id`);

--
-- Constraints for table `operationdoctor`
--
ALTER TABLE `operationdoctor`
  ADD CONSTRAINT `fk_operationdoctor_doctorid_staff_id` FOREIGN KEY (`DoctorId`) REFERENCES `staff` (`Id`),
  ADD CONSTRAINT `fk_operationdoctor_operationid_operationservice_id` FOREIGN KEY (`OperationId`) REFERENCES `operationservice` (`Id`);

--
-- Constraints for table `patientcondition`
--
ALTER TABLE `patientcondition`
  ADD CONSTRAINT `fk_patientcondition_patientid_patient_id` FOREIGN KEY (`PatientId`) REFERENCES `patient` (`Id`);

--
-- Constraints for table `roomservice`
--
ALTER TABLE `roomservice`
  ADD CONSTRAINT `fk_roomservice_roomid_room_id` FOREIGN KEY (`RoomId`) REFERENCES `room` (`Id`);

--
-- Constraints for table `service`
--
ALTER TABLE `service`
  ADD CONSTRAINT `fk_service_visitid_visit_id` FOREIGN KEY (`VisitId`) REFERENCES `visit` (`Id`);

--
-- Constraints for table `staff`
--
ALTER TABLE `staff`
  ADD CONSTRAINT `fk_staff_id_account_id` FOREIGN KEY (`Id`) REFERENCES `account` (`Id`);

--
-- Constraints for table `visit`
--
ALTER TABLE `visit`
  ADD CONSTRAINT `fk_visit_patientid_patient_id` FOREIGN KEY (`PatientId`) REFERENCES `patient` (`Id`);

--
-- Constraints for table `vitals`
--
ALTER TABLE `vitals`
  ADD CONSTRAINT `fk_vitals_patientid_patient_id` FOREIGN KEY (`PatientId`) REFERENCES `patient` (`Id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
