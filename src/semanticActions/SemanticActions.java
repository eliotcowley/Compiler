package semanticActions;

import java.lang.*;
import errors.*;
import java.util.*;
import lex.*;
import parser.*;
import symbolTable.*;
import drivers.*;

/**
 * Class to handle semantic actions of parser.
 * 
 * @author elcowley
 */
public class SemanticActions {
	
	public SymbolTable globalTable;
        public SymbolTable constantTable;
        public SymbolTable localTable;
        private SymbolTable keywordTable;
    
        public int lineNumber;
        private Stack<Object> semanticStack;
	public Quadruples quads ;
	private boolean insert; // flags insertion/search mode in symbol table
	private boolean isArray; // flags array vs. simple variable
	private boolean global; // flags global vs. local environment
	private int globalMemory; // next offset available in global memory
	private int localMemory;
	private int tableSize;
        private TokenType TYP; // type of token
        private int UB; // upper bound of array
        private int LB; // lower bound of array
        /* quadruple array location of ALLOC statement for global memory */
        private int globalStore;
        /* quadruple array location of ALLOC statement for local memory */
        private int localStore;
        
        /* used to compute the size of a variable or array in order to add to 
         * the counter for global memory */
        private int MSIZE;
        
        /* used to refer to the offset value for a subscripted variable 
         * appearing on the left side of an assignment operator. If there is no 
         * subscript, the offset is set to NULL. */
        private SymbolTableEntry offset;
        
        /* ETYPE refers to a "type" given internally to each expression and
           operand, either ARITHMETIC or RELATIONAL */
        private EType eType;
        
        /* True and False lists of relational expressions */
        private LinkedList eTrue;
        private LinkedList eFalse;
        
        /* pointers to lists of integers representing quadruple numbers, used to 
         * enable backpatching targets in code generated for relational 
         * expressions and flow-of-control statements */
        private LinkedList skipElse;
        
        /* integer variable that saves the quadruple number for the statement at 
         * the top of a while loop, so that a "goto" targetting that quadruple 
         * can be generated after processing the loop body */
        private int beginLoop;
        
        /* CURRENTFUNCTION = NIL {symbol table entry for function being parsed} */
        private SymbolTableEntry currentFunction;
        
        /* stack for no. of parameters in proc declaration or call */
        private Stack<Integer> parmCount;
        
        /* pointer to a stack of pointers to items in the parameter information 
         * list for procedures and functions. */
        private Stack<LinkedList<ParmEntry>> nextParm;

        /**
         * Default constructor for SemanticActions class.
         */
	public SemanticActions() {
		semanticStack = new Stack<Object>();
		quads = new Quadruples();
                /* TODO: flags insertions/search mode in symbol table--shouldn't 
                 * this start out as true? */
		insert = false; 
		isArray = false;
//		isParm = false;
		global = true;
		globalMemory = 0;
		localMemory = 0;
                tableSize = 37;
		globalTable = new SymbolTable(tableSize);
		constantTable = new SymbolTable(tableSize);
		InstallBuiltins(globalTable);
                keywordTable = new SymbolTable(17);
                fillOutKeywordTable();
                localTable = new SymbolTable(tableSize);
                globalStore = 0;
                localStore = 0;
                parmCount = new Stack<Integer>();
                nextParm = new Stack<LinkedList<ParmEntry>>();
	}
	
