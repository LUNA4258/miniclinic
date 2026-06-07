package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.edu.fju.miniclinic.model.Appointment;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.PatientRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stats")
public class StatsApiController {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    @GetMapping
    public Map<String, Object> getSystemStats() {
        Map<String, Object> response = new HashMap<>();

        // 1. 基本總筆數 (巢狀物件 totals)
        Map<String, Long> totals = new HashMap<>();
        totals.put("doctors", doctorRepo.count());
        totals.put("patients", patientRepo.count());
        totals.put("appointments", appointmentRepo.count());
        response.put("totals", totals);

        // 2. 依掛號狀態分組計算 (巢狀物件 statusCounts)
        List<Appointment> allAppointments = appointmentRepo.findAll();
        Map<String, Long> statusCounts = allAppointments.stream()
                .collect(Collectors.groupingBy(Appointment::getStatus, Collectors.counting()));
        response.put("statusCounts", statusCounts);

        return response;
    }
}