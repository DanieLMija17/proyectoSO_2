/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author danie
 */
public class Archivo {
    private String nombre;
    private int tamaño; // Tamaño en bloques
    private int[] bloquesAsignados; // Bloques asignados en el disco

    public Archivo(String nombre, int tamaño) {
        this.nombre = nombre;
        this.tamaño = tamaño;
        this.bloquesAsignados = new int[tamaño];
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTamaño() {
        return tamaño;
    }

    public int[] getBloquesAsignados() {
        return bloquesAsignados;
    }

    public void setBloquesAsignados(int[] bloquesAsignados) {
        this.bloquesAsignados = bloquesAsignados;
    }
}
