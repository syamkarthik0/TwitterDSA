package com.auth.graph.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A thread-safe implementation of a directed graph using adjacency lists.
 * @param <T> The type of data stored in the graph's nodes
 */
public class DirectedGraph<T> {
    private final Map<Long, Node<T>> nodes;
    private final Map<Long, Set<Long>> outgoingEdges; // source -> [targets]
    private final Map<Long, Set<Long>> incomingEdges; // target -> [sources]

    public DirectedGraph() {
        this.nodes = new ConcurrentHashMap<>();
        this.outgoingEdges = new ConcurrentHashMap<>();
        this.incomingEdges = new ConcurrentHashMap<>();
    }

    /**
     * Adds a node to the graph.
     */
    public synchronized void addNode(Node<T> node) {
        nodes.put(node.getId(), node);
        outgoingEdges.putIfAbsent(node.getId(), ConcurrentHashMap.newKeySet());
        incomingEdges.putIfAbsent(node.getId(), ConcurrentHashMap.newKeySet());
    }

    /**
     * Adds a directed edge from source to target.
     */
    public synchronized void addEdge(long sourceId, long targetId) {
        if (!nodes.containsKey(sourceId) || !nodes.containsKey(targetId)) {
            throw new IllegalArgumentException("Both nodes must exist in the graph");
        }
        outgoingEdges.get(sourceId).add(targetId);
        incomingEdges.get(targetId).add(sourceId);
    }

    /**
     * Removes a directed edge from source to target.
     */
    public synchronized void removeEdge(long sourceId, long targetId) {
        if (!nodes.containsKey(sourceId) || !nodes.containsKey(targetId)) {
            throw new IllegalArgumentException("Both nodes must exist in the graph");
        }
        outgoingEdges.get(sourceId).remove(targetId);
        incomingEdges.get(targetId).remove(sourceId);
    }

    /**
     * Gets all nodes that have edges pointing to them from the source node.
     */
    public Set<Node<T>> getOutgoingNodes(long sourceId) {
        return outgoingEdges.getOrDefault(sourceId, Collections.emptySet())
                .stream()
                .map(nodes::get)
                .collect(Collectors.toSet());
    }

    /**
     * Gets all nodes that have edges pointing to the target node.
     */
    public Set<Node<T>> getIncomingNodes(long targetId) {
        return incomingEdges.getOrDefault(targetId, Collections.emptySet())
                .stream()
                .map(nodes::get)
                .collect(Collectors.toSet());
    }

    /**
     * Checks if there is a directed edge from source to target.
     */
    public boolean hasEdge(long sourceId, long targetId) {
        return outgoingEdges.containsKey(sourceId) && 
               outgoingEdges.get(sourceId).contains(targetId);
    }

    /**
     * Gets node by ID.
     */
    public Optional<Node<T>> getNode(long id) {
        return Optional.ofNullable(nodes.get(id));
    }

    /**
     * Gets all nodes that are two hops away from the source node.
     * Useful for friend-of-friend recommendations.
     */
    public Set<Node<T>> getTwoHopNodes(long sourceId) {
        Set<Node<T>> result = new HashSet<>();
        Set<Long> firstHop = outgoingEdges.getOrDefault(sourceId, Collections.emptySet());
        
        for (Long friendId : firstHop) {
            result.addAll(getOutgoingNodes(friendId));
        }
        
        // Remove the original node and its direct connections
        result.removeIf(node -> node.getId() == sourceId || firstHop.contains(node.getId()));
        return result;
    }

    /**
     * Gets mutual connections between two nodes.
     */
    public Set<Node<T>> getMutualConnections(long node1Id, long node2Id) {
        Set<Long> connections1 = outgoingEdges.getOrDefault(node1Id, Collections.emptySet());
        Set<Long> connections2 = outgoingEdges.getOrDefault(node2Id, Collections.emptySet());
        
        Set<Long> mutual = new HashSet<>(connections1);
        mutual.retainAll(connections2);
        
        return mutual.stream()
                .map(nodes::get)
                .collect(Collectors.toSet());
    }
}
