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
        String[] columnas = {"Nombre", "Tamaño", "Bloques Asignados", "Versión"};
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
        JButton btnCrearVersion = new JButton("Crear Nueva Versión");
        JButton btnRestaurarVersion = new JButton("Restaurar Versión");


        panelBotones.add(btnCrearArchivo);
        panelBotones.add(btnEliminarArchivo);
        panelBotones.add(btnCrearDirectorio);
        panelBotones.add(btnEliminarDirectorio);
        panelBotones.add(btnCambiarModo);
        panelPrincipal.add(panelBotones, BorderLayout.NORTH);
        panelBotones.add(btnCrearVersion);
        panelBotones.add(btnRestaurarVersion);

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
        btnCrearVersion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crearNuevaVersion();
            }
        });
        
        btnRestaurarVersion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restaurarVersion();
            }
        });

        

        // Mostrar el marco
        frame.setVisible(true);
    }
    private void crearNuevaVersion() {
        String nombre = JOptionPane.showInputDialog("Nombre del archivo para crear una nueva versión:");
        if (nombre != null && !nombre.isEmpty()) {
            Archivo archivo = buscarArchivo(nombre, sistema.getRaiz());
            if (archivo != null) {
                int[] nuevosBloques = sistema.asignarBloques(archivo.getTamaño());
                if (nuevosBloques != null) {
                    archivo.crearNuevaVersion(nuevosBloques);
                    sistema.getAuditoria().registrarOperacion("Nueva versión creada: " + nombre, sistema.getModo().toString());
                    actualizarInterfaz();
                } else {
                    JOptionPane.showMessageDialog(null, "No hay suficientes bloques libres para crear una nueva versión.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Archivo no encontrado.");
            }
        }
    }
    
    private void restaurarVersion() {
    String nombre = JOptionPane.showInputDialog("Nombre del archivo para restaurar una versión:");
    if (nombre != null && !nombre.isEmpty()) {
        Archivo archivo = buscarArchivo(nombre, sistema.getRaiz());
        if (archivo != null) {
            int numVersiones = archivo.getNumVersiones();
            if (numVersiones > 1) {
                String[] opciones = new String[numVersiones];
                for (int i = 0; i < numVersiones; i++) {
                    opciones[i] = "Versión " + i;
                }
                int seleccion = JOptionPane.showOptionDialog(null, "Seleccione la versión a restaurar:", "Restaurar Versión", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
                if (seleccion >= 0 && seleccion < numVersiones) {
                    archivo.restaurarVersion(seleccion);
                    sistema.getAuditoria().registrarOperacion("Versión " + seleccion + " restaurada: " + nombre, sistema.getModo().toString());
                    actualizarInterfaz();
                }
            } else {
                JOptionPane.showMessageDialog(null, "No hay versiones anteriores para restaurar.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Archivo no encontrado.");
        }
    }
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
            Archivo archivo = buscarArchivo(entrada.getNombreArchivo(), sistema.getRaiz());
            datos[i][0] = entrada.getNombreArchivo();
            datos[i][1] = entrada.getTamaño();
            datos[i][2] = java.util.Arrays.toString(entrada.getBloquesAsignados());
            if (archivo != null) {
            datos[i][3] = "Versión " + archivo.getVersionActual();
        } else {
            datos[i][3] = "N/A"; // En caso de no encontrarse
        }
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
        tablaAsignacion.setModel(new javax.swing.table.DefaultTableModel(datos, new String[]{"Nombre", "Tamaño", "Bloques Asignados"}));

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
            sistema.crearArchivo(nombre, tamaño, sistema.getRaiz());
            actualizarInterfaz();
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
            sistema.crearDirectorio(nombre, sistema.getRaiz());
            actualizarInterfaz();
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
    // Buscar recursivamente en subdirectorios
    for (int i = 0; i < directorio.getNumSubdirectorios(); i++) {
        Archivo encontrado = buscarArchivo(nombre, directorio.getSubdirectorios()[i]);
        if (encontrado != null) {
            return encontrado;
        }
    }
    return null;
}

    private Directorio buscarDirectorio(String nombre, Directorio directorio) {
        for (int i = 0; i < directorio.getNumSubdirectorios(); i++) {
            if (directorio.getSubdirectorios()[i].getNombre().equals(nombre)) {
                return directorio.getSubdirectorios()[i];
            }
        }
        return null;
    }
}