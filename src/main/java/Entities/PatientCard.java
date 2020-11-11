package Entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "PatientCard")
public class PatientCard implements Serializable {

    private PatientCard(){

    }

    public PatientCard(int doctorId, int patientId, LocalDate initialDate) {
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.initialDate = initialDate;
    }

    @Id
    @Column(name = "doctor_id")
    private int doctorId;

    @Id
    @Column(name = "patient_id")
    private int patientId;

    @Column(name = "initial_date")
    private LocalDate initialDate;

    private String diagnosis;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

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

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }
}