        /**
         * Execute a given semantic action.
         * @param action Semantic action to execute.
         * @param token Token passed by parser to be used in semantic action.
         * @throws SemanticError 
         */
	public void Execute (SemanticAction action, Token token) throws 
                SemanticError {
                //dumpSemanticStack();
		int actionNumber = action.getIndex();
		System.out.println("calling action : " + actionNumber + 
                        " with token " + token.getValue() + " of type " + 
                        token.getType());
                System.out.print("\n");
                switch (actionNumber) {
                    // INSERT/SEARCH = INSERT
                    case 1: {
                        this.insert = true;
                        break;
                    }
                    // INSERT/SEARCH = SEARCH
                    case 2: {
                        this.insert = false;
                        break;
                    }
                    case 3: {
                        // TYP = pop TYPE
                        Token temp = (Token) this.semanticStack.pop();
                        this.TYP = temp.getType();
                        // if ARRAY/SIMPLE = ARRAY
                        if (this.isArray) {
                            /* get upper and lower bounds of array */
                            // UB = pop CONSTANT
                            Token tempUB = (Token) this.semanticStack.pop();
                            // LB = pop CONSTANT
                            Token tempLB = (Token) this.semanticStack.pop();
                            this.UB = Integer.parseInt(tempUB.getValue());
                            this.LB = Integer.parseInt(tempLB.getValue());
                            // MSIZE = (UB - LB) + 1
                            this.MSIZE = (this.UB - this.LB) + 1;
                            // For each id on the semantic stack:
                            while ((!semanticStack.empty()) && (semanticStack.peek() instanceof Token)) {
                                // ID = pop id
                                Token tempToken = (Token) semanticStack.pop();
                                // if GLOBAL/LOCAL = GLOBAL
                                if (global) {
                                    // insert id in global symbol table (Array_entry)
                                    // id^.type = TYP
                                    // id^.address = GLOBAL_MEM 
                                    globalTable.insert(new ArrayEntry(
                                            tempToken.getValue(), globalMemory, 
                                            this.TYP, this.UB, this.LB));
                                    // GLOBAL_MEM = GLOBAL_MEM + MSIZE
                                    globalMemory += MSIZE;
                                }
                                else {
                                    // else insert id in local symbol table (Array_entry)
                                    // id^.address = LOCAL_MEM
                                    localTable.insert(new ArrayEntry(
                                            tempToken.getValue(), localMemory, 
                                            this.TYP, this.UB, this.LB));
                                    // LOCAL_MEM = LOCAL_MEM + MSIZE
                                    localMemory += MSIZE; 
                                }
                            }
                        }
                        else { /* simple variable */
                            // For each id on the semantic stack:
                            while ((!semanticStack.empty()) && (semanticStack.peek() instanceof Token)) {
                                // ID = pop id
                                Object temp2 = semanticStack.pop();
                                // if GLOBAL/LOCAL = GLOBAL
                                if (global) {
                                    /* insert id in global symbol table 
                                     * (Variable_entry) */
                                    globalTable.insert(new VariableEntry(
                                            ((Token)temp2).getValue(), 
                                            globalMemory, 
                                            TYP));
                                    globalMemory++;
                                }
                                /* else insert id in local symbol table 
                                 * (Variable_entry) */
                                else {
                                    localTable.insert(new VariableEntry(
                                            ((Token)temp2).getValue(), 
                                            localMemory, 
                                            TYP));
                                    localMemory++;
                                }
                            }
                        }
                        isArray = false;
                        break;
                    }
                    /* TODO: Is that last line in the document (: ARRAY/SIMPLE = 
                     * SIMPLE) part of semantic action 3, or a separate action?
                     */
                    // push TYPE
                    case 4: {
                        this.semanticStack.push(token);
                        break;
                    }
                    case 5: {
                        // INSERT/SEARCH = SEARCH
                        this.insert = false;
                        // pop id
                        SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
                        // GEN(PROCBEGIN,id)
                        generate("PROCBEGIN", id);
                        // LOCAL_STORE = NEXTQUAD
                        localStore = quads.getNextQuad();
                        // GEN(alloc,_)
                        generate("alloc", "_");
                        break;
                    }
                    // ARRAY/SIMPLE = ARRAY
                    case 6: {
                        this.isArray = true;
                        break;
                    }
                    // push CONSTANT
                    case 7: {
                        this.semanticStack.push(token);
                        break;
                    }
                    /*  : for each id on semantic stack
                            - insert id in symbol table (variable_entry), mark 
                            * as RESTRICTED
                        : pop ids
                        : INSERT/SEARCH = SEARCH
                        : GEN(CODE)
                        : GEN(call,main,0)
                        : GEN(exit) */
                    case 9: {
                        while (!this.semanticStack.isEmpty()) {
                            Token temp = (Token) this.semanticStack.pop();
                            VariableEntry temp2 = new VariableEntry(
                                    temp.getValue());
                            temp2.makeReserved();
                            insertHelper(temp2);
                        }
                        this.insert = false;
                        generate("CODE");
                        generate("call", "main", 0);
                        generate("exit");
                        break;
                    }
                    case 11: {
                        // GLOBAL/LOCAL = GLOBAL
                        global = true;
                        // delete local symbol table entries
                        localTable.delete();
                        // CURRENTFUNCTION = nil
                        currentFunction = null;
                        /* Fill in quadruple at location LOCAL_STORE with value 
                         * of LOCAL_MEM */
                        /* TODO: Which field of the quad do I set? Since it is
                         * filling in an alloc _ statement, I guess 1? */
                        quads.setField(localStore, 1, Integer.toString(localMemory));
                        // GEN(free, LOCAL_MEM)
                        generate("free", localMemory);
                        // GEN(PROCEND)
                        generate("PROCEND");
                        break;
                    }
                    // push id
                    case 13: {
                        this.semanticStack.push(token);
                        break;
                    }
                    case 15: {
                        FunctionEntry id = new FunctionEntry(token.getValue());
                        /* CREATE(FUN_NAME,INTEGER) {dummy type until we know 
                         * the real one} */
                        VariableEntry temp = (VariableEntry) create("FUN_NAME", 
                                TokenType.INTEGER);
                        // id^.result = $$FUN_NAME^
                        id.setResult(temp);
                        // insert id in symbol table (function_entry)
                        insertHelper(id);
                        // push id^                        
                        semanticStack.push(id);                
                        // GLOBAL/LOCAL = LOCAL
                        global = false;
                        // instantiate local symbol table
                        localTable = new SymbolTable(tableSize);
                        // LOCAL_MEM = 0
                        localMemory = 0;
                        break;
                    }
                    case 16: {
                        // pop TYPE
                        this.TYP = ((Token)semanticStack.pop()).getType();
                        // id^.type = TYPE
                        SymbolTableEntry idRef = lookupHelper((SymbolTableEntry)semanticStack.peek());
                        idRef.setType(TYP);
                        insertHelper(idRef);
                        // $$FUN_NAME^.type = TYPE
                        SymbolTableEntry funRef = lookupHelper("$$FUN_NAME");
                        funRef.setType(TYP);
                        insertHelper(funRef);
                        // CURRENTFUNCTION = id^
                        currentFunction = idRef;
                        break;
                    }
                    case 17: {
                        // insert id in symbol table (procedure_entry)
                        ProcedureEntry id = new ProcedureEntry(token.getValue());
                        insertHelper(id);
                        // push id^
                        semanticStack.push(id);
                        // GLOBAL/LOCAL = LOCAL
                        global = false;
                        // instantiate Local symbol table
                        localTable = new SymbolTable(tableSize);
                        // LOCAL_MEM = 0
                        localMemory = 0;
                        break;
                    }
                    case 19: {
                        // PARMCOUNT = 0
                        // TODO: is parmCount a Stack or an int?
                        parmCount.push(0);
                        break;
                    }
                    case 20: {
                        // pop PARMCOUNT
                        int temp = parmCount.pop();
                        // id^.number_of_parameters = PARMCOUNT
                        SymbolTableEntry proc = (SymbolTableEntry)semanticStack.peek();
                        SymbolTableEntry procRef = globalTable.lookup(proc);                        
                        procRef.setNumberOfParameters(temp);
                        insertHelper(procRef);
                        break;
                    }
                    case 21: {
                        // pop TYPE, ids
                        TYP = ((Token)semanticStack.pop()).getType();
                        // for each id (parameter) on stack:
                        Object proc = new Object();
                        if (semanticStack.get(0) instanceof ProcedureEntry) {
                            proc = (ProcedureEntry)semanticStack.get(0);
                        }
                        else if (semanticStack.get(0) instanceof FunctionEntry) {
                            proc = (FunctionEntry)semanticStack.get(0);
                        }
                        int i = semanticStack.size()-1;
                        while (i > 0) {
                            Token param;
                            // add a new element to id^.parminfo   // id is procedure name
                            ParmEntry newParm = new ParmEntry();
                            if (isArray) {
                                // id^.upper_bound = CONSTANT(1)
                                Token constant1 = (Token) semanticStack.pop();
                                i--;
                                int uBound = Integer.valueOf(constant1.getValue());
                                // id^.lower_bound = CONSTANT(2)
                                Token constant2 = (Token) semanticStack.pop();
                                i--;
                                int lBound = Integer.valueOf(constant2.getValue());
                                param = (Token) semanticStack.pop();
                                i--;
                                // insert symbol table entry (array, is_parameter returns true)
                                ArrayEntry temp = new ArrayEntry(param.getValue());
                                temp.setParm(true);
                                temp.setUpperBound(uBound);
                                temp.setLowerBound(lBound);
                                /* set UBOUND and LBOUND in the current element 
                                 * of PARMINFO to id^.ubound and id^.lbound */
                                newParm.setUpperBound(uBound);
                                newParm.setLowerBound(lBound);
                                // set array flag in current PARMINFO element to TRUE
                                newParm.setArray(true);
                                // id^.address = LOCAL_MEM 
                                temp.setAddress(localMemory);
                                // LOCAL_MEM = LOCAL_MEM + 1
                                localMemory++;
                                // id^.type = TYPE {on stack}
                                temp.setType(TYP);
                                insertHelper(temp);
                            }
                            // else 
                            else {
                                param = (Token) semanticStack.pop();
                                i--;
                                // insert new symbol table entry (variable entry, with is_parameter returning true)
                                VariableEntry temp = new VariableEntry(param.getValue());
                                temp.setParm();
                                // id^.address = LOCAL_MEM 
                                temp.setAddress(localMemory);
                                // LOCAL_MEM = LOCAL_MEM + 1
                                localMemory++;
                                // id^.type = TYPE {on stack}
                                temp.setType(TYP);
                                insertHelper(temp);
                                // set array flag in current PARMINFO entry to FALSE
                                newParm.setArray(false);
                            }
                            // set TYPE in current entry of PARMINFO to TYPE
                            newParm.setType(TYP);
                            // increment PARMCOUNT
                            int currentParmCount = parmCount.pop();
                            parmCount.push(currentParmCount+1);
                            if (proc instanceof ProcedureEntry) {
                                ((ProcedureEntry)proc).addParameter(newParm);
                            }
                            else {
                                ((FunctionEntry)proc).addParameter(newParm);
                            }
                        }
                        // ARRAY/SIMPLE = SIMPLE
                        isArray = false;
                        break;
                    }
                    case 22: {
                        // pop ETYPE
                        eType = (EType) semanticStack.pop();
                        // if ETYPE <> RELATIONAL, ERROR
                        if (eType != EType.RELATIONAL) {
                            throw new SemanticError("ERROR on line " + 
                                    lineNumber + ": Expected relational "
                                    + "expression");
                        }
                        // BACKPATCH(E.TRUE, NEXTQUAD)
                        backpatch(eTrue, quads.getNextQuad());
                        break;
                    }
                    case 24: {
                        // set BEGINLOOP = NEXTQUAD
                        beginLoop = quads.getNextQuad();
                        // push BEGINLOOP
                        semanticStack.push(beginLoop);
                        break;
                    }
                    case 25: {
                        // pop ETYPE
                        eType = (EType) semanticStack.pop();
                        // if ETYPE <> RELATIONAL, ERROR
                        if (eType != EType.RELATIONAL) {
                            throw new SemanticError("ERROR on line " + 
                                    lineNumber + ": Expected relational "
                                    + "expression");
                        }
                        // BACKPATCH(E.TRUE, NEXTQUAD)
                        backpatch(eTrue, quads.getNextQuad());
                        break;
                    }
                    case 26: {
                        // pop E.TRUE, E.FALSE, BEGINLOOP
                        eFalse = (LinkedList) semanticStack.pop();
                        eTrue = (LinkedList) semanticStack.pop();
                        beginLoop = (int) semanticStack.pop();
                        // GEN(goto BEGINLOOP) {pushed on stack in #24}
                        generate("goto " + beginLoop);
                        // BACKPATCH(E.FALSE, NEXTQUAD)
                        backpatch(eFalse, quads.getNextQuad());
                        break;
                    }
                    case 27: {
                        // set SKIP_ELSE = makelist(NEXTQUAD)
                        skipElse = makeList(quads.getNextQuad());
                        // push SKIP_ELSE
                        semanticStack.push(skipElse);
                        // GEN(goto _ )
                        generate("goto", "_");
                        // BACKPATCH(E.FALSE, NEXTQUAD)
                        backpatch(eFalse, quads.getNextQuad());
                        break;
                    }
                    case 28: {
                        // pop SKIP_ELSE, E.FALSE, E.TRUE
                        eTrue = (LinkedList) semanticStack.pop();
                        eFalse = (LinkedList) semanticStack.pop();
                        skipElse = (LinkedList) semanticStack.pop();
                        /* BACKPATCH(SKIP_ELSE, NEXTQUAD) {pushed on stack in 
                         * #27} */
                        backpatch(skipElse, quads.getNextQuad());
                        break;
                    }
                    case 29: {
                        // pop E.FALSE, E.TRUE
                        eTrue = (LinkedList) semanticStack.pop();
                        eFalse = (LinkedList) semanticStack.pop();
                        // BACKPATCH(E.FALSE,NEXTQUAD)
                        backpatch(eFalse, quads.getNextQuad());
                        break;
                    }
                    case 30: {
                        // lookup id in symbol table
                        SymbolTableEntry id;
                        id = lookupHelper(token.getValue());
                        // if not found, ERROR (undeclared variable)
                        if (id == null) {
                            throw new SemanticError("ERROR: On line " + lineNumber + " -- Undeclared variable");
                        }
                        // push id^
                        semanticStack.push(id);
                        // push ETYPE(ARITHMETIC)
                        semanticStack.push(EType.ARITHMETIC);
                        break;
                    }
                    case 31: {
                        // pop ETYPE, id1, offset, id2
                        eType = (EType) semanticStack.pop();
                        SymbolTableEntry id2 = (SymbolTableEntry) semanticStack.pop();
                        offset = (SymbolTableEntry) semanticStack.pop();
                        SymbolTableEntry id1 = (SymbolTableEntry) semanticStack.pop();
                        // if ETYPE <> ARITHMETIC, ERROR
                        if (eType != EType.ARITHMETIC) {
                            throw new SemanticError("ERROR on line " + 
                                    lineNumber + ": Expected arithmetic "
                                    + "expression");
                        } 
                        // if TYPECHECK(id1,id2) = 3, ERROR
                        if (typeCheck(id1, id2) == 3) {
                            SymbolTableEntry temp = create("TEMP", TokenType.REAL);
                            generate("ltof", id1, temp);
                            // if OFFSET = NULL,
                            if (offset.getName() == null) {
                                // GEN(move,$$TEMP,id1)
                                generate("move", temp, id1);
                            }
                            // else GEN(stor $$TEMP,offset,id1)
                            else {
                                generate("stor", temp, offset, id1);
                            }
                        }
                        // if TYPECHECK(id1,id2) = 2
                        else if (typeCheck(id1, id2) == 2) {
                            // CREATE(TEMP,REAL)
                            SymbolTableEntry temp = create("TEMP", TokenType.REAL);
                            // GEN(ltof,id2,$$TEMP)
                            generate("ltof", id2, temp);
                            // if OFFSET = NULL,
                            if (offset.getName() == null) {
                                // GEN(move,$$TEMP,id1)
                                generate("move", temp, id1);
                            }
                            // else GEN(stor $$TEMP,offset,id1)
                            else {
                                generate("stor", temp, offset, id1);
                            }
                        }
                        // else if OFFSET = NULL,
                        else if (offset.getName() == null) {
                            // GEN(move,id2,id1)
                            generate("move", id2, id1);
                        }
                        // else GEN(stor id2,offset,id1)
                        else {
                            generate("stor", id2, offset, id1);
                        }
                        break;
                    }
                    case 32: {
                        // pop ETYPE
                        eType = (EType) semanticStack.pop();
                        // if ETYPE <> ARITHMETIC, ERROR
                        if (eType != EType.ARITHMETIC) {
                            throw new SemanticError("ERROR on line " + 
                                    lineNumber + ": Expected arithmetic "
                                    + "expression");
                        }
                        // if not id^.is_array, ERROR
                        SymbolTableEntry idRef = lookupHelper(token.getValue());
                        if (!(idRef instanceof ArrayEntry)) {
                            throw new SemanticError("ERROR on line " + 
                                    lineNumber + ": Array expected");
                        }
                        break;
                    }
                    case 33: {
                        // pop ETYPE
                        eType = (EType) semanticStack.pop();
                        // if ETYPE <> ARITHMETIC, ERROR
                        if (eType != EType.ARITHMETIC) {
                            throw new SemanticError("ERROR on line " + 
                                    lineNumber + ": Arithmetic expression "
                                    + "expected");
                        }
                        // pop id
                        SymbolTableEntry id = (SymbolTableEntry) 
                                semanticStack.pop();
                        /* if id^.type <> INTEGER, ERROR  {id is pointer on 
                         * top of stack} */
                        SymbolTableEntry idRef = lookupHelper(id);
                        if (idRef == null) {
                            idRef = constantTable.lookup(id);
                        }
                        if (idRef.getType() != TokenType.INTEGER) {
                            throw new SemanticError("ERROR on line " + 
                                    lineNumber + ": Identifier should be of "
                                    + "type integer");
                        }
                        // : CREATE(TEMP,INTEGER)
                        SymbolTableEntry temp = create("TEMP", TokenType.INTEGER);
                        /* : GEN(sub,id,array_name.lbound,$$TEMP) {array_name is 
                         * id on bottom of stack} */
                        // 0 is index of bottom of stack
                        //ArrayEntry arrayName = (ArrayEntry) semanticStack.get(0);
                        /* This solution is clearly wrong, however I get two odd
                         * objects, EType ARITHMETIC and ProcedureEntry write,
                         * at the bottom of the stack, so getting the bottom of
                         * the stack doesn't work */
                        ArrayEntry arrayName = new ArrayEntry();
                        for (int i = semanticStack.size()-1; i >= 0; i--) {
                            if (semanticStack.get(i) instanceof ArrayEntry) {
                                arrayName = (ArrayEntry) semanticStack.get(i);
                            }
                        }
                        generate("sub", id, arrayName.getLowerBound(), temp);
                        // push $$TEMP
                        semanticStack.push(temp);
                        break;
                    }
                    case 34: {
                        // pop ETYPE
                        eType = (EType) semanticStack.pop();
                        // if id on stack is a function, call action 52
                        if (!semanticStack.empty()) {
                            Object id = semanticStack.peek();
                            if (id instanceof FunctionEntry) {
                                this.dumpSemanticStack();
                                Execute(SemanticAction.action52, token);
                            }
                            else {
                                // else push NULL OFFSET
                                semanticStack.push(new SymbolTableEntry());
                            }
                        }
                        // else push NULL OFFSET
                        else {
                            /* Since offset is just a SymbolTableEntry, if we 
                             * construct a new null SymbolTableEntry that will 
                             * be our null offset */
                            semanticStack.push(new SymbolTableEntry());
                        }
                        break;
                    }
                    case 35: {
                        // push new element on PARMCOUNT stack and set to 0
                        parmCount.push(0);
                        // push new element on NEXTPARM stack
                        // set NEXTPARM = id^.parminfo
                        nextParm.push(lookupHelper(token.getValue()).getParameterInfo());
                        break;
                    }
                    case 36: {
                        // pop id, ETYPE
                        eType = (EType) semanticStack.pop();
                        SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
                        // if id^.number_of_parameters <> 0, ERROR
                        if (id.getNumberOfParameters() != 0) {
                            throw new SemanticError("ERROR on line " + lineNumber + ": no parameters expected in procedure");
                        }
                        // GEN(call,id,0)
                        generate("call", id, 0);
                        break;
                    }
                    case 37: {
                        // pop ETYPE
                        eType = (EType) semanticStack.pop();
                        // if ETYPE <> ARITHMETIC, ERROR
                        if (eType != EType.ARITHMETIC) {
                            throw new SemanticError("ERROR on line " + lineNumber + ": Arithmetic expression expected");
                        }
                        SymbolTableEntry id = (SymbolTableEntry)semanticStack.peek();
                        SymbolTableEntry idRef = lookupHelper(id);
                        if (idRef == null) {
                            idRef = constantTable.lookup(id);
                        }
                        /* if  NOT (id^.is_variable OR id^.is_constant 
                         * OR id^.is_function_result OR id^.is_array), ERROR */
                        if (!((idRef instanceof VariableEntry) || 
                                (idRef instanceof ConstantEntry) || 
                                (idRef.isFunctionResult()) || 
                                (idRef instanceof ArrayEntry))) {
                            throw new SemanticError("ERROR on line " + 
                                    lineNumber + ": Expected variable, constant, "
                                    + "function result, or array");
                        }
                        // if proc_or_fun^.name <> READ or WRITE:
                        // {proc_or_fun is the procedure or function pointer, on bottom of stack}
                        int i = 0;
                        while ((i < semanticStack.size()) && 
                                (!(semanticStack.get(i) instanceof FunctionEntry)) &&
                                (!(semanticStack.get(i) instanceof ProcedureEntry))) {
                            i++;
                        }
                        SymbolTableEntry procOrFun = (SymbolTableEntry) semanticStack.get(i);
                        String procOrFunName = procOrFun.getName();
                        if (!((procOrFunName.equalsIgnoreCase("READ")) || (procOrFunName.equalsIgnoreCase("WRITE")))) {
                            LinkedList<ParmEntry> paramList = nextParm.peek();
                            int paramIndex = parmCount.peek();
                            ParmEntry currentParm = paramList.get(paramIndex);
                            // increment PARMCOUNT.top
                            int parmCountTop = parmCount.pop();
                            parmCount.push(parmCountTop+1);
                            // if PARMCOUNT.top > proc_or_fun.number_of_parameters, ERROR
                            if (parmCount.peek() > procOrFun.getNumberOfParameters()) {
                                throw new SemanticError("ERROR on line " + lineNumber + ": Parameter count mismatch");
                            }
                            // if id^.type <> NEXTPARM^.type, ERROR 
                            if (idRef.getType() != currentParm.getType()) {
                                throw new SemanticError("ERROR on line " + lineNumber + ": Parameter type mismatch");
                            }
                            // if NEXTPARM^.array = TRUE,
                            if (currentParm.isArray()) {
                                /* if id^.lbound <> NEXTPARM^.lbound OR 
                                 * id^.ubound <> NEXTPARM^.ubound, ERROR */
                                if ((idRef.getLowerBound() != 
                                        currentParm.getLowerBound()) || 
                                        (idRef.getUpperBound() != 
                                        currentParm.getUpperBound())) {
                                    throw new SemanticError("ERROR on line " + 
                                            lineNumber + ": Array index out of "
                                            + "bounds");
                                }
                            }
                            /* increment NEXTPARM {to point to next item in 
                             * parinfo list} */
//                            int temp = parmCount.pop();
//                            parmCount.push(temp+1);
                        }
//                        else {
//                            semanticStack.push(id);
//                        }
                        break;
                    }
                    case 38: {
                        // pop ETYPE
                        eType = (EType) semanticStack.pop();
                        // if ETYPE <> ARITHMETIC, ERROR
                        if (eType != EType.ARITHMETIC) {
                            throw new SemanticError("ERROR on line " + 
                                    lineNumber + ": Expected arithmetic "
                                    + "expression");
                        }
                        // push operator
                        semanticStack.push(token);
                        break;
                    }
                    case 39: {
                        // pop ETYPE
                        eType = (EType) semanticStack.pop();
                        // if ETYPE <> ARITHMETIC, ERROR
                        if (eType != EType.ARITHMETIC) {
                            throw new SemanticError("ERROR on line " + 
                                    lineNumber + ": Expected arithmetic "
                                    + "expression");
                        }
                        // pop ids, operator
                        SymbolTableEntry id2 = (SymbolTableEntry) semanticStack.pop();
                        Token operator = (Token) semanticStack.pop();
                        SymbolTableEntry id1 = (SymbolTableEntry) semanticStack.pop();
                        // if TYPECHECK(id1,id2) = 2, 
                        if (typeCheck(id1, id2) == 2) {
                            // CREATE(TEMP1,REAL)
                            SymbolTableEntry temp1 = create("TEMP1", TokenType.REAL);
                            // GEN(ltof,id2,$$TEMP1)
                            generate("ltof", id2, temp1);
                            // GEN(***,id1,$$TEMP1,_)   {*** replaced by blt, ble, bgt, etc.}
                            generate(operator.getTVICode(), id1, temp1, "_");
                        }
                        // if TYPECHECK(id1,id2) = 3, 
                        else if (typeCheck(id1, id2) == 3) {
                            // CREATE(TEMP1,REAL)
                            SymbolTableEntry temp1 = create("TEMP1", TokenType.REAL);
                            // GEN(ltof,id1,$$TEMP1)
                            generate("ltof", id1, temp1);
                            // GEN(***,$$TEMP1,id2,_)
                            generate(operator.getTVICode(), temp1, id2, "_");
                        }
                        // else
                        else {
                            // GEN(***,id1,id2,_)
                            generate(operator.getTVICode(), id1, id2, "_");
                        }
                        // GEN(goto _ )
                        generate("goto _");
                        // E.TRUE  = MAKELIST(NEXTQUAD - 2)
                        eTrue = makeList(quads.getNextQuad()-2);
                        // E.FALSE = MAKELIST(NEXTQUAD - 1)
                        eFalse = makeList(quads.getNextQuad()-1);
                        // push E.TRUE, E.FALSE
                        semanticStack.push(eTrue);
                        semanticStack.push(eFalse);
                        // push ETYPE(RELATIONAL)
                        semanticStack.push(EType.RELATIONAL);
                        break;
                    }
                    /* : push sign */
                    case 40: {
                        semanticStack.push(token);
                        break;
                    }
                    case 41: {
                        // pop ETYPE
                        eType = (EType)semanticStack.pop();
                        // if ETYPE <> ARITHMETIC, ERROR
                        if (eType != EType.ARITHMETIC) {
                            throw new SemanticError("ERROR on line " + lineNumber + ": Arithmetic expression expected");
                        }
                        int i = semanticStack.size()-1;
                        while (!(semanticStack.get(i) instanceof Token)) {
                            i--;
                        }
                        Token sign = (Token)semanticStack.get(i);
                        // if sign {on stack} = UNARYMINUS:
                        if (sign.getType() == TokenType.UNARYMINUS) {
                            // pop sign, id
                            SymbolTableEntry id = (SymbolTableEntry)semanticStack.pop();
                            sign = (Token)semanticStack.pop();
                            // CREATE(TEMP,idtype)
                            SymbolTableEntry temp = create("TEMP", id.getType());
                            // GEN(uminus,id,$$TEMP) 
                            generate("uminus", id, temp);
                            // push $$TEMP on stack
                            semanticStack.push(temp);
                        }
                        // else 
                        else {
                            // pop sign, id
                            SymbolTableEntry id = (SymbolTableEntry)semanticStack.pop();
                            semanticStack.pop();
                            // push id
                            semanticStack.push(id);
                        }
                        // push ETYPE(ARITHMETIC)
                        semanticStack.push(EType.ARITHMETIC);
                        break;
                    }
                    case 42: {
                        // pop ETYPE
                        eType = (EType) semanticStack.pop();
                        // if operator = OR:
                        if (token.getOpType() == OpValue.OR) {
                            // if ETYPE <> RELATIONAL, ERROR
                            if (eType != EType.RELATIONAL) {
                                throw new SemanticError("ERROR on line " + 
                                        lineNumber + ": Expected relational "
                                        + "expression");
                            }
                            // BACKPATCH (E.FALSE, NEXTQUAD)
                            backpatch(eFalse, quads.getNextQuad());
                        }
                        else {
                            // check ETYPE = ARITHMETIC
                            if (eType != EType.ARITHMETIC) {
                                throw new SemanticError("ERROR on line " + 
                                        lineNumber + ": Expected arithmetic "
                                        + "expression");
                            }
                        }
                        // push operator
                        semanticStack.push(token);
                        break;
                    }
                    case 43: {
                        // pop ETYPE
                        eType = (EType) semanticStack.pop();
                        if (semanticStack.peek() instanceof EType) semanticStack.pop();
                        // if ETYPE = RELATIONAL:
                        if (eType == EType.RELATIONAL) {
                            /* pop E(1).TRUE, E(1).FALSE, operator, 
                             * E(2).TRUE, E(2).FALSE, ETYPE */
                            LinkedList e2False = (LinkedList) semanticStack.pop();
                            LinkedList e2True = (LinkedList) semanticStack.pop();
                            Token operator = (Token) semanticStack.pop();
                            LinkedList e1False = (LinkedList) semanticStack.pop();
                            LinkedList e1True = (LinkedList) semanticStack.pop();
                            // if operator = OR:
                            if (operator.getOpType() == OpValue.OR) {
                                // -E.TRUE = MERGE (E(1).TRUE, E(2).TRUE)
                                eTrue = merge(e1True, e2True);
                                // -E.FALSE = E(2).FALSE {on stack}
                                eFalse = e2False;
                                // push E.TRUE, E.FALSE, ETYPE(RELATIONAL)
                                semanticStack.push(eTrue);
                                semanticStack.push(eFalse);
                                semanticStack.push(EType.RELATIONAL);
                            }
                        }
                        else {
                            // if ETYPE <> ARITHMETIC, ERROR
                            if (eType != EType.ARITHMETIC) {
                                throw new SemanticError("ERROR on line " + 
                                        lineNumber + ": Expression type should be "
                                        + "arithmetic");
                            }
                            // pop ids, operator, ETYPE
                            SymbolTableEntry id2 = (SymbolTableEntry) semanticStack.pop();
                            Token operator = (Token) semanticStack.pop();
                            SymbolTableEntry id1 = (SymbolTableEntry) semanticStack.pop();
                            // if TYPECHECK(id1,id2) = 0,
                            if (typeCheck(id1, id2) == 0) {
                                // CREATE(TEMP,INTEGER)
                                SymbolTableEntry temp = create("TEMP", TokenType.INTEGER);
                                // GEN(***,id1,id2,$$TEMP) {*** replaced by add, sub, etc.}
                                generate(operator.getTVICode(), id1, id2, temp);
                                // push result variable ($$TEMP or $$TEMP2)
                                semanticStack.push(temp);
                            }
                            // if TYPECHECK(id1,id2) = 1,
                            else if (typeCheck(id1, id2) == 1) {
                                // CREATE(TEMP,REAL)
                                SymbolTableEntry temp = create("TEMP", TokenType.REAL);
                                // GEN(f***,id1,id2,$$TEMP)
                                generate("f" + operator.getTVICode(), id1, id2, temp);
                                // push result variable ($$TEMP or $$TEMP2) 
                                semanticStack.push(temp);
                            }
                            // if TYPECHECK(id1,id2) = 2,
                            else if (typeCheck(id1, id2) == 2) {
                                // CREATE(TEMP1,REAL)
                                SymbolTableEntry temp1 = create("TEMP1", TokenType.REAL);
                                // GEN(ltof,id2,$$TEMP1)
                                generate("ltof", id2, temp1);
                                // CREATE(TEMP2,REAL)
                                SymbolTableEntry temp2 = create("TEMP2", TokenType.REAL);
                                // GEN(f***,id1,$$TEMP1,$$TEMP2)
                                generate("f" + operator.getTVICode(), id1, temp1, temp2);
                                // push result variable ($$TEMP or $$TEMP2)
                                semanticStack.push(temp2);
                            }
                            // if TYPECHECK(id1,id2) = 3
                            else if (typeCheck(id1, id2) == 3) {
                                // CREATE(TEMP1,REAL)
                                SymbolTableEntry temp1 = create("TEMP1", TokenType.REAL);
                                // GEN(ltof,id1,$$TEMP1)
                                generate("ltof", id1, temp1);
                                // CREATE(TEMP2,REAL)
                                SymbolTableEntry temp2 = create("TEMP2", TokenType.REAL);
                                // GEN(f***,$$TEMP1,id2,$$TEMP2)
                                generate("f" + operator.getTVICode(), temp1, id2, temp2);
                                // push result variable ($$TEMP or $$TEMP2)
                                semanticStack.push(temp2);
                            }
                            // push ETYPE(ARITHMETIC)
                            semanticStack.push(EType.ARITHMETIC);
                        }
                        break;
                    }
                    case 44: {
                        // pop ETYPE
                        eType = (EType) semanticStack.pop();
                        // if ETYPE = RELATIONAL:
                        if (eType == EType.RELATIONAL) {
                            // if operator = AND, BACKPATCH (E.TRUE, NEXTQUAD)
                            if (token.getOpType() == OpValue.AND) {
                                backpatch(eTrue, quads.getNextQuad());
                            }
                        }
                        // push operator
                        semanticStack.push(token);
                        break;
                    }
                    case 45: {
                        /* I realize this isn't the way the pseudocode is written,
                         * but depending on what the operator is, we pop different
                         * things. If the operator is AND, we pop ETYPE, E2.FALSE, 
                         * then E2.TRUE, then operator. However, if the operator 
                         * is not AND, then we pop ETYPE, then the operator, then
                         * the ids. So we have to know what the operator is before
                         * we start popping things off the stack, but the operator
                         * is several items below, so we have to do this based on
                         * the ETYPE instead.
                         */
                        /* -pop E(1).TRUE, E(1).FALSE, operator, E(2).TRUE, 
                         * E(2).FALSE, ETYPE */
                        eType = (EType) semanticStack.pop();
                        if (eType == EType.RELATIONAL) {
                            LinkedList e2False = (LinkedList) semanticStack.pop();
                            LinkedList e2True = (LinkedList) semanticStack.pop();
                            Token operator = (Token) semanticStack.pop();
                            LinkedList e1False = (LinkedList) semanticStack.pop();
                            LinkedList e1True = (LinkedList) semanticStack.pop();
                            // if operator = AND:
                            if (operator.getOpType() == OpValue.AND) {
                                // -E.TRUE =  E(2).TRUE
                                eTrue = e2True;
                                // -E.FALSE = MERGE (E(1).FALSE, E(2).FALSE)
                                eFalse = merge(e1False, e2False);
                                // -push E.TRUE, E.FALSE, ETYPE(RELATIONAL)
                                semanticStack.push(eTrue);
                                semanticStack.push(eFalse);
                                semanticStack.push(EType.RELATIONAL);
                            }
                        }
                        // : else
                        else {
                            // -pop ids, operator
                            SymbolTableEntry id1 = (SymbolTableEntry) semanticStack.pop();
                            Token operator = (Token) semanticStack.pop();
                            SymbolTableEntry id2 = (SymbolTableEntry) semanticStack.pop();
                            // -if ETYPE <> ARITHMETIC, ERROR
                            if (eType != EType.ARITHMETIC) {
                                throw new SemanticError("ERROR on line " + 
                                    lineNumber + ": Expected arithmetic "
                                    + "expression");
                            }
                            // -if TYPECHECK(id1,id2) <> 0 and operator = MOD,
                            if ((typeCheck(id1, id2) != 0) && (operator.getOpType() == OpValue.MOD)) {
                                // {MOD requires integer operands} ERROR
                                throw new SemanticError("ERROR: MOD requires integer operands on line " + lineNumber);
                            }
                            // -if TYPECHECK(id1,id2) = 0, 
                            if ((typeCheck(id1, id2)) == 0) {
                                // if operator = MOD
                                if (operator.getOpType() == OpValue.MOD) {
                                    // CREATE(TEMP1,INTEGER)
                                    SymbolTableEntry temp1 = create("TEMP1", TokenType.INTEGER);
                                    // GEN(move,id1,$$TEMP1)
                                    generate("move", id1, temp1);
                                    // CREATE(TEMP2,INTEGER)
                                    SymbolTableEntry temp2 = create("TEMP2", TokenType.INTEGER);
                                    // GEN(move,$$TEMP1,$$TEMP2)
                                    generate("move", temp1, temp2);
                                    // GEN(sub,$$TEMP2,id2,$$TEMP1)
                                    generate("sub", temp2, id2, temp1);
                                    // GEN(bge,$$TEMP1,id2,NEXTQUAD-2) {result will be in $$TEMP1} 
                                    generate("bge", temp1, id2, quads.getNextQuad()-2);
                                    // -push result variable ($$TEMP or $$TEMP2)
                                    semanticStack.push(temp2);
                                } // else if operator = /
                                else if (operator.getOpType() == OpValue.DIVISION) {
                                    // CREATE(TEMP1,REAL)
                                    SymbolTableEntry temp1 = create("TEMP1", TokenType.REAL);
                                    // GEN(ltof,id1,$$TEMP1)
                                    generate("ltof", id1, temp1);
                                    // CREATE(TEMP2,REAL)
                                    SymbolTableEntry temp2 = create("TEMP2", TokenType.REAL);
                                    // GEN(ltof,id2,$$TEMP2)
                                    generate("ltof", id2, temp2);
                                    // CREATE(TEMP3,REAL)
                                    SymbolTableEntry temp3 = create("TEMP3", TokenType.REAL);
                                    // GEN(fdiv,$$TEMP1,$$TEMP2,$$TEMP3)
                                    generate("fdiv", temp1, temp2, temp3);
                                    // -push result variable ($$TEMP or $$TEMP2)
                                    semanticStack.push(temp2);
                                }
                                // else
                                else {
                                    // CREATE(TEMP,INTEGER)
                                    SymbolTableEntry temp = create("TEMP", TokenType.INTEGER);
                                    // GEN(***,id1,id2,$$TEMP) {*** replaced by div, mul, etc.}
                                    generate(operator.getTVICode(), id1, id2, temp);
                                    // -push result variable ($$TEMP or $$TEMP2)
                                    semanticStack.push(temp);
                                }
                            }
                            // -if TYPECHECK(id1,id2) = 1, 
                            else if (typeCheck(id1, id2) == 1) {
                                // -if operator = DIV
                                if (operator.getOpType() == OpValue.DIV) {
                                    // CREATE(TEMP1,INTEGER)
                                    SymbolTableEntry temp1 = create("TEMP1", TokenType.INTEGER);
                                    // GEN(ftol,id1,$$TEMP1)
                                    generate("ftol", id1, temp1);
                                    // CREATE(TEMP2,INTEGER)
                                    SymbolTableEntry temp2 = create("TEMP2", TokenType.INTEGER);
                                    // GEN(ftol,id2,$$TEMP2)
                                    generate("ftol", id2, temp2);
                                    // CREATE(TEMP3,REAL)
                                    SymbolTableEntry temp3 = create("TEMP3", TokenType.REAL);
                                    // GEN(div,$$TEMP1,$$TEMP2,$$TEMP3) 
                                    generate("div", temp1, temp2, temp3);
                                    // -push result variable ($$TEMP or $$TEMP2)
                                    semanticStack.push(temp2);
                                } 
                                else {
                                    // CREATE(TEMP,REAL)
                                    SymbolTableEntry temp = create("TEMP", TokenType.REAL);
                                    // GEN(f***,id1,id2,$$TEMP)
                                    generate("f" + operator.getTVICode(), id1, id2, temp);
                                    // -push result variable ($$TEMP or $$TEMP2)
                                    semanticStack.push(temp);
                                }
                            }              
                            // -if TYPECHECK(id1,id2) = 2
                            else if (typeCheck(id1, id2) == 2) {
                                // -if operator = DIV
                                if (operator.getOpType() == OpValue.DIV) {
                                    // CREATE(TEMP1,INTEGER)
                                    SymbolTableEntry temp1 = create("TEMP1", TokenType.INTEGER);
                                    // GEN(ftol,id1,$$TEMP1)
                                    generate("ftol", id1, temp1);
                                    // CREATE(TEMP2,INTEGER)
                                    SymbolTableEntry temp2 = create("TEMP2", TokenType.INTEGER);
                                    // GEN(div,$$TEMP1,id2,$$TEMP2)
                                    generate("div", temp1, id2, temp2);
                                    // -push result variable ($$TEMP or $$TEMP2)
                                    semanticStack.push(temp2);
                                } 
                                else {
                                    // CREATE(TEMP1,REAL) 
                                    SymbolTableEntry temp1 = create("TEMP1", TokenType.REAL);
                                    // GEN(ltof,id2,$$TEMP1)
                                    generate("ltof", id2, temp1);
                                    // CREATE(TEMP2,REAL)
                                    SymbolTableEntry temp2 = create("TEMP2", TokenType.REAL);
                                    // GEN(f***,id1,$$TEMP1,$$TEMP2)
                                    generate("f" + operator.getTVICode(), id1, temp1, temp2);
                                    // -push result variable ($$TEMP or $$TEMP2)
                                    semanticStack.push(temp2);
                                }
                            }
                            // -if TYPECHECK(id1,id2) = 3,
                            else if (typeCheck(id1, id2) == 3) {
                                // -if operator = DIV
                                if (operator.getOpType() == OpValue.DIV) {
                                    // CREATE(TEMP1,INTEGER)
                                    SymbolTableEntry temp1 = create("TEMP1", TokenType.INTEGER);
                                    // GEN(ftol,id2,$$TEMP1)
                                    generate("ftol", id2, temp1);
                                    // CREATE(TEMP2,INTEGER)
                                    SymbolTableEntry temp2 = create("TEMP2", TokenType.INTEGER);
                                    // GEN(div,id1,$$TEMP1,$$TEMP2)
                                    generate("div", id1, temp1, temp2);
                                    // -push result variable ($$TEMP or $$TEMP2)
                                    semanticStack.push(temp2);
                                } 
                                else {
                                    // CREATE(TEMP1,REAL)
                                    SymbolTableEntry temp1 = create("TEMP1", TokenType.REAL);
                                    // GEN(ltof,id1,$$TEMP1)
                                    generate("ltof", id1, temp1);
                                    // CREATE(TEMP2,REAL)
                                    SymbolTableEntry temp2 = create("TEMP2", TokenType.REAL);
                                    // GEN(f***,$$TEMP1,id2,$$TEMP2) 
                                    generate("f" + operator.getTVICode(), temp1, id2, temp2);
                                    // -push result variable ($$TEMP or $$TEMP2)
                                    semanticStack.push(temp2);
                                }
                            }
                            // -push ETYPE(ARITHMETIC)
                            semanticStack.push(EType.ARITHMETIC);
                        }
                        break;
                    }
                    case 46: {
                        // if token is an identifier
                        if (token.getType() == TokenType.IDENTIFIER) {
                            // lookup in symbol table
                            if (global) {
                                SymbolTableEntry temp = lookupHelper(token.getValue());
                                // if not found, ERROR (undeclared variable)
                                if (temp == null) {
                                    throw new SemanticError("ERROR: Undeclared variable on line " + lineNumber);
                                }
                                // push id^
                                semanticStack.push(temp);
                            }
                            else {
                                SymbolTableEntry temp = lookupHelper(token.getValue());
                                if (temp == null) {
                                    throw new SemanticError("ERROR: Undeclared variable on line " + lineNumber);
                                }
                                semanticStack.push(temp);
                            }
                        }
                        // if token is a constant
                        else if ((token.getType() == TokenType.INTCONSTANT) || (token.getType() == TokenType.REALCONSTANT)) {
                            // lookup in constant (symbol) table
                            SymbolTableEntry temp = constantTable.lookup(token.getValue());
                            // if not found, 
                            if (temp == null) {
                                // if tokentype = INTCONSTANT, set type field to INTEGER
                                if (token.getType() == TokenType.INTCONSTANT) {
                                    temp = new ConstantEntry(token.getValue(), TokenType.INTEGER);
                                    //temp.setType(TokenType.INTEGER);
                                }
                                // else set type field to REAL 
                                else {
                                    temp = new ConstantEntry(token.getValue(), TokenType.REAL);
                                    //temp.setType(TokenType.REAL);
                                }
                                // insert in constant table
                                constantTable.insert(temp);
                            }
                            // push pointer to constant entry
                            semanticStack.push(constantTable.lookup(temp));
                        }
                        semanticStack.push(EType.ARITHMETIC);
                        break;
                    }
                    case 47: {
                        // : pop E.TRUE, E.FALSE, ETYPE
                        eType = (EType) semanticStack.pop();
                        eFalse = (LinkedList) semanticStack.pop();
                        eTrue = (LinkedList) semanticStack.pop();
                        // : if ETYPE <> RELATIONAL, ERROR
                        if (eType != EType.RELATIONAL) {
                            throw new SemanticError("ERROR on line " + 
                                    lineNumber + ": Expected relational "
                                    + "expression");
                        }
                        // E.TRUE = E.FALSE
                        eTrue = eFalse;
                        // E.FALSE = E.TRUE
                        eFalse = eTrue;
                        // push new E.TRUE, E.FALSE
                        semanticStack.push(eTrue);
                        semanticStack.push(eFalse);
                        // push ETYPE(RELATIONAL)
                        semanticStack.push(EType.RELATIONAL);
                        break;
                    }
                    case 48: {
                        // if offset (on stack) <> NULL,
                        // search for offset on stack from top to bottom
                        Object top = semanticStack.peek();
                        SymbolTableEntry tempOffset;
                        boolean offsetOnStack;
                        if (top instanceof SymbolTableEntry) {
                            tempOffset = (SymbolTableEntry) semanticStack.peek();
                            offsetOnStack = true;
                        }
                        else {
                            tempOffset = new SymbolTableEntry();
                            offsetOnStack = false;
                        }
                        if (tempOffset.getName() != null) {
                            // if offset.type <> INTEGER, ERROR
                            if (tempOffset.getType() != TokenType.INTEGER) {
                                throw new SemanticError("ERROR: At line " + 
                                        lineNumber + " -- Offset on semantic "
                                        + "stack is not of type integer");
                            }
                            else {
                                // pop offset, ETYPE, id
                                offset = (SymbolTableEntry) semanticStack.pop();
                                SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
                                // search for EType
                                int j = semanticStack.size() - 1;
                                while ((j >= 0) && (!semanticStack.empty()) && !(semanticStack.get(j) instanceof EType)) {
                                    j--;
                                }
                                if (j >= 0) {
                                    if (semanticStack.get(j) instanceof EType) {
                                        eType = (EType) semanticStack.remove(j);
                                    }
                                }
                                // CREATE(TEMP,id^.type)
                                SymbolTableEntry idRef = lookupHelper(id);
                                SymbolTableEntry temp = create("TEMP", idRef.getType());
                                // GEN(load id,offset,$$TEMP)
                                generate("load", id, offset, temp);
                                // push $$TEMP
                                semanticStack.push(temp);
                            }
                        }
                        // else pop offset
                        else {
                            if (offsetOnStack) {
                                offset = (SymbolTableEntry) semanticStack.pop();
                            }
                        }
                        // push ETYPE(ARITHMETIC)
                        semanticStack.push(EType.ARITHMETIC);
                        break;
                    }
                    case 49: {
                        // if ETYPE <> ARITHMETIC, ERROR
                        eType = (EType)semanticStack.peek();
                        if (eType != EType.ARITHMETIC) {
                            throw new SemanticError("ERROR on line " + 
                                    lineNumber + ": Expected arithmetic "
                                    + "expression");
                        }
                        // if not id^.is_function, ERROR
                        SymbolTableEntry id = lookupHelper(token.getValue());
                        if (!(id instanceof FunctionEntry)) {
                            throw new SemanticError("ERROR on line " + 
                                    lineNumber + ": Expected function");
                        }
                        // push new element on PARMCOUNT stack
                        // PARMCOUNT.top = 0
                        parmCount.push(0);
                        // push new element on NEXTPARM stack
                        /* set NEXTPARM = id^.parminfo {pointer to info about 
                         * parameters} */
                        nextParm.push(id.getParameterInfo());
                        break;
                    }
                    case 50: {
                        Stack<Object> tempStack = new Stack<Object>();
                        while ((!(semanticStack.peek() instanceof ProcedureEntry)) && (!(semanticStack.peek() instanceof FunctionEntry))) {
                            tempStack.push(semanticStack.pop());
                        }
                        SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
                        eType = (EType)tempStack.pop();
                        while (!tempStack.empty()) {
                            
                            SymbolTableEntry param = (SymbolTableEntry)tempStack.pop();
                            // GEN(param id)
                            generate("param", param);
                            // LOCAL_MEM = LOCAL_MEM + 1
                                localMemory++;
                        }
                        // pop PARMCOUNT.top, NEXTPARM.top, ETYPE
                        int parmCountTop = parmCount.pop();
                        nextParm.pop();
                        // if PARMCOUNT.top > id^.number_of_parameters, ERROR
                        SymbolTableEntry idRef = lookupHelper(id);
                        if (parmCountTop > idRef.getNumberOfParameters()) {
                            throw new SemanticError("ERROR on line " + 
                                    lineNumber + ": Parameter count mismatch");
                        }
                        // GEN(call id, PARMCOUNT)
                        generate("call", idRef, parmCountTop);
                        // CREATE(TEMP,id^.type)
                        SymbolTableEntry temp = create("TEMP", idRef.getType());
                        // GEN(move,id^.result,$$TEMP) {id^.result is $$function-name}
                        generate("move", lookupHelper("$$FUN_NAME"), temp);
                        // push $$TEMP
                        semanticStack.push(temp);
                        // push ETYPE(ARITHMETIC)
                        semanticStack.push(EType.ARITHMETIC);
                        break;
                    }
                    case 51: {
                        // if id^.name = READ call #51READ
                        /* look through stack, from bottom to top, for function
                         * or procedure */
                        int i = semanticStack.size()-1;
                        while (!((semanticStack.get(i) instanceof FunctionEntry) 
                                || (semanticStack.get(i) instanceof ProcedureEntry)) && (i >= 0)) {
                            i--;
                        }
                        SymbolTableEntry id = (SymbolTableEntry) semanticStack.get(i);
                        SymbolTableEntry idRef = lookupHelper(id);
                        if (idRef.getName().equalsIgnoreCase("READ")) {
                            Execute(SemanticAction.action51READ, token);
                        }
                        // if id^.name = WRITE call #51WRITE
                        else if (idRef.getName().equalsIgnoreCase("WRITE")) {
                            dumpSemanticStack();
                            Execute(SemanticAction.action51WRITE, token);
                        }
                        // else
                        else {
                            // if PARMCOUNT.top <> id^.number_of_parameters, ERROR
                            if (parmCount.peek() != idRef.getNumberOfParameters()) {
                                throw new SemanticError("ERROR on line " + lineNumber + ": Parameter count mismatch");
                            }
                            Stack<Object> tempStack = new Stack<Object>();
                            while ((!(semanticStack.peek() instanceof ProcedureEntry)) && (!(semanticStack.peek() instanceof FunctionEntry))) {
                                tempStack.push(semanticStack.pop());
                            }
                            // for each parameter (id) on stack:
                            // (NOTE: must be done from bottom to top)
                            if (tempStack.peek() instanceof EType) {
                                eType = (EType)tempStack.pop();
                            }
                            while (!tempStack.empty()) {
                                SymbolTableEntry id2 = (SymbolTableEntry) tempStack.pop();
                                if (id2.isParameter()) {
                                    // GEN(param id)
                                    generate("param", id2);
                                    // LOCAL_MEM = LOCAL_MEM + 1
                                    localMemory++;
                                    // pop id--already popped
                                }
//                                else {
//                                    // don't pop non-params
//                                    semanticStack.push(id2);
//                                }
                            }
                            // GEN(call,id, PARMCOUNT.top)
                            generate("call", id, parmCount.peek());
                            // pop PARMCOUNT.top, NEXTPARM.top, ETYPE
                            parmCount.pop();
                            nextParm.pop();
                            // pop id
                            semanticStack.pop();
                        }
                        break;
                    }
                    case 52: {
                        // pop ETYPE, id
                        SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
                        //eType = (EType) semanticStack.pop();
                        // if not id^.is_function, ERROR
                        SymbolTableEntry idRef = lookupHelper(id);
                        if (!(idRef instanceof FunctionEntry)) {
                            throw new SemanticError("ERROR on line " + 
                                    lineNumber + ": Function expected");
                        }
                        // if id^.number_of_parameters > 0, ERROR
                        if (idRef.getNumberOfParameters() > 0) {
                            throw new SemanticError("ERROR on line " + 
                                    lineNumber + ": Expected no parameters");
                        }
                        // GEN(call id, 0)
                        generate("call", id, 0);
                        // CREATE(TEMP,id^.type)
                        SymbolTableEntry temp = create("TEMP", idRef.getType());
                        // GEN(move,id^.result,$$TEMP) {id^.result is $$function-name}
                        generate("move", lookupHelper("$$FUN_NAME"), temp);
                        // push $$TEMP
                        semanticStack.push(temp);
                        // push ETYPE(ARITHMETIC)
                        semanticStack.push(EType.ARITHMETIC);
                        break;
                    }
                    case 53: {
                        eType = (EType) semanticStack.pop();
                        // if id^.is_function
                        SymbolTableEntry id = (SymbolTableEntry) semanticStack.peek();
                        SymbolTableEntry idRef = lookupHelper(id);
                        if (idRef instanceof FunctionEntry) {
                            // -if id <> CURRENTFUNCTION, ERROR
                            if (id != currentFunction) {
                                throw new SemanticError("ERROR on line " + 
                                        lineNumber + ": Wrong function being "
                                        + "called");
                            }
                            // pop ETYPE, id
                            id = (SymbolTableEntry) semanticStack.pop();
                            // -push id^.result {i.e., $$function-name}
                            semanticStack.push(lookupHelper("$$FUN_NAME"));
                        }
                        // -push ETYPE(ARITHMETIC)
                        semanticStack.push(EType.ARITHMETIC);                    
                        break;
                    }
                    case 54: {
                        // if not id^.is_procedure, ERROR
                        SymbolTableEntry id = lookupHelper(token.getValue());
                        if (!(id instanceof ProcedureEntry)) {
                            throw new SemanticError("ERROR on line " + 
                                    lineNumber + ": identifier " + 
                                    token.getValue() + " is not a procedure");
                        }
                        break;
                    }
                    case 55: {
                        // BACKPATCH(GLOBAL_STORE,GLOBAL_MEM)
                        backpatch(globalStore, globalMemory);
                        // GEN(free GLOBAL_MEM)
                        generate("free", globalMemory);
                        // GEN(PROCEND)
                        generate("PROCEND");
                        break;
                    }
                    /*  : GEN(PROCBEGIN main)
                        : GLOBAL_STORE = NEXTQUAD
                        : GEN(alloc,_) */
                    case 56: {
                        generate("PROCBEGIN", "main");
                        globalStore = quads.getNextQuad();
                        generate("alloc", "_");
                        break;
                    }
                    // 51WRITE
                    case 59: {
                        /* for each parameter on stack:
                         * (NOTE: must be done from bottom to top) */
                        Stack<Object> tempStack = new Stack<Object>();
                        int i = semanticStack.size()-1;
                        while (!(semanticStack.get(i) instanceof EType) 
                                && !((semanticStack.get(i) instanceof ProcedureEntry) && (((ProcedureEntry)semanticStack.get(i)).getName().equalsIgnoreCase("WRITE")))
                                && (i > 0)) {
                            tempStack.push(semanticStack.pop());
                            i--;
                        }
                        while (!tempStack.empty()) {
                            SymbolTableEntry id = (SymbolTableEntry) tempStack.pop();
                            SymbolTableEntry idRef = lookupHelper(id);
                            if (idRef == null) {
                                idRef = constantTable.lookup(id);
                            }
                            /* GEN(print,"<id^.name> = ")   {<id^.name> 
                             * is the name of the variable} */
                            generate("print", "\"" + idRef.getName() + " = \"");
                            // if id^.type = REAL, GEN(foutp,id)
                            if (idRef.getType() == TokenType.REAL) {
                                generate("foutp", id);
                            }
                            // else GEN(outp,id)
                            else {
                                generate("outp", id);
                            }
                            // GEN(newl)
                            generate("newl");
                        }
                        // pop PARMCOUNT.top, ETYPE
                        if (semanticStack.peek() instanceof EType) {
                            eType = (EType) semanticStack.pop();
                        }
                        parmCount.pop();
                        // pop id
                        semanticStack.pop();
                        break;
                    }
                    // 51READ
                    case 60: {
                        /* for each parameter on stack: (NOTE: must be done from 
                         * bottom to top) */
                        Stack<Object> tempStack = new Stack<Object>();
                        while (!semanticStack.empty()) {
                            tempStack.push(semanticStack.pop());
                        }
                        while (!tempStack.empty()) {
                            // pop id
                            Object id = tempStack.pop();
                            if (id instanceof VariableEntry) {
                                //if (((VariableEntry) id).isParameter()) {
                                    /* if id^.type = REAL, GEN(finp,id) else 
                                     * GEN(inp,id) */
                                    SymbolTableEntry idRef = lookupHelper((SymbolTableEntry) id);
                                    if (idRef.getType() == TokenType.REAL) {
                                        generate("finp", (SymbolTableEntry)id);
                                    }
                                    else {
                                        generate("inp", (SymbolTableEntry)id);
                                    }
                                //}
                            }
                            else {
                                semanticStack.push(id);
                            }
                        }
                        // pop PARMCOUNT.top, ETYPE
                        eType = (EType)semanticStack.pop();
                        parmCount.pop();
                        // pop id
                        semanticStack.pop();
                        break;
                    }
                }
                dumpSemanticStack();
                System.out.println("Global Table:");
                globalTable.dumpTable();
                System.out.println();
                System.out.println("Local Table:");
                localTable.dumpTable();
                System.out.println();
                System.out.println("Global/Local: " + global);
                System.out.println();
                dumpParmCount();
                System.out.println("Line Number: " + lineNumber + "\n");
                System.out.println("Array: " + this.isArray + "\n");
                this.dumpNextParm();
                System.out.println("Global Memory: " + this.globalMemory + "\n");
                System.out.println("Global Store: " + this.globalStore + "\n");
        }

