package logica;

public class Pelicula implements Comparable<Pelicula> {
	
	private String id;
	private String titulo;
	private String director;
	private int duracion;
	private Boolean vista;
	
	public Pelicula(String id, String titulo, String director, int duracion, Boolean vista) {
		super();
		this.id = id;
		this.titulo = titulo;
		this.director = director;
		this.duracion = duracion;
		this.vista = vista;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}

	public Boolean getVista() {
		return vista;
	}

	public void setVista(Boolean vista) {
		this.vista = vista;
	}

	@Override
	public String toString() {
		return "Pelicula [id=" + id + ", titulo=" + titulo + ", director=" + director + ", duracion=" + duracion
				+ ", vista=" + vista + "]";
	}

	@Override
	public int compareTo(Pelicula o) {
		// TODO Auto-generated method stub
		return this.titulo.compareTo(o.titulo);
	}
	
	

}
