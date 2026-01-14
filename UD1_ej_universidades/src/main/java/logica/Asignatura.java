package logica;

public class Asignatura {

	private String idRef;
	private double nota;
	public Asignatura(String idRef, double nota) {
		super();
		this.idRef = idRef;
		this.nota = nota;
	}
	public String getIdRef() {
		return idRef;
	}
	public void setIdRef(String idRef) {
		this.idRef = idRef;
	}
	public double getNota() {
		return nota;
	}
	public void setNota(double nota) {
		this.nota = nota;
	}
	@Override
	public String toString() {
		return "Asignatura [idRef=" + idRef + ", nota=" + nota + ", getIdRef()=" + getIdRef() + ", getNota()="
				+ getNota() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
	
	
	
}
