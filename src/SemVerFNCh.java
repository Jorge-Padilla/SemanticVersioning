/*

    Jorge Alberto Padilla Gutiérrez
    A01635346

 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SemVerFNCh {

    public ArrayList<Simbolo> gramatica;

    public SemVerFNCh(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str = br.readLine();
        this.gramatica = new ArrayList<Simbolo>();
        this.gramatica.add(new Simbolo(str));
        char aux;
        boolean past = false;
        while((str = br.readLine()) != null){
            aux = str.charAt(0);
            check: for(Simbolo s : this.gramatica){
                for(int i = 0; i < s.producciones.size(); i++){
                    for(Simbolo ss : s.producciones.get(i)){
                        if(aux == ss.simbolo){
                            this.gramatica.add(ss);
                            this.gramatica.get(this.gramatica.size()-1).setProducciones(str);
                            past = true;
                            break check;
                        }
                    }
                }
            }
            if(!past) this.gramatica.add(new Simbolo(str));
        }
        for(int i = 0; i < this.gramatica.size(); i++){
            for(int j = 0; j < this.gramatica.get(i).producciones.size(); j++){
                for(int k = 0; k < this.gramatica.get(i).producciones.get(j).size(); k++){
                    for(int l = 0; l < this.gramatica.size(); l++){
                        if(this.gramatica.get(i).producciones.get(j).get(k).simbolo == this.gramatica.get(l).simbolo) {
                            this.gramatica.get(i).producciones.get(j).set(k,this.gramatica.get(l));
                        }
                    }
                }
            }
        }
        removeUnitary();
        removeEpsilon();    // Epsilon = &
        removeUseless();
        addTerminals();
        addExtras();
        System.out.println("Gramática de Semantic Versioning en la Forma Normal de Chomsky:");
        System.out.println(this);
    }

    public void removeUnitary(){
        for(int i = 0; i < this.gramatica.size(); i++){
            for(int j = 0; j < this.gramatica.get(i).producciones.size(); j++){
                if(this.gramatica.get(i).producciones.get(j).size() == 1 && !this.gramatica.get(i).producciones.get(j).get(0).terminal){
                    Simbolo s = this.gramatica.get(i).producciones.get(j).get(0);
                    this.gramatica.get(i).producciones.remove(j);
                    replace: for(int k = 0; k < s.producciones.size(); k++){
                        replace2: for(int l = 0; l < this.gramatica.get(i).producciones.size(); l++){
                            if(this.gramatica.get(i).producciones.get(l).size() != s.producciones.get(k).size()) continue;
                            for(int m = 0; m < this.gramatica.get(i).producciones.get(l).size(); m++){
                                if(this.gramatica.get(i).producciones.get(l).get(m).simbolo != s.producciones.get(k).get(m).simbolo) continue replace2;
                            }
                            continue replace;
                        }
                        this.gramatica.get(i).producciones.add(s.producciones.get(k));
                    }
                    j--;
                }
            }
        }
    }

    public void removeEpsilon(){
        for(int i = 0; i < this.gramatica.size(); i++){
            for(int j = 0; j < this.gramatica.get(i).producciones.size(); j++){
                if(this.gramatica.get(i).producciones.get(j).size() == 1 && this.gramatica.get(i).producciones.get(j).get(0).simbolo == '&'){
                    removeEpsilon(this.gramatica.get(i));
                    this.gramatica.get(i).producciones.remove(j);
                }
            }
        }
    }

    public void removeEpsilon(Simbolo s){
        for(int i = 0; i < this.gramatica.size(); i++){
            for(int j = 0; j < this.gramatica.get(i).producciones.size(); j++){
                replace: for(int k = 0; k < this.gramatica.get(i).producciones.get(j).size(); k++){
                    if(this.gramatica.get(i).producciones.get(j).get(k).simbolo == s.simbolo){
                        List<Simbolo> aux = new ArrayList<>(this.gramatica.get(i).producciones.get(j));
                        aux.remove(k);
                        replace2: for(int l = 0; l < this.gramatica.get(i).producciones.size(); l++){
                            if(this.gramatica.get(i).producciones.get(l).size() != aux.size()) continue;
                            for(int m = 0; m < this.gramatica.get(i).producciones.get(l).size(); m++){
                                if(this.gramatica.get(i).producciones.get(l).get(m).simbolo != aux.get(m).simbolo) continue replace2;
                            }
                            continue replace;
                        }
                        if(aux.size() > 0) {
                            if(aux.get(0) != this.gramatica.get(i)) this.gramatica.get(i).producciones.add((ArrayList<Simbolo>) aux);
                        }
                    }
                }
            }
        }
    }

    public void removeUseless(){
        simbolo: for(int s = 1; s < this.gramatica.size(); s++) {
            for(int i = 0; i < this.gramatica.size(); i++){
                for(int j = 0; j < this.gramatica.get(i).producciones.size(); j++){
                    for(int k = 0; k < this.gramatica.get(i).producciones.get(j).size(); k++){
                        if(this.gramatica.get(s) == this.gramatica.get(i).producciones.get(j).get(k)) continue simbolo;
                    }
                }
            }
            this.gramatica.remove(s);
        }
    }

    public void addTerminals(){
        int size = this.gramatica.size();
        for(int i = 0; i < size; i++){
            for(int j = 0; j < this.gramatica.get(i).producciones.size(); j++){
                terms: for(int k = 0; k < this.gramatica.get(i).producciones.get(j).size(); k++){
                    if(this.gramatica.get(i).producciones.get(j).get(k).terminal && this.gramatica.get(i).producciones.get(j).size() > 1){
                        if(this.gramatica.size() == size){
                            Simbolo aux = new Simbolo('T', this.gramatica.get(i).producciones.get(j).get(k));
                            this.gramatica.add(aux);
                            this.gramatica.get(i).producciones.get(j).set(k, aux);
                        }else{
                            for(int l = size; l < this.gramatica.size(); l++){
                                if(this.gramatica.get(l).sub == this.gramatica.get(i).producciones.get(j).get(k).simbolo){
                                    this.gramatica.get(i).producciones.get(j).set(k, this.gramatica.get(l));
                                    continue terms;
                                }
                            }
                            Simbolo aux = new Simbolo('T', this.gramatica.get(i).producciones.get(j).get(k));
                            this.gramatica.add(aux);
                            this.gramatica.get(i).producciones.get(j).set(k, aux);
                        }
                    }
                }
            }
        }
    }

    public void addExtras(){
        int count = 48;
        for(int i = 0; i < this.gramatica.size(); i++){
            for(int j = 0; j < this.gramatica.get(i).producciones.size(); j++){
                if(this.gramatica.get(i).producciones.get(j).size() >= 3){
                    Simbolo aux = new Simbolo('G',(char)count);
                    count++;
                    while(this.gramatica.get(i).producciones.get(j).size() > 1){
                        Simbolo aux2 = this.gramatica.get(i).producciones.get(j).remove(1);
                        aux.producciones.get(0).add(aux2);
                    }
                    this.gramatica.get(i).producciones.get(j).add(aux);
                    this.gramatica.add(aux);

                }
            }
        }
    }

    public String toString(){
        String txt = "";
        for(Simbolo s : this.gramatica){
            txt += s + "\n";
        }
        return txt;
    }

    public static void main(String[] args) throws IOException {
        SemVerFNCh fn = new SemVerFNCh(new File("Gramatica"));
        System.out.println(fn);
        //System.out.println(fn.gramatica.get(1).producciones.get(1).get(0));
        //System.out.println(fn.gramatica.get(2).producciones.get(0).get(1));
    }

}

class Simbolo{

    public char simbolo;
    public char sub;
    public boolean terminal;
    public ArrayList<ArrayList<Simbolo>> producciones;
    //public Object debug;

    public Simbolo(String txt){
        this.simbolo = txt.charAt(0);
        this.sub = '&';
        this.terminal = false;
        this.producciones = new ArrayList<ArrayList<Simbolo>>();
        String aux = txt.substring(3);
        String[] cadenas = aux.split("\\|");
        for(String cadena: cadenas){
            this.producciones.add(new ArrayList<Simbolo>());
            for(char simb: cadena.toCharArray()){
                if(simb == this.simbolo) this.producciones.get(this.producciones.size()-1).add(this);
                else this.producciones.get(this.producciones.size()-1).add(new Simbolo(simb));
            }
        }
        //this.debug = new Object();
    }

    public Simbolo(char simbolo){
        this.simbolo = simbolo;
        this.sub = '&';
        this.terminal = true;
        //this.debug = new Object();
    }

    public Simbolo(char simbolo, char sub){
        this.simbolo = simbolo;
        this.sub = sub;
        this.terminal = false;
        this.producciones = new ArrayList<>();
        this.producciones.add(new ArrayList<>());
        //this.debug = new Object();
    }

    public Simbolo(char simbolo, Simbolo sub){
        this.simbolo = simbolo;
        this.sub = sub.simbolo;
        this.terminal = false;
        this.producciones = new ArrayList<>();
        this.producciones.add(new ArrayList<>());
        this.producciones.get(0).add(sub);
        //this.debug = new Object();
    }

    public void setProducciones(String txt){
        this.terminal = false;
        this.producciones = new ArrayList<ArrayList<Simbolo>>();
        String aux = txt.substring(3);
        String[] cadenas = aux.split("\\|");
        for(String cadena: cadenas){
            this.producciones.add(new ArrayList<Simbolo>());
            for(char simb: cadena.toCharArray()){
                if(simb == this.simbolo) this.producciones.get(this.producciones.size()-1).add(this);
                else this.producciones.get(this.producciones.size()-1).add(new Simbolo(simb));
            }
        }
    }

    public String simb(){
        if(this.sub != '&') return this.simbolo + "" + this.sub;
        return this.simbolo + "";
    }

    public String simbP(){
        if(this.sub != '&') return "(" +  this.simbolo + this.sub + ")";
        return this.simbolo + "";
    }

    public String toString(){
        //String txt = this.simbolo + "" + this.debug + "->";
        if(this.terminal) return this.simbolo + "";
        String txt = this.simbolo + "";
        txt += this.sub == '&' ? "->" : this.sub + "->";
        for(ArrayList<Simbolo> produccion: this.producciones){
            for(int i = 0; i < produccion.size(); i++){
                //txt += produccion.get(i).simbolo;
                txt += produccion.get(i).sub != '&' ? "(" + produccion.get(i).simbolo + produccion.get(i).sub + ")" : produccion.get(i).simbolo;
                //txt += produccion.get(i).terminal ? "" : produccion.get(i).debug;
            }
            if(!produccion.equals(this.producciones.get(this.producciones.size()-1))) txt += "|";
        }
        return txt;
    }

    public boolean equals(Simbolo s){
        return this.simbolo == s.simbolo && this.sub == s.sub;
    }

}
