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
	
	public void generarBoletinNotasIA(String idAlumno) {
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
	
	public void generarBoletin(String idusuario) {
		boolean encontrado = false;
		Alumno alu = null;
		String nombreGrado = "";
		
		NodeList facultades = doc.getElementsByTagName("facultad");
		
		for (int i=0; i<facultades.getLength(); i++) {
			Element facultad = (Element) facultades.item(i);
			
			NodeList grados = facultad.getElementsByTagName("grado");
			for (int j=0; j<grados.getLength(); j++) {
				Element grado = (Element) grados.item(j);
				
				NodeList alumnos = grado.getElementsByTagName("alumno");
				for (int k=0; k<alumnos.getLength(); k++) {
					Element alumno = (Element) alumnos.item(k);
					
					if (alumno.getAttribute("id").equals(idusuario)) {
						encontrado = true;
						nombreGrado = grado.getAttribute("nombre");
						
						String idAlumno = alumno.getAttribute("id");
						boolean tieneBeca = Boolean.parseBoolean(alumno.getAttribute("beca"));
						
						
						Element datos = (Element) alumno.getElementsByTagName("datos").item(0);
						
						String nombre = datos.getElementsByTagName("nombre").item(0).getTextContent();
						String ciudad = datos.getElementsByTagName("ciudad").item(0).getTextContent();
						
						ArrayList<Asignatura> asigs = new ArrayList<>();
						
						Element expediente = (Element) alumno.getElementsByTagName("expediente").item(0);
						
						NodeList asignaturas = expediente.getElementsByTagName("asignatura");
						
						for (int y=0; y<asignaturas.getLength(); y++) {
							Element asignatura = (Element) asignaturas.item(y);
							
							String referenciaAsignatura = asignatura.getAttribute("ref");
							double notaAsignatura = Double.parseDouble(asignatura.getElementsByTagName("nota").item(0).getTextContent());
							
							Asignatura asig = new Asignatura(referenciaAsignatura, notaAsignatura);
							asigs.add(asig);
						}
						
						Alumno al = new Alumno(idAlumno, tieneBeca, nombre, ciudad, asigs );
						alu = al;
					}
				}
			}
		}
		
		
		Element planEstudios = (Element) doc.getElementsByTagName("plan_estudios").item(0);
		
		NodeList materias = planEstudios.getElementsByTagName("materia");
		
		System.out.println("Boletin de: "+alu.getNombre()+" ("+nombreGrado+")"+":");
		for (int x=0; x<materias.getLength(); x++) {
			Element materia = (Element) materias.item(x);
			
			for (Asignatura asig : alu.getAsignaturas()) {
				
				if (asig.getIdRef().equals(materia.getAttribute("id"))) {
					System.out.println("- "+materia.getTextContent()+":"+asig.getNota());
				}
			}
		}
		
	}
	
	public void listarAlumnosPorAsignatura(String nombreAsignatura) {
		
		boolean asignaturaEncontrada = false;
		String referencia = "";
		ArrayList<String> nombresAlumnos = new ArrayList<>();
		
		Element planEstudios = (Element) doc.getElementsByTagName("plan_estudios").item(0);
		
		NodeList materias = planEstudios.getElementsByTagName("materia");
		
		for (int i=0; i<materias.getLength(); i++) {
			
			Element materia = (Element) materias.item(i);
			
			if (materia.getTextContent().equals(nombreAsignatura)) {
				referencia = materia.getAttribute("id");
			}
		}
		
		NodeList alumnos = doc.getElementsByTagName("alumno");
		
		for (int j=0; j<alumnos.getLength(); j++) {
			Element alumno = (Element) alumnos.item(j);
			
			Element datos = (Element) alumno.getElementsByTagName("datos").item(0);
			
			
			Element expediente = (Element) alumno.getElementsByTagName("expediente").item(0);
			NodeList asignaturas = alumno.getElementsByTagName("asignatura");
			
			for (int k=0; k<asignaturas.getLength(); k++) {
				Element asignatura = (Element) asignaturas.item(k);
				
				if (asignatura.getAttribute("ref").equals(referencia)) {
					String nombreAlumno = datos.getElementsByTagName("nombre").item(0).getTextContent();
					nombresAlumnos.add(nombreAlumno);
				}
			}
		}
		
		System.out.println("Los nombres de los alumnos que estudian "+nombreAsignatura);
		for (String s : nombresAlumnos) {
			System.out.println(s);
		}
		
	}
	
	//Cuanto tiene que pagar 
	public int calcularPrecioMatricula(String idAlumno) {
		
		boolean tieneBeca = false;
		boolean encontrado = false;
		int costeTotal = 0;
		
		String nombreAlumno = "";
		
		ArrayList<String> referencias = new ArrayList<>();
		
		NodeList alumnos = doc.getElementsByTagName("alumno");
		
		for (int i=0; i<alumnos.getLength(); i++) {
			Element alumno = (Element) alumnos.item(i);
			
			if (alumno.getAttribute("id").equals(idAlumno)) {
				encontrado = true;
				//ahora comprobamos si tiene beca o no 
				Element datos = (Element) alumno.getElementsByTagName("datos").item(0);
				
				nombreAlumno = datos.getElementsByTagName("nombre").item(0).getTextContent();
				
				tieneBeca = Boolean.parseBoolean(alumno.getAttribute("beca"));
				
				if (tieneBeca) {
					break;
				} else {
					//Guardamos las referencias de la asignatura
					Element expediente = (Element) alumno.getElementsByTagName("expediente").item(0);
					NodeList asignaturas = expediente.getElementsByTagName("asignatura");
					
					for (int j=0; j<asignaturas.getLength(); j++) {
						Element asignatura = (Element) asignaturas.item(j);
						
						referencias.add(asignatura.getAttribute("ref"));
					}
				}
			}
		}
		
		//Una vez ya tenemos las referencias de las asignaturas y no paga beca
		if (referencias.isEmpty() | tieneBeca) {
			System.out.println("El alumno esta becado");
		} else {
			Element planEstudios = (Element) doc.getElementsByTagName("plan_estudios").item(0);
			NodeList materias = planEstudios.getElementsByTagName("materia");
			
			for (int y=0; y<materias.getLength(); y++) {
				Element materia = (Element) materias.item(y);
				
				for (String refe : referencias)	 {
					if (refe.equals(materia.getAttribute("id"))) {
						costeTotal += Integer.parseInt(materia.getAttribute("coste"));
					}
				}
			}
		}
		
		return costeTotal;
	}
	
	public double notaMediaFacultad(String nombreFacultad) {
		
		double suma = 0;
		int totalNotas = 0;
		
		NodeList facultades = doc.getElementsByTagName("facultad");
		
		for (int i=0; i<facultades.getLength(); i++) {
			Element facultad = (Element) facultades.item(i);
			
			if (facultad.getAttribute("nombre").equals(nombreFacultad)) {
				//No es necesario recorrer todo simplemente tener las notas porque estan dentro de facultad
				NodeList listaNotas = facultad.getElementsByTagName("nota");
				
				for (int j=0; j<listaNotas.getLength(); j++) {
					Element nodoNota = (Element) listaNotas.item(j);
					
					double valor = Double.parseDouble(nodoNota.getTextContent());
					suma += valor;
					
					totalNotas++;
				}
			}
		}
		
		return suma / totalNotas;
	} 
	
	public void modificarNota(String idAlumno, String refAsignatura, double nuevaNota) {
		boolean encontrado = false; 
		
		NodeList alumnos = doc.getElementsByTagName("alumno");
		
		for (int i=0; i<alumnos.getLength(); i++) {
			Element alumno = (Element) alumnos.item(i);
			
			if (alumno.getAttribute("id").equalsIgnoreCase(idAlumno)) {
				encontrado = true;
				
				Element expediente = (Element) alumno.getElementsByTagName("expediente").item(0);
				
				NodeList asignaturas = expediente.getElementsByTagName("asignatura");
				
				for (int j=0; j<asignaturas.getLength(); j++) {
					Element asignatura = (Element) asignaturas.item(j);
					
					if (asignatura.getAttribute("ref").equals(refAsignatura)) {
						asignatura.getElementsByTagName("nota").item(0).setTextContent(String.valueOf(nuevaNota));
						if (nuevaNota == 10) {
							System.out.println("TIENE MATRICULA DE HONOR, ENHORABUENA");
						}
						System.out.println("Nota cambiada");
						actualizarFichero();
					}
				}
			}
		}
		
		if (!encontrado) {
			System.out.println("No se ha podido encontrar ninguna alumno o asignatura con esas referencias");
		}
	}
	
	
	public void trasladoExpediente(String idAlumno, String nuevoGrado) {
		//En primer lugar vamos a guardar los datos del alumno
		
		boolean encontrado = false;
		
		Alumno al = new Alumno();
		NodeList alumnos = doc.getElementsByTagName("alumno");
		
		for (int i=0; i<alumnos.getLength(); i++) {
			 Element alumno = (Element) alumnos.item(i);
			 
			 if (alumno.getAttribute("id").equals(idAlumno)) {
				 encontrado = true;
				 
				 String idAlum = alumno.getAttribute("id");
				 boolean tieneBeca = Boolean.parseBoolean(alumno.getAttribute("beca"));
				 
				 Element datos = (Element) alumno.getElementsByTagName("datos").item(0);
				 
				 String nombreAlumno = datos.getElementsByTagName("nombre").item(0).getTextContent();
				 String ciudad = datos.getElementsByTagName("ciudad").item(0).getTextContent();
				 
				 Element expediente = (Element) alumno.getElementsByTagName("expediente");
				 
				 ArrayList<Asignatura> asigs = new ArrayList<>();
				 NodeList asignaturas = expediente.getElementsByTagName("asignatura");
				 
				 for (int j=0; j<asignaturas.getLength(); j++) {
					 Element asignatura = (Element) asignaturas.item(j);
					 
					 String referencia = asignatura.getAttribute("ref");
					 double nota = Double.parseDouble(asignatura.getElementsByTagName("nota").item(0).getTextContent());
					 
					 Asignatura asig = new Asignatura(referencia, nota);
					 asigs.add(asig);
					 
					 
					 
				 }
				 
				 alumno.getParentNode().removeChild(alumno);
				 actualizarFichero();
				 System.out.println("Alumno eliminado");
				 
				 Alumno alum = new Alumno(idAlum, tieneBeca, nombreAlumno, ciudad, asigs);
				 al = alum;
			 }
			 
		}
		
		NodeList grados = doc.getElementsByTagName("grado");
		
		for (int k=0; k<grados.getLength(); k++) {
			Element grado = (Element) grados.item(k);
			
			if (grado.getAttribute("nombre").equals(nuevoGrado)) {
				Element nodoNuevoAlumno = doc.createElement("alumno");
				nodoNuevoAlumno.setAttribute("id", al.getId());
				nodoNuevoAlumno.setAttribute("beca", String.valueOf(al.getTieneBeca()));
				
				grado.appendChild(nodoNuevoAlumno);
				
				Element nodoDatos = doc.createElement("datos");
				crearElemento(nodoDatos, "nombre", al.getNombre());
				crearElemento(nodoDatos, "ciudad", al.getCiudad());
				
				nodoNuevoAlumno.appendChild(nodoDatos);
				
				
				Element expediente = doc.createElement("expediente");
				
				nodoNuevoAlumno.appendChild(expediente);
				for (Asignatura a : al.getAsignaturas() ) {
					Element asignatura = doc.createElement("asignatura");
					asignatura.setAttribute("ref", a.getIdRef());
					crearElemento(asignatura, "nota", String.valueOf(a.getNota()));
					
					expediente.appendChild(asignatura);
				}
			}
		}
	}
	
	public void moverExpedienteOptimizado(String idAlumno, String nuevoGrado) {
		Element nodoAlumno = null;
		Element nodoNuevoGrado = null;
		
		//1. Buscamos el nodo del alumno (La "casa" entera)
		NodeList alumnos = doc.getElementsByTagName("alumno");
		for (int i=0; i<alumnos.getLength(); i++) {
			Element e = (Element) alumnos.item(i);
			
			if (e.getAttribute("id").equals(idAlumno)) {
				nodoAlumno = e;
				break;
			}
		}
		
		//2. Buscamos el nodo dle nuevo grado
		NodeList grados = doc.getElementsByTagName("grado");
		for (int i=0; i<grados.getLength(); i++) {
			Element g = (Element) grados.item(i);
			
			if (g.getAttribute("nombre").equals(nuevoGrado)) {
				nodoNuevoGrado = g;
				break;
			}
		}
		
		//3. LA MUDANZA	
		if (nodoAlumno != null && nodoNuevoGrado != null) {
			//Paso A: Lo sacmaos del sitio viejo
			nodoAlumno.getParentNode().removeChild(nodoAlumno);
			
			//Paso B: Lo pegamos en el sitio nuevo
			//ALERTA: Al pegar el nodoAlumno, se viene todo con el no hay que construir nada
			nodoNuevoGrado.appendChild(nodoAlumno);
			
			actualizarFichero();
			System.out.println("Alumno trasladado correctamente");
		} else {
			System.out.println("Error: no se puedo encontrar el alumno o el grado :(");
		}
	}
	
	public void limpiezaDeSuspensos() {
		//1. Pillamos TODAS las asignaturas de todos los alumnos de golpe
		NodeList todasLasAsignaturas = doc.getElementsByTagName("asignatura");
		
		int borrados = 0;
		
		//2. Bucle inverso
		/*
		 * Cuando boramos elementos de una lista NodeList, la lista cambia de tamaño en vivo
		 * Si tengo 3 supsensos y borro el a (indice = 0) el b es ahora el indice 0
		 * por lo que a la hora de aumentar el bucle pasa directamente al c saltandose asi el B
		 */
		for (int i=todasLasAsignaturas.getLength()-1; i>=0; i--) {
			//3. Leemos la nota
			Element asignatura = (Element) todasLasAsignaturas.item(i);
			
			Element notaElement = (Element) asignatura.getElementsByTagName("nota").item(0);
			double nota = Double.parseDouble(notaElement.getTextContent());
			
			//4. CRITERIO = Suspendio
			if (nota < 5.0) {
				//Si la nota esta suspensa le decimos a su padre que lo borre
				asignatura.getParentNode().removeChild(asignatura);
				borrados++;
			}
		}
		
		if (borrados > 0) {
			System.out.println("Se han limpiado los suspensos. Se han eliminado "+borrados+" asignaturas");
		} else {
			System.out.println("No habia ningun suspenso que limpiar");
		}
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
	
	private void crearElemento(Element padre, String etiqueta, String texto) {
		//PASO 1: Fabricar una caja vacia 
		Element elem = doc.createElement(etiqueta);
		
		//PASO 2: Meter el contenido dentro de la caja
		elem.setTextContent(texto);
		
		//PASO 3: Pegar la caja pequeña dentro de la caja grande
		padre.appendChild(elem);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
