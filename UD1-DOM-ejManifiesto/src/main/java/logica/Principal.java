package logica;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
			nombreFichero = prop.getProperty("datos");
		} catch (FileNotFoundException e) {
			nombreFichero = "manifiesto_galactico.xml";
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
	}
}
