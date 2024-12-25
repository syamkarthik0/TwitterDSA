package com.auth.graph.core;

import java.util.Objects;

/**
 * Represents a node in the graph with a generic type for the node's data.
 */
public class Node<T> {
    private final T data;
    private final long id;

    public Node(long id, T data) {
        this.id = id;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
        return id == node.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
