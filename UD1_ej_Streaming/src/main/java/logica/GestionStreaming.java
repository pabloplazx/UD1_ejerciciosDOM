package logica;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GestionStreaming {
	
	private Document doc;
	private File fichero;
	
	private static final Logger log = LogManager.getLogger(GestionStreaming.class);
	
	public GestionStreaming(String fichero) {
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
	
	public void buscarPelicula(String id) {
		NodeList generos = doc.getElementsByTagName("genero");
		
		for (int i=0; i<generos.getLength(); i++) {
			Element genero = (Element) generos.item(i);
			
			NodeList peliculas = genero.getElementsByTagName("pelicula");
			
			for (int j=0; j<peliculas.getLength(); j++) {
				Element pelicula = (Element) peliculas.item(j);
				
				if (pelicula.getAttribute("id").equalsIgnoreCase(id)) {
					String id_pelicula = id;
					String titulo = pelicula.getElementsByTagName("titulo").item(0).getTextContent();
					String director = pelicula.getElementsByTagName("director").item(0).getTextContent();
					int duracion = Integer.parseInt(pelicula.getElementsByTagName("duracion_minutos").item(0).getTextContent());
					String texto_Visto = pelicula.getElementsByTagName("vista").item(0).getTextContent();
					
					boolean visto = false;
					
					if (texto_Visto.equals("true")) {
						visto = true;
					}
					
					Pelicula peli = new Pelicula(id_pelicula, titulo, director, duracion, visto);
					System.out.println("Los datos de la pelicula con el id: "+id+" son los siguientes");
					System.out.println(peli);
				}
			}
		}
	}
	
	public void listarPorGenero(String generoBuscar) {
		
		Set<Pelicula> pelis = new HashSet<>();
		
		NodeList generos = doc.getElementsByTagName("genero");
		
		for (int i=0; i<generos.getLength(); i++) {
			Element genero = (Element) generos.item(i);
			
			if (genero.getAttribute("nombre").equals(generoBuscar)) {
				
				NodeList peliculas = genero.getElementsByTagName("pelicula");
				
				for (int j=0; j<peliculas.getLength(); j++) {
					Element pelicula = (Element) peliculas.item(j);
					
					String id = pelicula.getAttribute("id");
					String titulo = pelicula.getElementsByTagName("titulo").item(0).getTextContent();
					String director = pelicula.getElementsByTagName("director").item(0).getTextContent();
					int duracion = Integer.parseInt(pelicula.getElementsByTagName("duracion_minutos").item(0).getTextContent());
					
					String texto_vista = pelicula.getElementsByTagName("vista").item(0).getTextContent();
					boolean vista = false;
					
					if (texto_vista.equals("true")) {
						vista = true;
					}
					
					Pelicula p = new Pelicula(id, titulo, director, duracion, vista);
					pelis.add(p);
					
					
					
					
				}
			}
			
		}
		
		for (Pelicula pe : pelis) {
			System.out.println(pe);
		}
	}
	
	public int contarTiempo() {
		int tiempoCompleto = 0;
		
		NodeList generos = doc.getElementsByTagName("genero");
		
		for (int i=0; i<generos.getLength(); i++) {
			Element genero = (Element) generos.item(i);
			
			NodeList peliculas = genero.getElementsByTagName("pelicula");
			
			for (int j=0; j<peliculas.getLength(); j++ ) {
				Element pelicula = (Element) peliculas.item(j);
				
				int duracion = Integer.parseInt(pelicula.getElementsByTagName("duracion_minutos").item(0).getTextContent());
				tiempoCompleto += duracion;
			}
		}
		
		return tiempoCompleto;
	}
	
	public ArrayList<Pelicula> peliculasPendientes() {
		
		ArrayList<Pelicula> pelisPendientes = new ArrayList<>();
		
		NodeList generos = doc.getElementsByTagName("genero");
		
		for (int i=0; i<generos.getLength(); i++) {
			Element genero = (Element) generos.item(i);
			
			NodeList peliculas = genero.getElementsByTagName("pelicula");
			
			for (int j=0; j<peliculas.getLength(); j++) {
				Element pelicula = (Element) peliculas.item(j);
				
				String texto_vista = pelicula.getElementsByTagName("vista").item(0).getTextContent();
				
				if (texto_vista.equals("false")) {
					String id = pelicula.getAttribute("id");
					String titulo = pelicula.getElementsByTagName("titulo").item(0).getTextContent();
					String director = pelicula.getElementsByTagName("director").item(0).getTextContent();
					int duracion = Integer.parseInt(pelicula.getElementsByTagName("duracion_minutos").item(0).getTextContent());
					boolean vista = false;
					
					Pelicula peli = new Pelicula(id, titulo, director, duracion, vista);
					pelisPendientes.add(peli);
				}
			}
		}
		
		
		return pelisPendientes; 
	}

	public boolean marcarComoVista(String id) {
	    boolean encontrada = false;
	    NodeList generos = doc.getElementsByTagName("genero");

	    // Recorremos los géneros
	    for (int i = 0; i < generos.getLength(); i++) {
	        Element genero = (Element) generos.item(i);
	        NodeList peliculas = genero.getElementsByTagName("pelicula");

	        // Recorremos las películas dentro del género
	        for (int j = 0; j < peliculas.getLength(); j++) {
	            Element pelicula = (Element) peliculas.item(j);

	            // Comparamos el ID
	            if (pelicula.getAttribute("id").equalsIgnoreCase(id)) {
	                
	                // 1. Obtenemos el elemento <vista>
	                Element elementoVista = (Element) pelicula.getElementsByTagName("vista").item(0);
	                
	                // 2. Comprobamos si ya estaba vista para avisar al usuario (opcional)
	                if ("true".equals(elementoVista.getTextContent())) {
	                    System.out.println("La película ya estaba marcada como vista.");
	                    return true; 
	                }

	                // 3. MODIFICAMOS EL VALOR EN MEMORIA
	                elementoVista.setTextContent("true");
	                
	                // 4. GUARDAMOS LOS CAMBIOS EN EL FICHERO
	                actualizarFichero();
	                
	                System.out.println("Película marcada como vista y cambios guardados.");
	                encontrada = true;
	                
	                // Como ya la encontramos y modificamos, podemos salir del bucle
	                return true; 
	            }
	        }
	    }
	    
	    if (!encontrada) {
	        System.out.println("No se encontró ninguna película con el ID: " + id);
	    }
	    
	    return encontrada;
	}
	
	public void añadirPelicula(String generoPelicula, Pelicula nuevaPeli) {
	    NodeList generos = doc.getElementsByTagName("genero");
	    boolean generoEncontrado = false;
	    
	    for (int i=0; i<generos.getLength(); i++) {
	        Element genero = (Element) generos.item(i);
	        
	        if (genero.getAttribute("nombre").equalsIgnoreCase(generoPelicula)) {
	            generoEncontrado = true;
	            
	            // CORRECCIÓN 1: No hagas cast de (Document) genero. 
	            // Usa la variable global 'doc' para fabricar elementos.
	            Element nuevoNodo = doc.createElement("pelicula");
	            
	            nuevoNodo.setAttribute("id", nuevaPeli.getId());
	            
	            crearElemento(nuevoNodo, "titulo", nuevaPeli.getTitulo());
	            crearElemento(nuevoNodo, "director", nuevaPeli.getDirector());
	            
	            // CORRECCIÓN 2: Convertir el int a String aquí
	            String duracionTexto = String.valueOf(nuevaPeli.getDuracion());
	            crearElemento(nuevoNodo, "duracion_minutos", duracionTexto);
	            
	            // No olvides el campo 'vista'
	            String vistaTexto = String.valueOf(nuevaPeli.getVista());
	            crearElemento(nuevoNodo, "vista", vistaTexto);
	            
	            // IMPORTANTE: Pegar la película nueva dentro del género
	            genero.appendChild(nuevoNodo);
	            
	            // Guardar en el disco duro
	            actualizarFichero();
	            
	            System.out.println("Película añadida con éxito.");
	        }
	    }
	}
	
	public void eliminarPelicula(String id) {
	    boolean borrado = false;
	    NodeList generos = doc.getElementsByTagName("genero");

	    // Etiqueta para poder romper ambos bucles a la vez si la encontramos
	    buclePrincipal: 
	    for (int i = 0; i < generos.getLength(); i++) {
	        Element genero = (Element) generos.item(i);
	        NodeList peliculas = genero.getElementsByTagName("pelicula");

	        for (int j = 0; j < peliculas.getLength(); j++) {
	            Element pelicula = (Element) peliculas.item(j);

	            // Uso equalsIgnoreCase para que no importen mayúsculas/minúsculas
	            if (pelicula.getAttribute("id").equalsIgnoreCase(id)) {
	                
	                // 1. Borramos el nodo
	                pelicula.getParentNode().removeChild(pelicula);
	                System.out.println("Película con id: " + id + " eliminada correctamente.");
	                
	                borrado = true;
	                
	                // 2. IMPORTANTE: Rompemos el bucle porque ya terminamos
	                // Al borrar un nodo, la lista 'peliculas' cambia de tamaño en tiempo real
	                break buclePrincipal; 
	            }
	        }
	    }

	    if (borrado) {
	        // 3. Guardamos los cambios (asegúrate que el nombre coincide con tu método privado)
	    	actualizarFichero(); 
	    } else {
	        System.out.println("No se pudo encontrar ninguna película con ese id.");
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
	

}
