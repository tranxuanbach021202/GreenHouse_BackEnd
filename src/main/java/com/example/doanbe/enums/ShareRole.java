package com.example.doanbe.enums;

public enum ShareRole {
    VIEWER("VIEWER"),         // Chỉ xem
    COMMENTER("COMMENTER"),   // Xem và comment
    EDITOR("EDITOR"),         // Xem, sửa, không xóa
    OWNER("OWNER");          // Toàn quyền

    private String role;

    ShareRole(String role) {
        this.role = role;
    }
}