        /**
         * Insert default procedures (read, write, and main) into the 
         * global table.
         * 
         * @param globalTable The global symbol table.
         */
        private void InstallBuiltins(SymbolTable globalTable) {
            LinkedList<ParmEntry> emptyParmList = new LinkedList<ParmEntry>();
            emptyParmList.add(new ParmEntry());
            globalTable.insert(new ProcedureEntry("read", emptyParmList));
            globalTable.insert(new ProcedureEntry("write", emptyParmList));
            globalTable.insert(new ProcedureEntry("main", 0));
        }
        
        /**
         * Insert the keywords of the language into a special keyword 
         * table, so that we can tell the difference between keywords 
         * and identifiers easily.
         */
        private void fillOutKeywordTable() {
            keywordTable.insert(new KeywordEntry("program", TokenType.PROGRAM));
            keywordTable.insert(new KeywordEntry("begin", TokenType.BEGIN));
            keywordTable.insert(new KeywordEntry("end", TokenType.END));
            keywordTable.insert(new KeywordEntry("var", TokenType.VAR));
            keywordTable.insert(new KeywordEntry("function",
                    TokenType.FUNCTION));
            keywordTable.insert(new KeywordEntry("procedure",
                    TokenType.PROCEDURE));
            keywordTable.insert(new KeywordEntry("result", TokenType.RESULT));
            keywordTable.insert(new KeywordEntry("integer", TokenType.INTEGER));
            keywordTable.insert(new KeywordEntry("real", TokenType.REAL));
            keywordTable.insert(new KeywordEntry("array", TokenType.ARRAY));
            keywordTable.insert(new KeywordEntry("of", TokenType.OF));
            keywordTable.insert(new KeywordEntry("if", TokenType.IF));
            keywordTable.insert(new KeywordEntry("then", TokenType.THEN));
            keywordTable.insert(new KeywordEntry("else", TokenType.ELSE));
            keywordTable.insert(new KeywordEntry("while", TokenType.WHILE));
            keywordTable.insert(new KeywordEntry("do", TokenType.DO));
            keywordTable.insert(new KeywordEntry("not", TokenType.NOT));
        }
        
