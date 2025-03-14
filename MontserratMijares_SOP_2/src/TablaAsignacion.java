/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author danie
 */
public class TablaAsignacion {
    private EntradaTabla[] tabla; // Array para almacenar las entradas de la tabla
    private int numEntradas; // Contador de entradas en la tabla

    // Tamaño máximo de la tabla (ajustable según sea necesario)
    private static final int MAX_ENTRADAS = 100;

    // Constructor
    public TablaAsignacion() {
        this.tabla = new EntradaTabla[MAX_ENTRADAS];
        this.numEntradas = 0;
    }

    // Método para agregar una entrada a la tabla
    public void agregarEntrada(Archivo archivo) {
        if (numEntradas < MAX_ENTRADAS) {
            EntradaTabla entrada = new EntradaTabla(archivo.getNombre(), archivo.getTamaño(), archivo.getBloquesAsignados());
            tabla[numEntradas] = entrada;
            numEntradas++;
        } else {
            System.out.println("Error: La tabla de asignación está llena.");
        }
    }

    // Método para eliminar una entrada de la tabla
    public void eliminarEntrada(String nombreArchivo) {
        for (int i = 0; i < numEntradas; i++) {
            if (tabla[i].getNombreArchivo().equals(nombreArchivo)) {
                // Mover las entradas restantes una posición hacia atrás
                for (int j = i; j < numEntradas - 1; j++) {
                    tabla[j] = tabla[j + 1];
                }
                numEntradas--;
                tabla[numEntradas] = null; // Liberar la referencia
                break;
            }
        }
    }

    // Método para obtener la tabla completa
    public EntradaTabla[] getTabla() {
        return tabla;
    }

    // Método para obtener el número de entradas en la tabla
    public int getNumEntradas() {
        return numEntradas;
    }

    // Clase interna para representar una entrada en la tabla
    public static class EntradaTabla {
        private String nombreArchivo;
        private int tamaño;
        private int[] bloquesAsignados;

        public EntradaTabla(String nombreArchivo, int tamaño, int[] bloquesAsignados) {
            this.nombreArchivo = nombreArchivo;
            this.tamaño = tamaño;
            this.bloquesAsignados = bloquesAsignados;
        }

        // Getters
        public String getNombreArchivo() {
            return nombreArchivo;
        }

        public int getTamaño() {
            return tamaño;
        }

        public int[] getBloquesAsignados() {
            return bloquesAsignados;
        }
    }
}
