package ui;

import model.Pedido;
import model.Repartidor;
import model.ZonaDeCarga;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main
{
   // Punto de entrada para ejecutar la aplicación:
    public static void main(String[] args)
    {
        // 1 - Se crean una nueva Zona de carga para almacenar los pedidos recibidos por Speed fast:
        ZonaDeCarga acopio1 = new ZonaDeCarga(3);

        // 2 - Se generan los pedidos para posteriormente, almacenarlos en la bodega:
        Pedido pedido1 = new Pedido("La Travesía # 7420");
        Pedido pedido2 = new Pedido ("Argentina # 412");
        Pedido pedido3 = new Pedido ("Zenteno # 123");
        Pedido pedido4 = new Pedido("Moneda # 6731");
        Pedido pedido5 = new Pedido ("Agustinas # 83");
        Pedido pedido6 = new Pedido ("Ricardo Lyon # 2345");

        // 3 - Se agregan los pedidos al almacén de pedidos:
        Thread productor = new Thread(() -> {
            try
            {
                Thread.sleep(1000);
                acopio1.agregarPedido(pedido1);
                Thread.sleep(2000);
                acopio1.agregarPedido(pedido2);
                Thread.sleep(3000);
                acopio1.agregarPedido(pedido3);
                Thread.sleep(4000);
                acopio1.agregarPedido(pedido4);
                Thread.sleep(3000);
                acopio1.agregarPedido(pedido5);
                Thread.sleep(2000);
                acopio1.agregarPedido(pedido6);
                Thread.sleep(1000);
                acopio1.finalizarProduccion(); // Se llama a este método para notificar a todos los hilos que no quedan más productos almacenados.
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                System.out.println("[ZONA DE CARGA] : Se ha interrumpido el almacenamiento de pedidos");
            }
        });
        productor.start();

        // 4 - se crean tres repartidores para entregar los
        Repartidor repartidor1 = new Repartidor("Cristian Urbina", acopio1);
        Repartidor repartidor2 = new Repartidor("Ignacio Aedo", acopio1);
        Repartidor repartidor3 = new Repartidor("Nadia González", acopio1);

        // 5 - Se crean 3 hilos para que cada repartidor ejecute su proceso de retiro y entrega de pedidos.
        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.execute(repartidor1);
        executor.execute(repartidor2);
        executor.execute(repartidor3);

        // Se establece la cantidad de tiempo en que se va a realizar la simulación durante 30 segundos
        try
        {
            // Permite que la simulación se mantenga activa por 20 segundos
            Thread.sleep(30000);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        // Apagar el sistema, para que nos e inicien nuevos hilos.
        executor.shutdown();

        try
        {
            // Si los hilos no han terminado en 60 segundos, lanza el siguiente mensaje:
            if (!executor.awaitTermination(60, TimeUnit.SECONDS))
            {
                System.out.println("[Main] Algunos hilos no finalizaron correctamente.");
            }
        }
        catch (InterruptedException e)
        {
            executor.shutdownNow();
        }
    }
}