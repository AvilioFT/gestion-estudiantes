package pe.edu.gestionestudiantes.dao;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import pe.edu.gestionestudiantes.modelo.Estudiante;

@ApplicationScoped
public class EstudianteDAO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource(lookup = "jdbc/GestionEstudiantes")
    private DataSource dataSource;

    // CONSULTAR TODOS LOS ESTUDIANTES
    public List<Estudiante> listar() throws SQLException {

        List<Estudiante> lista = new ArrayList<>();

        String sql = """
            SELECT id, codigo, dni, nombres, apellidos,
                   correo, telefono, estado
            FROM estudiantes
            ORDER BY id DESC
        """;

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Estudiante e = new Estudiante();

                e.setId(rs.getLong("id"));
                e.setCodigo(rs.getString("codigo"));
                e.setDni(rs.getString("dni"));
                e.setNombres(rs.getString("nombres"));
                e.setApellidos(rs.getString("apellidos"));
                e.setCorreo(rs.getString("correo"));
                e.setTelefono(rs.getString("telefono"));
                e.setEstado(rs.getString("estado"));

                lista.add(e);
            }
        }

        return lista;
    }

    // REGISTRAR ESTUDIANTE
    public void insertar(Estudiante e) throws SQLException {

        String sql = """
            INSERT INTO estudiantes
            (codigo, dni, nombres, apellidos,
             correo, telefono, estado)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, e.getCodigo());
            ps.setString(2, e.getDni());
            ps.setString(3, e.getNombres());
            ps.setString(4, e.getApellidos());
            ps.setString(5, e.getCorreo());
            ps.setString(6, e.getTelefono());
            ps.setString(7, e.getEstado());

            ps.executeUpdate();
        }
    }

    // ACTUALIZAR ESTUDIANTE
    public void actualizar(Estudiante e) throws SQLException {

        String sql = """
            UPDATE estudiantes SET
                codigo = ?,
                dni = ?,
                nombres = ?,
                apellidos = ?,
                correo = ?,
                telefono = ?,
                estado = ?
            WHERE id = ?
        """;

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, e.getCodigo());
            ps.setString(2, e.getDni());
            ps.setString(3, e.getNombres());
            ps.setString(4, e.getApellidos());
            ps.setString(5, e.getCorreo());
            ps.setString(6, e.getTelefono());
            ps.setString(7, e.getEstado());
            ps.setLong(8, e.getId());

            ps.executeUpdate();
        }
    }

    // ELIMINAR ESTUDIANTE
    public void eliminar(Long id) throws SQLException {

        String sql = "DELETE FROM estudiantes WHERE id = ?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}