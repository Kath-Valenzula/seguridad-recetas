package com.demo.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.demo.models.Receta;
import com.demo.demo.repository.RecetaRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.demo.demo.dto.ComentarioDTO;
import com.demo.demo.dto.RecetaMediaDTO;
import com.demo.demo.dto.ValoracionDTO;
import com.demo.demo.models.Comentario;
import com.demo.demo.models.RecetaMedia;
import com.demo.demo.models.User;
import com.demo.demo.models.Valoracion;
import com.demo.demo.repository.ComentarioRepository;
import com.demo.demo.repository.RecetaMediaRepository;
import com.demo.demo.repository.UserRepository;
import com.demo.demo.repository.ValoracionRepository;

@Service
@Transactional
@SuppressWarnings("null")
public class RecetaService {

    private final RecetaRepository recetaRepository;
    private final RecetaMediaRepository recetaMediaRepository;
    private final ComentarioRepository comentarioRepository;
    private final ValoracionRepository valoracionRepository;
    private final UserRepository userRepository;

    public RecetaService(RecetaRepository recetaRepository,
            RecetaMediaRepository recetaMediaRepository,
            ComentarioRepository comentarioRepository,
            ValoracionRepository valoracionRepository,
            UserRepository userRepository) {
        this.recetaRepository = recetaRepository;
        this.recetaMediaRepository = recetaMediaRepository;
        this.comentarioRepository = comentarioRepository;
        this.valoracionRepository = valoracionRepository;
        this.userRepository = userRepository;
    }

    public List<com.demo.demo.dto.RecetaDTO> listarTodas() {
        return recetaRepository.findAll().stream()
                .map(this::convertirARecetaDTO)
                .toList();
    }

    public List<com.demo.demo.dto.RecetaDTO> listarPopulares() {
        return recetaRepository.findByPopularTrue().stream()
                .map(this::convertirARecetaDTO)
                .toList();
    }

    public Optional<Receta> obtenerPorId(Long id) {
        Objects.requireNonNull(id, "id es requerido");
        return recetaRepository.findById(id);
    }

    public Receta guardar(Receta receta) {
        return recetaRepository.save(receta);
    }

    public List<com.demo.demo.dto.RecetaDTO> buscar(String nombre, String tipoCocina, String ingredientes, String pais,
            String dificultad,
            Boolean popular) {
        List<Receta> recetas;
        if (popular != null)
            recetas = recetaRepository.findByPopularTrue();
        else if (nombre != null && !nombre.isEmpty())
            recetas = recetaRepository.findByNombreContainingIgnoreCase(nombre);
        else if (tipoCocina != null && !tipoCocina.isEmpty())
            recetas = recetaRepository.findByTipoCocinaContainingIgnoreCase(tipoCocina);
        else if (ingredientes != null && !ingredientes.isEmpty())
            recetas = recetaRepository.findByIngredientesContainingIgnoreCase(ingredientes);
        else if (pais != null && !pais.isEmpty())
            recetas = recetaRepository.findByPaisOrigenContainingIgnoreCase(pais);
        else if (dificultad != null && !dificultad.isEmpty())
            recetas = recetaRepository.findByDificultadContainingIgnoreCase(dificultad);
        else
            recetas = recetaRepository.findAll();

        return recetas.stream()
                .map(this::convertirARecetaDTO)
                .toList();
    }

    private com.demo.demo.dto.RecetaDTO convertirARecetaDTO(Receta receta) {
        Objects.requireNonNull(receta, "receta es requerida");
        com.demo.demo.dto.RecetaDTO dto = new com.demo.demo.dto.RecetaDTO();
        dto.setId(receta.getId());
        dto.setNombre(receta.getNombre());
        dto.setTipoCocina(receta.getTipoCocina());
        dto.setIngredientes(receta.getIngredientes());
        dto.setPaisOrigen(receta.getPaisOrigen());
        dto.setDificultad(receta.getDificultad());
        dto.setTiempoCoccion(receta.getTiempoCoccion());
        dto.setInstrucciones(receta.getInstrucciones());
        dto.setImagenUrl(receta.getImagenUrl());
        dto.setPopular(receta.isPopular());
        return dto;
    }

    public void agregarMedia(Long recetaId, List<RecetaMediaDTO> mediaDtos) {
        Objects.requireNonNull(recetaId, "recetaId es requerido");
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        for (RecetaMediaDTO dto : mediaDtos) {
            RecetaMedia media = new RecetaMedia();
            media.setReceta(receta);
            media.setUrl(dto.getUrl());
            media.setTipo(dto.getTipo());
            recetaMediaRepository.save(media);
        }
    }

    public void agregarComentario(Long recetaId, Integer userId, ComentarioDTO comentarioDTO) {
        Objects.requireNonNull(recetaId, "recetaId es requerido");
        Objects.requireNonNull(userId, "userId es requerido");
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Comentario comentario = new Comentario();
        comentario.setReceta(receta);
        comentario.setUsuario(user);
        comentario.setContenido(comentarioDTO.getContenido());
        comentarioRepository.save(comentario);
    }

    public void agregarValoracion(Long recetaId, Integer userId, ValoracionDTO valoracionDTO) {
        Objects.requireNonNull(recetaId, "recetaId es requerido");
        Objects.requireNonNull(userId, "userId es requerido");
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Valoracion valoracion = new Valoracion();
        valoracion.setReceta(receta);
        valoracion.setUsuario(user);
        valoracion.setPuntuacion(valoracionDTO.getPuntuacion());
        valoracionRepository.save(valoracion);
    }

    public com.demo.demo.dto.RecetaDetalleDTO obtenerDetallePorId(Long id) {
        Objects.requireNonNull(id, "id es requerido");
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        com.demo.demo.dto.RecetaDetalleDTO dto = new com.demo.demo.dto.RecetaDetalleDTO();
        dto.setId(receta.getId());
        dto.setNombre(receta.getNombre());
        dto.setTipoCocina(receta.getTipoCocina());
        dto.setIngredientes(receta.getIngredientes());
        dto.setPaisOrigen(receta.getPaisOrigen());
        dto.setDificultad(receta.getDificultad());
        dto.setTiempoCoccion(receta.getTiempoCoccion());
        dto.setInstrucciones(receta.getInstrucciones());
        dto.setImagenUrl(receta.getImagenUrl());
        dto.setPopular(receta.isPopular());

        // Map media
        dto.setMedia(receta.getMedia().stream()
                .map(m -> new com.demo.demo.dto.RecetaDetalleDTO.MediaDTO(m.getId(), m.getUrl(), m.getTipo()))
                .toList());

        // Map comentarios
        dto.setComentarios(receta.getComentarios().stream()
                .map(c -> new com.demo.demo.dto.RecetaDetalleDTO.ComentarioDetalleDTO(
                        c.getId(), c.getContenido(), c.getUsuario().getUsername(), c.getFechaCreacion()))
                .toList());

        // Map valoraciones
        dto.setValoraciones(receta.getValoraciones().stream()
                .map(v -> new com.demo.demo.dto.RecetaDetalleDTO.ValoracionDetalleDTO(
                        v.getId(), v.getPuntuacion(), v.getUsuario().getUsername()))
                .toList());

        return dto;
    }
}
