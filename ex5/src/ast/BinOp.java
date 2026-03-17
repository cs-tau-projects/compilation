package ast;

public enum BinOp {
	PLUS,    // +
	MINUS,   // -
	TIMES,   // *
	DIVIDE,  // /
	LT,      // <
	GT,      // >
	EQ;      // =
	
	/**
	 * Convert the operator to a printable string
	 */
	public String toString() {
		switch (this) {
			case PLUS:   return "+";
			case MINUS:  return "-";
			case TIMES:  return "*";
			case DIVIDE: return "/";
			case LT:     return "<";
			case GT:     return ">";
			case EQ:     return "=";
			default:     return "?";
		}
	}
}

