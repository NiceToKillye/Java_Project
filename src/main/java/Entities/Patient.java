package Entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "birthday_date")
    private LocalDate birthdayDate;

    @Column(name = "home_address")
    private String homeAddress;

    private String login;
    private String password;
    private String surname;
    private String name;
    private String patronymic;

    public Patient(){

    }

    public Patient(String login, String password, String surname, String name,
                   String patronymic, LocalDate birthdayDate, String homeAddress) {
        this.login = login;
        this.password = password;
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.birthdayDate = birthdayDate;
        this.homeAddress = homeAddress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public LocalDate getBirthdayDate() {
        return birthdayDate;
    }

    public void setBirthdayDate(LocalDate birthdayDate) {
        this.birthdayDate = birthdayDate;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }
}
