package logica;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GestionManifiesto {

	
	private File fichero;
	private Document doc;
	
	public GestionManifiesto(String fichero) {
		this.fichero = new File(fichero);
		cargarDoc();
	}
	
	private void cargarDoc() {
		
		DocumentBuilderFactory dbf 
			= DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db 
					= dbf.newDocumentBuilder();
			doc = db.parse(fichero);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	private List<String> obtenerNavesEnReparacion() {
		List<String> navesReparacion = new ArrayList<>();
		
		
		NodeList estaciones = doc.getElementsByTagName("estacion");
		
		for (int i=0; i<estaciones.getLength(); i++) {
			Element estacion = (Element) estaciones.item(i);
			
			NodeList naves = estacion.getElementsByTagName("nave");
			
			for (int j=0; j<naves.getLength(); j++) {
				Element nave = (Element) naves.item(j);
				
				String estadoMotor = nave.getElementsByTagName("estado_motor").item(0).getTextContent();
				
				if (estadoMotor.equalsIgnoreCase("operativo")) {
					navesReparacion.add(nave.getAttribute("matricula"));
				}

			}
		}
		
		if (navesReparacion.isEmpty()) {
			System.out.println("Ninguna nave necesita reparacion");
		}
		return navesReparacion;
	}
	
	public String infoCapitan(String matricula) {	
		
		String nombreCapitan =  "";
		
		boolean encontrado = false;
		
		NodeList naves = doc.getElementsByTagName("nave");
		
		for (int i=0; i<naves.getLength(); i++) {
			Element nave = (Element) naves.item(i);
			
			if (nave.getAttribute("matricula").equalsIgnoreCase(matricula)) {
				encontrado = true;
				Element capitan = (Element) nave.getElementsByTagName("capitan").item(0);
				
				nombreCapitan = capitan.getTextContent();
			}
		}
		
		if (!encontrado) {
			nombreCapitan = "No se ha encontrado esta matricula";
		}
		return nombreCapitan;
	}
	
	public double calcularCargaPeligrosa(String idEstacion) {
		double valorCarga = 0;
		
		boolean estacionEncontrada = false;
		
		NodeList estaciones = doc.getElementsByTagName("estacion");
		
		for (int i=0; i<estaciones.getLength(); i++) {
			Element estacion = (Element) estaciones.item(i);
			
			if (estacion.getAttribute("id").equalsIgnoreCase(idEstacion)) {
				
				estacionEncontrada = true;
				
				NodeList naves = estacion.getElementsByTagName("nave");
				
				for (int j=0; j<naves.getLength(); j++) {
					Element nave = (Element) naves.item(j);
					
					NodeList items = nave.getElementsByTagName("item");
					
					for (int k=0; k<items.getLength(); k++) {
						Element item = (Element) items.item(k);
						
						if (item.getAttribute("id").equalsIgnoreCase("true")) {
							valorCarga += Double.parseDouble(item.getElementsByTagName("valor_mercado").item(0).getTextContent());
						} 
					}
				}
			}
		}
		
		if (!estacionEncontrada) {
			System.out.println("No se pudo encontrar ninguna estacion con ese id");
		}
		
		return valorCarga;
	}
	
	public double calcularPeso() {
		return null;
	}
	
}
