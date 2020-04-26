package minilexical;

import java.io.IOException;

public class MiniLexical {

    public static void main(String[] args) throws IOException, ParserException, InterruptedException {
//        FillRegex();
        Node tree = new Node("" ,"",false);
        Analyzer a = new Analyzer();
        a.ReadFile();
        a.Program();
        System.out.println("************************************");
        tree.print(a.root);
        
        tree.printRec(a.root);
        
    }
    static void FillRegex() throws IOException {
        myParser tokenizer = new myParser();
        tokenizer.add("(/\\*).+(\\*/)","<MULTI_COMMENT1>");
        tokenizer.add("(/\\*)","<MULTI_COMMENT>");
        tokenizer.add("//.*","<SINGLE_COMMENT>");
        tokenizer.add("\\bdouble\\b", "<DOUBLE>");
        tokenizer.add("\\bfloat\\b", "<FLOAT>");
        tokenizer.add("\\bint\\b", "<INT>");
        tokenizer.add("\\bchar\\b", "<CHAR>");
        tokenizer.add("0\\z", "<EOF>");
        tokenizer.add("\\bdo\\b", "<DO>");
        tokenizer.add("\\btrue\\b", "<TRUE>");
        tokenizer.add("\\bfalse\\b", "<FALSE>");
        tokenizer.add("\\bbreak\\b", "<BREAK>");
        tokenizer.add("\\benum\\b", "<ENUM>");
        tokenizer.add("\\bextern\\b", "<EXTERN>");
        tokenizer.add("\\bcontinue\\b", "<CONTINUE>");
        tokenizer.add("\\bnew\\b", "<NEW>");
        tokenizer.add("\\bconst\\b", "<CONST>");
        tokenizer.add("\\bdefault\\b", "<DEFAULT>");
        tokenizer.add("\\bcase\\b", "<CASE>");
        tokenizer.add("long\\s", "<LONG>");
        tokenizer.add("short\\s", "<SHORT>");
        tokenizer.add("\\bbool\\b", "<BOOLEAN>");
        tokenizer.add("\\bauto\\b", "<AUTO>");
        tokenizer.add("\\bif\\b", "<IF>");
        tokenizer.add("\\belse\\b", "<ELSE>");
        tokenizer.add("\bvoid\b", "<VOID>");
        tokenizer.add("^for", "<FOR>");
        tokenizer.add("^(goto)\\s", "<GOTO>");
        tokenizer.add("\b(return)\b", "<RETURN>");
        tokenizer.add("^void\\s", "<VOID>");
        tokenizer.add("static\\s", "<STATIC>");//*******************
        tokenizer.add("register\\s", "<REGISTER>");
        tokenizer.add("sizeof\\s", "<SIZEOF>");//******************
        tokenizer.add("unsigned\\s", "<UNSIGNED>");
        tokenizer.add("signed\\s", "<SIGNED>");
        tokenizer.add("^struct\\s", "<STRUCT>");
        tokenizer.add("^switch\\s", "<SWITCH>");
        tokenizer.add("^typedef\\s", "<TYPEDOF>");
        tokenizer.add("^union\\s", "<UNION>");
        tokenizer.add("volatile\\s", "<VOLATILE>");
        tokenizer.add("^while\\s", "<WHILE>");
        tokenizer.add("\"[^\"\\\\]*(\\\\.[^\"\\\\]*)*\"", "<STRING_LITERAL>");
        tokenizer.add("[0-9]+", "<INTEGRAL_LITERAL>");
        tokenizer.add("[a-zA-Z_][a-zA-Z_\\$0-9]*", "<ID>");     //\\d+[a-zA-Z_]+    
        tokenizer.add("\\+", "<PLUS>"); //
        tokenizer.add("-", "<MINUS>");
        tokenizer.add("/", "<DIVIDE>");
        tokenizer.add("\\*", "<ASTERICK>");
        tokenizer.add("([0-9]+)?\\.[0-9]+", "<FLOAT_LITERAL>");

        tokenizer.add("^\".*\"$", "<STRING_LITERAL>");
        tokenizer.add("\'.?\'", "<CHAR_LITERAL>");
        tokenizer.add("(&&)", "<AND>");
        tokenizer.add(";", "<SEMI-COLON>");
        tokenizer.add("\\(", "<LEFT_BRACKETS>");
        tokenizer.add("\\)", "<RIGHT_BRACKETS>");
        tokenizer.add("\\{", "<LEFT_BRACKETS_PARTH>");
        tokenizer.add("\\}", "<RIGHT_BRACKETS_PARTH>");
        tokenizer.add("\\[", "<LEFT_SQUARE_B>");
        tokenizer.add("\\]", "<RIGHT_SQUARE_B>");
        tokenizer.add(",", "<COMMA>");
        tokenizer.add("!", "<NOT>");
        tokenizer.add("\\.", "<DOT>");
        tokenizer.add(":", "<COLON>");
        tokenizer.add("^#", "<PREPROCESSOR>");
        tokenizer.add("\\\\", "<BACKWARD_SLASH>");
 
        tokenizer.add("\\>=","<GREAT_EQ>");
        tokenizer.add("\\<=","<LESS_EQ>");
        tokenizer.add("!=","<NOT_EQUAL>");
        tokenizer.add("==","<EQUAL>");
        tokenizer.add("=", "<ASSIGN_OPERATOR>");
        tokenizer.add("\\<","<LESSTHAN>");
        tokenizer.add("\\>","<GREATERTHAN>");
        tokenizer.add("\\^","<BITWISE_XOR>");
        tokenizer.add(">>","<LEFT_SHIFT>");
        tokenizer.add("<<","<RIGHT_SHIFT>");
        tokenizer.add("~","<BITWISE_NOT>");
        tokenizer.add("\\*","<ASTERICK>");
        tokenizer.add("&","<BITWISE_AND>");
        tokenizer.add("\\|\\|","<OR>");
        tokenizer.add("\\|","<BITWISE_OR>");

        try {
            tokenizer.tokenize();
            tokenizer.WriteFile();
            System.out.println("Parsing Done ... :) ^_^ ");
            System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        } catch (ParserException e) {
            System.out.println(e.getMessage());
        }
        
    }

}
