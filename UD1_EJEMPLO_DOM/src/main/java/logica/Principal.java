package logica;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import consola.Leer;

public class Principal {
	
	private static Logger log; 

	public static void main(String[] args) {

		String nombreFichero = "";
		
		Properties prop = new Properties();
		
		try {
			
			
			//ASÍ NO ENCUENTRA EL FICHERO
			//prop.load(new FileInputStream("config.properties"));
			InputStream is 
				= GestionEmpleados.class
				.getClassLoader()
				.getResourceAsStream("config.properties");
					
			prop.load(is);
			nombreFichero = prop.getProperty("datos");
		} catch (FileNotFoundException e) {
			nombreFichero = "empleados.xml";
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		GestionEmpleados gestion = new GestionEmpleados(nombreFichero);
		
		System.out.println("--BIENVENIDO A NUESTRA APLICACIÓN DE GESTIÓN DE EMPLEADOS--");
		System.out.println("Opciones:");
		System.out.println("1.- Buscar por id del empleado");
		System.out.println("2.- Mostrar todos los empleados");
		System.out.println("3.- Añadir un nuevo empleado");
		System.out.println("4.- Borrar un nuevo empleado ");
		System.out.println("5.- SALIR");
		System.out.println("-------------");
		
		
		int op = 0;
		
		while (op != 5) {
			System.out.println("Introduzca opcion: ");
			op = Leer.entero();
			
			switch (op) {
			case 1:
				System.out.print("Introduzca id del empleado que quiera buscar: ");
				String id = Leer.linea();
				
				gestion.buscarId(id);
				break;
			case 2:
				gestion.listarEmpleados();
				break;
			case 3: 
				System.out.println("Proceda a introducir los datos del nuevo empleado: ");
				System.out.print("Id: ");
				String id_nuevo = Leer.linea();
				System.out.print("Nombre: ");
				String nombre = Leer.linea();
				System.out.print("Puesto: ");
				String puesto = Leer.linea();
				System.out.print("Salario: ");
				String salario = Leer.linea();
				System.out.print("Fecha contratación: ");
				String fecha_contratacion = Leer.linea();
				System.out.print("Email: ");
				String email = Leer.linea();
				
				Empleado empleado = new Empleado(id_nuevo, nombre, puesto, salario, fecha_contratacion, email);
				gestion.añadirEmpleado(empleado);
				
				break;
			case 4:	
				System.out.println("Introduzca el id del empleado que deseea eliminar: ");
				String id_eliminar = Leer.linea();
				
				gestion.eliminarEmpleado(id_eliminar);
				break;
			default:
				System.out.println("La opcion seleccionada no es valida :(");
				break;
			
		
			}
		}
	}
}
