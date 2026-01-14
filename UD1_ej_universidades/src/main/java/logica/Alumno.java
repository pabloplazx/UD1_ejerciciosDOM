package logica;

import java.util.ArrayList;

public class Alumno {

	private String id;
	private Boolean tieneBeca;
	private String nombre;
	private String ciudad;
	private ArrayList<Asignatura> asignaturas = new ArrayList<>();
	
	public Alumno(String id, Boolean tieneBeca, String nombre, String ciudad, ArrayList<Asignatura> asignaturas) {
		super();
		this.id = id;
		this.tieneBeca = tieneBeca;
		this.nombre = nombre;
		this.ciudad = ciudad;
		this.asignaturas = asignaturas;
	}
	
	public Alumno() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getTieneBeca() {
		return tieneBeca;
	}

	public void setTieneBeca(Boolean tieneBeca) {
		this.tieneBeca = tieneBeca;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public ArrayList<Asignatura> getAsignaturas() {
		return asignaturas;
	}

	public void setAsignaturas(ArrayList<Asignatura> asignaturas) {
		this.asignaturas = asignaturas;
	}
	
	
}
