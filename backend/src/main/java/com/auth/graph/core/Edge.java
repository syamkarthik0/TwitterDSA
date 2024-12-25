package com.auth.graph.core;

import java.util.Objects;

/**
 * Represents a directed edge in the graph between two nodes.
 */
public class Edge<T> {
    private final Node<T> source;
    private final Node<T> target;

    public Edge(Node<T> source, Node<T> target) {
        this.source = source;
        this.target = target;
    }

    public Node<T> getSource() {
        return source;
    }

    public Node<T> getTarget() {
        return target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge<?> edge = (Edge<?>) o;
        return Objects.equals(source, edge.source) && Objects.equals(target, edge.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }
}
