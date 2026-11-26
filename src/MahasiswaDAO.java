import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MahasiswaDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/latihan-thread-database_db";
    private static final String USER = "root";
    private static final String PASS = "";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // CREATE
    public void insert(Mahasiswa m) throws SQLException {
        String sql = "INSERT INTO mahasiswa (nim, nama, jurusan) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getNim());
            ps.setString(2, m.getNama());
            ps.setString(3, m.getJurusan());
            ps.executeUpdate();
        }
    }

    // READ
    public List<Mahasiswa> getAll() throws SQLException {
        List<Mahasiswa> list = new ArrayList<>();
        String sql = "SELECT * FROM mahasiswa";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Mahasiswa(
                    rs.getInt("id"),
                    rs.getString("nim"),
                    rs.getString("nama"),
                    rs.getString("jurusan")));
            }
        }
        return list;
    }

    // UPDATE (pakai transaction)
    public void update(Mahasiswa m) throws SQLException {
        String sql = "UPDATE mahasiswa SET nim=?, nama=?, jurusan=? WHERE id=?";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, m.getNim());
                ps.setString(2, m.getNama());
                ps.setString(3, m.getJurusan());
                ps.setInt(4, m.getId());
                ps.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // DELETE (pakai transaction)
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM mahasiswa WHERE id=?";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}