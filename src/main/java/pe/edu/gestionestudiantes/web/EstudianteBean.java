package pe.edu.gestionestudiantes.web;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pe.edu.gestionestudiantes.dao.EstudianteDAO;
import pe.edu.gestionestudiantes.modelo.Estudiante;

@Named("estudianteBean")
@ViewScoped
public class EstudianteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER =
            Logger.getLogger(EstudianteBean.class.getName());

    private static final DateTimeFormatter HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    @Inject
    private EstudianteDAO estudianteDAO;

    private List<Estudiante> estudiantes = new ArrayList<>();

    private Estudiante estudiante = new Estudiante();

    private String ultimaCarga = "—";

    // CARGAR ESTUDIANTES AL ABRIR LA PÁGINA
    @PostConstruct
    public void init() {
        cargarEstudiantes();
    }

    public void cargarEstudiantes() {
        try {
            estudiantes = estudianteDAO.listar();
            ultimaCarga = LocalTime.now().format(HORA);

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al consultar estudiantes", ex);
            mostrarMensaje(
                    FacesMessage.SEVERITY_ERROR,
                    "Error",
                    "No se pudieron cargar los estudiantes."
            );
        }
    }

    // PREPARAR UN NUEVO ESTUDIANTE
    public void nuevo() {
        estudiante = new Estudiante();
    }

    // SELECCIONAR UN ESTUDIANTE PARA EDITAR
    public void editar(Estudiante seleccionado) {
        estudiante = new Estudiante(seleccionado);
    }

    // GUARDAR O ACTUALIZAR
    public void guardar() {

        try {

            if (estudiante.getId() == null) {

                estudianteDAO.insertar(estudiante);

                mostrarMensaje(
                        FacesMessage.SEVERITY_INFO,
                        "Registrado",
                        "El estudiante fue registrado correctamente."
                );

            } else {

                estudianteDAO.actualizar(estudiante);

                mostrarMensaje(
                        FacesMessage.SEVERITY_INFO,
                        "Actualizado",
                        "El estudiante fue actualizado correctamente."
                );
            }

            cargarEstudiantes();
            estudiante = new Estudiante();

        } catch (SQLException ex) {

            LOGGER.log(Level.SEVERE, "Error al guardar estudiante", ex);

            FacesContext.getCurrentInstance().validationFailed();

            mostrarMensaje(
                    FacesMessage.SEVERITY_ERROR,
                    "Error al guardar",
                    "Verifica que el código, DNI y correo no estén duplicados."
            );
        }
    }

    // ELIMINAR ESTUDIANTE
    public void eliminar(Estudiante seleccionado) {

        try {

            estudianteDAO.eliminar(seleccionado.getId());

            mostrarMensaje(
                    FacesMessage.SEVERITY_INFO,
                    "Eliminado",
                    "El estudiante fue eliminado correctamente."
            );

            cargarEstudiantes();

        } catch (SQLException ex) {

            LOGGER.log(Level.SEVERE, "Error al eliminar estudiante", ex);

            mostrarMensaje(
                    FacesMessage.SEVERITY_ERROR,
                    "No se pudo eliminar",
                    "El estudiante podría tener matrículas relacionadas."
            );
        }
    }

    // MOSTRAR MENSAJES EN PRIMEFACES
    private void mostrarMensaje(
            FacesMessage.Severity tipo,
            String titulo,
            String detalle) {

        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(tipo, titulo, detalle)
        );
    }

    // GETTERS Y SETTERS

    public List<Estudiante> getEstudiantes() {
        return estudiantes;
    }

    public String getUltimaCarga() {
        return ultimaCarga;
    }

    public long getTotal() {
        return estudiantes.size();
    }

    public long getActivos() {
        return estudiantes.stream()
                .filter(e -> "ACTIVO".equals(e.getEstado()))
                .count();
    }

    public long getInactivos() {
        return estudiantes.stream()
                .filter(e -> "INACTIVO".equals(e.getEstado()))
                .count();
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }
}