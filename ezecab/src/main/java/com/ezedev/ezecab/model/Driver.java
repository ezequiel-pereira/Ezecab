package com.ezedev.ezecab.model;

public class Driver {
    String id;
    String nombre;
    String email;
    String carBrand;
    String carPlate;

    public Driver(String id, String nombre, String email, String carBrand, String carPlate) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.carBrand = carBrand;
        this.carPlate = carPlate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarPlate() {
        return carPlate;
    }

    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
    }
}
