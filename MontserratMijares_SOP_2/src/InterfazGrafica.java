import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InterfazGrafica {

    private SistemaArchivos sistema;
    private JTree tree;
    private JTable tablaAsignacion;
    private JTextArea logAuditoria;

    public InterfazGrafica(SistemaArchivos sistema) {
        this.sistema = sistema;
    }

    public void mostrar() {
        // Crear el marco principal
        JFrame frame = new JFrame("Simulador de Sistema de Archivos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);

        // Crear el panel principal con BorderLayout
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        frame.add(panelPrincipal);

        // 1. Panel izquierdo: JTree para la estructura de archivos
        DefaultMutableTreeNode raizTree = construirArbol(sistema.getRaiz());
        tree = new JTree(raizTree);
        JScrollPane treeScrollPane = new JScrollPane(tree);
        panelPrincipal.add(treeScrollPane, BorderLayout.WEST);

        // 2. Panel central: JTable para la tabla de asignación
        String[] columnas = {"Nombre", "Bloques Asignados", "Primer Bloque"};
        Object[][] datos = obtenerDatosTablaAsignacion();
        tablaAsignacion = new JTable(datos, columnas);
        JScrollPane tablaScrollPane = new JScrollPane(tablaAsignacion);
        panelPrincipal.add(tablaScrollPane, BorderLayout.CENTER);

        // 3. Panel inferior: JTextArea para el registro de auditoría
        logAuditoria = new JTextArea();
        logAuditoria.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logAuditoria);
        panelPrincipal.add(logScrollPane, BorderLayout.SOUTH);

        // 4. Panel superior: Botones para operaciones CRUD
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCrearArchivo = new JButton("Crear Archivo");
        JButton btnEliminarArchivo = new JButton("Eliminar Archivo");
        JButton btnCrearDirectorio = new JButton("Crear Directorio");
        JButton btnEliminarDirectorio = new JButton("Eliminar Directorio");
        JButton btnCambiarModo = new JButton("Cambiar Modo");
        JButton btnModificarArchivo = new JButton("Modificar Nombre Archivo");
        JButton btnModificarDirectorio = new JButton("Modificar Nombre Directorio");

        panelBotones.add(btnCrearArchivo);
        panelBotones.add(btnEliminarArchivo);
        panelBotones.add(btnCrearDirectorio);
        panelBotones.add(btnEliminarDirectorio);
        panelBotones.add(btnCambiarModo);
        panelBotones.add(btnModificarArchivo);
        panelBotones.add(btnModificarDirectorio);
        panelPrincipal.add(panelBotones, BorderLayout.NORTH);

        // 5. Manejar eventos de los botones
        btnCrearArchivo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crearArchivo();
            }
        });

        btnEliminarArchivo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarArchivo();
            }
        });

        btnCrearDirectorio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crearDirectorio();
            }
        });

        btnEliminarDirectorio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarDirectorio();
            }
        });

        btnCambiarModo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cambiarModo();
            }
        });

        btnModificarArchivo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificarNombreArchivo();
            }
        });

        btnModificarDirectorio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificarNombreDirectorio();
            }
        });

        // Mostrar el marco
        frame.setVisible(true);
    }

    // Método para construir el árbol a partir de la estructura de directorios
    private DefaultMutableTreeNode construirArbol(Directorio directorio) {
        DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(directorio.getNombre());

        // Agregar archivos
        for (int i = 0; i < directorio.getNumArchivos(); i++) {
            Archivo archivo = directorio.getArchivos()[i];
            nodo.add(new DefaultMutableTreeNode(archivo.getNombre()));
        }

        // Agregar subdirectorios (recursivo)
        for (int i = 0; i < directorio.getNumSubdirectorios(); i++) {
            Directorio subdirectorio = directorio.getSubdirectorios()[i];
            nodo.add(construirArbol(subdirectorio));
        }

        return nodo;
    }

    // Método para obtener los datos de la tabla de asignación
    private Object[][] obtenerDatosTablaAsignacion() {
        TablaAsignacion tabla = sistema.getTablaAsignacion();
        int numEntradas = tabla.getNumEntradas();
        Object[][] datos = new Object[numEntradas][3];

        for (int i = 0; i < numEntradas; i++) {
            TablaAsignacion.EntradaTabla entrada = tabla.getTabla()[i];
            datos[i][0] = entrada.getNombreArchivo(); // Nombre del archivo
            datos[i][1] = entrada.getTamaño();       // Cantidad de bloques asignados (tamaño)
            datos[i][2] = entrada.getPrimerBloque(); // Dirección del primer bloque
        }

        return datos;
    }

    // Método para actualizar la interfaz
    private void actualizarInterfaz() {
        // Actualizar el árbol
        DefaultMutableTreeNode raizTree = construirArbol(sistema.getRaiz());
        tree.setModel(new javax.swing.tree.DefaultTreeModel(raizTree));

        // Actualizar la tabla de asignación
        Object[][] datos = obtenerDatosTablaAsignacion();
        String[] columnas = {"Nombre", "Bloques Asignados", "Primer Bloque"}; // Nombres de columnas correctos
        tablaAsignacion.setModel(new javax.swing.table.DefaultTableModel(datos, columnas));

        // Actualizar el registro de auditoría
        StringBuilder log = new StringBuilder();
        for (int i = 0; i < sistema.getAuditoria().getNumRegistros(); i++) {
            Auditoria.RegistroAuditoria registro = sistema.getAuditoria().getRegistros()[i];
            log.append(registro.getTimestamp()).append(" - ").append(registro.getOperacion()).append(" - ").append(registro.getUsuario()).append("\n");
        }
        logAuditoria.setText(log.toString());
    }

    // Métodos para manejar las operaciones CRUD
    private void crearArchivo() {
        String nombre = JOptionPane.showInputDialog("Nombre del archivo:");
        if (nombre != null && !nombre.isEmpty()) {
            int tamaño = Integer.parseInt(JOptionPane.showInputDialog("Tamaño del archivo (en bloques):"));
            String rutaDirectorio = JOptionPane.showInputDialog("Ruta del directorio (dejar vacío para raíz):");
            Directorio directorioDestino = buscarDirectorioPorRuta(rutaDirectorio, sistema.getRaiz());

            if (directorioDestino != null) {
                sistema.crearArchivo(nombre, tamaño, directorioDestino);
                actualizarInterfaz();
            } else {
                JOptionPane.showMessageDialog(null, "Directorio no encontrado.");
            }
        }
    }

    private void eliminarArchivo() {
        String nombre = JOptionPane.showInputDialog("Nombre del archivo a eliminar:");
        if (nombre != null && !nombre.isEmpty()) {
            Archivo archivo = buscarArchivo(nombre, sistema.getRaiz());
            if (archivo != null) {
                sistema.eliminarArchivo(archivo, sistema.getRaiz());
                actualizarInterfaz();
            } else {
                JOptionPane.showMessageDialog(null, "Archivo no encontrado.");
            }
        }
    }

    private void crearDirectorio() {
        String nombre = JOptionPane.showInputDialog("Nombre del directorio:");
        if (nombre != null && !nombre.isEmpty()) {
            String rutaDirectorio = JOptionPane.showInputDialog("Ruta del directorio (dejar vacío para raíz):");
            Directorio directorioDestino = buscarDirectorioPorRuta(rutaDirectorio, sistema.getRaiz());

            if (directorioDestino != null) {
                sistema.crearDirectorio(nombre, directorioDestino);
                actualizarInterfaz();
            } else {
                JOptionPane.showMessageDialog(null, "Directorio no encontrado.");
            }
        }
    }

    private void eliminarDirectorio() {
        String nombre = JOptionPane.showInputDialog("Nombre del directorio a eliminar:");
        if (nombre != null && !nombre.isEmpty()) {
            Directorio directorio = buscarDirectorio(nombre, sistema.getRaiz());
            if (directorio != null) {
                sistema.eliminarDirectorio(directorio);
                actualizarInterfaz();
            } else {
                JOptionPane.showMessageDialog(null, "Directorio no encontrado.");
            }
        }
    }

    private Directorio buscarDirectorioPorRuta(String ruta, Directorio directorioActual) {
        if (ruta == null || ruta.isEmpty()) {
            return directorioActual; // Si no se especifica ruta, usar el directorio actual
        }

        String[] partes = ruta.split("/");
        for (String parte : partes) {
            if (parte.isEmpty()) {
                continue; // Ignorar partes vacías (por ejemplo, si la ruta empieza con "/")
            }
            boolean encontrado = false;
            for (int i = 0; i < directorioActual.getNumSubdirectorios(); i++) {
                if (directorioActual.getSubdirectorios()[i].getNombre().equals(parte)) {
                    directorioActual = directorioActual.getSubdirectorios()[i];
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                return null; // Directorio no encontrado
            }
        }
        return directorioActual;
    }

    private void cambiarModo() {
        String[] opciones = {"Administrador", "Usuario"};
        int seleccion = JOptionPane.showOptionDialog(null, "Seleccione el modo:", "Cambiar Modo", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (seleccion == 0) {
            sistema.cambiarModo(SistemaArchivos.ModoUsuario.ADMINISTRADOR);
        } else {
            sistema.cambiarModo(SistemaArchivos.ModoUsuario.USUARIO);
        }
        actualizarInterfaz();
    }

    // Métodos auxiliares para buscar archivos y directorios
    private Archivo buscarArchivo(String nombre, Directorio directorio) {
        for (int i = 0; i < directorio.getNumArchivos(); i++) {
            if (directorio.getArchivos()[i].getNombre().equals(nombre)) {
                return directorio.getArchivos()[i];
            }
        }
        for (int i = 0; i < directorio.getNumSubdirectorios(); i++) {
            Archivo archivo = buscarArchivo(nombre, directorio.getSubdirectorios()[i]);
            if (archivo != null) {
                return archivo;
            }
        }
        return null;
    }

    private Directorio buscarDirectorio(String nombre, Directorio directorio) {
        if (directorio.getNombre().equals(nombre)) {
            return directorio;
        }
        for (int i = 0; i < directorio.getNumSubdirectorios(); i++) {
            Directorio subdirectorio = buscarDirectorio(nombre, directorio.getSubdirectorios()[i]);
            if (subdirectorio != null) {
                return subdirectorio;
            }
        }
        return null;
    }

    private void modificarNombreArchivo() {
        String nombreActual = JOptionPane.showInputDialog("Nombre actual del archivo:");
        if (nombreActual != null && !nombreActual.isEmpty()) {
            String nuevoNombre = JOptionPane.showInputDialog("Nuevo nombre del archivo:");
            if (nuevoNombre != null && !nuevoNombre.isEmpty()) {
                Archivo archivo = buscarArchivo(nombreActual, sistema.getRaiz());
                if (archivo != null) {
                    archivo.setNombre(nuevoNombre);
                    sistema.getAuditoria().registrarOperacion("Modificar nombre de archivo: " + nombreActual + " -> " + nuevoNombre, sistema.getModo().toString());
                    actualizarInterfaz();
                } else {
                    JOptionPane.showMessageDialog(null, "Archivo no encontrado.");
                }
            }
        }
    }

    private void modificarNombreDirectorio() {
        String nombreActual = JOptionPane.showInputDialog("Nombre actual del directorio:");
        if (nombreActual != null && !nombreActual.isEmpty()) {
            String nuevoNombre = JOptionPane.showInputDialog("Nuevo nombre del directorio:");
            if (nuevoNombre != null && !nuevoNombre.isEmpty()) {
                Directorio directorio = buscarDirectorio(nombreActual, sistema.getRaiz());
                if (directorio != null) {
                    directorio.setNombre(nuevoNombre);
                    sistema.getAuditoria().registrarOperacion("Modificar nombre de directorio: " + nombreActual + " -> " + nuevoNombre, sistema.getModo().toString());
                    actualizarInterfaz();
                } else {
                    JOptionPane.showMessageDialog(null, "Directorio no encontrado.");
                }
            }
        }
    }
}