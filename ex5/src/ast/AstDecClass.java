package ast;

import types.*;
import symboltable.*;
import java.util.*;

public class AstDecClass extends AstNode {
	public String id;
	public String parentId; // can be null
	public AstFieldList fields;

	public AstDecClass(String id, String parentId, AstFieldList fields, int lineNumber) {
		serialNumber = AstNodeSerialNumber.getFresh();
		this.id = id;
		this.parentId = parentId;
		this.fields = fields;
		this.lineNumber = lineNumber;
	}

	public void printMe() {
		System.out.format("AST CLASS DEC NODE: %s\n", id);
		if (parentId != null) {
			System.out.format("EXTENDS: %s\n", parentId);
		}
		if (fields != null)
			fields.printMe();

		String label = (parentId != null) ? String.format("CLASS\n%s\nEXTENDS %s", id, parentId)
				: String.format("CLASS\n%s", id);

		AstGraphviz.getInstance().logNode(serialNumber, label);

		if (fields != null)
			AstGraphviz.getInstance().logEdge(serialNumber, fields.serialNumber);
	}

	public Type semantMe() throws SemanticException {
		TypeClass parentClass = null;

		// name and duplicate check
		TypeUtils.checkNotReservedKeyword(id, lineNumber);

		if (SymbolTable.getInstance().find(id) != null) {
			throw new SemanticException("class " + id + " already exists", lineNumber);
		}

		// parent check
		if (parentId != null) {
			Type parentType = SymbolTable.getInstance().find(parentId);
			if (parentType == null) {
				throw new SemanticException("parent class " + parentId + " does not exist", lineNumber);
			}
			if (!parentType.isClass()) {
				throw new SemanticException("parent " + parentId + " is not a class", lineNumber);
			}
			parentClass = (TypeClass) parentType;

			if (checkCircularInheritance(id, parentClass)) {
				throw new SemanticException("circular inheritance detected for class " + id, lineNumber);
			}
		}

		// placeholder
		TypeClass classType = new TypeClass(parentClass, id, null);
		SymbolTable.getInstance().enter(id, classType);

		// collect members
		TypeList classMembers = null;
		Set<String> memberNames = new HashSet<>();
		List<AstDecFunc> methodsToProcess = new ArrayList<>();

		for (AstFieldList it = fields; it != null; it = it.tail) {
			if (it.head.decVar != null) {
				// Field variable
				AstDecVar fieldVar = it.head.decVar;

				// Check if name already exists in current class
				if (memberNames.contains(fieldVar.id)) {
					throw new SemanticException("duplicate field " + fieldVar.id + " in class " + id,
							fieldVar.lineNumber);
				}

				// Check for shadowing - field can't have same name as ANY inherited member
				Type inheritedMember = findInParent(parentClass, fieldVar.id);
				if (inheritedMember != null) {
					throw new SemanticException("field " + fieldVar.id + " shadows inherited member",
							fieldVar.lineNumber);
				}

				memberNames.add(fieldVar.id);

				// Check that field type exists
				Type fieldType = SymbolTable.getInstance().find(fieldVar.type.typeName);
				if (fieldType == null) {
					throw new SemanticException("non existing type " + fieldVar.type.typeName, fieldVar.lineNumber);
				}

				// Check that field type is not void
				if (fieldType instanceof TypeVoid) {
					throw new SemanticException("field cannot have void type", fieldVar.lineNumber);
				}

				classMembers = new TypeList(new TypeField(fieldType, fieldVar.id, fieldVar.exp), classMembers);
			} else if (it.head.decFunc != null) {
				// Method
				AstDecFunc method = it.head.decFunc;

				// Check if method name already exists in current class
				// (catches duplicate methods and field-method name conflicts)
				if (memberNames.contains(method.funcName)) {
					throw new SemanticException("duplicate member name: " + method.funcName, method.lineNumber);
				}

				memberNames.add(method.funcName);

				// Save method for processing later (after class is registered)
				methodsToProcess.add(method);

				// Validate method signature and handle overriding
				Type retType = SymbolTable.getInstance().find(method.returnType.typeName);
				if (retType == null) {
					throw new SemanticException("non existing return type " + method.returnType.typeName,
							method.lineNumber);
				}

				// Build parameter type list in correct order
				TypeList paramTypes = TypeUtils.buildParameterTypeList(method.params, lineNumber);

				TypeFunction methodType = new TypeFunction(retType, method.funcName, paramTypes);

				// Check for overriding or shadowing
				Type inheritedMember = findInParent(parentClass, method.funcName);
				if (inheritedMember != null) {
					// Check if inherited member is a field (shadowing not allowed)
					if (inheritedMember instanceof TypeField) {
						throw new SemanticException("method " + method.funcName + " shadows inherited field",
								method.lineNumber);
					}

					// It's a method - validate override signature matches exactly
					if (inheritedMember instanceof TypeFunction) {
						TypeFunction inheritedMethod = (TypeFunction) inheritedMember;

						// Check return type matches
						if (inheritedMethod.returnType != retType) {
							throw new SemanticException(
									"method " + method.funcName + " override has different return type",
									method.lineNumber);
						}

						// Check parameter types match exactly (same types, same order)
						if (!parameterListsMatch(inheritedMethod.params, paramTypes)) {
							throw new SemanticException(
									"method " + method.funcName + " override has different parameter types",
									method.lineNumber);
						}

						// Override is valid - signature matches exactly
					}
				}

				classMembers = new TypeList(methodType, classMembers);
			}
		}

		// 5. Update the class type with members
		classType.dataMembers = classMembers;

		// 6. Perform semantic analysis on method bodies within class context
		SymbolTable.getInstance().beginScope();

		// Add inherited members to scope (all inherited members are visible)
		if (parentClass != null) {
			addInheritedMembersToScope(parentClass);
		}

		// Process fields and methods in order, adding each to scope before processing
		// next
		// This ensures members can only reference earlier-defined members
		int methodIndex = 0;
		for (AstFieldList it = fields; it != null; it = it.tail) {
			if (it.head.decVar != null) {
				// Field - add to scope (type already validated above)
				AstDecVar fieldVar = it.head.decVar;
				Type fieldType = SymbolTable.getInstance().find(fieldVar.type.typeName);
				SymbolTable.getInstance().enter(fieldVar.id, new TypeField(fieldType, fieldVar.id, fieldVar.exp));
			} else if (it.head.decFunc != null) {
				// Method - process body, then add to scope
				AstDecFunc method = methodsToProcess.get(methodIndex++);
				method.className = id;
				method.semantMe(true);

				// Add method to scope after processing (for later methods to reference)
				Type retType = SymbolTable.getInstance().find(method.returnType.typeName);
				TypeList paramTypes = TypeUtils.buildParameterTypeList(method.params, lineNumber);
				SymbolTable.getInstance().enter(method.funcName,
						new TypeFunction(retType, method.funcName, paramTypes));
			}
		}

		SymbolTable.getInstance().endScope();

		// Class declarations don't have a value type context
		return null;
	}

