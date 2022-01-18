import java.security.Provider;
import java.util.Vector;


public class Room {

    String title;//방제목
    int count;
    String boss;
    Vector<Service> userV;// userV: 같은 방에 접속한 Client정보 저장

    public Room(){
        userV = new Vector<>();
    }

}
