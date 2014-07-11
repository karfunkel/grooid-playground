package groovy.android.factory

/**
 * This returns a mutable java.util.Collection of some sort, to which items are added.
 * Copy of groovy.swing.factory.CollectionFactory only to get rid of module dependency to swing module
 */
public class CollectionFactory extends AbstractFactory {

    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        FactoryBuilderSupport.checkValueIsNull(value, name);
        if (attributes.isEmpty()) {
            return new ArrayList();
        } else {
            def item = attributes.entrySet().iterator().next();
            throw new MissingPropertyException(
                    "The builder element '$name' is a collections element and accepts no attributes",
                    item.key as String, item.value as Class);
        }
    }

    public void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        parent.add(child)
    }
}
