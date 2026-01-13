package logica;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

public class GestionPartida {

	private File fichero;
	private Document doc;
	
	private static final Logger log = LogManager.getLogger(GestionPartida.class); 
	
	public GestionPartida(String fichero) {
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
	
	public ArrayList<Heroe> listarEquipoVivo() {
		
		ArrayList<Heroe> equipoVivo = new ArrayList<>();
		
		boolean encontrado = false;
		
		NodeList gremios = doc.getElementsByTagName("gremio");
		
		for (int i=0; i<gremios.getLength(); i++) {
			Element gremio = (Element) gremios.item(i);
			
			NodeList heroes = gremio.getElementsByTagName("heroe");
			
			for (int j=0; j<heroes.getLength(); j++) {
				Element heroe = (Element) heroes.item(j);
				
				String textoVivo = heroe.getElementsByTagName("esta_vivo").item(0).getTextContent();
				
				if (textoVivo.equalsIgnoreCase("true")) {
					
					encontrado = true; 
					
					String id = heroe.getAttribute("id");
					String clase = heroe.getAttribute("clase");
					String nombre = heroe.getElementsByTagName("nombre").item(0).getTextContent();
					int nivel = Integer.parseInt(heroe.getElementsByTagName("nivel").item(0).getTextContent());
					int vidaActual = Integer.parseInt(heroe.getElementsByTagName("vida_actual").item(0).getTextContent());
					int vidaMaxima = Integer.parseInt(heroe.getElementsByTagName("vida_maxima").item(0).getTextContent());
					int ataque = Integer.parseInt(heroe.getElementsByTagName("ataque").item(0).getTextContent());
					Boolean esta_vivo = true;
					
					Heroe he = new Heroe(id, clase, nombre, nivel, vidaActual, vidaMaxima, ataque, esta_vivo);
					equipoVivo.add(he);
					
					break;	
					
				}
			}
			
		}
		
		if (!encontrado) {
			System.out.println("No se encontro ningun heroe del equipo vivo");
		}
		
		return equipoVivo;

	}
	
	public Heroe heroeMasFuerte() {
		Heroe heroeTop = null;
		int ataqueMaximo = Integer.MIN_VALUE;
		
		//No es necesario que busquemos en todo los gremios debido a que solo nos importa buscar los heroes
		
		NodeList todosLosHeroes = doc.getElementsByTagName("heroe");
		
		for (int i=0; i<todosLosHeroes.getLength(); i++) {
			Element heroe = (Element) todosLosHeroes.item(i);
			
			int ataqueActual = Integer.parseInt(heroe.getElementsByTagName("ataque").item(0).getTextContent());
			
			if (ataqueActual > ataqueMaximo) {
				
				//1. ACTUALIZAMOS EL RECORD	
				ataqueMaximo = ataqueActual;
				
				//2. RECOGEMOS YA LOS DATOS	
				String id = heroe.getAttribute("id");
	            String clase = heroe.getAttribute("clase");
	            String nombre = heroe.getElementsByTagName("nombre").item(0).getTextContent();
	            int nivel = Integer.parseInt(heroe.getElementsByTagName("nivel").item(0).getTextContent());
	            int vida_actual = Integer.parseInt(heroe.getElementsByTagName("vida_actual").item(0).getTextContent());
	            int vida_maxima = Integer.parseInt(heroe.getElementsByTagName("vida_maxima").item(0).getTextContent());
	            
	            //SE PUEDEN PARSEAR LOS BOOLEAN COMO LOS INTS	
	            boolean estaVivo = Boolean.parseBoolean(heroe.getElementsByTagName("esta_vivo").item(0).getTextContent());
	            
	            heroeTop = new Heroe(id, clase, nombre, nivel, vida_actual, vida_maxima, ataqueActual ,estaVivo);
	
			}
		}
		
		return heroeTop;
	}
	
	public void beberPorcion(String idHeroe, int cantidadCura) {
		
		boolean encontrado = false;
		
		NodeList heroes = doc.getElementsByTagName("heroe");
		
		for (int i=0; i<heroes.getLength(); i++	 ) {
			Element heroe = (Element) heroes.item(i);
			
			if (heroe.getAttribute("id").equals(idHeroe)) {
				
				//COMPROBAR SI ESTA MUERTO O NO
				String textoVivo = heroe.getElementsByTagName("esta_vivo").item(0).getTextContent();
				if (textoVivo.equals("false")) {
					System.out.println("No puedes curar a un muerto");
					return;
				}
				
				Element nodoVidaActual = (Element) heroe.getElementsByTagName("vida_actual");
				
				int vidaActual = Integer.parseInt(nodoVidaActual.getTextContent());
				int vidaMaxima = Integer.parseInt(heroe.getElementsByTagName("vida_maxima").item(0).getTextContent());
				
				if ((vidaActual + cantidadCura) > vidaMaxima) {
					vidaActual = vidaMaxima;
				} else {
					vidaActual += cantidadCura;
				}
				
				nodoVidaActual.setTextContent(String.valueOf(vidaActual));
				
				System.out.println("Héroe curado, vida actual: "+vidaActual);
				actualizarFichero();
				
				return;
				
			}
			
			
		}
		System.out.println("No se encontro ningun heroe con ese id");
	}
	
	public void recibeGolpe(String idHero, int dañoRecibido) {
		boolean encontrado = false;
		
		NodeList heroes = doc.getElementsByTagName("heroe");
		
		for (int i=0; i<heroes.getLength(); i++) {
			Element heroe = (Element) heroes.item(i);
			
			if (heroe.getAttribute("id").equals(idHero)) {
				encontrado = true;
				
				String textoVivo = heroe.getElementsByTagName("esta_vivo").item(0).getTextContent();
				
				if (textoVivo.equals("false")) {
					System.out.println("El heroe ya esta muerto no puede recibir daño");
					return;
				}
				
				Element nodoVida = (Element) heroe.getElementsByTagName("vida_actual");
				int vidaActual = Integer.parseInt(nodoVida.getTextContent());
				
				if ((vidaActual - dañoRecibido) < 0) {
					vidaActual = 0;
					
					heroe.getElementsByTagName("esta_vivo").item(0).setTextContent("false");
					System.out.println("El heroe ha recibido un daño fatal y ha fallecido");
				} else {
					vidaActual -= dañoRecibido;
					System.out.println("El heroe ha recibido daño. Vida restante: "+vidaActual);
					
				}
				
				nodoVida.setTextContent(String.valueOf(vidaActual));
				actualizarFichero();
			}
			
		}
		
		if (!encontrado) {
			System.out.println("No se ha podido encontrar ningun heroe con ese id");
		}
	}
	
	public void venderObjeto(String idObjeto) {
		Element inventarioGlobal = doc.getElementById("inventario_global");
		
		NodeList objetos = inventarioGlobal.getElementsByTagName("objeto");
		
		for (int i=0; i<objetos.getLength(); i++) {
			Element objeto = (Element) objetos.item(i);
			
			if (objeto.getAttribute("id").equals(idObjeto)) {
				objeto.getParentNode().removeChild(objeto);
				
				System.out.println("Objeto eliminado");
				actualizarFichero();
			}
		}
	}
	
	public void crearMercenario(String gremioIntroducir, Heroe heroeAñadir) {
		NodeList gremios = doc.getElementsByTagName("gremio");
		
		boolean gremioEncontrado = false;
		
		for (int i=0; i<gremios.getLength(); i++) {
			Element gremio = (Element) gremios.item(i);
			
			
			if (gremio.getAttribute("nombre").equals(gremioIntroducir)) {
				gremioEncontrado = true;
				
				Element nuevoNodo = doc.createElement("heroe");
				
				nuevoNodo.setAttribute("id", heroeAñadir.getId());
				nuevoNodo.setAttribute("clase", heroeAñadir.getClase());
				
				crearElemento(nuevoNodo, "nombre", heroeAñadir.getNombre());
				crearElemento(nuevoNodo, "nivel", String.valueOf(heroeAñadir.getNivel()));
				crearElemento(nuevoNodo, "vida_actual", String.valueOf(heroeAñadir.getVidaActual()));
				crearElemento(nuevoNodo, "vida_maxima", String.valueOf(heroeAñadir.getVidaMaxima()));
				crearElemento(nuevoNodo, "ataque", String.valueOf(heroeAñadir.getAtaque()));
				crearElemento(nuevoNodo, "esta_vivo", String.valueOf(heroeAñadir.getEstaVivo()));
				
				gremio.appendChild(nuevoNodo);
				actualizarFichero();
				System.out.println("Heroe añadido correctamente");
				return;
			}
		}
		
		if (!gremioEncontrado) {
			System.out.println("No existe ningun gremio llamado así");
		}
	}
	
	public void subirNivelMundo() {
		//1. OPTIMIZACION: En lugar de entrar en Gremio -> Heroe, pedimos directamente todos los nodos heroes.
		NodeList todosLosHeroes = doc.getElementsByTagName("heroe");
		
		//Variable para sabir si hemos tocado algo
		boolean huboCambios = false;
		
		//2. Recorremos la lista única de heroes
		for (int i=0; i<todosLosHeroes.getLength(); i++) {
			Element heroe = (Element) todosLosHeroes.item(i);
			
			//3. Obtenemos el estado de vida
			//Importante hacer esto antes de hacer cammbios
			//Si esta muerto pasamos al siguiente
			String textoVivo = heroe.getElementsByTagName("esto_vivo").item(0).getTextContent();
			
			if (textoVivo.equals("false")) {
				continue;
			}
			
			// --- A PARTIR DE AQUÍ SOLO ENTRAN LOS VIVOS ---
	        
	        // 4. Referencias a los nodos que vamos a modificar
	        // (Guardamos los nodos en variables para no buscarlos dos veces)
	        Element nodoNivel = (Element) heroe.getElementsByTagName("nivel").item(0);
	        Element nodoVidaMax = (Element) heroe.getElementsByTagName("vida_maxima").item(0);
	        Element nodoVidaAct = (Element) heroe.getElementsByTagName("vida_actual").item(0);
	        
	        // 5. Lógica Matemática: Leemos, Convertimos, Operamos
	        
	        // A) SUBIR NIVEL (+1)
	        int nivelAntiguo = Integer.parseInt(nodoNivel.getTextContent());
	        int nivelNuevo = nivelAntiguo + 1;
	        
	        // B) SUBIR VIDA MÁXIMA (+20)
	        int vidaMaxAntigua = Integer.parseInt(nodoVidaMax.getTextContent());
	        int vidaMaxNueva = vidaMaxAntigua + 20;
	        
	        // C) RESTAURAR VIDA ACTUAL (Curación completa)
	        // La nueva vida actual es igual a la nueva vida máxima.
	        int vidaActNueva = vidaMaxNueva; 
	        
	        // 6. Escritura: Guardamos los nuevos valores en los nodos
	        // Usamos String.valueOf() para convertir los int a texto
	        nodoNivel.setTextContent(String.valueOf(nivelNuevo));
	        nodoVidaMax.setTextContent(String.valueOf(vidaMaxNueva));
	        nodoVidaAct.setTextContent(String.valueOf(vidaActNueva));
	        
	        // Marcamos que hemos hecho cambios
	        huboCambios = true;
	        
	        System.out.println("El héroe " + heroe.getElementsByTagName("nombre").item(0).getTextContent() 
	                           + " ha subido al nivel " + nivelNuevo);
	    }
	    
	    // 7. PERSISTENCIA FINAL
	    // IMPORTANTE: Guardamos el fichero UNA SOLA VEZ al final del todo.
	    // No lo metas dentro del bucle, o escribirás en disco 50 veces (muy lento).
	    if (huboCambios) {
	        actualizarFichero();
	        System.out.println("--- Evento Global Finalizado: Mundo Actualizado ---");
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
