
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
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

        // Crear una versión inicial (versión 0)
        int[] bloquesArchivo = obtenerBloquesArchivo(primerBloque, tamaño);
        archivo.agregarVersion(bloquesArchivo);

        // Registrar en auditoría
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
    
   public void crearVersion(Archivo archivo) {
        if (modo == ModoUsuario.USUARIO) {
            System.out.println("Error: Solo el administrador puede crear versiones.");
            return;
        }

        // Obtener los bloques asignados al archivo
        int[] bloquesArchivo = obtenerBloquesArchivo(archivo.getPrimerBloque(), archivo.getTamaño());
        archivo.agregarVersion(bloquesArchivo);

        // Registrar en auditoría
        auditoria.registrarOperacion("Crear versión del archivo: " + archivo.getNombre(), modo.toString());
        System.out.println("Versión del archivo '" + archivo.getNombre() + "' creada exitosamente.");
    }

    // Método auxiliar para obtener los bloques asignados a un archivo
    private int[] obtenerBloquesArchivo(int primerBloque, int tamaño) {
        int[] bloques = new int[tamaño];
        int bloqueActual = primerBloque;
        for (int i = 0; i < tamaño; i++) {
            bloques[i] = bloqueActual;
            bloqueActual = bloquesDisco[bloqueActual];
        }
        return bloques;
    }
    
    public boolean restaurarVersion(Archivo archivo, int indiceVersion) {
        if (modo == ModoUsuario.USUARIO) {
            System.out.println("Error: Solo el administrador puede restaurar versiones.");
            return false;
        }

        int[] bloquesVersion = archivo.restaurarVersion(indiceVersion);
        if (bloquesVersion == null) {
            System.out.println("Error: Versión no encontrada.");
            return false;
        }

        // Liberar los bloques actuales del archivo
        liberarBloquesEncadenados(archivo.getPrimerBloque());

        // Asignar los bloques de la versión restaurada
        archivo.setPrimerBloque(bloquesVersion[0]);
        for (int i = 0; i < bloquesVersion.length - 1; i++) {
            bloquesDisco[bloquesVersion[i]] = bloquesVersion[i + 1];
        }
        bloquesDisco[bloquesVersion[bloquesVersion.length - 1]] = -1; // Último bloque

        // Registrar en auditoría
        auditoria.registrarOperacion("Restaurar versión " + indiceVersion + " del archivo: " + archivo.getNombre(), modo.toString());
        System.out.println("Versión " + indiceVersion + " del archivo '" + archivo.getNombre() + "' restaurada exitosamente.");
        return true;
    }
    
    public Archivo getArchivoPorNombre(String nombreArchivo) {
    return buscarArchivo(nombreArchivo, raiz);
}

// Método auxiliar para buscar un archivo por nombre (recursivo)
    private Archivo buscarArchivo(String nombre, Directorio directorio) {
        // Buscar en los archivos del directorio actual
        for (int i = 0; i < directorio.getNumArchivos(); i++) {
            if (directorio.getArchivos()[i].getNombre().equals(nombre)) {
                return directorio.getArchivos()[i];
            }
        }

        // Buscar en los subdirectorios (recursivo)
        for (int i = 0; i < directorio.getNumSubdirectorios(); i++) {
            Archivo archivo = buscarArchivo(nombre, directorio.getSubdirectorios()[i]);
            if (archivo != null) {
                return archivo;
            }
        }

        return null; // Archivo no encontrado
    }
    
    public void guardarEstadoEnArchivo(String rutaArchivo) {
    try (FileWriter writer = new FileWriter(rutaArchivo)) {
        // Guardar la estructura de directorios y archivos
        guardarDirectorioEnArchivo(raiz, writer);

        // Guardar la tabla de asignación
        writer.write("=== Tabla de Asignación ===\n");
        for (int i = 0; i < tablaAsignacion.getNumEntradas(); i++) {
            TablaAsignacion.EntradaTabla entrada = tablaAsignacion.getTabla()[i];
            writer.write(entrada.getNombreArchivo() + " | " + entrada.getTamaño() + " | " + entrada.getPrimerBloque() + "\n");
        }

        // Guardar el registro de auditoría
        writer.write("=== Registro de Auditoría ===\n");
        for (int i = 0; i < auditoria.getNumRegistros(); i++) {
            Auditoria.RegistroAuditoria registro = auditoria.getRegistros()[i];
            writer.write(registro.getTimestamp() + " | " + registro.getOperacion() + " | " + registro.getUsuario() + "\n");
        }

        System.out.println("Estado del sistema guardado en: " + rutaArchivo);
    } catch (IOException e) {
        System.out.println("Error al guardar el estado del sistema: " + e.getMessage());
    }
}

