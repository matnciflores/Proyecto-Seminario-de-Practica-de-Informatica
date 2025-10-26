package manager;

import model.Cliente;
import java.util.ArrayList;
import java.util.List;

public class ClienteManager {

    private List<Cliente> clientes;
    private int proximoId = 1; //simular el AUTO_INCREMENT de la BD

    public ClienteManager() {
        this.clientes = new ArrayList<>();
    }

    //agrega un nuevo cliente al sistema.
    public void agregarCliente(Cliente cliente) throws Exception {
        if (buscarClientePorDni(cliente.getDni()) != null) {
            throw new Exception("Error: El DNI " + cliente.getDni() + " ya está registrado.");
        }
        
        cliente.setClienteId(proximoId++); 
        this.clientes.add(cliente);
        System.out.println("Cliente agregado: " + cliente);
    }

    public Cliente buscarClientePorId(int id) {
        for (Cliente c : this.clientes) {
            if (c.getClienteId() == id) {
                return c;
            }
        }
        return null;
    }
    
    public void modificarCliente(int id, String nuevoNombre, String nuevoApellido, String nuevoDni, String nuevoTelefono, String nuevoEmail) throws Exception {
        
        Cliente clienteConEseDni = buscarClientePorDni(nuevoDni);
        if (clienteConEseDni != null && clienteConEseDni.getClienteId() != id) {
            throw new Exception("Error: El DNI " + nuevoDni + " ya pertenece a otro cliente.");
        }
        
        //cliente a modificar
        Cliente clienteAModificar = buscarClientePorId(id);
        if (clienteAModificar != null) {
            //actualizamos sus datos
            clienteAModificar.setNombre(nuevoNombre);
            clienteAModificar.setApellido(nuevoApellido);
            clienteAModificar.setDni(nuevoDni);
            clienteAModificar.setTelefono(nuevoTelefono);
            clienteAModificar.setEmail(nuevoEmail);
            System.out.println("Cliente modificado: " + clienteAModificar);
        } else {
            throw new Exception("Error: No se encontró el cliente con ID " + id);
        }
    }
    
    public Cliente buscarClientePorDni(String dni) {
        // ESTRUCTURA REPETITIVA (for) para búsqueda
        for (Cliente c : this.clientes) {
            if (c.getDni().equals(dni)) {
                return c;
            }
        }
        return null;     }

    
    public List<Cliente> obtenerTodosLosClientes() {
        return new ArrayList<>(this.clientes);
    }
}