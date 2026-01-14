package logica;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GestionUniversidad {

	private File fichero;
	private Document doc;
	
	private static final Logger log = LogManager.getLogger(GestionUniversidad.class); 
	
	public GestionUniversidad(String fichero) {
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
	
	public void generarBoletinNotas(String idAlumno) {
	    // Variables para guardar lo que encontremos
	    Alumno alumnoBuscado = null; 
	    String nombreGrado = "";
	    boolean encontrado = false;

	    // 1. BÚSQUEDA DEL ALUMNO (Jerarquía: Facultad -> Grado -> Alumno)
	    NodeList facultades = doc.getElementsByTagName("facultad");
	    
	    // Usamos una etiqueta 'buclePrincipal' para poder romper todos los for si lo encontramos
	    buclePrincipal:
	    for (int i = 0; i < facultades.getLength(); i++) {
	        Element facultad = (Element) facultades.item(i);
	        
	        NodeList grados = facultad.getElementsByTagName("grado");
	        for (int j = 0; j < grados.getLength(); j++) {
	            Element grado = (Element) grados.item(j);
	            
	            NodeList alumnos = grado.getElementsByTagName("alumno");
	            for (int y = 0; y < alumnos.getLength(); y++) {
	                Element alumno = (Element) alumnos.item(y);
	                    
	                // Comprobamos si es el alumno que buscamos
	                if (alumno.getAttribute("id").equals(idAlumno)) {
	                    encontrado = true;
	                    
	                    // --- RECOPILACIÓN DE DATOS DEL ALUMNO ---
	                    nombreGrado = grado.getAttribute("nombre");
	                    String idAlum = idAlumno;
	                    // Corrección: Parseamos el booleano
	                    boolean tieneBeca = Boolean.parseBoolean(alumno.getAttribute("beca"));
	                    
	                    // OJO: Añadimos .item(0) porque getElementsByTagName devuelve siempre una lista
	                    Element datos = (Element) alumno.getElementsByTagName("datos").item(0);
	                    
	                    String nombre = datos.getElementsByTagName("nombre").item(0).getTextContent();
	                    String ciudad = datos.getElementsByTagName("ciudad").item(0).getTextContent();
	                    
	                    // Procesamos el expediente (Notas y Referencias)
	                    Element expediente = (Element) alumno.getElementsByTagName("expediente").item(0);
	                    NodeList asignaturasXML = expediente.getElementsByTagName("asignatura");
	                    
	                    ArrayList<Asignatura> listaAsignaturas = new ArrayList<>();
	                    
	                    for (int k = 0; k < asignaturasXML.getLength(); k++) {
	                        Element asignatura = (Element) asignaturasXML.item(k);
	                        
	                        String referencia = asignatura.getAttribute("ref");
	                        // OJO: Convertimos el texto de la nota a double
	                        double nota = Double.parseDouble(asignatura.getElementsByTagName("nota").item(0).getTextContent());
	                        
	                        // Guardamos la referencia (ej: "AS01") y la nota (ej: 8.5)
	                        Asignatura asig = new Asignatura(referencia, nota);
	                        listaAsignaturas.add(asig);
	                    }
	                    
	                    // Creamos el objeto alumno con toda la info
	                    alumnoBuscado = new Alumno(idAlum, tieneBeca, nombre, ciudad, listaAsignaturas);
	                    
	                    // Rompemos el bucle principal porque ya lo tenemos
	                    break buclePrincipal;
	                }
	            }
	        }
	    }
	    
	    // Si terminamos los bucles y no está el alumno, avisamos y salimos
	    if (!encontrado || alumnoBuscado == null) {
	        System.out.println("No se ha encontrado al alumno con ID: " + idAlumno);
	        return;
	    }

	    // --- PARTE 2: CRUZAR DATOS (Lo difícil) ---
	    // Ahora vamos a imprimir el boletín buscando los nombres reales de las asignaturas
	    
	    System.out.println("Alumno: " + alumnoBuscado.getNombre() + " (" + nombreGrado + ")");
	    System.out.println("-------------------------------------------");
	    
	    // 1. Obtenemos el plan de estudios general
	    // CORRECCIÓN IMPORTANTE: Añadimos .item(0) porque solo hay un <plan_estudios> pero java devuelve lista
	    Element planEstudios = (Element) doc.getElementsByTagName("plan_estudios").item(0);
	    NodeList materiasGlobales = planEstudios.getElementsByTagName("materia");
	    
	    // 2. Recorremos las asignaturas que tiene EL ALUMNO
	    for (Asignatura asigAlumno : alumnoBuscado.getAsignaturas()) {
	        
	        String nombreRealAsignatura = "Desconocida"; // Valor por defecto
	        
	        // 3. Para cada asignatura del alumno, buscamos su nombre en la lista global de materias
	        for (int h = 0; h < materiasGlobales.getLength(); h++) {
	            Element materia = (Element) materiasGlobales.item(h);
	            
	            // Si el ID de la materia global coincide con la referencia del alumno...
	            if (materia.getAttribute("id").equals(asigAlumno.getIdRef())) {
	                
	                // CORRECCIÓN: El texto está directo en <materia>, no hay otra etiqueta hija
	                nombreRealAsignatura = materia.getTextContent();
	                break; // Ya encontramos el nombre, dejamos de buscar en materias
	            }
	        }
	        
	        // 4. Imprimimos el resultado cruzado
	        System.out.println("- " + nombreRealAsignatura + ": " + asigAlumno.getNota());
	    }
	}

}
