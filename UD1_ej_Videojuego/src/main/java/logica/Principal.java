package logica;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
				= GestionPartida.class
				.getClassLoader()
				.getResourceAsStream("config.properties");
					
			prop.load(is);
			nombreFichero = prop.getProperty("datos");
		} catch (FileNotFoundException e) {
			nombreFichero = "partida_guardada.xml";
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		GestionPartida gestion = new GestionPartida(nombreFichero);
		int op = 0;
		do {
			mostrarMenu();
			System.out.print("Introduzca opcion: ");
			op = Leer.entero();
			
			switch (op) {
			case 1:
				gestion.listarEquipoVivo();
				break;
			case 2:
				System.out.println(gestion.heroeMasFuerte());
				break;
			case 3:
				System.out.print("Introduzca id del heroe que quieres que tome una pocion: ");
				String id_pocion = Leer.linea();
				
				System.out.println("Cantidad de vida: ");
				int cantidadVida = Leer.entero();
				
				gestion.beberPorcion(id_pocion, cantidadVida);
				break;
			case 4:
				System.out.print("Introduzca id del heroe que quieres dañar: ");
				String id_dañar = Leer.linea();
				
				System.out.print("Introduzca cuando daño: ");
				int daño = Leer.entero();
				
				gestion.recibeGolpe(id_dañar, daño);
				break;
			case 5:
				System.out.print("Introduzca id del objeto a eliminar: ");
				String id_objeto = Leer.linea();
				
				gestion.venderObjeto(id_objeto);
				break;
			case 6:
				System.out.print("En que gremio lo quiere introducir?: ");
				String gremio = Leer.linea();
				
				System.out.println("Introduzca los datos del heroe");
				System.out.print("ID: ");
				String id = Leer.linea();
				System.out.print("Clase: ");
				String clase = Leer.linea();
				System.out.print("Nombre: ");
				String nombre = Leer.linea();
				System.out.print("Nivel: ");
				int nivel = Leer.entero();
				System.out.print("Vida actual: ");
				int vidaActual = Leer.entero();
				System.out.print("Vida maxima: ");
				int vidaMaxima = Leer.entero();
				System.out.print("Ataque: ");
				int ataque = Leer.entero();
				System.out.print("Esta vivo?: ");
				Boolean estaVivo = Leer.booleano();
				
				Heroe heroe = new Heroe(id, clase, nombre, nivel, vidaActual, vidaMaxima, ataque, estaVivo);
				gestion.crearMercenario(gremio, heroe);
				break;
				
			}
			
		} while (op !=8);
		
		
	}
	
	private static void mostrarMenu() {
		System.out.println("1.- Listar el equipo de heroes que esta vivo");
		System.out.println("2.- Buscar al heroe mas fuerte");
		System.out.println("3.- Hacer que un heroe  tome una pocion");
		System.out.println("4.- Un heroe recibe un golpe");
		System.out.println("5.- Vender un objeto");
		System.out.println("6.- Añadir un heroe");
		System.out.println("7.- Suubir de nivel el mundo");
		System.out.println("8.- SALIR");
	}
	
	
	
	
	

}
