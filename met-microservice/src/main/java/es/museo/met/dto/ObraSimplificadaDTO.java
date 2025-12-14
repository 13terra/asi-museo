package es.museo.met.dto;

/**
 * DTO simplificado para listados de búsqueda (HU48)
 */
public class ObraSimplificadaDTO {
    private Long idExterno;
    private String titulo;
    private String autor;
    private String imagen;

    public ObraSimplificadaDTO() {}


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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
