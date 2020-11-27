package Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "PatientCard")
public class PatientCard implements Serializable {

    @EmbeddedId
    LinkedKey key;

    @Column(name = "doctor_id", insertable = false, updatable = false)
    private int doctorId;

    @Column(name = "patient_id", insertable = false, updatable = false)
    private int patientId;

    @Column(name = "initial_date", insertable = false, updatable = false)
    private LocalDate initialDate;

    private String diagnosis;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "doctor_FIO")
    private String docFIO;

    @Column(name = "patient_FIO")
    private String patFIO;

    private PatientCard(){

    }

    public PatientCard(int doctorId, int patientId, LocalDate initialDate, String docFIO, String patFIO) {
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.initialDate = initialDate;
        this.docFIO = docFIO;
        this.patFIO = patFIO;
    }

    public LinkedKey getKey() {
        return key;
    }

    public void setKey(LinkedKey key) {
        this.key = key;
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

    public String getDocFIO() {
        return docFIO;
    }

    public void setDocFIO(String docFIO) {
        this.docFIO = docFIO;
    }

    public String getPatFIO() {
        return patFIO;
    }

    public void setPatFIO(String patFIO) {
        this.patFIO = patFIO;
    }
}
