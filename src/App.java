import java.util.List;

public class App {
    public static void main(String[] args) {
        MahasiswaDAO dao = new MahasiswaDAO();
        try {
            dao.insert(new Mahasiswa(0, "F1D02410033", "Ade Ratu Safitri", "Teknik Informatika"));
            List<Mahasiswa> data = dao.getAll();
            for (Mahasiswa m : data) {
                System.out.println(m.getId() + " | " + m.getNim() + " | " + m.getNama() + " | " + m.getJurusan());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}