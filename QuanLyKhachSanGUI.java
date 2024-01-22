import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;


class Phong {
    private int soHieu;
    private boolean daDat;
    private String hoTenKhach;
    private String sdtKhach;
    private String diaChiKhach;
    private Date ngayDat;

    public Phong(int soHieu) {
        this.soHieu = soHieu;
        this.daDat = false;
        this.hoTenKhach = "";
        this.sdtKhach = "";
        this.diaChiKhach = "";
        this.ngayDat = null;
    }

    public int getSoHieu() {
        return soHieu;
    }

    public boolean isDaDat() {
        return daDat;
    }

    public String getHoTenKhach() {
        return hoTenKhach;
    }

    public String getSdtKhach() {
        return sdtKhach;
    }

    public String getDiaChiKhach() {
        return diaChiKhach;
    }

    public Date getNgayDat() {
        return ngayDat;
    }

    public void datPhong(String hoTenKhach, String sdtKhach, String diaChiKhach, Date ngayDat) {
        if (hoTenKhach.isEmpty() || sdtKhach.isEmpty() || diaChiKhach.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin khách hàng.");
            return;
        }

        if (!sdtKhach.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(null, "Số điện thoại không hợp lệ. Vui lòng nhập 10 chữ số.");
            return;
        }

        this.hoTenKhach = hoTenKhach;
        this.sdtKhach = sdtKhach;
        this.diaChiKhach = diaChiKhach;
        this.daDat = true;
        this.ngayDat = ngayDat;
        if (this.isDaDat()) {
            JOptionPane.showMessageDialog(null, "Phòng " + soHieu + " đã được đặt cho " + hoTenKhach + ".");
        }

        // Lưu vào cơ sở dữ liệu
        luuDatPhongVaoCSDL();
        
        JOptionPane.showMessageDialog(null, "Phòng " + soHieu + " đã được đặt cho " + hoTenKhach + ".");
    }
    public void huyDatPhong() {
	    this.daDat = false;
	    this.hoTenKhach = "";
	    this.sdtKhach = "";
	    this.diaChiKhach = "";
	    this.ngayDat = null;
	
	    // Xóa khỏi cơ sở dữ liệu
	    xoaDatPhongKhoiCSDL();
	    
	    JOptionPane.showMessageDialog(null, "Đã hủy đặt phòng " + soHieu + ".");
	}

    public void capNhatThongTin(String hoTenKhach, String sdtKhach, String diaChiKhach) {
        if (hoTenKhach.isEmpty() || sdtKhach.isEmpty() || diaChiKhach.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin khách hàng.");
            return;
        }

        if (!sdtKhach.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(null, "Số điện thoại không hợp lệ. Vui lòng nhập 10 chữ số.");
            return;
        }

        this.hoTenKhach = hoTenKhach;
        this.sdtKhach = sdtKhach;
        this.diaChiKhach = diaChiKhach;

        JOptionPane.showMessageDialog(null, "Thông tin đã được cập nhật thành công.");
        // Cập nhật thông tin trong cơ sở dữ liệu
        capNhatDuLieu();
    }

