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

public class GestionPartida {

    private File fichero;
    private Document doc;

    public GestionPartida(String fichero) {
        this.fichero = new File(fichero);
        cargarDoc();
    }

    private void cargarDoc() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(fichero);
            // Esto ayuda a evitar errores con nodos de texto vacíos
            doc.getDocumentElement().normalize(); 
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    // 1. Obtener Nombre
    public String obtenerNombreJugador() {
        String nombreJugador = "";
        NodeList jugadores = doc.getElementsByTagName("jugador");

        if (jugadores.getLength() > 0) {
            Element jugador = (Element) jugadores.item(0);
            Element nombre = (Element) jugador.getElementsByTagName("nombre").item(0);
            nombreJugador = nombre.getTextContent();
        }
        return nombreJugador;
    }

    // 2. Calcular Oro (CORREGIDO: Solo completadas y chequeo de nulos)
    public int calcularOroGanadoMisiones() {
        int oroMisiones = 0;
        NodeList misiones = doc.getElementsByTagName("mision");

        for (int i = 0; i < misiones.getLength(); i++) {
            Element mision = (Element) misiones.item(i);

            // Filtro 1: Solo misiones completadas
            if (mision.getAttribute("completada").equalsIgnoreCase("true")) {
                
                // Filtro 2: Comprobar si la misión da oro (algunas dan XP)
                NodeList listaOro = mision.getElementsByTagName("recompensa_oro");
                if (listaOro.getLength() > 0) {
                    Element recompensaOro = (Element) listaOro.item(0);
                    oroMisiones += Integer.parseInt(recompensaOro.getTextContent());
                }
            }
        }
        return oroMisiones;
    }

    // 3. Armas Equipadas (CORREGIDO: Filtro de equipado=true)
    public List<String> obtenerArmasEquipadas() {
        List<String> armasEquipadas = new ArrayList<>();
        NodeList jugadores = doc.getElementsByTagName("jugador");

        for (int i = 0; i < jugadores.getLength(); i++) {
            Element jugador = (Element) jugadores.item(i);
            NodeList items = jugador.getElementsByTagName("item");

            for (int j = 0; j < items.getLength(); j++) {
                Element item = (Element) items.item(j);

                // Verificamos que sea tipo arma Y esté equipada
                if (item.getAttribute("tipo").equalsIgnoreCase("arma") && 
                    item.getAttribute("equipado").equalsIgnoreCase("true")) {
                    
                    Element nombreItem = (Element) item.getElementsByTagName("nombre").item(0);
                    armasEquipadas.add(nombreItem.getTextContent());
                }
            }
        }
        return armasEquipadas;
    }

    // 4. Compañeros Muertos
    public List<String> listarCompanerosMuertos() {
        List<String> compañerosMuertos = new ArrayList<>();
        NodeList compañeros = doc.getElementsByTagName("companero"); // Ojo: en el XML es "companero" sin ñ

        for (int i = 0; i < compañeros.getLength(); i++) {
            Element compañero = (Element) compañeros.item(i);

            if (compañero.getAttribute("estado").equalsIgnoreCase("MUERTO")) {
                Element nombre = (Element) compañero.getElementsByTagName("nombre").item(0);
                compañerosMuertos.add(nombre.getTextContent());
            }
        }
        return compañerosMuertos;
    }

    // 5. Valor Inventario (CORREGIDO: Multiplicar por cantidad)
    public int calcularValorIventario() {
        int valorInventario = 0;
        NodeList jugadores = doc.getElementsByTagName("jugador");

        for (int i = 0; i < jugadores.getLength(); i++) {
            Element jugador = (Element) jugadores.item(i);
            NodeList items = jugador.getElementsByTagName("item");

            for (int j = 0; j < items.getLength(); j++) {
                Element item = (Element) items.item(j);

                // Obtenemos el valor unitario
                Element valorTag = (Element) item.getElementsByTagName("valor").item(0);
                int valorUnitario = Integer.parseInt(valorTag.getTextContent());
                
                // Obtenemos la cantidad (si no existe, asumimos que es 1)
                int cantidad = 1;
                NodeList listaCantidades = item.getElementsByTagName("cantidad");
                if (listaCantidades.getLength() > 0) {
                    cantidad = Integer.parseInt(listaCantidades.item(0).getTextContent());
                }

                valorInventario += (valorUnitario * cantidad);
            }
        }
        return valorInventario;
    }

    // 6. Items Rotos
    public List<String> encontraItemsRotos() {
        List<String> itemsRotos = new ArrayList<>();
        NodeList jugadores = doc.getElementsByTagName("jugador");

        for (int i = 0; i < jugadores.getLength(); i++) {
            Element jugador = (Element) jugadores.item(i);
            NodeList items = jugador.getElementsByTagName("item");

            for (int j = 0; j < items.getLength(); j++) {
                Element item = (Element) items.item(j);

                // Primero verificamos si tiene stats (las pociones no tienen)
                NodeList statsList = item.getElementsByTagName("stats");
                
                if (statsList.getLength() > 0) {
                    Element stats = (Element) statsList.item(0);
                    
                    // Verificamos si tiene durabilidad
                    NodeList durabilidadList = stats.getElementsByTagName("durabilidad");
                    
                    if (durabilidadList.getLength() > 0) {
                        Element durabilidad = (Element) durabilidadList.item(0);
                        
                        // Si la durabilidad es 0, está roto
                        if (Integer.parseInt(durabilidad.getTextContent()) == 0) {
                            Element nombreItem = (Element) item.getElementsByTagName("nombre").item(0);
                            itemsRotos.add(nombreItem.getTextContent());
                        }
                    }
                }
            }
        }
        return itemsRotos;
    }

