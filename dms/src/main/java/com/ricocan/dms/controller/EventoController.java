package com.ricocan.dms.controller;

import com.ricocan.dms.model.*;
import com.ricocan.dms.repository.EventoRepository;
import com.ricocan.dms.repository.JefaturaAreaRepository;
import com.ricocan.dms.service.AreaService;
import com.ricocan.dms.service.PlantaService;
import com.ricocan.dms.service.SeccionService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reportes")
public class EventoController {

    @Autowired private EventoRepository eventoRepository;
    @Autowired private JefaturaAreaRepository jefaturaAreaRepository;
    @Autowired private PlantaService plantaService;
    @Autowired private AreaService areaService;
    @Autowired private SeccionService seccionService;

    @GetMapping("/areas/{plantaId}")
    @ResponseBody
    public List<Area> obtenerAreasPorPlanta(@PathVariable int plantaId) {
        return areaService.listarPorIdPlanta(plantaId);
    }

    @GetMapping("/secciones/{idArea}")
    @ResponseBody
    public List<Seccion> obtenerSeccionesPorArea(@PathVariable int idArea) {
        return seccionService.listarPorArea(idArea);
    }

    @GetMapping("/llenar")
    public String mostrarFormulario(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("evento", new Evento());
        model.addAttribute("plantas", plantaService.obtenerTodas());
        model.addAttribute("areas", areaService.listarTodas());
        model.addAttribute("secions", seccionService.listarSeccionTodas());
        model.addAttribute("usuario", usuario);
        return "formulario_evento";
    }

    @PostMapping("/llenar")
    public String guardarEvento(@ModelAttribute Evento evento,
                                @RequestParam("area") String areaNombre,
                                HttpSession session,
                                Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        evento.setUsuario(usuario);
        evento.setFecha(LocalDate.now());
        evento.setHora(LocalTime.now().withSecond(0).withNano(0));
        evento.setArea(areaNombre);

        if ("JEFATURA".equals(usuario.getRol()) && "Producción".equalsIgnoreCase(areaNombre)) {
            if (evento.getDescargo() == null || evento.getDescargo().isBlank() || evento.getFechaLevantamiento() == null) {
                model.addAttribute("error", "Los campos de descargo y fecha de levantamiento son obligatorios.");
                model.addAttribute("evento", evento);
                model.addAttribute("plantas", plantaService.obtenerTodas());
                model.addAttribute("areas", areaService.listarTodas());
                model.addAttribute("secions", seccionService.listarSeccionTodas());
                model.addAttribute("usuario", usuario);
                return "formulario_evento";
            }
        }

        eventoRepository.save(evento);
        return "redirect:/reportes/todos";
    }

