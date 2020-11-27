package Entities;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class LinkedKey implements Serializable {
    @Column(name = "doctor_id")
    private int doctorId;

    @Column(name = "patient_id")
    private int patientId;

    @Column(name = "initial_date")
    private LocalDate initialDate;

    public LinkedKey(){

    }

    public LinkedKey(int patientId, int doctorId, LocalDate initialDate){
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.initialDate = initialDate;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public LocalDate getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(LocalDate initialDate) {
        this.initialDate = initialDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkedKey linkedKey = (LinkedKey) o;
        return doctorId == linkedKey.doctorId &&
                patientId == linkedKey.patientId &&
                initialDate.equals(linkedKey.initialDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorId, patientId, initialDate);
    }
}
