package model;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * Clase que representa una zona de carga desde donde se retirarán los pedidos
 */
public class ZonaDeCarga
{
    private boolean produccionFinalizada = false;                    // Atributo que indica si la carga de productos a la zona de carga se encuentra finalizada. Es falso por defecto.
    private final PriorityBlockingQueue<Pedido> almacenDePedidos;    // Atributo para almacenar pedidos por prioridad de llegada.
    private final int capacidadMaxima;                               // Atributo que almacena la capacidad máxima del almacén de pedidos.

    // Se implementa un constructor para asignar una capacidad inicial para almacenar elementos en el "almacen de pedidos".
    public ZonaDeCarga(int capacidadMaxima)
    {
        this.almacenDePedidos = new PriorityBlockingQueue<>();
        this.capacidadMaxima = capacidadMaxima;
    }

    // Se implementa un método "agregarPedido"

    /**
     * Método que almacena pedidos en la cola de pedidos de esta clase.
     * En caso de que no tenga espacio, se detiene la carga de objetos.
     * @param pedido objeto tipo "Pedido" que se necesita almacenar.
     */
    public synchronized void agregarPedido(Pedido pedido)
    {
        try
        {
            // Mientras la cantidad de objetos de almacén de pedidos, sea mayor a la capacidad máxima de almacenamiento:
            while (almacenDePedidos.size() >= capacidadMaxima)
            {
                System.out.println("[ZONA DE CARGA] : Zona de Carga a máxima capacidad. Esperando un espacio disponible.....");
                wait();
            }
            // En caso contrario, agrega un pedido al almacén de pedidos, imprime el mensaje, y notifica a todos los hilos
            almacenDePedidos.add(pedido);
            System.out.println("[ZONA DE CARGA] : El pedido N°: " + pedido.getIdPedido() + " ha sido agregado a la Zona de Carga." + " Estado del pedido: " + pedido.getEstadoPedido() +".");
            notifyAll();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            System.out.println("[ZONA DE CARGA]: Se ha interrumpido el proceso de carga.");
        }
    }

    /**
     * Método sincronizado para retirar un pedido desde el almacén de pedidos.
     * @return un objeto tipo pedido.
     */
    public synchronized Pedido retirarPedido()
    {
        try
        {
            // Mientras el almacen de pedidos se encuentre vacío...
            while (almacenDePedidos.isEmpty())
            {
                // Si se ha finalizado la producción...
                if (produccionFinalizada)
                {
                    // Imprime esto, y termina el bucle.
                    System.out.println("[" + Thread.currentThread().getName() + "] [REPARTIDOR] : no quedan pedidos que repartir.");
                    break;
                }
                // Si NO se ha finalizado la producción, pero el almacén está vacío, imprime esto y mantente a la espera de nuevos pedidos.
                System.out.println(("[" + Thread.currentThread().getName() + "] [REPARTIDOR] : no hay pedidos almacenados. Esperando ....."));
                wait();
            }
            // En caso de que el almacén tenga pedidos, retira el primer elemento y guárdalo como un pedido.
            Pedido pedido = almacenDePedidos.poll();
            // Si no hay un pedido al sacar un elemento de la lista, imprime esto:
            if (pedido == null)
            {
                System.out.println("[ZONA DE CARGA] : No hay pedidos disponibles");
                return null;
            }
            // establece un número tipo integer, que refleje cuantos pedidos quedan:
            int espaciosDisponibles = capacidadMaxima - almacenDePedidos.size();

            System.out.println("[ZONA DE CARGA] : " + "[" + Thread.currentThread().getName() + "] " + "retiró el pedido N°: " + pedido.getIdPedido() + " " + ".Espacio disponible: " +  " " + espaciosDisponibles + "/" + capacidadMaxima +".");
            notifyAll();
            return pedido;
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            System.out.println("[ZONA DE CARGA] : Se ha interrumpido el proceso de entrega.");
        }
        return null;
    }

    /**
     * Método que se utiliza para indicar que no se aceptarán más pedidos
     */
    public synchronized void finalizarProduccion()
    {
        produccionFinalizada = true;
        System.out.println("[ZONA DE CARGA] : Finalizada la recepción de productos, no se recibirán más pedidos.");
        notifyAll();
    }

    // Se implementa un método "Getter":

    /**
     * Método que retorna la lista "almacén de pedidos".
     * @return una Priority Blocking Queue, llamada "almacénDePedidos".
     */
    public PriorityBlockingQueue getAlmacenDePedidos() {return almacenDePedidos;}
}
