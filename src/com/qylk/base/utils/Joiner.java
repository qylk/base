package com.qylk.base.utils;

import java.io.IOException;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;

public class Joiner {
    /**
     * Returns a joiner which automatically places {@code separator} between consecutive elements.
     */
    public static Joiner on(String separator) {
        return new Joiner(separator);
    }

    /**
     * Returns a joiner which automatically places {@code separator} between consecutive elements.
     */
    public static Joiner on(char separator) {
        return new Joiner(String.valueOf(separator));
    }

    private final String separator;

    private Joiner(String separator) {
        this.separator = checkNotNull(separator);
    }

    private Joiner(Joiner prototype) {
        this.separator = prototype.separator;
    }

    /**
     * Appends the string representation of each of {@code parts}, using the previously configured
     * separator between each, to {@code appendable}.
     */
    public <A extends Appendable> A appendTo(A appendable, Iterable<?> parts) throws IOException {
        return appendTo(appendable, parts.iterator());
    }

    /**
     * Appends the string representation of each of {@code parts}, using the previously configured
     * separator between each, to {@code appendable}.
     *
     * @since 11.0
     */
    public <A extends Appendable> A appendTo(A appendable, Iterator<?> parts) throws IOException {
        checkNotNull(appendable);
        if (parts.hasNext()) {
            appendable.append(toString(parts.next()));
            while (parts.hasNext()) {
                appendable.append(separator);
                appendable.append(toString(parts.next()));
            }
        }
        return appendable;
    }

    /**
     * Appends the string representation of each of {@code parts}, using the previously configured
     * separator between each, to {@code appendable}.
     */
    public final <A extends Appendable> A appendTo(A appendable, Object[] parts) throws IOException {
        return appendTo(appendable, Arrays.asList(parts));
    }

    /**
     * Appends to {@code appendable} the string representation of each of the remaining arguments.
     */
    public final <A extends Appendable> A appendTo(
            A appendable, Object first, Object second, Object... rest)
            throws IOException {
        return appendTo(appendable, iterable(first, second, rest));
    }

    /**
     * Appends the string representation of each of {@code parts}, using the previously configured
     * separator between each, to {@code builder}. Identical to {@link #appendTo(Appendable,
     * Iterable)}, except that it does not throw {@link java.io.IOException}.
     */
    public final StringBuilder appendTo(StringBuilder builder, Iterable<?> parts) {
        return appendTo(builder, parts.iterator());
    }

    /**
     * Appends the string representation of each of {@code parts}, using the previously configured
     * separator between each, to {@code builder}. Identical to {@link #appendTo(Appendable,
     * Iterable)}, except that it does not throw {@link java.io.IOException}.
     *
     * @since 11.0
     */
    public final StringBuilder appendTo(StringBuilder builder, Iterator<?> parts) {
        try {
            appendTo((Appendable) builder, parts);
        } catch (IOException impossible) {
            throw new AssertionError(impossible);
        }
        return builder;
    }

    /**
     * Appends the string representation of each of {@code parts}, using the previously configured
     * separator between each, to {@code builder}. Identical to {@link #appendTo(Appendable,
     * Iterable)}, except that it does not throw {@link java.io.IOException}.
     */
    public final StringBuilder appendTo(StringBuilder builder, Object[] parts) {
        return appendTo(builder, Arrays.asList(parts));
    }

    /**
     * Appends to {@code builder} the string representation of each of the remaining arguments.
     * Identical to {@link #appendTo(Appendable, Object, Object, Object...)}, except that it does not
     * throw {@link java.io.IOException}.
     */
    public final StringBuilder appendTo(
            StringBuilder builder, Object first, Object second, Object... rest) {
        return appendTo(builder, iterable(first, second, rest));
    }

    /**
     * Returns a string containing the string representation of each of {@code parts}, using the
     * previously configured separator between each.
     */
    public final String join(Iterable<?> parts) {
        return join(parts.iterator());
    }

    /**
     * Returns a string containing the string representation of each of {@code parts}, using the
     * previously configured separator between each.
     *
     * @since 11.0
     */
    public final String join(Iterator<?> parts) {
        return appendTo(new StringBuilder(), parts).toString();
    }

    /**
     * Returns a string containing the string representation of each of {@code parts}, using the
     * previously configured separator between each.
     */
    public final String join(Object[] parts) {
        return join(Arrays.asList(parts));
    }

    /**
     * Returns a string containing the string representation of each argument, using the previously
     * configured separator between each.
     */
    public final String join(Object first, Object second, Object... rest) {
        return join(iterable(first, second, rest));
    }

    /**
     * Returns a joiner with the same behavior as this one, except automatically substituting {@code
     * nullText} for any provided null elements.
     */
    public Joiner useForNull(final String nullText) {
        checkNotNull(nullText);
        return new Joiner(this) {
            @Override
            CharSequence toString(Object part) {
                return (part == null) ? nullText : Joiner.this.toString(part);
            }

            @Override
            public Joiner useForNull(String nullText) {
                throw new UnsupportedOperationException("already specified useForNull");
            }

            @Override
            public Joiner skipNulls() {
                throw new UnsupportedOperationException("already specified useForNull");
            }
        };
    }

    /**
     * Returns a joiner with the same behavior as this joiner, except automatically skipping over any
     * provided null elements.
     */
    public Joiner skipNulls() {
        return new Joiner(this) {
            @Override
            public <A extends Appendable> A appendTo(A appendable, Iterator<?> parts)
                    throws IOException {
                checkNotNull(appendable, "appendable");
                checkNotNull(parts, "parts");
                while (parts.hasNext()) {
                    Object part = parts.next();
                    if (part != null) {
                        appendable.append(Joiner.this.toString(part));
                        break;
                    }
                }
                while (parts.hasNext()) {
                    Object part = parts.next();
                    if (part != null) {
                        appendable.append(separator);
                        appendable.append(Joiner.this.toString(part));
                    }
                }
                return appendable;
            }

            @Override
            public Joiner useForNull(String nullText) {
                throw new UnsupportedOperationException("already specified skipNulls");
            }
        };
    }

    CharSequence toString(Object part) {
        checkNotNull(part);  // checkNotNull for GWT (do not optimize).
        return (part instanceof CharSequence) ? (CharSequence) part : part.toString();
    }

    private static Iterable<Object> iterable(
            final Object first, final Object second, final Object[] rest) {
        checkNotNull(rest);
        return new AbstractList<Object>() {
            @Override
            public int size() {
                return rest.length + 2;
            }

            @Override
            public Object get(int index) {
                switch (index) {
                    case 0:
                        return first;
                    case 1:
                        return second;
                    default:
                        return rest[index - 2];
                }
            }
        };
    }


    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference an object reference
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference    an object reference
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     *                     string using {@link String#valueOf(Object)}
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }
}
