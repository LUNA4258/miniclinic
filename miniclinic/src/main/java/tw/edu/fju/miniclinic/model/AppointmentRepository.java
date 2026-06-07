package tw.edu.fju.miniclinic.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByApptDate(LocalDate apptDate);
    List<Appointment> findByDoctor(Doctor doctor);
    List<Appointment> findByPatient(Patient patient);
    long countByApptDateBetween(LocalDate from, LocalDate to);
    List<Appointment> findByDoctorAndApptDate(Doctor doctor, LocalDate apptDate);

    @Query(value = "SELECT department, COUNT(*) FROM appointment a " +
    "JOIN doctor d ON a.doctor_id = d.doctor_id " +
    "GROUP BY department", nativeQuery = true)
    List<Object[]> countByDepartment();
}