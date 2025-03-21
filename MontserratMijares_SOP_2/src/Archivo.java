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
    private int primerBloque;
    private int[][] versiones;
    private int numVersiones;
    private static final int MAX_VERSIONES = 10;
    
    public Archivo(String nombre, int tamaño) {
        this.nombre = nombre;
        this.tamaño = tamaño;
        this.primerBloque = -1; 
        this.versiones = new int[MAX_VERSIONES][];
        this.numVersiones = 0;
    }
    
    public void agregarVersion(int[] bloques) {
        if (numVersiones < MAX_VERSIONES) {
            versiones[numVersiones] = bloques.clone(); // Almacenar una copia de los bloques
            numVersiones++;
        } else {
            System.out.println("Error: No se pueden agregar más versiones.");
        }
    }
     
    public int[] restaurarVersion(int indiceVersion) {
        if (indiceVersion >= 0 && indiceVersion < numVersiones) {
            return versiones[indiceVersion];
        }
        return null;
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

     public int getPrimerBloque() {
        return primerBloque;
    }

    public void setPrimerBloque(int primerBloque) {
        this.primerBloque = primerBloque;
    }
    
    public int getNumVersiones() {
        return numVersiones;
    }
    
    public int[][] getVersiones() {
        return versiones;
    }

}
