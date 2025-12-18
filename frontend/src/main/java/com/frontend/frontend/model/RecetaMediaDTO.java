package com.frontend.frontend.model;

public class RecetaMediaDTO {
    private Long id;
    private String url;
    private String tipo; // "FOTO" or "VIDEO"

    public RecetaMediaDTO() {
    }

    public RecetaMediaDTO(String url, String tipo) {
        this.url = url;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
