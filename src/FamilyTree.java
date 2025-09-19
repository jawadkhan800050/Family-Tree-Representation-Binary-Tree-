import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FamilyTree implements Serializable {
    public person root;

    public FamilyTree(String name, String gender) {
        root = new person(name, gender);
    }

    public boolean addChild(String parentName, String childName, String gender, boolean isLeft) {
        person parent = search(root, parentName);
        if (parent == null) return false;

        person child = new person(childName, gender);
        if (isLeft) parent.leftchild = child;
        else parent.rightchild = child;
        return true;
    }

    public person search(person node, String name) {
        if (node == null) return null;
        if (node.name.equalsIgnoreCase(name)) return node;
        person left = search(node.leftchild, name);
        if (left != null) return left;
        return search(node.rightchild, name);
    }

    public String preorder(person node) {
        if (node == null) return "";
        return node.name + " " + preorder(node.leftchild) + preorder(node.rightchild);
    }

    public String inorder(person node) {
        if (node == null) return "";
        return inorder(node.leftchild) + node.name + " " + inorder(node.rightchild);
    }

    public String postorder(person node) {
        if (node == null) return "";
        return postorder(node.leftchild) + postorder(node.rightchild) + node.name + " ";
    }

    public List<String> getAncestryPath(String name) {
        List<String> path = new ArrayList<>();
        if (findPath(root, name, path)) return path;
        return new ArrayList<>();
    }

    private boolean findPath(person node, String name, List<String> path) {
        if (node == null) return false;
        path.add(node.name);
        if (node.name.equalsIgnoreCase(name)) return true;
        if (findPath(node.leftchild, name, path) || findPath(node.rightchild, name, path)) return true;
        path.remove(path.size() - 1);
        return false;
    }
}
