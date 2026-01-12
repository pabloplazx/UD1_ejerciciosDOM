package logica;

public class Empleado implements Comparable<Empleado>{
	
	private String id; 
	private String nombre;
	private String puesto;
	private String salario;
	private String fechaContratacion;
	private String email;
	
	public Empleado(String id, String nombre, String puesto, String salario, String fechaContratacion, String email) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.puesto = puesto;
		this.salario = salario;
		this.fechaContratacion = fechaContratacion;
		this.email = email;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}

	public String getSalario() {
		return salario;
	}

	public void setSalario(String salario) {
		this.salario = salario;
	}

	public String getFechaContratacion() {
		return fechaContratacion;
	}

	public void setFechaContratacion(String fechaContratacion) {
		this.fechaContratacion = fechaContratacion;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Empleado [id=" + id + ", nombre=" + nombre + ", puesto=" + puesto + ", salario=" + salario
				+ ", fechaContratacion=" + fechaContratacion + ", email=" + email + "]";
	}

	@Override
	public int compareTo(Empleado otroEmpleado) {
	    // Como los IDs tienen letras (ej: E013), debemos comparar como Texto (String)
	    // Esto ordenará alfabéticamente.
	    return this.id.compareTo(otroEmpleado.getId());
	}



	
	
	
	

}