// Método auxiliar para guardar la estructura de directorios y archivos (recursivo)
    private void guardarDirectorioEnArchivo(Directorio directorio, FileWriter writer) throws IOException {
        writer.write("=== Directorio: " + directorio.getNombre() + " ===\n");

        // Guardar archivos en el directorio actual
        for (int i = 0; i < directorio.getNumArchivos(); i++) {
            Archivo archivo = directorio.getArchivos()[i];
            writer.write("Archivo: " + archivo.getNombre() + " | Tamaño: " + archivo.getTamaño() + " | Primer Bloque: " + archivo.getPrimerBloque() + "\n");

            // Guardar versiones del archivo
            writer.write("Versiones:\n");
            for (int j = 0; j < archivo.getNumVersiones(); j++) {
                int[] bloques = archivo.getVersiones()[j];
                writer.write("Versión " + j + ": ");
                for (int bloque : bloques) {
                    writer.write(bloque + " ");
                }
                writer.write("\n");
            }
        }

        // Guardar subdirectorios (recursivo)
        for (int i = 0; i < directorio.getNumSubdirectorios(); i++) {
            guardarDirectorioEnArchivo(directorio.getSubdirectorios()[i], writer);
        }
    }

    public void cargarEstadoDesdeArchivo(String rutaArchivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.startsWith("=== Directorio: ")) {
                    // Procesar directorio
                    String nombreDirectorio = linea.substring(15, linea.length() - 4);
                    Directorio directorio = new Directorio(nombreDirectorio, null);
                    // Aquí debes implementar la lógica para reconstruir la estructura de directorios y archivos
                } else if (linea.startsWith("Archivo: ")) {
                    // Procesar archivo
                    String[] partes = linea.split(" \\| ");
                    String nombreArchivo = partes[0].substring(9);
                    int tamaño = Integer.parseInt(partes[1].substring(8));
                    int primerBloque = Integer.parseInt(partes[2].substring(14));
                    Archivo archivo = new Archivo(nombreArchivo, tamaño);
                    archivo.setPrimerBloque(primerBloque);
                    // Aquí debes agregar el archivo al directorio correspondiente
                } else if (linea.startsWith("Versión ")) {
                    // Procesar versión
                    String[] partes = linea.split(": ");
                    int indiceVersion = Integer.parseInt(partes[0].substring(8));
                    String[] bloquesStr = partes[1].split(" ");
                    int[] bloques = new int[bloquesStr.length];
                    for (int i = 0; i < bloquesStr.length; i++) {
                        bloques[i] = Integer.parseInt(bloquesStr[i]);
                    }
                    // Aquí debes agregar la versión al archivo correspondiente
                } else if (linea.startsWith("=== Tabla de Asignación ===")) {
                    // Procesar tabla de asignación
                    while ((linea = reader.readLine()) != null && !linea.startsWith("===")) {
                        String[] partes = linea.split(" \\| ");
                        String nombreArchivo = partes[0];
                        int tamaño = Integer.parseInt(partes[1]);
                        int primerBloque = Integer.parseInt(partes[2]);
                        // Aquí debes reconstruir la tabla de asignación
                    }
                } else if (linea.startsWith("=== Registro de Auditoría ===")) {
                    // Procesar registro de auditoría
                    while ((linea = reader.readLine()) != null && !linea.startsWith("===")) {
                        String[] partes = linea.split(" \\| ");
                        String timestamp = partes[0];
                        String operacion = partes[1];
                        String usuario = partes[2];
                        auditoria.registrarOperacion(operacion, usuario);
                    }
                }
            }
            System.out.println("Estado del sistema cargado desde: " + rutaArchivo);
        } catch (IOException e) {
            System.out.println("Error al cargar el estado del sistema: " + e.getMessage());
        }
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
