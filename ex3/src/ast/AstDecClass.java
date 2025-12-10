package ast;

import types.*;
import symboltable.*;
import java.util.*;

public class AstDecClass extends AstNode{
    public String id;
    public String parentId; // can be null
    public AstFieldList fields;

    public AstDecClass(String id, String parentId, AstFieldList fields){
        serialNumber = AstNodeSerialNumber.getFresh();
        this.id = id;
        this.parentId = parentId;
        this.fields = fields;
    }

    public void printMe(){
        System.out.format("AST CLASS DEC NODE: %s\n", id);
        if (parentId != null) {
            System.out.format("EXTENDS: %s\n", parentId);
        }
        if (fields != null) fields.printMe();

        String label = (parentId != null) ?
            String.format("CLASS\n%s\nEXTENDS %s", id, parentId) :
            String.format("CLASS\n%s", id);

        AstGraphviz.getInstance().logNode(serialNumber, label);

        if (fields != null) AstGraphviz.getInstance().logEdge(serialNumber, fields.serialNumber);
    }

    public Type semantMe() throws SemanticException
	{
		TypeClass parentClass = null;

		/************************************/
		/* [0a] Check for reserved keyword  */
		/************************************/
		TypeUtils.checkNotReservedKeyword(id, lineNumber);

		/**************************************/
		/* [0b] Check if class name already exists */
		/**************************************/
		if (SymbolTable.getInstance().find(id) != null)
		{
			throw new SemanticException("class " + id + " already exists", lineNumber);
		}

		/**************************************/
		/* [1] Check if parent class exists   */
		/**************************************/
		if (parentId != null)
		{
			Type parentType = SymbolTable.getInstance().find(parentId);
			if (parentType == null)
			{
				throw new SemanticException("parent class " + parentId + " does not exist", lineNumber);
			}
			if (!parentType.isClass())
			{
				throw new SemanticException("parent " + parentId + " is not a class", lineNumber);
			}
			parentClass = (TypeClass) parentType;

			/**************************************/
			/* [2] Check for circular inheritance */
			/**************************************/
			if (checkCircularInheritance(id, parentClass))
			{
				throw new SemanticException("circular inheritance detected for class " + id, lineNumber);
			}
		}

		/********************************************************/
		/* [3] Create a placeholder class type and register it */
		/*     This allows self-referential fields (e.g., IntList tail) */
		/********************************************************/
		TypeClass classType = new TypeClass(parentClass, id, null);
		SymbolTable.getInstance().enter(id, classType);

		/********************************************************/
		/* [4] Build class members list (fields and methods)   */
		/*     Check for overloading, overriding, shadowing    */
		/********************************************************/
		TypeList classMembers = null;
		Set<String> memberNames = new HashSet<>();
		List<AstDecFunc> methodsToProcess = new ArrayList<>();

		for (AstFieldList it = fields; it != null; it = it.tail)
		{
			if (it.head.decVar != null)
			{
				// Field variable
				AstDecVar fieldVar = it.head.decVar;

				// Check if name already exists in current class
				if (memberNames.contains(fieldVar.id))
				{
					throw new SemanticException("duplicate field " + fieldVar.id + " in class " + id, lineNumber);
				}

				// Check for shadowing - field can't have same name as ANY inherited member
				Type inheritedMember = findInParent(parentClass, fieldVar.id);
				if (inheritedMember != null)
				{
					throw new SemanticException("field " + fieldVar.id + " shadows inherited member", lineNumber);
				}

				memberNames.add(fieldVar.id);

				// Check that field type exists
				Type fieldType = SymbolTable.getInstance().find(fieldVar.type.typeName);
				if (fieldType == null)
				{
					throw new SemanticException("non existing type " + fieldVar.type.typeName, lineNumber);
				}

				// Check that field type is not void
				if (fieldType instanceof TypeVoid)
				{
					throw new SemanticException("field cannot have void type", lineNumber);
				}

				classMembers = new TypeList(new TypeField(fieldType, fieldVar.id), classMembers);
			}
			else if (it.head.decFunc != null)
			{
				// Method
				AstDecFunc method = it.head.decFunc;

				// Check if method name already exists in current class
				// (catches duplicate methods and field-method name conflicts)
				if (memberNames.contains(method.funcName))
				{
					throw new SemanticException("duplicate member name: " + method.funcName, lineNumber);
				}

				memberNames.add(method.funcName);

				// Save method for processing later (after class is registered)
				methodsToProcess.add(method);

				// Build method type from signature
				// We need to validate types exist here because we use them for overriding checks
				Type retType = SymbolTable.getInstance().find(method.returnType.typeName);
				if (retType == null)
				{
					throw new SemanticException("non existing return type " + method.returnType.typeName, lineNumber);
				}

				// Build parameter type list in correct order
				TypeList paramTypes = TypeUtils.buildParameterTypeList(method.params, lineNumber);

				TypeFunction methodType = new TypeFunction(retType, method.funcName, paramTypes);

				// Check for overriding or shadowing
				Type inheritedMember = findInParent(parentClass, method.funcName);
				if (inheritedMember != null)
				{
					// Check if inherited member is a field (shadowing not allowed)
					if (inheritedMember instanceof TypeField)
					{
						throw new SemanticException("method " + method.funcName + " shadows inherited field", lineNumber);
					}

					// It's a method - validate override signature matches exactly
					if (inheritedMember instanceof TypeFunction)
					{
						TypeFunction inheritedMethod = (TypeFunction) inheritedMember;

						// Check return type matches
						if (inheritedMethod.returnType != retType)
						{
							throw new SemanticException("method " + method.funcName + " override has different return type", lineNumber);
						}

						// Check parameter types match exactly (same types, same order)
						if (!parameterListsMatch(inheritedMethod.params, paramTypes))
						{
							throw new SemanticException("method " + method.funcName + " override has different parameter types", lineNumber);
						}

						// Override is valid - signature matches exactly
					}
				}

				classMembers = new TypeList(methodType, classMembers);
			}
		}

		/************************************************/
		/* [5] Update the class type with members      */
		/************************************************/
		classType.dataMembers = classMembers;

		/********************************************************/
		/* [5] Now process method bodies in class context      */
		/*     Members can only reference earlier-defined members */
		/********************************************************/
		SymbolTable.getInstance().beginScope();

		// Add inherited members to scope (all inherited members are visible)
		if (parentClass != null)
		{
			addInheritedMembersToScope(parentClass);
		}

		// Process fields and methods in order, adding each to scope before processing next
		// This ensures members can only reference earlier-defined members
		int methodIndex = 0;
		for (AstFieldList it = fields; it != null; it = it.tail)
		{
			if (it.head.decVar != null)
			{
				// Field - add to scope (type already validated above)
				AstDecVar fieldVar = it.head.decVar;
				Type fieldType = SymbolTable.getInstance().find(fieldVar.type.typeName);
				SymbolTable.getInstance().enter(fieldVar.id, new TypeField(fieldType, fieldVar.id));
			}
			else if (it.head.decFunc != null)
			{
				// Method - process body, then add to scope
				AstDecFunc method = methodsToProcess.get(methodIndex++);
				method.semantMe(true);

				// Add method to scope after processing (for later methods to reference)
				Type retType = SymbolTable.getInstance().find(method.returnType.typeName);
				TypeList paramTypes = TypeUtils.buildParameterTypeList(method.params, lineNumber);
				SymbolTable.getInstance().enter(method.funcName, new TypeFunction(retType, method.funcName, paramTypes));
			}
		}

		SymbolTable.getInstance().endScope();

		/*********************************************************/
		/* [6] Return value is irrelevant for class declarations */
		/*********************************************************/
		return null;
	}

