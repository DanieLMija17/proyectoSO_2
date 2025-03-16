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
    private int[][] versionesBloques; // Array de versiones de bloques asignados
    private int versionActual; // Índice de la versión actual

    public Archivo(String nombre, int tamaño) {
        this.nombre = nombre;
        this.tamaño = tamaño;
        this.versionesBloques = new int[10][tamaño]; // Suponemos un máximo de 10 versiones
        this.versionActual = 0;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public int getTamaño() {
        return tamaño;
    }

    public int[] getBloquesAsignados() {
        return versionesBloques[versionActual];
    }

    public void setBloquesAsignados(int[] bloquesAsignados) {
        this.versionesBloques[versionActual] = bloquesAsignados;
    }

    // Método para crear una nueva versión
    public void crearNuevaVersion(int[] nuevosBloques) {
        if (versionActual < versionesBloques.length - 1) {
            versionActual++;
            versionesBloques[versionActual] = nuevosBloques;
        } else {
            System.out.println("Error: No se pueden crear más versiones.");
        }
    }

    // Método para restaurar una versión anterior
    public boolean restaurarVersion(int version) {
        if (version >= 0 && version < versionesBloques.length && versionesBloques[version] != null) {
            versionActual = version;
            return true;
        }
        return false;
    }

    // Método para obtener la versión actual
    public int getVersionActual() {
        return versionActual;
    }

    // Método para obtener el número de versiones
    public int getNumVersiones() {
        return versionActual + 1;
    }
}