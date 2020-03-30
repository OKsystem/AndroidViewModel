package eu.inloop.viewmodel;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

public class ProxyViewHelper {

    private static final class ProxyDummyClass {
    }

    private static final ProxyDummyClass sDummyClass = new ProxyDummyClass();
    private static final Class[] sInterfaces = new Class[1];

    private ProxyViewHelper() {
    }

    @SuppressWarnings("unchecked")
    @NonNull
    static <T> T init(@NonNull Class<?> in) {
        sInterfaces[0] = in;
        return (T) Proxy.newProxyInstance(sDummyClass.getClass().getClassLoader(), sInterfaces, sInvocationHandler);
    }

    @Nullable
    public static Class<?> getGenericType(@NonNull Class<?> in, @NonNull Class<?> whichExtends) {
        final Type genericSuperclass = in.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            final Type[] typeArgs = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
            for (Type arg : typeArgs) {
                if (arg instanceof ParameterizedType) {
                    arg = ((ParameterizedType) arg).getRawType();
                }
                if (arg instanceof Class<?>) {
                    final Class<?> argClass = (Class<?>) arg;
                    if (whichExtends.isAssignableFrom(argClass)) {
                        return argClass;
                    }
                }
            }
        }
        return null;
    }

    private static final InvocationHandler sInvocationHandler = (proxy, method, args) -> {
        if (String.class == method.getReturnType()) {
            return "";
        }
        else if (Integer.class == method.getReturnType()) {
            return 0;
        }
        else if (int.class == method.getReturnType()) {
            return 0;
        }
        else if (Float.class == method.getReturnType()) {
            return 0f;
        }
        else if (float.class == method.getReturnType()) {
            return 0f;
        }
        else if (Double.class == method.getReturnType()) {
            return (double) 0;
        }
        else if (double.class == method.getReturnType()) {
            return (double) 0;
        }
        else if (Boolean.class == method.getReturnType()) {
            return Boolean.FALSE;
        }
        else if (boolean.class == method.getReturnType()) {
            return false;
        }
        else {
            return null;
        }
    };

}
