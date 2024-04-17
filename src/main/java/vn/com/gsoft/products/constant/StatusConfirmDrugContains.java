package vn.com.gsoft.products.constant;

public class StatusConfirmDrugContains {
    // Action
    //  0 là chưa xác nhận, 1 không xác định, 2 là xác nhận, 3 là thuốc mới, 4 là đã xác thực hệ thống.
    public static final long NOTCONFIRM = 0L;
    public static final long UNDEFINED = 1L;
    public static final long CONFIRMED = 2L;
    public static final long ADDNEW = 3L;
    public static final long MAPPEDBYSYSTEM = 4L;
}