    @GetMapping("/todos")
    public String listarEventos(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        List<Evento> eventos;

        if ("JEFATURA".equals(usuario.getRol())) {
            List<JefaturaArea> jefaturas = jefaturaAreaRepository.findByUsuarioId(usuario.getId());
            Set<String> areasAsignadas = jefaturas.stream()
                    .map(j -> j.getArea().getArea().trim().toLowerCase())
                    .collect(Collectors.toSet());

            eventos = eventoRepository.findAll().stream()
                    .filter(e -> e.getArea() != null && areasAsignadas.contains(e.getArea().trim().toLowerCase()))
                    .filter(e -> !"LEVANTADO".equalsIgnoreCase(e.getEstado())) // Ocultar levantados para JEFATURA
                    .collect(Collectors.toList());

        } else if ("AUXSEGURIDAD".equals(usuario.getRol())) {
            eventos = eventoRepository.findAll().stream()
                    .filter(e -> !"LEVANTADO".equalsIgnoreCase(e.getEstado())) // Ocultar levantados para AUXSEGURIDAD
                    .collect(Collectors.toList());

        } else {
            eventos = eventoRepository.findAll(); // ADMIN y USUARIO ven todo
        }

        model.addAttribute("eventos", eventos);
        model.addAttribute("usuario", usuario);
        return "lista_eventos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarEvento(@PathVariable Long id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || !"ADMIN".equals(usuario.getRol())) {
            return "redirect:/reportes/todos";
        }
        eventoRepository.deleteById(id);
        return "redirect:/reportes/todos";
    }

    @PostMapping("/eliminar-todos")
    @Transactional
    public String eliminarTodos(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || !"ADMIN".equals(usuario.getRol())) {
            return "redirect:/reportes/todos";
        }
        eventoRepository.deleteAll();
        eventoRepository.resetAutoIncrement();
        return "redirect:/reportes/todos";
    }

    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id, @RequestParam String estado, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || !"AUXSEGURIDAD".equals(usuario.getRol())) {
            return "redirect:/login";
        }
        eventoRepository.actualizarEstado(id, estado);
        return "redirect:/reportes/todos";
    }

    @GetMapping("/editar/{id}")
    public String editarEvento(@PathVariable Long id, Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        Optional<Evento> eventoOpt = eventoRepository.findById(id);
        if (eventoOpt.isEmpty()) return "redirect:/reportes/todos";

        Evento evento = eventoOpt.get();

        if ("JEFATURA".equals(usuario.getRol())) {
            List<JefaturaArea> jefaturas = jefaturaAreaRepository.findByUsuarioId(usuario.getId());
            Set<String> areasAsignadas = jefaturas.stream()
                .map(j -> j.getArea().getArea().trim().toLowerCase())
                .collect(Collectors.toSet());

            if (!areasAsignadas.contains(evento.getArea().trim().toLowerCase())) {
                return "redirect:/reportes/todos";
            }

            model.addAttribute("soloDescargo", true);
        }

        model.addAttribute("evento", evento);
        model.addAttribute("usuario", usuario);
        return "formulario_editar_evento";
    }

    @PostMapping("/editar/{id}")
    public String actualizarEventoJefatura(@PathVariable Long id,
                                           @RequestParam("descargo") String descargo,
                                           @RequestParam("fechaLevantamiento") String fechaLevantamientoStr,
                                           HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || !"JEFATURA".equals(usuario.getRol())) return "redirect:/login";

        Optional<Evento> eventoOpt = eventoRepository.findById(id);
        if (eventoOpt.isPresent()) {
            Evento evento = eventoOpt.get();

            List<JefaturaArea> jefaturas = jefaturaAreaRepository.findByUsuarioId(usuario.getId());
            Set<String> areasAsignadas = jefaturas.stream()
                .map(j -> j.getArea().getArea().trim().toLowerCase())
                .collect(Collectors.toSet());

            if (!areasAsignadas.contains(evento.getArea().trim().toLowerCase())) {
                return "redirect:/reportes/todos";
            }

            evento.setDescargo(descargo);
            if (!fechaLevantamientoStr.isBlank()) {
                evento.setFechaLevantamiento(LocalDate.parse(fechaLevantamientoStr));
            }

            eventoRepository.save(evento);
        }

        return "redirect:/reportes/todos";
    }

    @GetMapping("/exportar-pdf/{id}")
    public void exportarPDF(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Optional<Evento> eventoOpt = eventoRepository.findById(id);
        if (eventoOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Reporte no encontrado");
            return;
        }

        Evento evento = eventoOpt.get();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_" + evento.getId() + ".pdf");

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            com.lowagie.text.Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.BLACK);
            com.lowagie.text.Font lineaDecorativa = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.GRAY);
            com.lowagie.text.Font cabeceraFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            com.lowagie.text.Font cuerpoFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
            com.lowagie.text.Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, Color.GRAY);

            document.add(new Paragraph("Reporte de Seguridad", tituloFont));
            document.add(new Paragraph("──────────────────────────────────────────────────────────────", lineaDecorativa));

            PdfPTable tabla = new PdfPTable(new float[]{2, 6});
            tabla.setWidthPercentage(100);
            tabla.setSpacingBefore(10f);

            String[][] datos = {
                {"ID", evento.getId().toString()},
                {"Planta", evento.getPlanta()},
                {"Área", evento.getArea()},
                {"Sección", evento.getSeccion()},
                {"Ubicación", evento.getUbicacion()},
                {"Fecha", evento.getFecha().toString()},
                {"Hora", evento.getHora().toString()},
                {"Descripción", evento.getDescripcion()},
                {"Usuario", evento.getUsuario().getUsername()},
                {"Descargo", evento.getDescargo() != null ? evento.getDescargo() : "—"},
                {"Fecha Levantamiento", evento.getFechaLevantamiento() != null ? evento.getFechaLevantamiento().toString() : "—"},
                {"Estado", evento.getEstado() != null ? evento.getEstado() : "PENDIENTE"}
            };

            for (String[] fila : datos) {
                PdfPCell celdaTitulo = new PdfPCell(new Phrase(fila[0], cabeceraFont));
                celdaTitulo.setBackgroundColor(new Color(52, 152, 219));
                tabla.addCell(celdaTitulo);

                PdfPCell celdaValor = new PdfPCell(new Phrase(fila[1], cuerpoFont));
                celdaValor.setBackgroundColor(new Color(245, 245, 245));
                tabla.addCell(celdaValor);
            }

            document.add(tabla);
            document.add(new Paragraph("Generado por el sistema DMS Ricocan - " + LocalDate.now(), footerFont));
        } catch (DocumentException e) {
            throw new IOException("Error al generar el PDF", e);
        } finally {
            document.close();
        }
    }

    @GetMapping("/exportar-excel")
    public void exportarExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=reportes_eventos.xlsx");

        List<Evento> eventos = eventoRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Eventos");

        CellStyle headerStyle = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
        String[] columnas = {
            "ID", "Planta", "Área", "Sección", "Ubicación",
            "Fecha", "Hora", "Descripción", "Usuario",
            "Descargo", "Fecha Levantamiento", "Estado"
        };
        for (int i = 0; i < columnas.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (Evento e : eventos) {
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(e.getId());
            row.createCell(1).setCellValue(e.getPlanta());
            row.createCell(2).setCellValue(e.getArea());
            row.createCell(3).setCellValue(e.getSeccion());
            row.createCell(4).setCellValue(e.getUbicacion());
            row.createCell(5).setCellValue(e.getFecha().toString());
            row.createCell(6).setCellValue(e.getHora().toString());
            row.createCell(7).setCellValue(e.getDescripcion());
            row.createCell(8).setCellValue(e.getUsuario().getUsername());
            row.createCell(9).setCellValue(e.getDescargo() != null ? e.getDescargo() : "");
            row.createCell(10).setCellValue(e.getFechaLevantamiento() != null ? e.getFechaLevantamiento().toString() : "");
            row.createCell(11).setCellValue(e.getEstado() != null ? e.getEstado() : "PENDIENTE");
        }

        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("")
    public String redireccionarAReportesTodos() {
        return "redirect:/reportes/todos";
    }
}
