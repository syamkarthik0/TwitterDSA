
package com.auth.cache;

import com.auth.model.User; // Assuming User is the model class you provided

public class TreeNode {
    private User user;     // Reference to the User object
    private TreeNode left; // Left child in the BST
    private TreeNode right; // Right child in the BST

    // Constructor for TreeNode, takes a User object
    public TreeNode(User user) {
        this.user = user;
        this.left = null;
        this.right = null;
    }

    // Getter and setter for user, left, right
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TreeNode getLeft() {
        return left;
    }

    public void setLeft(TreeNode left) {
        this.left = left;
    }

    public TreeNode getRight() {
        return right;
    }

    public void setRight(TreeNode right) {
        this.right = right;
    }
}
