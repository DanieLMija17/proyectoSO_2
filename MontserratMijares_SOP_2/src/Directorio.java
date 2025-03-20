/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author danie
 */
public class Directorio {
    private String nombre;
    private Directorio padre;
    private Archivo[] archivos; // Array de archivos
    private Directorio[] subdirectorios; // Array de subdirectorios
    private int numArchivos; // Contador de archivos
    private int numSubdirectorios; // Contador de subdirectorios

    // Tamaños máximos (pueden ajustarse según sea necesario)
    private static final int MAX_ARCHIVOS = 100;
    private static final int MAX_SUBDIRECTORIOS = 50;

    public Directorio(String nombre, Directorio padre) {
        this.nombre = nombre;
        this.padre = padre;
        this.archivos = new Archivo[MAX_ARCHIVOS];
        this.subdirectorios = new Directorio[MAX_SUBDIRECTORIOS];
        this.numArchivos = 0;
        this.numSubdirectorios = 0;
    }

    // Métodos para gestionar archivos
    public void agregarArchivo(Archivo archivo) {
        if (numArchivos < MAX_ARCHIVOS) {
            archivos[numArchivos] = archivo;
            numArchivos++;
        } else {
            System.out.println("Error: No se pueden agregar más archivos.");
        }
    }

    public void eliminarArchivo(Archivo archivo) {
        for (int i = 0; i < numArchivos; i++) {
            if (archivos[i] == archivo) {
                // Mover los archivos restantes una posición hacia atrás
                for (int j = i; j < numArchivos - 1; j++) {
                    archivos[j] = archivos[j + 1];
                }
                numArchivos--;
                archivos[numArchivos] = null; // Liberar la referencia
                break;
            }
        }
    }

    // Métodos para gestionar subdirectorios
    public void agregarSubdirectorio(Directorio subdirectorio) {
        if (numSubdirectorios < MAX_SUBDIRECTORIOS) {
            subdirectorios[numSubdirectorios] = subdirectorio;
            numSubdirectorios++;
        } else {
            System.out.println("Error: No se pueden agregar más subdirectorios.");
        }
    }

    public void eliminarSubdirectorio(Directorio subdirectorio) {
        for (int i = 0; i < numSubdirectorios; i++) {
            if (subdirectorios[i] == subdirectorio) {
                // Mover los subdirectorios restantes una posición hacia atrás
                for (int j = i; j < numSubdirectorios - 1; j++) {
                    subdirectorios[j] = subdirectorios[j + 1];
                }
                numSubdirectorios--;
                subdirectorios[numSubdirectorios] = null; // Liberar la referencia
                break;
            }
        }
    }

    // Getters
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Directorio getPadre() {
        return padre;
    }

    public Archivo[] getArchivos() {
        return archivos;
    }

    public int getNumArchivos() {
        return numArchivos;
    }

    public Directorio[] getSubdirectorios() {
        return subdirectorios;
    }

    public int getNumSubdirectorios() {
        return numSubdirectorios;
    }
}