        /**
         * Print the contents of the semantic stack to the screen (for 
         * debugging purposes).
         */
        private void dumpSemanticStack() {
            System.out.println("Semantic Stack:");
            /* print out backwards to reflect actual stack structure */
            for (int i = semanticStack.size()-1; i >= 0; i--) {
                if (semanticStack.get(i) instanceof Token) {
                    Token temp = (Token) semanticStack.get(i);
                    System.out.println("Token: " + temp.getType() + " " + temp.getValue());
                }
                else if (semanticStack.get(i) instanceof SymbolTableEntry) {
                    SymbolTableEntry temp = (SymbolTableEntry) semanticStack.get(i);
                    System.out.print(temp.getClass().getName() + ": " + temp.getType() + " " + temp.getName());
                    if (temp instanceof ProcedureEntry) {
                        System.out.print(" Number of Parameters: " + temp.getNumberOfParameters());
                        System.out.print(" Parameters: ");
                        for (int j = 0; j < temp.getParameterInfo().size(); j++) {
                            ParmEntry current = temp.getParameterInfo().get(j);
                            System.out.print(current.getType() + " " + current.getName() + " ");
                        }
                    }
                     System.out.print("\n");
                }
                else {
                    System.out.println("Other: " + semanticStack.get(i));
                }
            }
            System.out.println("");
        }
        