	/******************************************************************/
	/* Helper: Check for circular inheritance                        */
	/******************************************************************/
	private boolean checkCircularInheritance(String className, TypeClass parent)
	{
		TypeClass current = parent;
		while (current != null)
		{
			if (current.name.equals(className))
			{
				return true;
			}
			current = current.father;
		}
		return false;
	}

	/******************************************************************/
	/* Helper: Find a member in parent class hierarchy               */
	/******************************************************************/
	private Type findInParent(TypeClass parentClass, String memberName)
	{
		if (parentClass == null)
		{
			return null;
		}

		for (TypeList it = parentClass.dataMembers; it != null; it = it.tail)
		{
			if (it.head.name.equals(memberName))
			{
				return it.head;
			}
		}

		// Recursively search in parent's parent
		return findInParent(parentClass.father, memberName);
	}

	/******************************************************************/
	/* Helper: Check if two parameter lists match exactly            */
	/******************************************************************/
	private boolean parameterListsMatch(TypeList list1, TypeList list2)
	{
		// Both null - match
		if (list1 == null && list2 == null)
		{
			return true;
		}

		// One null, one not - no match
		if (list1 == null || list2 == null)
		{
			return false;
		}

		// Check if head types match
		if (list1.head != list2.head)
		{
			return false;
		}

		// Recursively check tails
		return parameterListsMatch(list1.tail, list2.tail);
	}

	/******************************************************************/
	/* Helper: Add all inherited members to scope recursively        */
	/******************************************************************/
	private void addInheritedMembersToScope(TypeClass parentClass)
	{
		if (parentClass == null)
		{
			return;
		}

		// First add grandparent's members (so they can be overridden)
		addInheritedMembersToScope(parentClass.father);

		// Then add parent's members
		for (TypeList it = parentClass.dataMembers; it != null; it = it.tail)
		{
			SymbolTable.getInstance().enter(it.head.name, it.head);
		}
	}
}