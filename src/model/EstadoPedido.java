package model;

/**
 * Clase que representa un conjunto fijo de variables.
 * Permiten establecer el estado del repartidor en relación con el pedido.
 */
public enum EstadoPedido
{
    PENDIENTE,      // Estado que se alcanza cuando el repartidor no tiene pedidos.
    EN_REPARTO,     // Estado que se alcanza cuando el repartidor tiene un pedido.
    ENTREGADO       // Estado que se alcanza cuando el repartidor entregó un pedido al cliente.
}