    private void capNhatDuLieu() {
        String jdbcURL = "jdbc:mysql://localhost:3306/quanlikhachsan";
        String username = "lequanghieu";
        String password = "040205";

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            String sql = "UPDATE dat_phong SET ho_ten_khach=?, sdt_khach=?, dia_chi_khach=? WHERE so_hieu=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, hoTenKhach);
                preparedStatement.setString(2, sdtKhach);
                preparedStatement.setString(3, diaChiKhach);
                preparedStatement.setInt(4, soHieu);

                // In ra câu lệnh SQL để kiểm tra
                System.out.println("SQL: " + preparedStatement.toString());

                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(null, "Dữ liệu đã được cập nhật thành công.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật thông tin trong cơ sở dữ liệu: " + e.getMessage());
        }
    }
	private boolean kiemTraTonTai(int soHieu) {
        String jdbcURL = "jdbc:mysql://localhost:3306/quanlikhachsan";
        String username = "lequanghieu";
        String password = "040205";

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            String sql = "SELECT COUNT(*) FROM dat_phong WHERE so_hieu = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, soHieu);
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi kiểm tra sự tồn tại của dữ liệu trong cơ sở dữ liệu.");
            return false;
        }
    }
    private void luuDatPhongVaoCSDL() {
        String jdbcURL = "jdbc:mysql://localhost:3306/quanlikhachsan";
        String username = "lequanghieu";
        String password = "040205";

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            // Kiểm tra sự tồn tại của bản ghi trước khi thêm mới
            if (kiemTraTonTai(soHieu)) {
                int option = JOptionPane.showConfirmDialog(null,
                        "Phòng đã tồn tại trong cơ sở dữ liệu. Bạn muốn cập nhật thông tin không?",
                        "Xác nhận cập nhật", JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    // Thực hiện cập nhật thông tin
                    capNhatDuLieu();
                } else {
                    // Người dùng không muốn cập nhật, reset dữ liệu
                    resetDuLieu();
                    JOptionPane.showMessageDialog(null, "Thông tin không được cập nhật. Dữ liệu đã được reset.");
                }
            } else {
                // Nếu không tồn tại, thêm mới
                String sql = "INSERT INTO dat_phong (so_hieu, ho_ten_khach, sdt_khach, dia_chi_khach, ngay_dat) " +
                             "VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, soHieu);
                    preparedStatement.setString(2, hoTenKhach);
                    preparedStatement.setString(3, sdtKhach);
                    preparedStatement.setString(4, diaChiKhach);
                    preparedStatement.setDate(5, new java.sql.Date(ngayDat.getTime()));
                    
                    // In ra câu lệnh SQL để kiểm tra
                    System.out.println("SQL: " + preparedStatement.toString());
                    
                    preparedStatement.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Dữ liệu đã được thêm mới thành công.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi thực hiện thao tác với cơ sở dữ liệu: " + e.getMessage());
        }
    }


    private void resetDuLieu() {
        int option = JOptionPane.showConfirmDialog(null,
                "Bạn muốn đặt lại dữ liệu của phòng " + soHieu + " không?",
                "Xác nhận đặt lại dữ liệu", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            // Xóa dữ liệu khỏi cơ sở dữ liệu và đặt lại dữ liệu của đối tượng Phong
            xoaDatPhongKhoiCSDL();
            this.daDat = false;
            this.hoTenKhach = "";
            this.sdtKhach = "";
            this.diaChiKhach = "";
            this.ngayDat = null;
        } else {
            // Không đặt lại dữ liệu, thông báo cho người dùng
            JOptionPane.showMessageDialog(null, "Dữ liệu không được đặt lại.");
        }
    }

    private void xoaDatPhongKhoiCSDL() {
        String jdbcURL = "jdbc:mysql://localhost:3306/quanlikhachsan";
        String username = "lequanghieu";
        String password = "040205";

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            String sql = "DELETE FROM dat_phong WHERE so_hieu = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, soHieu);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xóa dữ liệu khỏi cơ sở dữ liệu: " + e.getMessage());
        }
    }

    public String getStatus() {
        if (daDat) {
            return "Đã đặt cho " + hoTenKhach + " - " + sdtKhach;
        } else {
            return "Trống";
        }
    }
}

public class QuanLyKhachSanGUI {
    private static Phong[] danhSachPhong;

