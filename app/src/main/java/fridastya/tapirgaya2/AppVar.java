package fridastya.tapirgaya2;

/**
 * Created by SUBMIT on 31/05/2017.
 */

public class AppVar {

    //URL to our login.php file
    public static final String LOGIN_URL = "http://brendanattalia.pe.student.pens.ac.id/atappintar/process/api/login-mobile.php";
    public static final String GETDATA_URL = "http://brendanattalia.pe.student.pens.ac.id/atappintar/process/api/getdata-mobile.php";
    public static final String UPDATEDATAMODE_URL = "http://brendanattalia.pe.student.pens.ac.id/atappintar/process/api/update-modeatap-mobile.php";
    public static final String UPDATEDATASTATUS_URL = "http://brendanattalia.pe.student.pens.ac.id/atappintar/process/api/update-statusatap-mobile.php";

    //Keys for email and password as defined in our $_POST['key'] in login.php
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_MODEATAP = "modeatap";
    public static final String KEY_KONDISIATAP = "kondisiatap";

    public static final String KEY_USERGET = "username";

    //If server response is equal to this that means login is successful
    public static final String LOGIN_SUCCESS = "success";
    public static final String LOGIN_DATA = "data";

}