        private String genHelper(SymbolTableEntry operand) {
            String result;
            if (operand instanceof ConstantEntry) {
                SymbolTableEntry temp = create("TEMP", operand.getType());
                generate("move", operand.getName(), temp);
                int address = Math.abs(temp.getAddress());
                result = Integer.toString(address);
            }
            else {
                int address = Math.abs(operand.getAddress());
                result = Integer.toString(address);
            }
            if (operand.isParameter()) {
                return "^%" + result;
            }
            else {
                return prefixHelper(result);
            }
        }
        
        private String prefixHelper(String operand) {
            String result = "";
            if (global) {
                result = "_" + operand;
            }
            else {
                result = "%" + operand;
            }
            return result;
        }
        
        /**
         * Generates a new quadruple containing the instruction given in TVICODE.
         * 
         * GEN replaces all id references with memory addresses: 
         *      _n for offsets in GLOBAL memory 
         *      %n for offsets into the current stack frame (LOCAL variables) 
         *      ^%n for parameters (dereferences n) 
         * ("n" is the absolute value of the corresponding id.address). 
         * 
         * GEN also handles putting constants in actual memory locations. Before 
         * generating the quadruple, GEN does the following for each id: 
         * 
         *      if id^.is_constant 
         *          CREATE(TEMP,id.type) 
         *          GEN(move #,$$TEMP) {# is the value of the constant}
         *
         * When the quadruple containing the constant reference is generated,
         * $$TEMP's address is used instead.
         * 
         * All parameters are passed by reference. So, when generating PARAM 
         * statements, GEN produces the following:
         * 
         *      PARAM @_n   for global memory
         *      PARAM @%n   for (local) variables in current stack frame
         *      PARAM %n    for parameters that are themselves parameters
         *
         * GEN also increments NEXTQUAD after each quadruple is generated.
         *
         * @param tviCode
         * @param operand1
         * @param operand2
         * @param operand3 
         */
        private void generate(String tviCode, SymbolTableEntry operand1, 
                SymbolTableEntry operand2, SymbolTableEntry operand3) {
            String address1 = genHelper(operand1);
            String address2 = genHelper(operand2);
            String address3 = genHelper(operand3);
            String[] quad = {tviCode, address1, address2, address3};
            quads.addQuad(quad);
            quads.incrementNextQuad();
        }
        
