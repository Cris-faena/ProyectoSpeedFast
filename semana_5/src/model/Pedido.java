package model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Clase que representa un pedido genérico en "Speed Fast"-
 */
public class Pedido implements Comparable<Pedido>
{
    private static final AtomicInteger CONTADOR = new AtomicInteger();          // Se implementa un contador estático como atomic integer para asegurar un ID único en un entorno multi hilo.
    private final int idPedido;                                                 // Atributo que almacena el "ID" de un pedido.
    private String direccionEntrega;                                            // Atributo que almacena la dirección de entrega de un pedido.
    private EstadoPedido estadoPedido = EstadoPedido.PENDIENTE;                 // Atributo que almacena el estado del repartidor respecto al pedido.

    // Constructor con parámetros:
    public Pedido (String direccionEntrega)
    {
        this.idPedido = CONTADOR.incrementAndGet();         // Para cada instancia creada, se le asignará el ID de forma automática y segura.
        this.direccionEntrega = direccionEntrega;
    }

    // Constructor sin parámetros:
    public Pedido ()
    {
        this.idPedido = 0;
        this.direccionEntrega = "Sin dirección asignada";
    }

    // Se implementa un método "compareTo"

    /**
     * Método para comparar objetos por prioridad
     * @param otro un pedido diferente que se compara con el objeto creado
     * @return -1 si es menor, 0 si es igual, +1 si es mayor.
     */
    public int compareTo (Pedido otro)
    {
        return Integer.compare(this.idPedido, otro.idPedido);
    }

    // Se implementan los "Getters":
    /**
     * Método que retorna el valor de la variable "idPedido"
     * @return un valor tipo "int" con el valor de idPedido.
     */
    public int getIdPedido() {return idPedido;}

    /**
     * Método que retorna el valor de la variable "dirección entrega".
     * @return un String con la dirección de entrega de un pedido.
     */
    public String getDireccionEntrega() {return direccionEntrega;}

    /**
     * Método que retorna el valor de la variable "estado del pedido".
     * @return el estado del pedido actual del repartidor.
     * Puede ser PENDIENTE, EN_REPARTO o ENTREGADO.
     */
    public EstadoPedido getEstadoPedido() {return estadoPedido;}

    // Se implementan los "Setters"

    /**
     * Método que modifica el valor de la variable "idPedido".
     * @param direccionEntrega nuevo valor de la variable "direcciónEntrega" que se requiere asignar.
     */
    public void setDireccionEntrega(String direccionEntrega) {this.direccionEntrega = direccionEntrega;}

    /**
     * Método que modifica el valor de la variable "estadoPedido".
     * @param estadoPedido nuevo valor de la variable "estadoPedido" que se requiere asignar.
     */
    public void setEstado(EstadoPedido estadoPedido) {this.estadoPedido = estadoPedido;}

    // Se implementa un método "toString"

    /**
     * Método que devuelve la información del objeto en una cadena de texto.
     * @return Id, dirección y estado del pedido.
     */
    public String toString()
    {
        return String.format("→ eL ID del pedido es: %d%n" +
                             "→ La dirección de entrega es: %s%n" +
                             "→ El estado del pedido es %s%n",
                             idPedido, direccionEntrega, estadoPedido);
    }
}
