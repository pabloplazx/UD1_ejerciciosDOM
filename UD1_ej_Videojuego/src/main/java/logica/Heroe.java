package logica;

public class Heroe {
	
	private String id; 
	private String clase;
	private String nombre;
	private int nivel;
	private int vidaActual;
	private int vidaMaxima;
	private int ataque;
	private Boolean estaVivo;
	
	public Heroe(String id, String clase, String nombre, int nivel, int vidaActual, int vidaMaxima, int ataque,
			Boolean estaVivo) {
		super();
		this.id = id;
		this.clase = clase;
		this.nombre = nombre;
		this.nivel = nivel;
		this.vidaActual = vidaActual;
		this.vidaMaxima = vidaMaxima;
		this.ataque = ataque;
		this.estaVivo = estaVivo;
	}
	
	

	public Heroe() {
		super();
	}



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClase() {
		return clase;
	}

	public void setClase(String clase) {
		this.clase = clase;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	public int getVidaActual() {
		return vidaActual;
	}

	public void setVidaActual(int vidaActual) {
		this.vidaActual = vidaActual;
	}

	public int getVidaMaxima() {
		return vidaMaxima;
	}

	public void setVidaMaxima(int vidaMaxima) {
		this.vidaMaxima = vidaMaxima;
	}

	public int getAtaque() {
		return ataque;
	}

	public void setAtaque(int ataque) {
		this.ataque = ataque;
	}

	public Boolean getEstaVivo() {
		return estaVivo;
	}

	public void setEstaVivo(Boolean estaVivo) {
		this.estaVivo = estaVivo;
	}

	@Override
	public String toString() {
		return "Heroe [id=" + id + ", clase=" + clase + ", nombre=" + nombre + ", nivel=" + nivel + ", vidaActual="
				+ vidaActual + ", vidaMaxima=" + vidaMaxima + ", ataque=" + ataque + ", estaVivo=" + estaVivo + "]";
	}
	
	
	
	
	
	

}