	// Helper: Check for circular inheritance by walking up the hierarchy
	private boolean checkCircularInheritance(String className, TypeClass parent) {
		TypeClass current = parent;
		while (current != null) {
			if (current.name.equals(className)) {
				return true;
			}
			current = current.father;
		}
		return false;
	}

	// Helper: Look up member in parent classes
	private Type findInParent(TypeClass parentClass, String memberName) {
		if (parentClass == null) {
			return null;
		}

		for (TypeList it = parentClass.dataMembers; it != null; it = it.tail) {
			if (it.head.name.equals(memberName)) {
				return it.head;
			}
		}

		// Recursively search in parent's parent
		return findInParent(parentClass.father, memberName);
	}

	// Helper: Structural comparison of parameter lists
	private boolean parameterListsMatch(TypeList list1, TypeList list2) {
		// Both null - match
		if (list1 == null && list2 == null) {
			return true;
		}

		// One null, one not - no match
		if (list1 == null || list2 == null) {
			return false;
		}

		// Check if head types match
		if (list1.head != list2.head) {
			return false;
		}

		// Recursively check tails
		return parameterListsMatch(list1.tail, list2.tail);
	}

	// Helper: Recursively load inherited members into scope
	private void addInheritedMembersToScope(TypeClass parentClass) {
		if (parentClass == null) {
			return;
		}

		// First add grandparent's members (so they can be overridden)
		addInheritedMembersToScope(parentClass.father);

		// Then add parent's members
		for (TypeList it = parentClass.dataMembers; it != null; it = it.tail) {
			SymbolTable.getInstance().enter(it.head.name, it.head);
		}
	}

	public temp.Temp irMe() {
		TypeClass classType = (TypeClass) SymbolTable.getInstance().find(id);
		if (classType == null)
			return null;

		java.util.List<String> methods = buildVtable(classType);
		ir.Ir.getInstance().AddIrCommand(new ir.IrCommandVtable(id, methods));

		for (AstFieldList it = fields; it != null; it = it.tail) {
			if (it.head.decFunc != null) {
				it.head.decFunc.className = id;
				it.head.decFunc.irMe();
			}
		}

		return null;
	}

	public static java.util.List<String> buildVtable(TypeClass classType) {
		java.util.List<String> methods = new java.util.ArrayList<>();
		if (classType == null)
			return methods;

		if (classType.father != null) {
			methods.addAll(buildVtable(classType.father));
		}

		java.util.List<Type> ownMembers = new java.util.ArrayList<>();
		for (TypeList it = classType.dataMembers; it != null; it = it.tail) {
			ownMembers.add(it.head);
		}
		// Members are in reverse declaration order in dataMembers list,
		// so we reverse them back to original declaration order.
		java.util.Collections.reverse(ownMembers);

		for (Type member : ownMembers) {
			if (member instanceof TypeFunction) {
				String funcName = member.name;
				String methodLabel = "Method_" + classType.name + "_" + funcName;

				boolean overridden = false;
				for (int i = 0; i < methods.size(); i++) {
					if (methods.get(i).endsWith("_" + funcName)) {
						methods.set(i, methodLabel);
						overridden = true;
						break;
					}
				}
				if (!overridden) {
					methods.add(methodLabel);
				}
			}
		}
		return methods;
	}
}