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
				= GestionUniversidad.class
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
		
		GestionUniversidad gestion = new GestionUniversidad(nombreFichero);
		
		int op = 0;
		do {
			mostrarMenu();
			System.out.print("Introduzca opcion: ");
			op = Leer.entero();
			
			switch (op) {
			case 1:
				System.out.println("Introduzca id del alumno que quiere emitir el boletin de notas: ");
				String idBoletin = Leer.linea();
				
				gestion.generarBoletinNotas(idBoletin);
				break;
			case 8:
				System.out.println("Saliendo..");
				break;
			default: 
				System.out.println("Opcion no valida, introduzca otra opcion");
				break;
			}
		} while (op != 8);
		
	}
	
	private static void mostrarMenu() {
		System.out.println("1. Emitir boletin de notas");
		System.out.println("2. Listar alumnos por asignatura");
		System.out.println("3. Calcular Precio Matricula");
		System.out.println("4. Nota media de la facultad");
		System.out.println("5. Modificar nota");
		System.out.println("6. Traslado de expediente");
		System.out.println("7. Limpieza de suspensos");
		System.out.println("8. SALIR");
	}
}
