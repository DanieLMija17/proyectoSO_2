import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author danie
 */
public class SistemaArchivos {
    private Directorio raiz;
    private int tamañoDisco; // Tamaño total del disco en bloques
    private boolean[] bloquesDisco; // true = bloque ocupado, false = bloque libre
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
        this.bloquesDisco = new boolean[tamañoDisco]; // Inicialmente todos los bloques están libres
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
        int[] bloquesAsignados = asignarBloques(tamaño);
        if (bloquesAsignados == null) {
            System.out.println("Error: No se pudieron asignar bloques.");
            return false;
        }

        archivo.setBloquesAsignados(bloquesAsignados);
        directorio.agregarArchivo(archivo);
        tablaAsignacion.agregarEntrada(archivo); // Actualizar la tabla de asignación
        auditoria.registrarOperacion("Crear archivo: " + nombre, modo.toString()); // Registrar en auditoría
        System.out.println("Archivo '" + nombre + "' creado exitosamente.");
        return true;
    }

    // Método para eliminar un archivo
    public boolean eliminarArchivo(Archivo archivo, Directorio directorio) {
        if (modo == ModoUsuario.USUARIO) {
            System.out.println("Error: Solo el administrador puede eliminar archivos.");
            return false;
        }

        liberarBloques(archivo.getBloquesAsignados());
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
    
    //Metodo de restaurar versiones de un archvio
    public boolean restaurarVersionArchivo(Archivo archivo, int version) {
        if (modo == ModoUsuario.USUARIO) {
            System.out.println("Error: Solo el administrador puede restaurar versiones.");
            return false;
        }

        if (archivo.restaurarVersion(version)) {
            auditoria.registrarOperacion("Restaurar versión " + version + " del archivo: " + archivo.getNombre(), modo.toString());
            System.out.println("Versión " + version + " del archivo '" + archivo.getNombre() + "' restaurada exitosamente.");
            return true;
        } else {
            System.out.println("Error: No se pudo restaurar la versión " + version + " del archivo '" + archivo.getNombre() + "'.");
            return false;
        }
    }

    // Método para eliminar un directorio
    public boolean eliminarDirectorio(Directorio directorio) {
        if (modo == ModoUsuario.USUARIO) {
            System.out.println("Error: Solo el administrador puede eliminar directorios.");
            return false;
        }

        // Eliminar todos los archivos
        for (int i = 0; i < directorio.getNumArchivos(); i++) {
            liberarBloques(directorio.getArchivos()[i].getBloquesAsignados());
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

    // Método para asignar bloques a un archivo
    public int[] asignarBloques(int tamaño) {
        int[] bloquesAsignados = new int[tamaño];
        int bloquesAsignadosCount = 0;

        for (int i = 0; i < tamañoDisco; i++) {
            if (!bloquesDisco[i]) {
                bloquesDisco[i] = true; // Marcar como ocupado
                bloquesAsignados[bloquesAsignadosCount] = i;
                bloquesAsignadosCount++;

                if (bloquesAsignadosCount == tamaño) {
                    return bloquesAsignados;
                }
            }
        }

        return null; // No hay suficientes bloques libres
    }

    // Método para liberar bloques de un archivo
    private void liberarBloques(int[] bloques) {
        for (int bloque : bloques) {
            bloquesDisco[bloque] = false; // Marcar como libre
        }
    }

    // Método para contar bloques libres
    private int contarBloquesLibres() {
        int count = 0;
        for (boolean bloque : bloquesDisco) {
            if (!bloque) {
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

    public boolean[] getBloquesDisco() {
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
