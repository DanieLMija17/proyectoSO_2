/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

/**
 *
 * @author danie
 */
public class MontserratMijares_SOP_2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SistemaArchivos sistema = new SistemaArchivos(100); // Disco de 100 bloques
        InterfazGrafica interfaz = new InterfazGrafica(sistema);
        interfaz.mostrar();
    }
    
}
