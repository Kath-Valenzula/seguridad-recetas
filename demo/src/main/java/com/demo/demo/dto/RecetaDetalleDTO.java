package com.demo.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class RecetaDetalleDTO {
    private Long id;
    private String nombre;
    private String tipoCocina;
    private String ingredientes;
    private String paisOrigen;
    private String dificultad;
    private Integer tiempoCoccion;
    private String instrucciones;
    private String imagenUrl;
    private boolean popular;
    private List<MediaDTO> media;
    private List<ComentarioDetalleDTO> comentarios;
    private List<ValoracionDetalleDTO> valoraciones;

    public static class MediaDTO {
        private Long id;
        private String url;
        private String tipo;

        public MediaDTO(Long id, String url, String tipo) {
            this.id = id;
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

    public static class ComentarioDetalleDTO {
        private Long id;
        private String contenido;
        private String nombreUsuario;
        private LocalDateTime fechaCreacion;

        public ComentarioDetalleDTO(Long id, String contenido, String nombreUsuario, LocalDateTime fechaCreacion) {
            this.id = id;
            this.contenido = contenido;
            this.nombreUsuario = nombreUsuario;
            this.fechaCreacion = fechaCreacion;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getContenido() {
            return contenido;
        }

        public void setContenido(String contenido) {
            this.contenido = contenido;
        }

        public String getNombreUsuario() {
            return nombreUsuario;
        }

        public void setNombreUsuario(String nombreUsuario) {
            this.nombreUsuario = nombreUsuario;
        }

        public LocalDateTime getFechaCreacion() {
            return fechaCreacion;
        }

        public void setFechaCreacion(LocalDateTime fechaCreacion) {
            this.fechaCreacion = fechaCreacion;
        }
    }

    public static class ValoracionDetalleDTO {
        private Long id;
        private Integer puntuacion;
        private String nombreUsuario;

        public ValoracionDetalleDTO(Long id, Integer puntuacion, String nombreUsuario) {
            this.id = id;
            this.puntuacion = puntuacion;
            this.nombreUsuario = nombreUsuario;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Integer getPuntuacion() {
            return puntuacion;
        }

        public void setPuntuacion(Integer puntuacion) {
            this.puntuacion = puntuacion;
        }

        public String getNombreUsuario() {
            return nombreUsuario;
        }

        public void setNombreUsuario(String nombreUsuario) {
            this.nombreUsuario = nombreUsuario;
        }
    }

    // Getters and setters for main fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoCocina() {
        return tipoCocina;
    }

    public void setTipoCocina(String tipoCocina) {
        this.tipoCocina = tipoCocina;
    }

    public String getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(String ingredientes) {
        this.ingredientes = ingredientes;
    }

    public String getPaisOrigen() {
        return paisOrigen;
    }

    public void setPaisOrigen(String paisOrigen) {
        this.paisOrigen = paisOrigen;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public Integer getTiempoCoccion() {
        return tiempoCoccion;
    }

    public void setTiempoCoccion(Integer tiempoCoccion) {
        this.tiempoCoccion = tiempoCoccion;
    }

    public String getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(String instrucciones) {
        this.instrucciones = instrucciones;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public boolean isPopular() {
        return popular;
    }

    public void setPopular(boolean popular) {
        this.popular = popular;
    }

    public List<MediaDTO> getMedia() {
        return media;
    }

    public void setMedia(List<MediaDTO> media) {
        this.media = media;
    }

    public List<ComentarioDetalleDTO> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<ComentarioDetalleDTO> comentarios) {
        this.comentarios = comentarios;
    }

    public List<ValoracionDetalleDTO> getValoraciones() {
        return valoraciones;
    }

    public void setValoraciones(List<ValoracionDetalleDTO> valoraciones) {
        this.valoraciones = valoraciones;
    }
}
