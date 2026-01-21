package logica;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import consola.Leer;

public class Principal {

	public static void main(String[] args) {
		
		String nombreFichero = "";
		
		Properties prop = new Properties();
		
		try {
			
			
			//ASÍ NO ENCUENTRA EL FICHERO
			//prop.load(new FileInputStream("config.properties"));
			InputStream is 
				= GestionManifiesto.class
				.getClassLoader()
				.getResourceAsStream("config.properties");
					
			prop.load(is);
			nombreFichero = prop.getProperty("datos").replace("\"", "");
		} catch (FileNotFoundException e) {
			nombreFichero = "manifiesto_galactico.xml";
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		GestionManifiesto gestion = new GestionManifiesto(nombreFichero);
		
		int op = 0;
		
		do {
			mostrarMenu();
			System.out.print("Introduza opcion: ");
			op = Leer.datoInt();
			
			switch (op) {
			case 1:
				List<String> navesReparacion = gestion.obtenerNavesEnReparacion();
				
				for (String nave : navesReparacion) {
					System.out.println(nave);
				}
				break;
			case 2:
				System.out.print("Introduzca matricula: ");
				String matricula = Leer.dato();
				
				System.out.println(gestion.infoCapitan(matricula));;
				break;
			case 3:
				System.out.println("Introduzca id de la estacion: ");
				String idEstacion = Leer.dato();
				
				System.out.println("Valor carga peligrosa: "+gestion.calcularCargaPeligrosa(idEstacion));
				break;
			case 4:
				System.out.println("Peso total carga: "+gestion.calcularPeso());
				break;
			case 5:
				System.out.println("Saliendo...");
				break;
			default:
				System.out.println("Introduza una opcion valida");
			}
			
		} while (op != 5);
		
	}
	
	private static void mostrarMenu() {
		System.out.println("1. Obtener naves en reparacion");
		System.out.println("2. Encontrar al capitan basandonos en la nave");
		System.out.println("3. Calcular valor carga peligrosa por estacion");
		System.out.println("4. Calcular peso total carga");
		System.out.println("5. SALIR");
	}
}
