
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author danie
 */
public class SistemaArchivos {

    private Directorio raiz;
    private int tamañoDisco; // Tamaño total del disco en bloques
    private int[] bloquesDisco; // Array para almacenar los apuntadores (índice del siguiente bloque)
    private ModoUsuario modo;
    private TablaAsignacion tablaAsignacion;
    private Auditoria auditoria;

    // Enum para los modos de usuario
    public enum ModoUsuario {
        ADMINISTRADOR,
        USUARIO
    }

    // Constructor
    public SistemaArchivos(int tamañoDisco) {
        this.raiz = new Directorio("Raiz", null);
        this.tamañoDisco = tamañoDisco;
        this.bloquesDisco = new int[tamañoDisco]; // Inicialmente todos los bloques están libres (-1)
        for (int i = 0; i < tamañoDisco; i++) {
            bloquesDisco[i] = -1; // -1 indica que el bloque está libre
        }
        this.modo = ModoUsuario.ADMINISTRADOR; // Por defecto, modo administrador
        this.tablaAsignacion = new TablaAsignacion();
        this.auditoria = new Auditoria();
    }

    // Método para crear un archivo
    public boolean crearArchivo(String nombre, int tamaño, Directorio directorio) {
        if (modo == ModoUsuario.USUARIO) {
            System.out.println("Error: Solo el administrador puede crear archivos.");
            return false;
        }

        if (tamaño > contarBloquesLibres()) {
            System.out.println("Error: No hay suficientes bloques libres.");
            return false;
        }

        Archivo archivo = new Archivo(nombre, tamaño);
        int primerBloque = asignarBloquesEncadenados(tamaño);
        if (primerBloque == -1) {
            System.out.println("Error: No se pudieron asignar bloques.");
            return false;
        }

        archivo.setPrimerBloque(primerBloque);
        directorio.agregarArchivo(archivo);
        tablaAsignacion.agregarEntrada(archivo); // Actualizar la tabla de asignación

        // Registrar en auditoría con tamaño y primer bloque
        auditoria.registrarOperacion("Crear archivo: " + nombre + " (Tamaño: " + tamaño + " bloques, Primer Bloque: " + primerBloque + ")", modo.toString());

        System.out.println("Archivo '" + nombre + "' creado exitosamente.");
        return true;
    }

    // Método para eliminar un archivo
    public boolean eliminarArchivo(Archivo archivo, Directorio directorio) {
        if (modo == ModoUsuario.USUARIO) {
            System.out.println("Error: Solo el administrador puede eliminar archivos.");
            return false;
        }

        liberarBloquesEncadenados(archivo.getPrimerBloque()); // Liberar bloques encadenados
        directorio.eliminarArchivo(archivo);
        tablaAsignacion.eliminarEntrada(archivo.getNombre()); // Actualizar la tabla de asignación
        auditoria.registrarOperacion("Eliminar archivo: " + archivo.getNombre(), modo.toString()); // Registrar en auditoría
        System.out.println("Archivo '" + archivo.getNombre() + "' eliminado exitosamente.");
        return true;
    }

    // Método para crear un directorio
    public boolean crearDirectorio(String nombre, Directorio directorioPadre) {
        if (modo == ModoUsuario.USUARIO) {
            System.out.println("Error: Solo el administrador puede crear directorios.");
            return false;
        }

        Directorio nuevoDirectorio = new Directorio(nombre, directorioPadre);
        directorioPadre.agregarSubdirectorio(nuevoDirectorio);
        auditoria.registrarOperacion("Crear directorio: " + nombre, modo.toString()); // Registrar en auditoría
        System.out.println("Directorio '" + nombre + "' creado exitosamente.");
        return true;
    }

    // Método para eliminar un directorio
    public boolean eliminarDirectorio(Directorio directorio) {
        if (modo == ModoUsuario.USUARIO) {
            System.out.println("Error: Solo el administrador puede eliminar directorios.");
            return false;
        }

        // Eliminar todos los archivos
        for (int i = 0; i < directorio.getNumArchivos(); i++) {
            liberarBloquesEncadenados(directorio.getArchivos()[i].getPrimerBloque()); // Liberar bloques encadenados
            tablaAsignacion.eliminarEntrada(directorio.getArchivos()[i].getNombre()); // Actualizar la tabla de asignación
        }

        // Eliminar todos los subdirectorios (recursivo)
        for (int i = 0; i < directorio.getNumSubdirectorios(); i++) {
            eliminarDirectorio(directorio.getSubdirectorios()[i]);
        }

        if (directorio.getPadre() != null) {
            directorio.getPadre().eliminarSubdirectorio(directorio);
        }
        auditoria.registrarOperacion("Eliminar directorio: " + directorio.getNombre(), modo.toString()); // Registrar en auditoría
        System.out.println("Directorio '" + directorio.getNombre() + "' eliminado exitosamente.");
        return true;
    }

    // Método para asignar bloques encadenados
    private int asignarBloquesEncadenados(int tamaño) {
        int primerBloque = -1;
        int bloqueActual = -1;

        for (int i = 0; i < tamañoDisco; i++) {
            if (bloquesDisco[i] == -1) { // Bloque libre
                if (primerBloque == -1) {
                    primerBloque = i; // Primer bloque del archivo
                } else {
                    bloquesDisco[bloqueActual] = i; // Enlazar el bloque anterior con el actual
                }
                bloqueActual = i;
                tamaño--;
                if (tamaño == 0) {
                    bloquesDisco[bloqueActual] = -1; // Último bloque apunta a -1
                    return primerBloque;
                }
            }
        }

        return -1; // No hay suficientes bloques libres
    }

    // Método para liberar bloques encadenados
    private void liberarBloquesEncadenados(int primerBloque) {
        int bloqueActual = primerBloque;
        while (bloqueActual != -1) {
            int siguienteBloque = bloquesDisco[bloqueActual];
            bloquesDisco[bloqueActual] = -1; // Liberar el bloque
            bloqueActual = siguienteBloque;
        }
    }

    // Método para contar bloques libres
    private int contarBloquesLibres() {
        int count = 0;
        for (int bloque : bloquesDisco) {
            if (bloque == -1) {
                count++;
            }
        }
        return count;
    }

    // Método para cambiar el modo de usuario
    public void cambiarModo(ModoUsuario modo) {
        this.modo = modo;
        auditoria.registrarOperacion("Cambiar modo a: " + modo, "Sistema"); // Registrar en auditoría
        System.out.println("Modo cambiado a: " + modo);
    }

    // Getters
    public Directorio getRaiz() {
        return raiz;
    }

    public int[] getBloquesDisco() {
        return bloquesDisco;
    }

    public ModoUsuario getModo() {
        return modo;
    }

    public TablaAsignacion getTablaAsignacion() {
        return tablaAsignacion;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }
}
