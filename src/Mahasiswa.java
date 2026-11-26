public class Mahasiswa {
    private int id;
    private String nim;
    private String nama;
    private String jurusan;

    public Mahasiswa(int id, String nim, String nama, String jurusan) {
        this.id = id;
        this.nim = nim;
        this.nama = nama;
        this.jurusan = jurusan;
    }

    public int getId() { return id; }
    public String getNim() { return nim; }
    public String getNama() { return nama; }
    public String getJurusan() { return jurusan; }
}