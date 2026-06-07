package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import tw.edu.fju.miniclinic.model.AppointmentForm;
import tw.edu.fju.miniclinic.model.Appointment;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.Patient;
import tw.edu.fju.miniclinic.model.PatientRepository;

import java.time.LocalDate;
import java.util.Map;

@Controller
public class AppointmentController {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    // GET：顯示表單
    @GetMapping("/appointment/new")
    public String newAppointmentForm(Model model) {
        model.addAttribute("form", new AppointmentForm());
        model.addAttribute("doctors", doctorRepo.findAll());
        return "appointment-new";
    }

    // POST：接收表單
    @PostMapping("/appointment/new")
    public String submitAppointment(
        @Valid @ModelAttribute AppointmentForm form,
        BindingResult result,
        Model model) {
            if (result.hasErrors()) {
                model.addAttribute("form", form);
                model.addAttribute("doctors", doctorRepo.findAll());
                return "appointment-new";
            } // <-- 補上 if 區塊的右大括號

            Patient patient = patientRepo.findById(form.getChartNo()).orElse(null);
            Doctor doctor = doctorRepo.findById(form.getDoctorId()).orElse(null);

            if (patient == null || doctor == null) {
                model.addAttribute("error", "查無此病歷號或醫師");
                return "appointment-new";
            }

            Appointment appt = new Appointment();
            appt.setPatient(patient);
            appt.setDoctor(doctor);
            appt.setApptDate(LocalDate.parse(form.getApptDate()));
            appt.setTimeSlot(form.getTimeSlot());
            appt.setStatus("BOOKED");

            Appointment saved = appointmentRepo.save(appt);

            model.addAttribute("appointment", saved);

            return "appointment-result";
    }

    @PostMapping("/api/appointments")
    public ResponseEntity<Appointment> createAppointment(
		@RequestBody Map<String, String> request) {

	// 從 request 取出資料
	String chartNo = request.get("chartNo");
	String doctorId = request.get("doctorId");
	LocalDate apptDate = LocalDate.parse(request.get("apptDate"));
	String timeSlot = request.get("timeSlot");

	// 查詢關聯的 Patient 與 Doctor
	Patient patient = patientRepo.findById(chartNo).orElse(null);
	Doctor doctor = doctorRepo.findById(doctorId).orElse(null);

	if (patient == null || doctor == null) {
		return ResponseEntity.badRequest().build();
	}

	// 建立 Appointment 物件
	Appointment appt = new Appointment();
	appt.setPatient(patient);
	appt.setDoctor(doctor);
	appt.setApptDate(apptDate);
	appt.setTimeSlot(timeSlot);
	appt.setStatus("BOOKED");

	Appointment saved = appointmentRepo.save(appt);
	return ResponseEntity.status(201).body(saved);
}
}
