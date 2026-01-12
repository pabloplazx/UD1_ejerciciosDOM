package logica;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GestionEmpleados {
	
	private File fichero;
	private Document doc;

	private static final Logger log = LogManager.getLogger(GestionEmpleados.class);
	
	public GestionEmpleados(String fichero) {
		this.fichero = new File(fichero);
		cargarDoc();
	}
	
	private void cargarDoc() {
		
		log.debug("Cargando documento");
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
	
	public void buscarId(String id) {
		
		NodeList departamentos = doc.getElementsByTagName("departamento");
		
		for (int i=0; i<departamentos.getLength(); i++) {
			Element departamento = (Element) departamentos.item(i);
			
			NodeList empleados = departamento.getElementsByTagName("empleado");
			
			for (int j=0; j<empleados.getLength(); j++ ) {
				Element empleado = (Element) empleados.item(j);
				
				if (empleado.getAttribute("id").equals(id)) {
					String idEmpleado = id;
					String nombre = empleado.getElementsByTagName("nombre").item(0).getTextContent();
					String puesto = empleado.getElementsByTagName("puesto").item(0).getTextContent();
					String salario = empleado.getElementsByTagName("salario").item(0).getTextContent();
					String fecha_contratacion = empleado.getElementsByTagName("fecha_contratacion").item(0).getTextContent();
					String email = empleado.getElementsByTagName("email").item(0).getTextContent();
					
					Empleado emp = new Empleado(id, nombre, puesto, salario, fecha_contratacion, email );
					
					System.out.println("Los datos del empleado son los siguientes: ");
					System.out.println(emp);

					return;
				}
			}
			
		}
	}
	
	public void listarEmpleados() {
		
		ArrayList<Empleado> listaEmpleados = new ArrayList<Empleado>();
		
		NodeList departamentos = doc.getElementsByTagName("departamento");
		
		for (int i=0; i<departamentos.getLength(); i++ ) {
			Element departamento = (Element) departamentos.item(i);
			
			NodeList empleados = departamento.getElementsByTagName("empleado");
			
			
			for (int j=0; j<empleados.getLength(); j++) {
				Element empleado = (Element) empleados.item(j);
				
				String id = empleado.getAttribute("id");
				String nombre = empleado.getElementsByTagName("nombre").item(0).getTextContent();
				String puesto = empleado.getElementsByTagName("puesto").item(0).getTextContent();
				String salario = empleado.getElementsByTagName("salario").item(0).getTextContent();
				String fecha_contratacion = empleado.getElementsByTagName("fecha_contratacion").item(0).getTextContent();
				String email = empleado.getElementsByTagName("email").item(0).getTextContent();
				
				Empleado emple = new Empleado(id, nombre, puesto, salario, fecha_contratacion, email );
				listaEmpleados.add(emple);
			}
		}
		
		Collections.sort(listaEmpleados);
		
		System.out.println("La lista de empleados de la empresa es la siguiente: ");
		for (Empleado e : listaEmpleados) {
			System.out.println(e);
		}
		
	}
	
	public void añadirEmpleado(Empleado emp ) {
		//1. Comprobamos que el documento existe 
		if (doc == null) return;
		
		//2. Crear la etiquetea padre <empleado> y ponerle el atributo ID
		Element nuevoNodo = doc.createElement("empleado");
		nuevoNodo.setAttribute("id", emp.getId());
		
		//3. Creamos las etiquetas hijas (nombre, puesto, etc.) 
		//Usamos un mñetodo auxiliar para no repetir tanto código
		crearElemento(nuevoNodo, "nombre", emp.getNombre());
		crearElemento(nuevoNodo, "puesto", emp.getPuesto());
		crearElemento(nuevoNodo, "salario", emp.getSalario());
		crearElemento(nuevoNodo, "fecha_contratacion", emp.getFechaContratacion());
		crearElemento(nuevoNodo, "email", emp.getEmail());
		
		//4. Donde lo guardamos?
		//Como mi XML tiene departamentos, vamos a coger el PRIMER departamento y lo añadimos allí
		NodeList departamentos = doc.getElementsByTagName("departamento");
		if (departamentos.getLength() > 0) {
			Element primerDepartamento = (Element) departamentos.item(0);
			primerDepartamento.appendChild(nuevoNodo);
			
			System.out.println("Empelado añadido a la memoria");
			
			//5. IMPORTANTE: Guardar los cambios en el fichero físico 
			actualizarFichero();
		} else {
			System.out.println("No existen departamentos en los que añadir el empleado ");
		}
	}
	
	private void crearElemento(Element padre, String etiqueta, String texto) {
		//PASO 1: Fabricar una caja vacia 
		Element elem = doc.createElement(etiqueta);
		
		//PASO 2: Meter el contenido dentro de la caja
		elem.setTextContent(texto);
		
		//PASO 3: Pegar la caja pequeña dentro de la caja grande
		padre.appendChild(elem);
		
	}
	
	private void actualizarFichero() {
		
		TransformerFactory tf = TransformerFactory.newInstance();
		try {
			Transformer tr = tf.newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			doc.getDocumentElement().normalize();
			DOMSource source = new DOMSource(doc);
		    StreamResult result = new StreamResult(fichero);
		    tr.transform(source, result);
		    
		} catch (TransformerException e) {
			
			e.printStackTrace();
		}
	}
		//Los pasos a seguir son los siguientes: 
	// 1. Buscamos la rma que queremos cortar mediante un atributo identificativo como puede ser el ID 
	// 2. Le pides a su padre que elimine a ese hijo
	// 3. Guardas los cambios en el archivo
	public void eliminarEmpleado(String id) {
		
		if (doc == null) { //Protección 
			return;
		}
		
		NodeList departamentos = doc.getElementsByTagName("departamento");
		boolean borrado = false;
		
		// 1. Recorremos el bucle buscando al empleado
		for (int i=0; i<departamentos.getLength(); i++) {
			Element departamento = (Element) departamentos.item(i);
			
			NodeList empleados = departamento.getElementsByTagName("empleado");
			
			for (int j=0; j<empleados.getLength(); j++) {
				Element empleado = (Element) empleados.item(j);
				
				// 2. ¿Este es el empleado que buscamos?
				if (empleado.getAttribute("id").equalsIgnoreCase(id)) {
					
					// ---ELIMINACIÓN---
					//Para borrar un nodo, hay que llamar al padre y decirle borra a este hijo
					//getParentNode() obtiene automáticamente el departamento al que pertenece
					empleado.getParentNode().removeChild(empleado);
					
					System.out.println("El empleado con id: "+id+" ha sido eliminado de la base de datos");
					borrado = true;
					
					// 3. Como hemos encontrado lo que buscabamos interrumpimos el bucle, no hace falta buscar mas
					break;
					
				}
			} 
			
			if (borrado) break;
		}
		
		if (borrado) {
			actualizarFichero();
		} else {
			System.out.println("No se encontro ningun empleado con ese ID");
		}
	}

}
