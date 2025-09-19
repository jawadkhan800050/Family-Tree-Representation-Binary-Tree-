import java.io.*;

public class FileHandler {
    public static boolean saveTree(FamilyTree tree, String path) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(tree);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static FamilyTree loadTree(String path) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            return (FamilyTree) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
