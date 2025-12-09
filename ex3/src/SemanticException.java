/**
 * Exception thrown when a semantic error is encountered during semantic analysis.
 * Contains the line number where the error occurred.
 */
public class SemanticException extends Exception {
    private int lineNumber;
    
    public SemanticException(String message, int lineNumber) {
        super(message);
        this.lineNumber = lineNumber;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
}

