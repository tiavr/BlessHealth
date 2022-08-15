package com.example.blesshealth.NF;

import java.util.Date;

public class Patient {
    private int id;
    private String name;
    private String fullname;
    private Date ddn;

    /**
     * Constructor to allows the initiliazation of the class
     * @param id id of the patient in the database
     * @param name name of the patient
     * @param fullname fullname of the patient
     * @param ddn birth date of the patient
     */
    public Patient(int id, String name, String fullname, Date ddn){
        this.id = id;
        this.name = name;
        this.fullname = fullname;
        this.ddn = ddn;
    }

    public String toString(){
        return "Patient n°"+ id + "\n" + name + " " + fullname + "\n";
    }
}
