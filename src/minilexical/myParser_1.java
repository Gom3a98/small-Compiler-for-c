package minilexical;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.util.Pair;

class myParser {

    public String text = "";
    private ArrayList<TokenInfo> tokenInfos;
    private ArrayList<Token> tokens;

    private class TokenInfo {

        public final Pattern regex;
        public final String token;

        public TokenInfo(Pattern regex, String token) {
            super();
            this.regex = regex;
            this.token = token;
        }
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public void tokenize() throws ParserException, FileNotFoundException, IOException {
        File file = new File("test.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String s;
        while ((s = br.readLine()) != null) {
            //System.out.println(s);
            if (s.isEmpty()) {
                continue;
            }
            String currentLine = s;
//            
            Pattern pat = Pattern.compile("\\d+[a-zA-Z_]+");
            Matcher mmm = pat.matcher(s);
            boolean isError = false;
            if (mmm.find()){
                isError = true;
            }
            ArrayList<Token> temp = new ArrayList<>();
            while (!s.equals("")) {
                boolean match = false;
//                System.out.println(s);
                for (TokenInfo info : tokenInfos) {
                    Matcher m = info.regex.matcher(s);
                    if (m.find()) {
                        int position = m.start();
                        match = true;
                        String tok="" ;
                        if(info.token.equals("<MULTI_COMMENT>"))
                        {

                            tok += s;
                            
                            s = br.readLine();
                            do
                            {
                                 tok +=s;
                                 tok += "\n";
                                 s = br.readLine();     
                                 
                            }while(!s.contains("*/"));
                            tok += s;
                            s = "";

                        }
                        else
                        {
                            //System.out.println(s);
                            tok = m.group().trim();
                            s = m.replaceFirst(" ").trim();
                        }
//                        System.out.println(s);
                    temp.add(new Token(info.token, tok, position));
                    
                    break;
                    
                    }
                }
                if (!match || isError) {
                    throw new ParserException("Syntx Error : at this line: " + currentLine);
                }
//           
            }
            for (Token i : temp) {
                if(i.Type.equals("<MULTI_COMMENT>"))
                    break;
                
                int p = currentLine.indexOf(i.Value);
                StringBuffer buf = new StringBuffer(currentLine);

                int start = p;
                int end = p + i.Value.length();

                buf.replace(start, end, encode(i.Value)); 
                currentLine = buf.toString();
                i.position = p;
            }

            sortTokens(temp);

            for (Token i : temp) {
                if(i.Type.equals("<MULTI_COMMENT1>"))
                    i.Type="<MULTI_COMMENT>";
            
                tokens.add(i);
            }
        }
        br.close();
//                ;

    }

    private void sortTokens(ArrayList<Token> tokens) {
        int size = tokens.size();
        for (int i = 0; i < size - 1; i++) {
            int min = i;

            for (int j = i + 1; j < size; j++) {
                if (tokens.get(j).position < tokens.get(min).position) {
                    min = j;
                }
            }
            Token temp = tokens.get(min);
            tokens.set(min, tokens.get(i));
            tokens.set(i, temp);
        }
    }
    String encode(String s){
        String out = "";
        for (int i=0;i<s.length();i++){
            out+=" ";
        }
        return out;
    }
    public myParser() {

        tokenInfos = new ArrayList<TokenInfo>();
        tokens = new ArrayList<Token>();

    }

    public void add(String regex, String token) {
        tokenInfos.add(new TokenInfo(Pattern.compile(regex), token));
    }
    public void WriteFile() throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter("Tokens.txt"));
    for (Token i : tokens)
    {
        writer.write(i.Type+" "+i.Value);
        writer.newLine();
    }
     
    writer.close();
    }

}
