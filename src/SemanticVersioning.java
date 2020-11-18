/*

    Jorge Alberto Padilla Gutiérrez
    A01635346

 */

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SemanticVersioning {

    public SemVerFNCh SemVer;
    public char[] cadena;
    public boolean valid;

    public SemanticVersioning(File gramatica, String cadena) throws IOException {
        this.SemVer = new SemVerFNCh(gramatica);
        this.cadena = cadena.toCharArray();
        this.valid = this.cyk();
    }

    public boolean cyk(){
        ArrayList<Simbolo>[][] matrix = new ArrayList[cadena.length][cadena.length];
        ArrayList<Tree>[][] trees = new ArrayList[cadena.length][cadena.length];
        for(int i = 0; i < cadena.length; i++){
            for(int j = 0; j < cadena.length; j++){
                matrix[i][j] = new ArrayList<>();
                trees[i][j] = new ArrayList<>();
            }
        }
        for(int i = 0; i < cadena.length; i++){
            for(Simbolo a : SemVer.gramatica){
                for(int j = 0; j < a.producciones.size(); j++){
                    if(a.producciones.get(j).size() == 1 && a.producciones.get(j).get(0).simbolo == this.cadena[i]){
                        matrix[i][i].add(a);
                        trees[i][i].add(new Tree(matrix[i][i].get(matrix[i][i].size()-1),this.cadena[i]));
                    }
                }
            }
        }

        for(int l = 1; l <= cadena.length; l++){
            for(int i = 0; i <= cadena.length - l; i++){
                int j = i+l-1;
                for(int k = i; k < j; k++){
                    for(Simbolo a : SemVer.gramatica){
                        for(int s = 0; s < a.producciones.size(); s++){
                            if(a.producciones.get(s).size() == 2){
                                for(int row = 0; row < matrix[i][k].size();row++){
                                    for(int column = 0; column < matrix[k+1][j].size(); column++){
                                        if (a.producciones.get(s).get(0).equals(matrix[i][k].get(row)) && a.producciones.get(s).get(1).equals(matrix[k+1][j].get(column))) {
                                            if(!matrix[i][j].contains(a)) {
                                                matrix[i][j].add(a);
                                                trees[i][j].add(new Tree(matrix[i][j].get(matrix[i][j].size()-1),trees[i][k].get(row),trees[k+1][j].get(column)));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Resultado del algoritmo CYK:");

        for(int i = 0; i < cadena.length; i++){
            for(int j = 0; j < cadena.length; j++){
                System.out.print("[");
                String txt = "";
                for(Simbolo s : matrix[i][j]){
                    txt = s.sub == '&' ? s.simbolo + "" : s.simbolo + "" + s.sub;
                    txt += s.equals(matrix[i][j].get(matrix[i][j].size()-1)) ? "" : "|";
                    System.out.print(txt);
                }
                if(txt.equals("")) System.out.print(" ");
                System.out.print("] ");
            }
            System.out.println();
        }

        System.out.println();

        for(int i = 0; i < matrix[0][cadena.length-1].size(); i++){
            if(matrix[0][cadena.length-1].get(i).equals(this.SemVer.gramatica.get(0))){
                System.out.println("La cadena es valida en la Gramática de Semantic Versioning, este es su arbol de derivación:");
                System.out.println(trees[0][cadena.length-1].get(i) + "\n");
                return true;
            }
        }
        System.out.println("La cadena NO es valida en la Gramática de Semantic Versioning");
        return false;
    }

    public static void main(String[] args) throws IOException {
        SemanticVersioning sem = new SemanticVersioning(new File("SemVer.txt"), JOptionPane.showInputDialog("Inserte la cadena que desea evaluar"));
    }

}

class Tree{

    Simbolo node;
    Tree left;
    Tree right;
    char simbol;

    public Tree(Simbolo node, Tree left, Tree right){
        this.node = node;
        this.left = left;
        this.right = right;
        this.simbol = '&';
    }

    public Tree(Simbolo node, char simbol){
        this.node = node;
        this.left = null;
        this.right = null;
        this.simbol = simbol;
    }

    public String toString(){
        return this.toString(-1);
    }

    public String toString(int tab){
        tab++;
        if(this.left == null || this.right == null){
            return tabs(tab) + this.node.simb() + "->" + this.simbol + "\n" + tabs(tab+1) + "'" + this.simbol + "'";
        }
        return tabs(tab) + this.node.simb() + "->" + this.left.node.simbP() + "" + this.right.node.simbP() + "\n" + this.left.toString(tab) + "\n" + this.right.toString(tab);
    }

    public String tabs(int tab){
        String txt = "";
        for(int i = 0; i < tab; i++){
            txt += "\t";
        }
        return txt;
    }

    public boolean equals(Tree t){
        if(t == null) return false;
        return this.node == t.node;
    }

}