        private void generate (String tviCode, SymbolTableEntry operand1, String
            operand2) {
            String address1 = genHelper(operand1);
            String address2 = operand2;
            String[] quad = {tviCode, address1, address2};
            quads.addQuad(quad);
            quads.incrementNextQuad();
        }
        
        private void generate (String tviCode, SymbolTableEntry operand1,
            SymbolTableEntry operand2) {
            String address1 = genHelper(operand1);
            String address2 = genHelper(operand2);
            String[] quad = {tviCode, address1, address2};
            quads.addQuad(quad);
            quads.incrementNextQuad();
        }
        
        private void generate (String tviCode, String operand1, SymbolTableEntry
            operand2) {
            String address1 = operand1;
            String address2 = genHelper(operand2);
            String[] quad = {tviCode, address1, address2};
            quads.addQuad(quad);
            quads.incrementNextQuad();
        }
        
        private void generate (String tviCode, String operand1) {
            String address1 = operand1;
            String[] quad = {tviCode, address1};
            quads.addQuad(quad);
            quads.incrementNextQuad();
        }
        
        private void generate(String tviCode) {
            String[] quad = {tviCode};
            quads.addQuad(quad);
            quads.incrementNextQuad();
        }
        
        private void generate(String tviCode, SymbolTableEntry operand1, 
                int operand2) {
            String address1 = genHelper(operand1);
            String[] quad = {tviCode, address1, Integer.toString(operand2)};
            quads.addQuad(quad);
            quads.incrementNextQuad();
        }
        
