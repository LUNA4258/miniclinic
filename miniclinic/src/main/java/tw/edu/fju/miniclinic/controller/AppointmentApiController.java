package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import tw.edu.fju.miniclinic.model.Appointment;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentApiController {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getAppointmentCount() {
        return ResponseEntity.ok(Collections.singletonMap("count", appointmentRepo.count()));
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> getAppointments(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String doctorId) {

        if (date != null && !date.isBlank()) {
            return ResponseEntity.ok(appointmentRepo.findByApptDate(LocalDate.parse(date)));
        }

        if (doctorId != null && !doctorId.isBlank()) {
            Optional<Doctor> doctorOpt = doctorRepo.findById(doctorId);
            if (doctorOpt.isPresent()) {
                return ResponseEntity.ok(appointmentRepo.findByDoctor(doctorOpt.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        return ResponseEntity.ok(appointmentRepo.findAll());
    }

    @PutMapping("/api/appointments/{apptId}/status")
public ResponseEntity<Appointment> updateStatus(
		@PathVariable Long apptId,
		@RequestBody Map<String, String> payload,
		HttpSession session) {

	String loggedInDoctorId = (String) session.getAttribute("loggedInDoctorId");

	Appointment appt = appointmentRepo.findById(apptId).orElse(null);
	if (appt == null) {
		return ResponseEntity.notFound().build();
	}

	// 只能修改自己的掛號
	if (!appt.getDoctor().getDoctorId().equals(loggedInDoctorId)) {
		return ResponseEntity.status(403).build();
	}

	String newStatus = payload.get("status");
	if (!List.of("BOOKED", "COMPLETED", "CANCELLED").contains(newStatus)) {
		return ResponseEntity.badRequest().build();
	}

	appt.setStatus(newStatus);
	return ResponseEntity.ok(appointmentRepo.save(appt));
}
}