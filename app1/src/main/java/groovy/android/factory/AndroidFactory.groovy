package groovy.android.factory

import android.content.Context
import android.view.View
import groovy.android.AndroidBuilder

import java.lang.reflect.Method

class AndroidFactory extends AbstractFactory {
    Class beanClass
    protected boolean leaf
    protected Map<Method, List<Method>> listenerAccessors = [:]
    static List groovyObjectMethodNames = GroovyObject.methods*.name

    protected Closure newInstance

    AndroidFactory(Class beanClass, Closure newInstance = null) {
        this(beanClass, false, newInstance)
    }

    AndroidFactory(Class beanClass, boolean leaf, Closure newInstance = null) {
        this.beanClass = beanClass
        this.leaf = leaf
        this.newInstance = newInstance ?: this.&defaultNewInstance
        this.newInstance.delegate = this
        initListenerMethods()
    }

    def defaultNewInstance(AndroidBuilder builder, Context context, Object name, Object value, Map attributes) {
        def instance
        if (context)
            instance = beanClass.newInstance(context)
        else
            instance = beanClass.newInstance()

        return instance
    }

    public boolean isLeaf() {
        return leaf
    }

    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        if (builder instanceof AndroidBuilder) {
            if (value instanceof GString) value = value as String
            if (FactoryBuilderSupport.checkValueIsType(value, name, beanClass)) {
                return value
            }

            Context context = attributes.remove('context')
            if (context != null) {
                builder.context.put(builder.ANDROID_PARENT_CONTEXT, builder.findPropertyInContextStack(builder.ANDROID_CONTEXT))
                builder.context.put(builder.ANDROID_CONTEXT, context)
            }

            context = builder.findPropertyInContextStack(AndroidBuilder.ANDROID_CONTEXT)
            if (context == null)
                throw new IllegalArgumentException("Please provide a $Context.canonicalName as attribute context in this or a previous layer")

            addContextProperties(builder.context, beanClass)

            return this.newInstance(builder, context, name, value, attributes)
        } else
            throw new IllegalArgumentException("This factory can only be used with instances of $AndroidBuilder.canonicalName")
    }

    @Override
    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        if (node instanceof View) {
            List padding = attributes.remove('padding') ?: [attributes.remove('paddingStart') ?: attributes.remove('paddingLeft') ?: 0, attributes.remove('paddingTop') ?: 0, attributes.remove('paddingEnd') ?: attributes.remove('paddingRight') ?: 0, attributes.remove('paddingBottom') ?: 0]
            if (padding?.size() > 0) {
                if (padding.size() == 1)
                    padding = padding * 4
                else if (padding.size() == 2)
                    padding = padding * 2
                else if (padding.size() == 3)
                    padding << padding[1]
                node.setPaddingRelative(*padding)
            }

            def tag = attributes.remove('tag')
            if (tag != null)
                node.setTag(tag)

            // Set all attributes with numbers as keys as keyed tags.
            attributes.findAll { k, v -> (k instanceof Number) }.each { Number n, v ->
                node.setTag(n.intValue(), v)
                attributes.remove(n)
            }
        }
        return true
    }

    def postCompleteNode(FactoryBuilderSupport factory, Object parent, Object node) {
        return node
    }

    void addContextEnums(context, Class<? extends Enum> cls) {
        def contextEnums = [:]
        cls.enumConstants.each {
            def name = it.name()
            contextEnums.put(name, it)
        }
        context.putAll(contextEnums)
    }

    void addContextProperties(context, Class cls) {
        def contextProps = [:]
        cls.fields.each {
            def name = it.name
            if (name.toUpperCase() == name)
                contextProps.put(name, cls."$name")
        }
        context.putAll(contextProps)
    }

    protected void asId(Map attributes, String key) {
        if (attributes.containsKey(key)) {
            def view = attributes[key]
            if (view instanceof View)
                attributes[key] = view.id
        }
    }

    static void setCheckedProperty(Class type, Map<String, Object> attributes, String property,
                                   def target, Closure converter = null) {
        def value = attributes.remove(property)
        if (type.isInstance(value)) {
            if (converter != null)
                value = converter(value)
            target."$property" = value
        } else if (value != null)
            throw new IllegalArgumentException("$property must be of type $type.canonicalName")
    }

    void initListenerMethods() {
        beanClass.methods.findAll { it.name.endsWith('Listener') && it.parameterTypes.size() == 1 }.each { method ->
            if (method.name.startsWith('addOn') || method.name.startsWith('setOn'))
                addListenerMethod(method)
        }
    }

    void addListenerMethod(String name, Class... parameterTypes) {
        addListenerMethod(beanClass.getMethod(name, parameterTypes))
    }

    void addListenerMethod(Method method) {
        listenerAccessors[method] = method.parameterTypes[0].declaredMethods.inject([]) { l, m ->
            m.name.contains('$') ? l : groovyObjectMethodNames.contains(m.name) ? l : l << m
        }
    }

    // Dynamically adding addOn and setOn listeners
    static listenersAttributeDelegate(FactoryBuilderSupport builder, Object node, Map<String, Object> attributes) {
        AndroidFactory factory = builder.currentFactory
        factory.listenerAccessors.each { Method adder, List<Method> listenerMethods ->
            def listener = [:]
            listenerMethods.each { method ->
                def closure = attributes.remove(method.name)
                if (closure)
                    listener[method.name] = closure
            }
            if (listener)
                adder.invoke(node, listener.asType(adder.parameterTypes[0]))
        }
    }
}
