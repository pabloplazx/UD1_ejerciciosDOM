package logica;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import consola.Leer;

public class Principal {
	
	
	public static void main(String[] args) {
		
		String nombreFichero = "";
		
		Properties prop = new Properties();
		
		try {
			InputStream is =
					GestionStreaming.class
					.getClassLoader()
					.getResourceAsStream("config.properties");
			prop.load(is);
			nombreFichero = prop.getProperty("datos");
		} catch (FileNotFoundException e) {
			nombreFichero = "streaming.xml";
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		GestionStreaming gestion = new GestionStreaming(nombreFichero);
		
		int op = 0; 
		
		do {
			mostrarMenu();
			System.out.print("Introduzca opcion: ");
			op = Leer.entero();
			
			switch (op) {
			case 1:
				System.out.print("Introduzca el id de la pelicula que desea buscar: ");
				String id_buscar = Leer.linea();
				
				gestion.buscarPelicula(id_buscar);
				break;
			case 2:
				System.out.println("Genero del que quiere buscar las peliculas");
				String genero_buscar = Leer.linea();
				
				gestion.listarPorGenero(genero_buscar);
				break;
			case 3:
				System.out.println("El tiempo completo de la duración de todas las peliculas es: "+gestion.contarTiempo()+" minutos");
				break;
			case 4:
				ArrayList<Pelicula> pelisPendientes = gestion.peliculasPendientes();
				
				System.out.println("Las peliculas que tienes pendientes son las siguiente: ");
				for (Pelicula peli : pelisPendientes) {
					System.out.println(peli);
				}
				break;
				
			case 5: 
				System.out.println("Introduzca el id de la pelicula que quiere indicar como vista: ");
				String id_vista = Leer.linea();
				
				gestion.marcarComoVista(id_vista);
				break;
			case 6:
				System.out.println("Introduza los datos de la pelicula que deseea incorporar");
				
				System.out.println("¿Que genero es la película que desea incorporar?: ");
				String genero = Leer.linea();
				
				System.out.print("Id: ");
				String id = Leer.linea();
				System.out.print("Titulo: ");
				String titulo = Leer.linea();
				System.out.print("Director: ");
				String director = Leer.linea();
				System.out.print("Duracion en minutos: ");
				int duracion = Leer.entero();
				System.out.print("Visionada?: ");
				boolean vista = Leer.booleano();
				
				Pelicula pelicula = new Pelicula(id, titulo, director, duracion, vista);
				gestion.añadirPelicula(genero, pelicula);
				
				break;
			case 7: 
				System.out.println("Introduzca el id de la pelicula que quiere eliminar: ");
				String id_eliminar = Leer.linea();
				
				gestion.eliminarPelicula(id_eliminar);
				break;
			case 8:
				System.out.println("Saliendo...");
			}
		} while (op != 8);
	}
	
	private static void mostrarMenu() {
		System.out.println("1. Buscar datos de una pelicula");
		System.out.println("2. Listar pelicula por genero");
		System.out.println("3. Contador de tiempo en total");
		System.out.println("4. Listar pendientes");
		System.out.println("5. Marcar una pelicula como vista");
		System.out.println("6. Añadir una pelicula indicando el nombre de un genero");
		System.out.println("7. Borrar una pelicula");
		System.out.println("8. SALIR");
	}
	
	

}
