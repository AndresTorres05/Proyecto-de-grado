package com.proyectogrado.analitica_service.controller;

import com.proyectogrado.analitica_service.dto.ReportePdfRequest;
import com.proyectogrado.analitica_service.dto.ReportePdfRequest.Seccion;
import com.proyectogrado.analitica_service.pdf.GeneradorPdf;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDate;

/**
 * Descarga de un reporte de analítica en PDF. El frontend envía el
 * contenido del reporte (indicadores, imágenes de las gráficas y tablas) y
 * aquí se arma el documento.
 */
@RestController
@RequestMapping("/api/analitica/reportes")
public class PdfController {

    // Límites para no aceptar peticiones desproporcionadas
    private static final int MAX_SECCIONES = 12;
    private static final int MAX_INDICADORES = 8;
    private static final int MAX_FILAS = 1000;
    private static final int MAX_IMAGEN_BASE64 = 4 * 1024 * 1024; // ~3 MB de PNG

    private final OrganizacionActual organizacionActual;
    private final GeneradorPdf generadorPdf;

    public PdfController(OrganizacionActual organizacionActual, GeneradorPdf generadorPdf) {
        this.organizacionActual = organizacionActual;
        this.generadorPdf = generadorPdf;
    }

    @PostMapping("/pdf")
    public ResponseEntity<?> descargar(
            @RequestHeader("X-User-Id") Integer idUsuario,
            @RequestBody ReportePdfRequest reporte
    ) {
        OrganizacionActual.Organizacion organizacion = organizacionActual.de(idUsuario);
        if (organizacion == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo una organización puede descargar reportes de analítica");
        }

        String error = validar(reporte);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        byte[] pdf;
        try {
            pdf = generadorPdf.generar(reporte, organizacion.nombre());
        } catch (Exception e) {
            System.err.println("Error generando el PDF de analítica: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("No se pudo generar el PDF del reporte");
        }

        String archivo = "reporte-" + nombreArchivo(reporte.titulo()) + "-" + LocalDate.now() + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(archivo, StandardCharsets.UTF_8).build().toString())
                .body(pdf);
    }

    private String validar(ReportePdfRequest reporte) {
        if (reporte == null || reporte.titulo() == null || reporte.titulo().isBlank()) {
            return "El reporte no tiene título";
        }
        if (reporte.indicadores() != null && reporte.indicadores().size() > MAX_INDICADORES) {
            return "El reporte tiene demasiados indicadores";
        }
        if (reporte.secciones() != null) {
            if (reporte.secciones().size() > MAX_SECCIONES) {
                return "El reporte tiene demasiadas secciones";
            }
            for (Seccion seccion : reporte.secciones()) {
                if (seccion.imagen() != null && seccion.imagen().length() > MAX_IMAGEN_BASE64) {
                    return "Una de las gráficas es demasiado grande";
                }
                if (seccion.tabla() != null && seccion.tabla().filas() != null
                        && seccion.tabla().filas().size() > MAX_FILAS) {
                    return "Una de las tablas tiene demasiadas filas";
                }
            }
        }
        return null;
    }

    /** "Salud de la población" -> "salud-de-la-poblacion" */
    private String nombreArchivo(String titulo) {
        String sinTildes = Normalizer.normalize(titulo, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        String limpio = sinTildes.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        return limpio.isBlank() ? "analitica" : limpio;
    }
}
