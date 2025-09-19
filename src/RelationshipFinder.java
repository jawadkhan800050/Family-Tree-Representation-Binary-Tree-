import java.util.List;

public class RelationshipFinder {
    private FamilyTree tree;

    public RelationshipFinder(FamilyTree tree) {
        this.tree = tree;
    }

    public String relation(String a, String b) {
        List<String> pathA = tree.getAncestryPath(a);

        List<String> pathB = tree.getAncestryPath(b);

        if (pathA == null || pathB == null) return "No relation found (one or both not present)";

        // Find LCA index
        int i = 0;
        while (i < pathA.size() && i < pathB.size() && pathA.get(i).equalsIgnoreCase(pathB.get(i))) i++;
        int distA = pathA.size() - i; // steps down from LCA to a
        int distB = pathB.size() - i; // steps down from LCA to b

        if (distA == 0 && distB == 1) return "Parent";
        if (distA == 1 && distB == 0) return "Child";
        if (distA == 0 && distB == 2) return "Grandparent";
        if (distA == 2 && distB == 0) return "Grandchild";

        if (distA == 1 && distB == 1) return "Siblings";

        // Uncle / Aunt / Niece / Nephew
        if (distA == 1 && distB == 2) return "Uncle/Aunt";
        if (distA == 2 && distB == 1) return "Niece/Nephew";

        // Cousins (first cousins)
        if (distA == 2 && distB == 2) return "Cousins";

        // More distant ancestor/descendant
        if (distA == 0 && distB > 2) return "Ancestor";
        if (distB == 0 && distA > 2) return "Descendant";

        // Fallback for more distant relations
        return "Distant relation (approx)";
    }
}
