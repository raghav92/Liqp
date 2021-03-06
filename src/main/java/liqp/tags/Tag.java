package liqp.tags;

import liqp.LValue;
import liqp.nodes.LNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Tags are used for the logic in a template.
 */
public abstract class Tag extends LValue {

    public enum Statement {

        BREAK, CONTINUE;

        @Override
        public String toString() {
            return "";
        }
    }
    
    private static final Tag[] STANDARD_TAGS = new Tag[]{
        new Assign(),
        new Case(),
        new Capture(),
        new Comment(),
        new Cycle(),
        new For(),
        new If(),
        new Include(),
        new Raw(),
        new Tablerow(),
        new Unless()
    };

    /**
     * A map holding all tags.
     */
    private static final ThreadLocal<Map<String, Tag>> TAGS = new ThreadLocal<Map<String, Tag>>() {
        @Override
        protected Map<String, Tag> initialValue() {
            Map<String, Tag> map = new HashMap<String, Tag>();
            for(Tag t: STANDARD_TAGS) {
                map.put(t.name, t);
            }
            return map;
        }
    };

    /**
     * The name of this tag.
     */
    public final String name;

    /**
     * Used for all package protected tags in the liqp.tags-package
     * whose name is their class name lower cased.
     */
    protected Tag() {
        this.name = this.getClass().getSimpleName().toLowerCase();
    }

    /**
     * Creates a new instance of a Tag.
     *
     * @param name
     *         the name of the tag.
     */
    public Tag(String name) {
        this.name = name;
    }

    /**
     * Retrieves a filter with a specific name.
     *
     * @param name
     *         the name of the filter to retrieve.
     *
     * @return a filter with a specific name.
     */
    public static Tag getTag(String name) {

        Tag tag = TAGS.get().get(name);

        if (tag == null) {
            throw new RuntimeException("unknown tag: " + name);
        }

        return tag;
    }

    /**
     * Registers a new tag.
     *
     * @param tag
     *         the tag to be registered.
     */
    public static void registerTag(Tag tag) {
        TAGS.get().put(tag.name, tag);
    }

    /**
     * Renders this tag.
     *
     * @param context
     *         the context (variables) with which this
     *         node should be rendered.
     * @param nodes
     *         the nodes of this tag is created with. See
     *         the file `src/grammar/LiquidWalker.g` to see
     *         how each of the tags is created.
     *
     * @return an Object denoting the rendered AST.
     */
    public abstract Object render(Map<String, Object> context, LNode... nodes);
}
