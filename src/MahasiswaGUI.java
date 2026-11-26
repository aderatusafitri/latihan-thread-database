import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MahasiswaGUI extends JFrame {
    private final MahasiswaDAO dao = new MahasiswaDAO();

    private JTextField txtNim = new JTextField(15);
    private JTextField txtNama = new JTextField(15);
    private JTextField txtJurusan = new JTextField(15);

    private JButton btnSimpan = new JButton("Simpan");
    private JButton btnUbah = new JButton("Ubah");
    private JButton btnHapus = new JButton("Hapus");
    private JButton btnMuat = new JButton("Muat Data");

    private JLabel lblStatus = new JLabel("Siap");

    private DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "NIM", "Nama", "Jurusan"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) { return false; }
    };
    private JTable table = new JTable(model);
    private int selectedId = -1;

    // Tugas DB yang dijalankan di thread terpisah
    interface DbTask { void run() throws Exception; }

    public MahasiswaGUI() {
        setTitle("Latihan Thread & Database");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.add(new JLabel("NIM"));     form.add(txtNim);
        form.add(new JLabel("Nama"));    form.add(txtNama);
        form.add(new JLabel("Jurusan")); form.add(txtJurusan);

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        tombol.add(btnSimpan);
        tombol.add(btnUbah);
        tombol.add(btnHapus);
        tombol.add(btnMuat);

        JPanel atas = new JPanel(new BorderLayout());
        atas.add(form, BorderLayout.CENTER);
        atas.add(tombol, BorderLayout.SOUTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = table.getSelectedRow();
            if (row < 0) return;
            selectedId = (int) model.getValueAt(row, 0);
            txtNim.setText((String) model.getValueAt(row, 1));
            txtNama.setText((String) model.getValueAt(row, 2));
            txtJurusan.setText((String) model.getValueAt(row, 3));
        });

        add(atas, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(lblStatus, BorderLayout.SOUTH);

        btnSimpan.addActionListener(e -> simpan());
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
        btnMuat.addActionListener(e -> jalankan("Loading...", "Data dimuat", () -> {}));

        setSize(600, 450);
        setLocationRelativeTo(null);

        // Muat data saat aplikasi dibuka
        jalankan("Loading...", "Siap", () -> {});
    }

    private void simpan() {
        String nim = txtNim.getText().trim();
        String nama = txtNama.getText().trim();
        String jurusan = txtJurusan.getText().trim();

        if (nim.isEmpty() || nama.isEmpty()) {
            lblStatus.setText("NIM dan Nama wajib diisi");
            return;
        }
        jalankan("Saving...", "Tersimpan!",
                () -> dao.insert(new Mahasiswa(0, nim, nama, jurusan)));
    }

    private void ubah() {
        if (selectedId < 0) {
            lblStatus.setText("Pilih data di tabel dulu");
            return;
        }
        String nim = txtNim.getText().trim();
        String nama = txtNama.getText().trim();
        String jurusan = txtJurusan.getText().trim();
        int id = selectedId;

        if (nim.isEmpty() || nama.isEmpty()) {
            lblStatus.setText("NIM dan Nama wajib diisi");
            return;
        }
        jalankan("Updating...", "Data diubah!",
                () -> dao.update(new Mahasiswa(id, nim, nama, jurusan)));
    }

    private void hapus() {
        if (selectedId < 0) {
            lblStatus.setText("Pilih data di tabel dulu");
            return;
        }
        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Yakin hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) return;

        int id = selectedId;
        jalankan("Deleting...", "Data dihapus!", () -> dao.delete(id));
    }

    // Pola: klik -> disable tombol + label -> DB di thread -> invokeLater -> UI
    private void jalankan(String loadingText, String suksesText, DbTask task) {
        setTombol(false);
        lblStatus.setText(loadingText);

        new Thread(() -> {
            try {
                Thread.sleep(1000); // simulasi proses lama, hapus baris ini setelah demo
                task.run();
                List<Mahasiswa> data = dao.getAll();

                SwingUtilities.invokeLater(() -> {
                    isiTabel(data);
                    kosongkanForm();
                    lblStatus.setText(suksesText);
                    setTombol(true);
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    lblStatus.setText("Gagal: " + ex.getMessage());
                    setTombol(true);
                });
            }
        }).start();
    }

    private void isiTabel(List<Mahasiswa> data) {
        model.setRowCount(0);
        for (Mahasiswa m : data) {
            model.addRow(new Object[]{m.getId(), m.getNim(), m.getNama(), m.getJurusan()});
        }
    }

    private void kosongkanForm() {
        txtNim.setText("");
        txtNama.setText("");
        txtJurusan.setText("");
        selectedId = -1;
        table.clearSelection();
    }

    private void setTombol(boolean aktif) {
        btnSimpan.setEnabled(aktif);
        btnUbah.setEnabled(aktif);
        btnHapus.setEnabled(aktif);
        btnMuat.setEnabled(aktif);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MahasiswaGUI().setVisible(true));
    }
}