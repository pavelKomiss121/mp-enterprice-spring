/* @MENTEE_POWER (C)2026 */
package ru.mentee.blog.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(long id) {
        super("Post not found: " + id);
    }
}
