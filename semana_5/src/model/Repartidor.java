package model;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Clase que representa un repartidor genérico de Speed Fast.
 */
public class Repartidor implements Runnable
{
    private final ZonaDeCarga zona;     // Atributo que almacena un objeto tipo "Zona de Carga".
    private String nombreRepartidor;    // Atributo para almacenar el nombre del repartidor.
    private final PriorityBlockingQueue<Pedido> colaDePedidos = new PriorityBlockingQueue<>();    // Atributo para almacenar los pedidos que lleva.
    private final ReentrantLock candado = new ReentrantLock();  // Atributo para implementar una "Llave", que limitará el acceso al método crítico "retirar pedido de bodega".

    // Constructor con parámetros:
    public Repartidor(String nombreRepartidor, ZonaDeCarga zona)
    {
        this.nombreRepartidor = nombreRepartidor;
        this.zona = zona;
    }

    /**
     * Método que permite ejecutar secuencialmente los métodos al interior.
     */
    @Override
    public void run()
    {
        while(!Thread.currentThread().isInterrupted())  // mientras el hilo que se ejecuta no sea interrumpido, ejecuta esto:
        {
            try {
                Thread.sleep(tiempoAleatorio());
                viajarAZonaDeCarga();
                Thread.sleep(tiempoAleatorio());
                Pedido pedido = retirarPedidoDeBodega(); // Si los repartidores no tienen pedidos, se rompe el bucle WHILE
                if (pedido == null)
                {
                    System.out.println("[" + Thread.currentThread().getName() + "] [REPARTIDOR] : " + getNombreRepartidor() +
                            " finaliza su jornada.");
                    break;
                }

                Thread.sleep(tiempoAleatorio());
                enRepartoDePedidos();
                Thread.sleep(tiempoAleatorio());
                confirmarEntregaDePedidos();
                Thread.sleep(tiempoAleatorio());
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                System.out.println("[MAIN] se ha interrumpido el proceso 'run()'.");
            }
        }
    }

    /**
     * Método que se utiliza para simular un tiempo de espera.
     * @return un valor tipo integer con la cantidad de milisegundos que se desea pausar la ejecución de un método.
     */
    public int tiempoAleatorio()
    {
        return (int)(Math.random()*3500)+500;
    }

    /**
     * Método que establece el inicio de los movimientos del repartidor
     */
    public void viajarAZonaDeCarga()
    {
        try {
            System.out.println("[" + Thread.currentThread().getName() + "] " + "[REPARTIDOR]" + " " +
                    getNombreRepartidor() + " se encuentra viajando a retirar su pedido.");
            Thread.sleep(2000);

            System.out.println("[" + Thread.currentThread().getName() + "] " + "[REPARTIDOR]" + " " +
                    getNombreRepartidor() + " ha llegado exitosamente a retirar su pedido.");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("[" + Thread.currentThread().getName() + "] " + "[REPARTIDOR]" + getNombreRepartidor() + " no pudo llegar a la zona de carga.");
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Método que simula el retiro de pedidos desde la zona de carga.
     * Implementa un "candado con llave". Por lo que al ejecutar este método, no puede acceder a él otro repartidor.
     * @return un objeto de tipo "Pedido"
     */
    public Pedido retirarPedidoDeBodega()
    {
        // Al llamar a este método, cierra el candado y toma la llave:
        candado.lock();
        try
        {
            Pedido pedido = zona.retirarPedido();
            // Si el pedido es nulo, lanza este mensaje y retorna un valor null:
            if (pedido == null)
            {
                System.out.println("[REPARTIDOR]: No hay pedidos para retirar.");
                return null;
            }
            // Si se logra retirar un pedido de la bodega, ejecuta esto:
            pedido.setEstado(EstadoPedido.EN_REPARTO);  // A - Cambia el estado del pedido a "EN_REPARTO"
            colaDePedidos.add(pedido);                  // B - Añade el pedido a la cola de pedidos del repartidor.
            System.out.println("[" + Thread.currentThread().getName() + "] " + "[REPARTIDOR]" + " " + getNombreRepartidor() + " : se ha retirado su pedido N°" + " " + pedido.getIdPedido() +
                               " .El estado de su pedido es: " + pedido.getEstadoPedido());
            return pedido;
        }
        // Después de ejecutar este método, abre el candado y deja la llave puesta.
        finally
        {
            candado.unlock();
        }
    }

    /**
     * Método que muestra que el repartidor se encuentra en camino a su destino
     */
    public void enRepartoDePedidos()
    {
        try
        {
            System.out.println("[" + Thread.currentThread().getName() + "] " + "[REPARTIDOR] " +
                    getNombreRepartidor() +
                    " se encuentra viajando a entregar su pedido.");
            Thread.sleep(2000);

            System.out.println("[" + Thread.currentThread().getName() + "] " +  "[REPARTIDOR] " +
                    getNombreRepartidor() +
                    " ha llegado exitosamente a su domicilio.");
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            System.out.println("[" + Thread.currentThread().getName() + "] " + "[REPARTIDOR] " + getNombreRepartidor() + " no pudo llegar a la zona de carga.");
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Método que se llama para confirmar que el pedido fue entregado.
     * @return un objeto tipo "Pedido".
     */
    public Pedido confirmarEntregaDePedidos()
    {
        try
        {
            Pedido pedido = colaDePedidos.poll();
            // Si se entrega el pedido, cambia el estado a "ENTREGADO".
            if(pedido == null)
            {
                System.out.println("[" + Thread.currentThread().getName() + "] " +  "[REPARTIDOR] " +
                        getNombreRepartidor() + " no hay pedidos para entregar.");
                return null;
            }
            pedido.setEstado(EstadoPedido.ENTREGADO);
            System.out.println("[" + Thread.currentThread().getName() + "] " +  "[REPARTIDOR] " +
                    getNombreRepartidor() +
                    " ha entregado su pedido.");
            Thread.sleep(500);
            System.out.println("[REPARTIDOR] : El estado del pedido N°: " + pedido.getIdPedido() +  " es: " +  pedido.getEstadoPedido() +
                                " en la dirección: " + pedido.getDireccionEntrega());
            return pedido;
        }
        catch (InterruptedException e)
        {
            System.out.println("[" + Thread.currentThread().getName() + "] " + "[REPARTIDOR] " + getNombreRepartidor() + " no pudo llegar a la zona de carga");
            Thread.currentThread().interrupt();
        }
        return null;
    }

    // Se implementa un método "Getter"

    /**
     * Método que retorna el valor de la variable "nombreRepartidor"
     * @return un String con el nombre del repartidor.
     */
    public String getNombreRepartidor() {return nombreRepartidor;}

    /**
     * Método que retorna la lista de Cola de Pedidos.
     * @return la Priority blocking queue, llamada cola de pedidos.
     */
    public PriorityBlockingQueue getColaDePedidos() {return colaDePedidos;}

    // Se implementa un método "Setter":

    /**
     * Método que modifica el valor de la variable "nombreRepartidor"
     *
     * @param nombreRepartidor nuevo nombre que se requiere asignar a repartidor.
     */
    public void setNombreRepartidor(String nombreRepartidor) {this.nombreRepartidor = nombreRepartidor;}

    /**
     * Método que devuelve una cadena de texto con la información del objeto.
     * @return "nombre del repartidor"
     */
    public String toString() {return "el nombre del repartidor es: " + " " + nombreRepartidor;}
}


