import java.io.Serializable;

public class person implements Serializable {
    public String name;
    public String gender;
    public person leftchild;
    public person rightchild;

    public person(String name, String gender) {
        this.name = name;
        this.gender = gender;
        this.leftchild = null;
        this.rightchild = null;
    }
}
