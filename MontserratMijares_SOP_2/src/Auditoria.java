/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author danie
 */
import java.text.SimpleDateFormat;
import java.util.Date;

public class Auditoria {
    private RegistroAuditoria[] registros; // Array para almacenar los registros
    private int numRegistros; // Contador de registros

    // Tamaño máximo de registros (ajustable según sea necesario)
    private static final int MAX_REGISTROS = 1000;

    // Constructor
    public Auditoria() {
        this.registros = new RegistroAuditoria[MAX_REGISTROS];
        this.numRegistros = 0;
    }

    // Método para registrar una operación
    public void registrarOperacion(String operacion, String usuario) {
        if (numRegistros < MAX_REGISTROS) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            RegistroAuditoria registro = new RegistroAuditoria(timestamp, operacion, usuario);
            registros[numRegistros] = registro;
            numRegistros++;
        } else {
            System.out.println("Error: El registro de auditoría está lleno.");
        }
    }

    // Método para obtener todos los registros
    public RegistroAuditoria[] getRegistros() {
        return registros;
    }

    // Método para obtener el número de registros
    public int getNumRegistros() {
        return numRegistros;
    }

    // Clase interna para representar un registro de auditoría
    public static class RegistroAuditoria {
        private String timestamp;
        private String operacion;
        private String usuario;

        public RegistroAuditoria(String timestamp, String operacion, String usuario) {
            this.timestamp = timestamp;
            this.operacion = operacion;
            this.usuario = usuario;
        }

        // Getters
        public String getTimestamp() {
            return timestamp;
        }

        public String getOperacion() {
            return operacion;
        }

        public String getUsuario() {
            return usuario;
        }
    }
}