        private void generate(String tviCode, int address) {
            String address1 = Integer.toString(address);
            String[] quad = {tviCode, address1};
            quads.addQuad(quad);
            quads.incrementNextQuad();
        }
        
        private void generate(String tviCode, SymbolTableEntry operand1) {
            String address1 = genHelper(operand1);
            String[] quad = {tviCode, address1};
            quads.addQuad(quad);
            quads.incrementNextQuad();
        }
        
        private void generate(String tviCode, SymbolTableEntry operand1, 
                SymbolTableEntry operand2, String name) {
            String address1 = genHelper(operand1);
            String address2 = genHelper(operand2);
            String address3 = name;
            String[] quad = {tviCode, address1, address2, address3};
            quads.addQuad(quad);
            quads.incrementNextQuad();
        }
        
        private void generate(String tviCode, SymbolTableEntry operand1, String name1,
                String name2) {
            String address1 = genHelper(operand1);
            String[] quad = {tviCode, address1, name1, name2};
            quads.addQuad(quad);
            quads.incrementNextQuad();
        }
        
        private void generate(String tviCode, String operand1, SymbolTableEntry operand2,
                String operand3) {
            String address2 = genHelper(operand2);
            String[] quad = {tviCode, operand1, address2, operand3};
            quads.addQuad(quad);
            quads.incrementNextQuad();
        }
        