    // 7. Misiones Limite Tiempo (CORREGIDO: Evitar NullPointer)
    public List<String> obtenerMisionesConLimiteTiempo() {
        List<String> misionesLimite = new ArrayList<>();
        NodeList misiones = doc.getElementsByTagName("mision");

        for (int i = 0; i < misiones.getLength(); i++) {
            Element mision = (Element) misiones.item(i);

            // Comprobar si existe la etiqueta antes de intentar leerla
            NodeList listaLimites = mision.getElementsByTagName("limite_tiempo");
            
            if (listaLimites.getLength() > 0) {
                String titulo = mision.getElementsByTagName("titulo").item(0).getTextContent();
                // Opcional: concatenar el tiempo
                String tiempo = listaLimites.item(0).getTextContent();
                misionesLimite.add(titulo + " (" + tiempo + ")"); 
            }
        }
        return misionesLimite;
    }

    // 8. Mejor Estadística
    public String obtenerMejorEstadistica() {
        String mejorValor = "No encontrado";
        NodeList jugadores = doc.getElementsByTagName("jugador");

        for (int i = 0; i < jugadores.getLength(); i++) {
            Element jugador = (Element) jugadores.item(i);
            
            // Accedemos a <estadisticas>
            NodeList statsNode = jugador.getElementsByTagName("estadisticas");
            if (statsNode.getLength() > 0) {
                Element estadistica = (Element) statsNode.item(0);

                int fuerza = Integer.parseInt(estadistica.getElementsByTagName("fuerza").item(0).getTextContent());
                int destreza = Integer.parseInt(estadistica.getElementsByTagName("destreza").item(0).getTextContent());
                int inteligencia = Integer.parseInt(estadistica.getElementsByTagName("inteligencia").item(0).getTextContent());

                if (fuerza >= destreza && fuerza >= inteligencia) {
                    mejorValor = "Fuerza: " + fuerza;
                } else if (destreza >= fuerza && destreza >= inteligencia) {
                    mejorValor = "Destreza: " + destreza;
                } else {
                    mejorValor = "Inteligencia: " + inteligencia;
                }
            }
        }
        return mejorValor;
    }

    // 9. Enemigo Principal
    public String obtenerEnemigoPrincipal() {
        String nombreEnemigo = "Ninguno";
        NodeList facciones = doc.getElementsByTagName("faccion");

        for (int i = 0; i < facciones.getLength(); i++) {
            Element faccion = (Element) facciones.item(i);
            
            // Verificamos si es hostil primero (opcional, pero buena práctica)
            if (faccion.getAttribute("hostil").equalsIgnoreCase("true")) {
                
                Element reputacion = (Element) faccion.getElementsByTagName("reputacion").item(0);
                if (reputacion.getTextContent().equalsIgnoreCase("ODIADO")) {
                    nombreEnemigo = faccion.getAttribute("nombre");
                    // Si encontramos el peor enemigo, podemos salir
                    return nombreEnemigo; 
                }
            }
        }
        return nombreEnemigo;
    }

    // 10. Porcentaje Durabilidad (CORREGIDO CRÍTICAMENTE)
    public double porcentajeDurabilidad(String nombreArma) {
        double porcentaje = -1.0; // Valor de error si no se encuentra
        
        NodeList jugadores = doc.getElementsByTagName("jugador");

        for (int i = 0; i < jugadores.getLength(); i++) {
            Element jugador = (Element) jugadores.item(i);
            NodeList items = jugador.getElementsByTagName("item");

            for (int j = 0; j < items.getLength(); j++) {
                Element item = (Element) items.item(j);
                
                // 1. Obtenemos nombre del item actual
                String nombreActual = item.getElementsByTagName("nombre").item(0).getTextContent();
                
                // 2. Comparamos con el que busca el usuario
                if (nombreActual.equalsIgnoreCase(nombreArma)) {
                    
                    // 3. Verificamos si tiene stats y durabilidad
                    NodeList statsList = item.getElementsByTagName("stats");
                    if (statsList.getLength() > 0) {
                        Element stats = (Element) statsList.item(0);
                        NodeList durabilidadList = stats.getElementsByTagName("durabilidad");
                        
                        if (durabilidadList.getLength() > 0) {
                            Element durabilidadTag = (Element) durabilidadList.item(0);
                            
                            // 4. Casting a double para la división
                            double max = Double.parseDouble(durabilidadTag.getAttribute("max"));
                            double actual = Double.parseDouble(durabilidadTag.getTextContent());
                            
                            porcentaje = (actual / max) * 100;
                            return porcentaje; // Encontrado y calculado
                        }
                    }
                }
            }
        }
        return porcentaje;
    }
}