    public static void main(String[] args) {
        danhSachPhong = new Phong[20];
        for (int i = 0; i < danhSachPhong.length; i++) {
            danhSachPhong[i] = new Phong(i + 1);
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                connectToDatabase(); // Kết nối đến cơ sở dữ liệu
                docDuLieuTuCSDL(); // Đọc dữ liệu từ cơ sở dữ liệu
                createAndShowGUI();
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                dongKetNoiCSDL(null);
            }
        }));
    }
    
    private static void timKiemPhong() {
        String input = JOptionPane.showInputDialog("Nhập số hiệu hoặc tên khách cần tìm kiếm:");
        if (input != null && !input.isEmpty()) {
            String ketQua = "";
            for (Phong phong : danhSachPhong) {
                if (phong.getSoHieu() == Integer.parseInt(input) || phong.getStatus().toLowerCase().contains(input.toLowerCase())) {
                    ketQua += "Phòng " + phong.getSoHieu() + ": " + phong.getStatus();

                    if (phong.isDaDat()) {
                        ketQua += "\n   Họ tên khách: " + phong.getHoTenKhach();
                        ketQua += "\n   SĐT khách: " + phong.getSdtKhach();
                        ketQua += "\n   Địa chỉ khách: " + phong.getDiaChiKhach();
                        ketQua += "\n   Ngày đặt: " + new SimpleDateFormat("dd/MM/yyyy").format(phong.getNgayDat());
                    }
                    ketQua += "\n\n";
                }
            }
            if (!ketQua.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Kết quả tìm kiếm:\n" + ketQua);
            } else {
                JOptionPane.showMessageDialog(null, "Không tìm thấy kết quả nào phù hợp.");
            }
        }
    }
    private static void capNhatThongTinPhong() {
        try {
            String input = JOptionPane.showInputDialog("Nhập số hiệu phòng cần cập nhật:");
            int soHieu = Integer.parseInt(input);

            if (soHieu >= 1 && soHieu <= danhSachPhong.length) {
                if (danhSachPhong[soHieu - 1].isDaDat()) {
                    String hoTenKhach = JOptionPane.showInputDialog("Nhập họ tên khách:");
                    String sdtKhach = JOptionPane.showInputDialog("Nhập số điện thoại khách:");
                    String diaChiKhach = JOptionPane.showInputDialog("Nhập địa chỉ khách:");

                    // Cập nhật thông tin
                    danhSachPhong[soHieu - 1].capNhatThongTin(hoTenKhach, sdtKhach, diaChiKhach);

                    // Sau khi cập nhật, cập nhật lại thông tin trong cơ sở dữ liệu
                    docDuLieuTuCSDL(); // Đọc lại dữ liệu từ cơ sở dữ liệu để cập nhật danhSachPhong
                } else {
                    JOptionPane.showMessageDialog(null, "Phòng chưa được đặt.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Số hiệu phòng không hợp lệ.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập số hiệu phòng là một số nguyên.");
        }
    }
    private static void connectToDatabase() {
    	String jdbcURL = "jdbc:mysql://localhost:3306/quanlikhachsan";
        String username = "lequanghieu";
        String password = "040205";
        try {
            Connection connection = DriverManager.getConnection(jdbcURL, username, password);
            if (connection != null) {
                System.out.println("Kết nối đến cơ sở dữ liệu thành công!");
                // Thực hiện các thao tác với cơ sở dữ liệu ở đây...
                connection.close(); // Đóng kết nối sau khi sử dụng
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Kết nối đến cơ sở dữ liệu thất bại!");
        }
    }
    private static void docDuLieuTuCSDL() {
        String jdbcURL = "jdbc:mysql://localhost:3306/quanlikhachsan";
        String username = "lequanghieu";
        String password = "040205";

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            String sql = "SELECT * FROM dat_phong";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int soHieu = resultSet.getInt("so_hieu");
                    String hoTenKhach = resultSet.getString("ho_ten_khach");
                    String sdtKhach = resultSet.getString("sdt_khach");
                    String diaChiKhach = resultSet.getString("dia_chi_khach");
                    Date ngayDat = resultSet.getDate("ngay_dat");

                    danhSachPhong[soHieu - 1].datPhong(hoTenKhach, sdtKhach, diaChiKhach, ngayDat);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi đọc dữ liệu từ cơ sở dữ liệu.");
        }
    }
    private static void dongKetNoiCSDL(Connection connection) {
        // Đóng kết nối đến cơ sở dữ liệu
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Đã đóng kết nối đến cơ sở dữ liệu.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi đóng kết nối đến cơ sở dữ liệu.");
        }
    }
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Quản Lý Khách Sạn");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(2100, 1200);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imageIcon = new ImageIcon("C:\\Users\\admin\\OneDrive\\Pictures\\Saved Pictures\\background.jpg");
                Image image = imageIcon.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4; // Dòng 4 để đưa các nút và nhãn đến góc dưới bên phải
        gbc.anchor = GridBagConstraints.SOUTHEAST; // Đặt anchor về phía Đông-Nam
        gbc.insets = new Insets(10, 1000, 10, 0); // Điều chỉnh insets nếu cần thiết

        JButton xemButton = createStyledButton("Xem Thông Tin Phòng", 50, true);
        xemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xemThongTinPhong();
            }
        });
        panel.add(xemButton, gbc);

        JButton datButton = createStyledButton("Đặt Phòng", 50, true);
        datButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                datPhong();
            }
        });
        gbc.gridy = 5; // Dòng 5
        panel.add(datButton, gbc);

        JButton huyDatButton = createStyledButton("Hủy Đặt Phòng", 50, true);
        huyDatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                huyDatPhong();
            }
        });
        gbc.gridy = 6; // Dòng 6
        panel.add(huyDatButton, gbc);

        JButton thoatButton = createStyledButton("Thoát", 50, true);
        thoatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        JButton timKiemButton = createStyledButton("Tìm Kiếm", 50, true);
        timKiemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timKiemPhong();
            }
        });
        JButton capNhatButton = createStyledButton("Cập Nhật Thông Tin Phòng", 50, true);
        capNhatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                capNhatThongTinPhong();
            }
        });
        gbc.gridy = 7; // Dòng 7 (hoặc điều chỉnh tùy theo giao diện của bạn)
        panel.add(capNhatButton, gbc);
        gbc.gridy = 8; // Dòng 9 (hoặc điều chỉnh tùy theo giao diện của bạn)
        gbc.anchor = GridBagConstraints.LAST_LINE_END;
        panel.add(timKiemButton, gbc);
        gbc.gridy = 9; // Dòng 8
        panel.add(thoatButton, gbc);

        JLabel lienHeLabel = new JLabel("Liên hệ: lequanghieu360@gmail.com", SwingConstants.CENTER);
        lienHeLabel.setForeground(Color.BLACK);
        lienHeLabel.setFont(new Font(lienHeLabel.getFont().getName(), Font.PLAIN, 20));
        gbc.gridy = 8; // Dòng 8
        gbc.anchor = GridBagConstraints.SOUTHWEST; // Đặt anchor về phía Tây-Nam
        gbc.insets = new Insets(300, 0, 10, 10); // Điều chỉnh insets để nút chạm mép dưới và mép trái của panel
        panel.add(lienHeLabel, gbc);

        frame.add(panel);
        frame.setVisible(true);
        
        JLabel titleLabel = new JLabel("All Stars Hotel");
        titleLabel.setForeground(new Color(255, 205, 255)); // Màu chữ
        titleLabel.setFont(new Font("Lucida Handwriting", Font.PLAIN, 100)); // Font và kích thước chữ
        gbc.gridx = 0; // Đặt cột 0
        gbc.gridy = 0; // Đặt dòng 0
        gbc.gridwidth = 2; // Sử dụng 2 ô ngang
        gbc.anchor = GridBagConstraints.NORTHWEST; // Đặt anchor về phía Tây-Bắc
        gbc.insets = new Insets(0, 10, 10, 10); // Điều chỉnh insets nếu cần thiết
        panel.add(titleLabel, gbc);
    }

    private static JButton createStyledButton(String text, int fontSize, boolean isHighlight) {
        JButton button = new JButton(text);
        button.setForeground(new Color(255, 255, 255)); // Màu của viền chữ

        // Thay đổi màu nền của nút
        Color backgroundColor = isHighlight ? new Color(255, 105, 180) : new Color(255, 105, 180);
        button.setBackground(backgroundColor);

        button.setBorderPainted(true);
        button.setOpaque(true);

        // Tạo viền bằng cách kết hợp LineBorder và MatteBorder
        int borderSize = 2; // Điều chỉnh kích thước của viền
        button.setBorder(new CompoundBorder(
                new MatteBorder(borderSize, borderSize, borderSize, borderSize, new Color(255, 102, 102)), // Màu của nền để tạo hiệu ứng phát sáng
                new LineBorder(new Color(255, 51, 102), 1, true)
        ));

        button.setFont(new Font(button.getFont().getName(), Font.PLAIN, fontSize));

        return button;
    }
    private static void xemThongTinPhong() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String info = "---- Thông Tin Phòng ----\n";
        for (Phong phong : danhSachPhong) {
            info += "Phòng " + phong.getSoHieu() + ": " + phong.getStatus();

            if (phong.isDaDat()) {
                info += "\n   Họ tên khách: " + phong.getHoTenKhach();
                info += "\n   SĐT khách: " + phong.getSdtKhach();
                info += "\n   Địa chỉ khách: " + phong.getDiaChiKhach();
                info += "\n   Ngày đặt: " + dateFormat.format(phong.getNgayDat());
            }
            info += "\n";
        }
        JOptionPane.showMessageDialog(null, info);
    }

    private static void datPhong() {
        try {
            String input = JOptionPane.showInputDialog("Nhập số hiệu phòng cần đặt:");
            int soHieu = Integer.parseInt(input);

            if (soHieu >= 1 && soHieu <= danhSachPhong.length) {
                if (!danhSachPhong[soHieu - 1].isDaDat()) {
                    String hoTenKhach = JOptionPane.showInputDialog("Nhập họ tên khách:");
                    String sdtKhach = JOptionPane.showInputDialog("Nhập số điện thoại khách:");
                    String diaChiKhach = JOptionPane.showInputDialog("Nhập địa chỉ khách:");

                    // Kiểm tra và đặt phòng
                    danhSachPhong[soHieu - 1].datPhong(hoTenKhach, sdtKhach, diaChiKhach, new Date());
                } else {
                    JOptionPane.showMessageDialog(null, "Phòng đã được đặt trước đó.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Số hiệu phòng không hợp lệ.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập số hiệu phòng là một số nguyên.");
        }
    }

    private static void huyDatPhong() {
        try {
            String input = JOptionPane.showInputDialog("Nhập số hiệu phòng cần hủy đặt:");
            int soHieu = Integer.parseInt(input);

            if (soHieu >= 1 && soHieu <= danhSachPhong.length) {
                if (danhSachPhong[soHieu - 1].isDaDat()) {
                    danhSachPhong[soHieu - 1].huyDatPhong();
                } else {
                    JOptionPane.showMessageDialog(null, "Phòng chưa được đặt.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Số hiệu phòng không hợp lệ.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập số hiệu phòng là một số nguyên.");
        }
    }
}


