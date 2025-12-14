package es.museo.met.dto;

/**
 * DTO detallado para importar obra (HU49)
 */
public class ObraDetalleDTO {
    private Long idExterno;
    private String titulo;
    private String autor;
    private Integer anoCreacion;
    private String dimensiones;
    private String imagen;
    private String tecnica;

    public ObraDetalleDTO() {}


    public Long getIdExterno() {
        return idExterno;
    }

    public void setIdExterno(Long idExterno) {
        this.idExterno = idExterno;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Integer getAnoCreacion() {
        return anoCreacion;
    }

    public void setAnoCreacion(Integer anoCreacion) {
        this.anoCreacion = anoCreacion;
    }

    public String getDimensiones() {
        return dimensiones;
    }

    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getTecnica() {
        return tecnica;
    }

    public void setTecnica(String tecnica) {
        this.tecnica = tecnica;
    }
}
