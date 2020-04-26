package minilexical;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Analyzer {

    Queue<Token> tokens = new LinkedList<>();
    ArrayList<String> datatypes = new ArrayList<>();
    Node root = new Node("program" ,"Begin", true);
    public Analyzer() {
        datatypes.add("<VOID>");
        datatypes.add("<INT>");
        datatypes.add("<FLOAT>");
        datatypes.add("<BOOLEAN>");
    }

    void printTokens() {
        tokens.forEach((token) -> {
            System.out.println(token.Type + "   " + token.Value);
        });
        System.out.println("----------------------------------------");
    }
    void ReadFile() throws FileNotFoundException, IOException {
        File file = new File("Tokens.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String s = "";
        while ((s = br.readLine()) != null) {
            
//            System.out.println(s.toString());
            Token t = new Token(s.split(" ")[0].trim(), s.split(" ")[1].trim(), 0);
            if (!(t.Type.equals("<MULTI_COMMENT>") || t.Type.equals("<SINGLE_COMMENT>")))
                 tokens.add(t);
        }
        br.close();

    }
    Node Program() throws ParserException {
        if (!tokens.isEmpty()) {
            root.childs.add(decl_list());
            return root;
        } else {
            throw new ParserException("Nothing to parse");
        }
    }

    private Node decl_list() throws ParserException {
        Node temp = new Node("decl_list" ,"program", true);
        temp.childs.add(decl("decl_list"));
        temp.childs.add(new_decl_list("decl_list"));
        return temp;
    }

    private Node decl(String father) throws ParserException {
        Node temp = new Node("decl" ,father, true);
        Token type = tokens.poll();
        Token name = tokens.poll();
        Token next = tokens.peek();
        
        if (next.Type.equals("<LEFT_BRACKETS>")) {
            temp.childs.add(fun_decl(tokens.poll(), type, name , "decl"));
        } else {
            temp.childs.add(var_decl(type, name , "decl"));
        }
        return temp;

    }

    private Node new_decl_list(String d) throws ParserException {
        Node temp = new Node("new_decl_list" ,d, true);
        if (tokens.isEmpty()) {
            return null;
        }
        temp.childs.add(decl("new_decl_list"));
        temp.childs.add(new_decl_list(d));
        return temp;

    }

    private Node fun_decl(Token LB, Token Type, Token name, String decl) throws ParserException {
        Node temp = new Node("fun_decl" ,decl, true);
        if (LB.Type.equals("<LEFT_BRACKETS>")) {
            temp.childs.add(type_spec(Type,"fun_decl"));
            temp.childs.add(ID(name , "fun_decl"));
            temp.childs.add(new Node("(" ,decl, false));
            // ---- LB Here is poll from prev function
            temp.childs.add(params());
            temp.childs.add(new Node(")" ,decl, false));//removed from new_param_list
            if (tokens.peek().Type.equals("<LEFT_BRACKETS_PARTH>")) {
                tokens.poll();
                temp.childs.add(compound_stmt("fun_decl"));
            } else {
                throw new ParserException("No Bracket Found to function : {");
            }
        } else {
            throw new ParserException("Missing  Bracket: ( of function");
        }
        return temp;

    }

    private Node var_decl(Token Type, Token name, String decl) throws ParserException {
        Node temp = new Node("var_decl" ,decl, true);
        temp.childs.add(type_spec(Type, "var_decl"));
        temp.childs.add(ID(name, "var_decl"));
        temp.childs.add(new_var_decl(tokens.peek(),"var_decl"));
        if (tokens.poll().Type.equals("<SEMI-COLON>")) {
            temp.childs.add(new Node(";" ,"var_decl", false));
        } else {
            throw new ParserException("Missed semi colon ;");
        }
        return temp;
    }

    private Node type_spec(Token t, String father) throws ParserException {
        Node temp = new Node("type_spec" ,father, true);
        // TODO some Implementation for each if

        switch (t.Type) {
            case "<VOID>":
                temp.childs.add(new Node("VOID","type_spec",false));
                
                break;
            case "<BOOLEAN>":
                temp.childs.add(new Node("BOOL" , "type_spec",false));
                break;
            case "<INT>":
                temp.childs.add(new Node("INT", "type_spec", false));
                break;
            case "<FLOAT>":
                temp.childs.add(new Node("FLOAT" ,"type_spec", false));
                break;
            default:
                throw new ParserException("Wrong Variable Type : " + t.Value);
        }
        return temp;

    }

    private Node ID(Token name, String father) throws ParserException {
        Node temp = new Node("ID" , father , true);
        if (name.Type.equals("<ID>")) {
            temp.childs.add(new Node(name.Value , "ID",false));
        } else {
            throw new ParserException("Wrong function Name : " + name.Value);
        }
        return temp;
    }
    private Node new_var_decl(Token next, String var_decl) throws ParserException {
        Node temp = new Node("new_var_decl" , var_decl,true);
        if (tokens.peek().Type.equals("<LEFT_SQUARE_B>")) {
            tokens.poll();
            temp.childs.add(new Node("[","new_var_decl",false));
            Token next1 = tokens.poll();
            if (next1.Type.equals("<RIGHT_SQUARE_B>")) {
                temp.childs.add(new Node("]","new_var_decl",false));
            } else {
                throw new ParserException("Missing Closing Bracket: ]");
            }
        }
        return temp;

    }

    private Node params() throws ParserException {  // int Type , float name ) 
        Node temp = new Node("params" , "fun_decl",true);
        Token v = tokens.poll();
        if (v.Type.equals("<VOID>")) {
            temp.childs.add(new Node("VOID","params",false));
            tokens.poll();
        } else {
            Token t = v;
            Token n = tokens.poll();
            temp.childs.add(param_list(t, n));
        }
        return temp;
    }

    private Node param_list(Token Type, Token name) throws ParserException {
        Node temp = new Node("param_list","params",true);
        temp.childs.add(param(Type, name , "param_list"));//get polling
        temp.childs.add(new_param_list("param_list"));
        return temp;
    }

    private Node new_param_list(String p) throws ParserException {
        Node temp = new Node("new_param_list",p,true);
        if (tokens.peek().Type.equals("<RIGHT_BRACKETS>")) {
            tokens.poll();
            return null;
        }
        if (tokens.poll().Type.equals("<COMMA>")) {
            temp.childs.add(new Node("," , "new_param_list",false));
            temp.childs.add(param(tokens.poll(), tokens.poll(), "new_param_list"));
            temp.childs.add(new_param_list("new_param_list"));
        } else {
            throw new ParserException("not found comma in function parameter");
        }
        return temp;
    }

    private Node param(Token Type, Token name, String p) throws ParserException {
        Node temp = new Node("param",p,true);
        temp.childs.add(type_spec(Type, "param"));
        temp.childs.add(ID(name, "param"));
        temp.childs.add(new_var_decl(tokens.peek(), "param"));
        return temp;
    }

    private Node compound_stmt(String father) throws ParserException {
        Node temp = new Node("compound_stmt",father,true);
        temp.childs.add(new Node("{","compound_stmt",false));
        temp.childs.add(local_decls(tokens.peek(), tokens.peek(),"compound_stmt"));
        temp.childs.add(stmt_list(tokens.peek(),"compound_stmt"));
        temp.childs.add(new Node("}","compound_stmt",false));
        return temp;
    }

    private Node local_decls(Token Type, Token name , String f) throws ParserException {
        Node temp = new Node("local_decls",f,true);
        temp.childs.add(new_local_decls(Type, name , "local_decls"));
        return temp;
    }

    private Node stmt_list(Token s,String f) throws ParserException {
        Node temp = new Node("stmt_list",f,true);
        temp.childs.add(new_stmt_list(s , "stmt_list"));
        return temp;
    }

    private Node new_local_decls(Token Type, Token name , String f) throws ParserException {
        //  some note about peek and poll for first variable 
        Node temp = new Node("new_local_decls",f,true);
        if (!datatypes.contains(Type.Type)) {
            return null;
        }
        temp.childs.add(local_decl(tokens.poll(), tokens.poll(),"new_local_decls"));
        temp.childs.add(new_local_decls(tokens.peek(), tokens.peek(),f));
        return temp;

    }

    private Node local_decl(Token Type, Token name , String f) throws ParserException {
        Node temp = new Node("local_decl",f,true);
        temp.childs.add(type_spec(Type, "local_decl"));
        temp.childs.add(ID(name, "local_decl"));
        temp.childs.add(new_var_decl(tokens.peek(), "local_decl"));

        if (tokens.poll().Type.equals("<SEMI-COLON>")) {
            temp.childs.add(new Node(";" ,"local_decl", false));
        } else {
            throw new ParserException("Missed semi colon ;");
        }
        return temp;

    }

    private Node new_stmt_list(Token s, String f) throws ParserException {
        Node temp = new Node("new_stmt_list",f,true);
        if (s.Type.equals("<RIGHT_BRACKETS_PARTH>")) {
            tokens.poll();
            return null;
        }
        temp.childs.add(stmt(tokens.peek() , "new_stmt_list"));
        temp.childs.add(new_stmt_list(tokens.peek(),f));
        return temp;

    }

    private Node stmt(Token s , String f) throws ParserException {
        Node temp = new Node("stmt" , f , true);
        switch (s.Type) {
            case "<IF>":
                tokens.poll();
                temp.childs.add(if_stmt("stmt"));
                break;
            case "<WHILE>":
                tokens.poll();
                temp.childs.add(while_stmt("stmt"));
                break;
            case "<RETURN>":
                tokens.poll();
                temp.childs.add(return_stmt("stmt"));
                break;
            case "<BREAK>":
                tokens.poll();
                temp.childs.add(break_stmt("stmt"));
                break;
            case "<LEFT_BRACKETS_PARTH>":
                tokens.poll();
                temp.childs.add(compound_stmt("stmt"));
                break;
            default:
                temp.childs.add(expr_stmt("stmt"));
                break;
        }

        return temp;
    }

    private Node new_return_stmt(String s) throws ParserException {
        Node temp = new Node("new_return_stmt",s,true);
        temp.childs.add(expr(1 ,"new_return_stmt" ));
        return temp;
    }

    private Node if_stmt(String s) throws ParserException {

        Node temp = new Node("if_stmt",s,true);
        temp.childs.add(new Node("IF" ,"if_stmt", false));
        if (tokens.peek().Type.equals("<LEFT_BRACKETS>")) {
            tokens.poll();
            temp.childs.add(new Node("(","if_stmt",false));
            temp.childs.add(expr(1,"if_stmt"));
//            printTokens();
            if (tokens.poll().Type.equals("<RIGHT_BRACKETS>")) {
                temp.childs.add(new Node(")" ,"if_stmt", false));
                temp.childs.add(stmt(tokens.peek(),"if_stmt"));
                temp.childs.add(new_if_stmt("if_stmt"));
            } else {
                throw new ParserException("No Bracket Found : )");
            }
        } else {
            throw new ParserException("No Bracket Found : (");
        }
        return temp;

    }

    private Node new_if_stmt(String f) throws ParserException {
        Node temp = new Node("new_if_stmt",f,true);
        if (tokens.peek().Type.equals("<ELSE>")) {
            tokens.poll();
            temp.childs.add(stmt(tokens.peek(),"new_if_stmt"));
        }
        return temp;

    }

    private Node while_stmt(String s) throws ParserException {
        Node temp = new Node("while_stmt",s,true);
        temp.childs.add(new Node("while" ,"while_stmt", false));

        if (tokens.peek().Type.equals("<LEFT_BRACKETS>")) {
            tokens.poll();
            temp.childs.add(new Node("(","while_stmt",false));
            temp.childs.add(expr(1,"while_stmt"));
            if (tokens.peek().Type.equals("<RIGHT_BRACKETS>")) {
                tokens.poll();
                temp.childs.add(new Node(")" ,"while_stmt", false));
                temp.childs.add(stmt(tokens.peek(),"while_stmt"));
            } else {
                throw new ParserException("No Bracket Found : )");
            }
        } else {
            throw new ParserException("No Bracket Found : (");
        }

        return temp;
    }

    private Node return_stmt(String s) throws ParserException {
        Node temp = new Node("return_stmt",s,true);
        temp.childs.add(new Node("RETURN" ,"return_stmt", false));
        if (!tokens.peek().Type.equals("<SEMI-COLON>")) {
            temp.childs.add(new_return_stmt("return_stmt"));
            if (!tokens.peek().Type.equals("<SEMI-COLON>")) {
                throw new ParserException("No semi colon Found : ;");
            }

        }
        temp.childs.add(new Node(";" ,"return_stmt", false));
        tokens.poll();
        return temp;
    }

    private Node break_stmt(String s) throws ParserException {
        Node temp = new Node("break_stmt",s,true);
        temp.childs.add(new Node("break" ,"break_stmt",false));
        if (!tokens.poll().Type.equals("<SEMI-COLON>")) {
            throw new ParserException("No semi colon Found : ;");
        }
        temp.childs.add(new Node(";" ,"break_stmt", false));
        return temp;
    }
// expr → IDENT new_exp = expr
//----------------------- Expression Handling --------------------

    public Node expr_stmt(String s) throws ParserException {
        Node temp = new Node("expr_stmt" ,s, true);

        if (!tokens.peek().Type.equals("<SEMI-COLON>")) {
            temp.childs.add(expr(1,"expr_stmt"));
            if (!tokens.poll().Type.equals("<SEMI-COLON>")) {
                throw new ParserException("No semi colon Found : ;");
            }
            temp.childs.add(new Node(";","expr_stmt",false));
        }
        return temp;
    }

    private Node expr(int type , String father) throws ParserException {//not care about ( and ) --- begelo mn 8er ( we berg3 be ) mn 8er ma e4elo
        Node temp = new Node("expr" , father, true);
        if (type == 1) {
            temp.childs.add(expr(0,father));
            temp.childs.add(new_expr2("expr"));
        }
        Token check = tokens.peek();
        switch (check.Type) {
            case "<ID>":
                temp.childs.add(ID(check, "expr"));
                tokens.poll();
                temp.childs.add(new_expr1("expr"));
                break;
            case "<NOT>":
                temp.childs.add(new Node("!" ,"expr", false));
                tokens.poll();
                expr(1,father);
                break;
            case "<MINUS>":
                temp.childs.add(new Node("-", "expr",false));
                tokens.poll();
                expr(1,father);
                break;
            case "<PLUS>":
                temp.childs.add(new Node("+","expr", false));
                tokens.poll();
                expr(1,father);
                break;
            case "<LEFT_BRACKETS>":
                temp.childs.add(new Node("(", "expr",false));
                tokens.poll();
                expr(1,father);
                if (check.Type.equals("<RIGHT_BRACKETS>")) {
                    temp.childs.add(new Node(")","expr", false));
                    tokens.poll();
                } else {
                    throw new ParserException("No right square : )");
                }   break;
            case "<INTEGRAL_LITERAL>":
            case "<STRING_LITERAL>":
            case "<FLOAT_LITERAL>":
            case "<CHAR_LITERAL>":
                temp.childs.add(new Node(check.Value, "expr",false));
                tokens.poll();
                break;
            case "<NEW>":
                temp.childs.add(new Node("NEW", "expr",false));                
                tokens.poll();
                temp.childs.add(type_spec(tokens.poll(), "fun_decl"));
                if (tokens.peek().Type.equals("<LEFT_SQUARE_B>")) {
                    
                    temp.childs.add(new Node("[", "expr",false));
                    tokens.poll();
                    temp.childs.add(expr(1,father));
                    if (tokens.peek().Type.equals("<RIGHT_SQUARE_B>")) {
                        temp.childs.add(new Node("]","expr", false));
                        tokens.poll();
                    } else {
                        throw new ParserException("No right square : ]");
                    }
                } else {
                    throw new ParserException("No left square for express : [");
                }   break;
            default:
                break;
        }
        return temp;
    }

    private Node new_expr1(String father) throws ParserException {
        Node temp = new Node("new_expr1" , father,true);
        if (tokens.peek().Type.equals("<LEFT_SQUARE_B>")) {

            temp.childs.add(new Node("[","new_expr1", false));
            tokens.poll();
            temp.childs.add(expr(1,"new_expr1"));
            if (tokens.peek().Type.equals("<RIGHT_SQUARE_B>")) {
                temp.childs.add(new Node("]","new_expr1", false));
                tokens.poll();
                if (tokens.peek().Type.equals("<ASSIGN_OPERATOR>")) {
                    temp.childs.add(new Node("=","new_expr1", false));
                    tokens.poll();
                    temp.childs.add(expr(1,"new_expr1"));
                }
            } else {
                throw new ParserException("No right square : ]");
            }
        } else if (tokens.peek().Type.equals("<ASSIGN_OPERATOR>")) {
            temp.childs.add(new Node("=", "new_expr1",false));
            tokens.poll();
            temp.childs.add(expr(1,"new_expr1"));
        } else if (tokens.peek().Type.equals("<LEFT_SQUARE_B>")) {
            temp.childs.add(new Node("[", "new_expr1",false));
            temp.childs.add(expr(1,"new_expr1"));
            if (tokens.peek().Type.equals("<RIGHT_SQUARE_B>")) {
                tokens.poll();
                temp.childs.add(new Node("]","new_expr1", false));
            } else {
                throw new ParserException("No right bracket : ]");
            }
        } else if (tokens.peek().Type.equals("<DOT>"))//todo
        {
            tokens.poll();
            temp.childs.add(new Node(".", "new_expr1",false));
            if (tokens.poll().Value.equals("size")) {
                temp.childs.add(new Node("size","new_expr1", false));
            }

        } else if (tokens.peek().Type.equals("<LEFT_BRACKETS>")) {
            temp.childs.add(new Node("(","new_expr1", false));
            tokens.poll();
            temp.childs.add(args("new_expr1"));
            if (tokens.peek().Type.equals("<RIGHT_BRACKETS>")) {
                temp.childs.add(new Node(")","new_expr1", false));
                tokens.poll();
            } else {
                throw new ParserException("No right bracket : )");
            }
        } else {
            return null;
        }
        return temp;
    }

    private Node new_expr2(String father) throws ParserException {
        Node temp = new Node("new_expr2",father,true);

        switch (tokens.peek().Type) {
            case "<OR>":
                tokens.poll();
                temp.childs.add(new Node("||","new_expr2", false));
                temp.childs.add(expr(1,"new_expr2"));
                break;
            case "<EQUAL>":
                tokens.poll();
                temp.childs.add(new Node("==","new_expr2", false));
                temp.childs.add(expr(1,"new_expr2"));
                break;
            case "<NOT_EQUAL>":
                tokens.poll();
                temp.childs.add(new Node("!=", "new_expr2",false));
                temp.childs.add(expr(1, "new_expr2"));
                break;
            case "<GREAT_EQ>":
                tokens.poll();
                temp.childs.add(new Node("<=","new_expr2", false));
                temp.childs.add(expr(1, "new_expr2"));
                break;
            case "<LESS_EQ>":
                tokens.poll();
                temp.childs.add(new Node(">=","new_expr2", false));
                temp.childs.add(expr(1, "new_expr2"));
                break;
            case "<LESSTHAN>":
                tokens.poll();
                temp.childs.add(new Node(">","new_expr2", false));
                temp.childs.add(expr(1, "new_expr2"));
                break;
            case "<GREATERTHAN>":
                tokens.poll();
                temp.childs.add(new Node("<", "new_expr2",false));
                temp.childs.add(expr(1, "new_expr2"));
                break;
            case "<AND>":
                tokens.poll();
                temp.childs.add(new Node("&&","new_expr2", false));
                temp.childs.add(expr(1, "new_expr2"));
                break;
            case "<ASTERICK>":
                tokens.poll();
                temp.childs.add(new Node("*", "new_expr2",false));
                temp.childs.add(expr(1, "new_expr2"));
                break;
            case "<DIVIDE>":
                tokens.poll();
                temp.childs.add(new Node("/","new_expr2", false));
                temp.childs.add(expr(1,"new_expr2"));
                break;
            case "<MOD>":
                tokens.poll();
                temp.childs.add(new Node("%", "new_expr2",false));
                temp.childs.add(expr(1,"new_expr2"));
                break;
            case "<PLUS>":
                tokens.poll();
                temp.childs.add(new Node("+","new_expr2", false));
                temp.childs.add(expr(1,"new_expr2"));
                break;
            case "<MINUS>":
                tokens.poll();
                temp.childs.add(new Node("-","new_expr2", false));
                temp.childs.add(expr(1,"new_expr2"));
                break;
            default:
                break;
        }

        return temp;
    }

    private Node args(String father) throws ParserException {
        Node temp = new Node("args",father,true);
        temp.childs.add(arg_list("args"));

        return temp;

    }

    private Node arg_list(String father) throws ParserException {
        Node temp = new Node("arg_list",father,true);
        temp.childs.add(expr(1,"arg_list"));
        temp.childs.add(arg_listM("arg_list"));
        return temp;
    }

    private Node arg_listM(String father) throws ParserException {
        Node temp = new Node("arg_listM",father,true);
        Token comma = tokens.peek();
        if (comma.Type.equals("<COMMA>")){
            tokens.poll();
            temp.childs.add(new Node("," ,"arg_listM" ,false)); 
            temp.childs.add(expr(1,"arg_listM"));
            temp.childs.add(arg_listM(father));
        }
        return temp;
    }
}
