package logica;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
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
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class GestionBiblioteca {

	private File fichero;
	private Document doc;
	private static Logger log;
	
	public GestionBiblioteca(String fichero) {
		
		this.fichero = new File(fichero);
		cargarDoc();
	}
	
	//CARGAMOS EL ÁRBOL DOM DESDE EL FICHERO .XML
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
	
	public void listarLibros() {
		
		NodeList libros = 
				doc.getElementsByTagName("libro");
		
		for(int i=0;i<libros.getLength();i++) {
			Element libro = (Element) libros.item(i);
			Element titulo 
				= (Element) libro.getElementsByTagName("titulo")
				.item(0);
			
			System.out.println("Título: "+titulo.getTextContent());
			Element autor 
				= (Element) libro.getElementsByTagName("autor").item(0);
			System.out.println("Autor: "+autor.getTextContent());
			
		}
	}

	public List<String> buscarTitulosPorAutor(String autorBuscado) {
		
		List<String> titulos = new ArrayList<>();
		
		NodeList libros = 
				doc.getElementsByTagName("libro");
		
		for(int i=0;i<libros.getLength();i++) {
			Element libro = (Element) libros.item(i);
			Element autor 
			= (Element) libro.getElementsByTagName("autor").item(0);
			if (autor.getTextContent().equals(autorBuscado)) {
				Element titulo 
				= (Element) libro.getElementsByTagName("titulo")
				.item(0);
				titulos.add(titulo.getTextContent());
				
			}
				
			
		}
		
		return titulos;
		
	}
	
	public List<String> librosPrestadosA(String nombreLector) {
		
		List<String> result = new LinkedList<>();
		
		
		NodeList lectores = doc.getElementsByTagName("lector");
		for(int i=0;i<lectores.getLength();i++) {
			Element lector = (Element) lectores.item(i);
			if (lector.getTextContent().equals(nombreLector)){
				Element libro = 
						(Element) lector.getParentNode().getParentNode().getParentNode();
				result.add(libro.getElementsByTagName("titulo")
						.item(0).getTextContent());
			}
		}
		//VAMOS A QUITAR LOS TÍTULOS QUE EMPIECEN POR CIFRA
		
		result = result.stream()
				.filter(x -> x.charAt(0)<48 || x.charAt(0)>57).toList();
		return result;
	}
	
	public boolean eliminarLibroPorId(String id) {
		
		NodeList libros = doc.getElementsByTagName("libro");
		for(int i=0;i<libros.getLength();i++) {
			Element libro = (Element) libros.item(i);
			if(libro.getAttribute("id").equals(id)) {
				libro.getParentNode().removeChild(libro);
				actualizarFichero();
				return true;
			}
		}
		return false;
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

	public boolean setDisponibilidad(String id, boolean disponible) {
        
        return false;
    }
	
	public void agregarLibro(String id, boolean disponible, String titulo, String autor, String genero, int anio) {
		
		Element lib = doc.createElement("libro");
		Element tit = doc.createElement("titulo");
		Text textTit = doc.createTextNode(titulo);
		tit.appendChild(textTit);
		Element aut = (Element) doc.createElement("autor");
		aut.appendChild(doc.createTextNode(autor));
		Element gen = (Element) doc.createElement("genero");
		gen.appendChild(doc.createTextNode(genero));
		Element an = (Element) doc.createElement("anio");
		an.appendChild(doc.createTextNode(anio+""));
		lib.appendChild(tit);
		lib.appendChild(aut);
		lib.appendChild(gen);
		lib.appendChild(an);
		//NODO RAÍZ
		doc.getDocumentElement().appendChild(lib);
		lib.setAttribute("id", id);
		lib.setAttribute("disponible", String.valueOf(disponible));
		actualizarFichero();
		
		
	}
	
	public boolean agregarPrestamo(String idLibro, String lector, String fecha) {
		
		return false;
	}
	
	public boolean eliminarPrestamosDeLector(String nombreLector) {
		return false;
	}
	
	
	public static void main(String[] args) {
		
		log = LogManager.getLogger(GestionBiblioteca.class);
		
		log.info("Arrancando aplicacion");
		String nomFichero = "";
		
		Properties prop = new Properties();
		
		try {
			
			
			//ASÍ NO ENCUENTRA EL FICHERO
			//prop.load(new FileInputStream("config.properties"));
			InputStream is 
				= GestionBiblioteca.class
				.getClassLoader()
				.getResourceAsStream("config.properties");
					
			prop.load(is);
			nomFichero = prop.getProperty("datos");
		} catch (FileNotFoundException e) {
			log.error("No se puedo encontrar properties, usamos un fichero estándar");
			nomFichero = "biblio.xml";
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		GestionBiblioteca gestion = 
				new GestionBiblioteca(nomFichero);
		
		gestion.listarLibros();
		System.out.println("--------------------");
		
		gestion.buscarTitulosPorAutor("J. R. R. Tolkien")
			.stream().forEach(System.out::println);
		
		System.out.println("Libros prestados a Juan (exceptuando"
				+ " los que empiecen por cifra:");
		gestion.librosPrestadosA("Juan")
			.stream().forEach(System.out::println);
		
		System.out.println("Eliminamos id = 2:");
		System.out.println(gestion.eliminarLibroPorId("2"));
		
		System.out.println("Agregamos libro:");
		gestion.agregarLibro("5", false, "Momo", "Ende", "fantasía", 1990);
		
		

	}

}
