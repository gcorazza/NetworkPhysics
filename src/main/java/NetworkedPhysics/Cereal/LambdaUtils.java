package NetworkedPhysics.Cereal;

import java.util.function.Consumer;
import java.util.function.Function;

public class LambdaUtils {
    @SuppressWarnings("unchecked")
    private static <A,E extends Throwable> A thr(Throwable e) throws E {
        throw (E) e;
    }
    public static <A> A silentThrow(Throwable t) {
        return thr(t);
    }
    @FunctionalInterface
    public interface ThrowingConsumer<T> {
        void accept(T t) throws Exception;
    }
    @FunctionalInterface
    public interface ThrowingFunction<A,B> {
        B apply(A t) throws Exception;
    }
    public static <T> Consumer<T> silentConsumer(ThrowingConsumer<T> c) {
        return t -> {
            try {
                c.accept(t);
            } catch (Exception e) {
                silentThrow(e);
            }
        };
    }
    public static <A,B> Function<A,B> silentFunction(ThrowingFunction<A,B> c) {
        return t -> {
            try {
                return c.apply(t);
            } catch (Exception e) {
                return silentThrow(e);
            }
        };
    }

}
