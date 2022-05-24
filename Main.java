/*
  Created by: Fei Song
  File Name: Main.java
  To Build: 
  After the scanner, tiny.flex, and the parser, tiny.cup, have been created.
    javac Main.java
  
  To Run: 
    java -classpath /usr/share/java/cup.jar:. Main gcd.tiny

  where gcd.tiny is an test input file for the tiny language.
*/
   
import java.io.*;
import absyn.*;
   
class Main {
  public static boolean SHOW_TREE = false;
  public static boolean SHOW_SYMBOL = false;
  public static boolean GEN_CODE = false;
  static public void main(String argv[]) {   
    /* Start the parser */
    for (String s: argv){
      if (s.equals("-a")){
        SHOW_TREE = true;
      }
      if (s.equals("-s")){
        SHOW_SYMBOL = true;
      }
      if(s.equals("-g")){
        GEN_CODE = true;
      }
      
    }
    try {
      parser p = new parser(new Lexer(new FileReader(argv[0])));
      Absyn result = (Absyn)(p.parse().value);
      
         
      if (SHOW_TREE && result != null) {
         System.out.println("The abstract syntax tree is:");
         ShowTreeVisitor visitor = new ShowTreeVisitor();
         result.accept(visitor, 0, true); 
      }
      if(SHOW_SYMBOL && result != null){
        if(SHOW_TREE){
          System.out.println();
        } 
        System.out.println("The symbol table is:");
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        result.accept(analyzer, 0,true); 
      }
      if(GEN_CODE && result != null){
        CodeGenerator gen = new CodeGenerator(argv[0]);
        result.accept(gen, 0,false);
      }
    } catch (Exception e) {
      /* do cleanup here -- possibly rethrow e */
      e.printStackTrace();
    }
  }
}


