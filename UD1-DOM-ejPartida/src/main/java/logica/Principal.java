package logica;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import consola.Leer;

public class Principal {

    public static void main(String[] args) {
        
        String nombreFichero = "partida_rol.xml"; // Valor por defecto
        Properties prop = new Properties();
        
        try {
            // Carga robusta del fichero de propiedades
            InputStream is = GestionPartida.class
                .getClassLoader()
                .getResourceAsStream("config.properties");
            
            if (is != null) {
                prop.load(is);
                // LEEMOS EL FICHERO Y QUITAMOS COMILLAS Y ESPACIOS
                String datos = prop.getProperty("datos");
                if (datos != null) {
                    nombreFichero = datos.replace("\"", "").trim();
                }
            } else {
                System.err.println("AVISO: No se encontró config.properties, usando valor por defecto.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Instanciamos la gestión
        GestionPartida gestion = new GestionPartida(nombreFichero);
        
        int op = 0;
        
        do {
            mostrarMenu();
            System.out.print("Introduzca la opcion: ");
            op = Leer.datoInt();
            
            // Separador visual
            System.out.println("------------------------------------------------"); 
            
            switch (op) {
            case 1: 
                System.out.println("Nombre jugador: " + gestion.obtenerNombreJugador());
                break;
            case 2:
                System.out.println("Oro ganado en misiones (completadas): " + gestion.calcularOroGanadoMisiones());
                break;
            case 3:
                System.out.println("ARMAS EQUIPADAS:");
                List<String> armas = gestion.obtenerArmasEquipadas();
                if (armas.isEmpty()) System.out.println(" - Ninguna -");
                for (String arma : armas) {
                    System.out.println(" - " + arma);
                }
                break;
            case 4: 
                System.out.println("COMPAÑEROS MUERTOS:");
                List<String> muertos = gestion.listarCompanerosMuertos();
                for (String compañero : muertos) {
                    System.out.println(" † " + compañero);
                }
                break;
            case 5:
                System.out.println("Valor total inventario: " + gestion.calcularValorIventario());
                break;
            case 6:
                System.out.println("ITEMS ROTOS:");
                List<String> rotos = gestion.encontraItemsRotos();
                if (rotos.isEmpty()) System.out.println(" - Ninguno -");
                for (String itemRoto : rotos) {
                    System.out.println(" - " + itemRoto);
                }
                break;
            case 7:
                System.out.println("MISIONES CON LÍMITE DE TIEMPO:");
                for (String mision : gestion.obtenerMisionesConLimiteTiempo()) {
                    System.out.println(" -> " + mision);
                }
                break;
            case 8:
                System.out.println("Mejor estadística: " + gestion.obtenerMejorEstadistica());
                break;
            case 9:
                System.out.println("Enemigo principal: " + gestion.obtenerEnemigoPrincipal());
                break;
            case 10:
                System.out.print("Introduzca nombre del arma (Ej: Espada Solar): ");
                String nombreArma = Leer.dato();
                
                // CORRECCIÓN IMPORTANTE: Capturamos el resultado y lo mostramos
                double durabilidad = gestion.porcentajeDurabilidad(nombreArma);
                
                if (durabilidad == -1) {
                    System.out.println("ERROR: El arma no existe o no tiene durabilidad.");
                } else {
                    // Usamos printf para formatear a 2 decimales
                    System.out.printf("Durabilidad restante de %s: %.2f%%\n", nombreArma, durabilidad);
                }
                break;
            case 11:
                System.out.println("Guardando partida y saliendo...");
                break;
            default:
                System.out.println("Opción no válida.");
                break;
            }
            System.out.println("------------------------------------------------\n");
            
        } while (op != 11);
    }
    
    private static void mostrarMenu() {
        System.out.println("=== GESTOR DE PARTIDA RPG ===");
        System.out.println("1. Obtener nombre del jugador");
        System.out.println("2. Calcular el oro ganado en misiones");
        System.out.println("3. Obtener las armas equipadas");
        System.out.println("4. Lista de compañeros muertos");
        System.out.println("5. Calcular valor total del inventario");
        System.out.println("6. Encontrar items rotos");
        System.out.println("7. Obtener misiones con limite de tiempo");
        System.out.println("8. Obtener mejores estadisticas");
        System.out.println("9. Obtener enemigo principal");
        System.out.println("10. Calcular porcentaje de durabilidad de un arma");
        System.out.println("11. SALIR");
    }
}