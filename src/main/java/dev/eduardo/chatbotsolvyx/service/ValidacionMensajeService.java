package dev.eduardo.chatbotsolvyx.service;

import dev.eduardo.chatbotsolvyx.config.CacheConfig;
import dev.eduardo.chatbotsolvyx.exception.MensajeProhibidoException;
import dev.eduardo.chatbotsolvyx.repository.PalabraProhibidaRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ValidacionMensajeService {

    private static final int LONGITUD_MAXIMA_INPUT = 600;
    private static final Pattern PATRON_DIACRITICOS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private final PalabraProhibidaRepository palabraProhibidaRepository;

    public ValidacionMensajeService(PalabraProhibidaRepository palabraProhibidaRepository) {
        this.palabraProhibidaRepository = palabraProhibidaRepository;
    }

    public void validarInput(String mensaje) {
        if (mensaje == null || mensaje.isBlank()) {
            throw new MensajeProhibidoException("El mensaje no puede estar vacio");
        }
        if (mensaje.length() > LONGITUD_MAXIMA_INPUT) {
            throw new MensajeProhibidoException("El mensaje supera los " + LONGITUD_MAXIMA_INPUT + " caracteres permitidos");
        }
        validarPalabrasProhibidas(mensaje);
    }

    public void validarOutput(String respuesta) {
        if (respuesta == null || respuesta.isBlank()) {
            throw new MensajeProhibidoException("La respuesta generada esta vacia");
        }
        validarPalabrasProhibidas(respuesta);
    }

    private void validarPalabrasProhibidas(String texto) {
        String normalizado = normalizar(texto);
        for (String palabra : obtenerPalabrasActivas()) {
            if (normalizado.contains(palabra)) {
                throw new MensajeProhibidoException("El mensaje contiene contenido no permitido");
            }
        }
    }

    @Cacheable(CacheConfig.CACHE_PALABRAS_PROHIBIDAS)
    public List<String> obtenerPalabrasActivas() {
        return palabraProhibidaRepository.findByActivoTrue()
                .stream()
                .map(p -> normalizar(p.getPalabra()))
                .toList();
    }

    @CacheEvict(value = CacheConfig.CACHE_PALABRAS_PROHIBIDAS, allEntries = true)
    public void refrescarCachePalabrasProhibidas() {
        // Invalida el cache; la proxima lectura recarga desde la base de datos.
    }

    public String normalizar(String texto) {
        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD);
        sinAcentos = PATRON_DIACRITICOS.matcher(sinAcentos).replaceAll("");
        return sinAcentos.toLowerCase();
    }
}
