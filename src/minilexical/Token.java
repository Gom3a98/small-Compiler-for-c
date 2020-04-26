/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minilexical;


public class Token {
    public String Type = "";
    public String Value = "";
    public int position ;
    public Token(String token, String sequence , int pos)
    {
      super();
      this.Type = token;
      this.Value = sequence;
      this.position = pos;
    }
}