        // generate("bge", entry1, id2, quads.getNextQuad()-2);
        private void generate(String tviCode, SymbolTableEntry operand1, 
                SymbolTableEntry operand2, int operand3) {
            String address1 = genHelper(operand1);
            String address2 = genHelper(operand2);
            String[] quad = {tviCode, address1, address2, Integer.toString(operand3)};
            quads.addQuad(quad);
            quads.incrementNextQuad();
        }
        
        // generate("stor", temp, offset, id1);
        private void generate(String tviCode, SymbolTableEntry operand1, 
                Token operand2, SymbolTableEntry operand3) {
            String address1 = genHelper(operand1);
            String address2 = operand2.getValue();
            String address3 = genHelper(operand3);
            String[] quad = {tviCode, address1, address2, address3};
            quads.addQuad(quad);
            quads.incrementNextQuad();
        }
        
        private void generate(String tviCode, SymbolTableEntry operand1, 
                int operand2, SymbolTableEntry operand3) {
            String address1 = genHelper(operand1);
            String address2 = Integer.toString(operand2);
            String address3 = genHelper(operand3);
            String[] quad = {tviCode, address1, address2, address3};
            quads.addQuad(quad);
            quads.incrementNextQuad();
        }
        
        private void generate(String tviCode, String operand1, int operand2) {
            String[] quad = {tviCode, operand1, Integer.toString(operand2)};
            quads.addQuad(quad);
            quads.incrementNextQuad();
        }
        
        /**
         * CREATE(NAME,type)
        Creates a new memory location by doing the following:
         - insert $$NAME in symbol table (Variable_entry)
         - $$NAME.type = type
         - $$NAME.address = NEGATIVE value of GLOBAL_MEM or LOCAL_MEM (as appropriate)
         - increment GLOBAL_MEM or LOCAL_MEM (as appropriate)
         - return $$NAME^
         */
        private SymbolTableEntry create(String name, TokenType type) {
            String name2 = "$$" + name;
            int address;
            VariableEntry entry;
            if (global) {
                address = -globalMemory;
                entry = new VariableEntry(name2, address, type);
                globalTable.insert(entry);
                globalMemory++;
            }
            else {
                address = -localMemory;
                entry = new VariableEntry(name2, address, type);
                localTable.insert(entry);
                localMemory++;
            }
            return entry;
        }
        
        /* TYPECHECK(id1,id2) 
 
            Checks the types of id1 and id2, and 
            returns the following : 
             0 if id1 and id2 are both integers 
             1 if id1 and id2 are both reals 
             2 if id1 is real and id2 is integer 
             3 if id1 is integer and id2 is real */
        private int typeCheck(Token id1, Token id2) {
            TokenType type1 = id1.getType();
            TokenType type2 = id2.getType();
            if ((type1 == TokenType.INTEGER) && (type2 == TokenType.INTEGER))  {
                return 0;
            }
            else if ((type1 == TokenType.REAL) && (type2 == TokenType.REAL)) {
                return 1;
            }
            else if ((type1 == TokenType.REAL) && (type2 == TokenType.INTEGER)) {
                return 2;
            }
            else if ((type1 == TokenType.INTEGER) && (type2 == TokenType.REAL)) {
                return 3;
            }
            return -1; // failed
        }
        
        private int typeCheck(SymbolTableEntry id1, SymbolTableEntry id2) {
            TokenType type1 = id1.getType();
            TokenType type2 = id2.getType();
            if ((type1 == TokenType.INTEGER) && (type2 == TokenType.INTEGER))  {
                return 0;
            }
            else if ((type1 == TokenType.REAL) && (type2 == TokenType.REAL)) {
                return 1;
            }
            else if ((type1 == TokenType.REAL) && (type2 == TokenType.INTEGER)) {
                return 2;
            }
            else if ((type1 == TokenType.INTEGER) && (type2 == TokenType.REAL)) {
                return 3;
            }
            return -1; // failed
        }
        
        /**
         * Creates a new list containing only i, an index into the array 
         * of quadruples. Returns a pointer to the list it has made. 
         * @param i Index into the array of quadruples.
         * @return 
         */
        private LinkedList makeList(int i) {
            LinkedList list = new LinkedList<Integer>();
            list.add(i);
            return list;
        }
        
        /**
         * Concatenates the lists pointed to by p1 and p2, returns a 
         * pointer to the concatenated list.
         * @param p1 The first list to be concatenated.
         * @param p2 The second list to be concatenated.
         * @return 
         */
        private LinkedList merge(LinkedList p1, LinkedList p2) {
            p1.addAll(p2);
            return p1;
        }
        
        /**
         * Inserts i as the target label for each of the statements on 
         * the list pointed to by p.
         * @param p LinkedList (E.true or E.false) to be filled in
         * @param i Integer to fill in LinkedList
         */
        private void backpatch(LinkedList<Integer> p, int i) {
            ListIterator iter = p.listIterator(0);
            while (iter.hasNext()) {
                int quadNum = (int) iter.next();
                String[] quad = quads.getQuad(quadNum);
                for (int j = 0; j < quad.length; j++) {
                    if (quad[j].equals("_")) {
                        quads.setField(quadNum, j, Integer.toString(i));
                    }
                    else if (quad[j].equals("goto _")) {
                        quads.setField(quadNum, j, "goto " + Integer.toString(i));
                    }
                }
            }
        }
        
        private void backpatch(int p, int i) {
            String[] quad = quads.getQuad(p);
            for (int j = 0; j < quad.length; j++) {
                if (quad[j].equals("_")) {
                    quads.setField(p, j, Integer.toString(i));
                }
            }
        }
        
        /**
         * Helper function for inserting SymbolTableEntry's in symbol tables.
         * @param id SymbolTableEntry to insert.
         */
        private void insertHelper(SymbolTableEntry id) {
            if (global) {
                globalTable.insert(id);
            }
            else {
                localTable.insert(id);
            }
        }
        
        /**
         * Helper function for looking up SymbolTableEntry's in symbol tables.
         * @param id SymbolTableEntry to look up.
         * @return Reference to SymbolTableEntry in symbol table.
         */
        private SymbolTableEntry lookupHelper(SymbolTableEntry id) {
            if (global) {
                return globalTable.lookup(id);
            }
            else {
                if (localTable.lookup(id) != null) {
                    return localTable.lookup(id);
                }
                else {
                    return globalTable.lookup(id);
                }
            }
        }
        
        private SymbolTableEntry lookupHelper(String id) {
            if (global) {
                return globalTable.lookup(id);
            }
            else {
                if (localTable.lookup(id) != null) {
                    return localTable.lookup(id);
                }
                else {
                    return globalTable.lookup(id);
                }
            }
        }
        
        private void dumpParmCount() {
            System.out.println("ParmCount Stack:");
            /* print out backwards to reflect actual stack structure */
            for (int i = parmCount.size()-1; i >= 0; i--) {
                System.out.println(parmCount.get(i));
            }
            System.out.println("");
        }
        
        private void dumpNextParm() {
            System.out.println("Next Parm: ");
            for (int i = nextParm.size()-1; i >= 0; i--) {
                LinkedList<ParmEntry> current = nextParm.get(i);
                Iterator iter = current.listIterator(0);
                while (iter.hasNext()) {
                    ParmEntry next = (ParmEntry)iter.next();
                    System.out.print(next.getType() + " " + next.getName() + ", ");
                }
                System.out.print("\n");
            }
            System.out.println();
        }
        
}