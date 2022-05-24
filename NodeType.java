import absyn.*;
public class NodeType {
    public String name;
    public String type;
    public String arrSize;
    public ParamList list;
    public int row;
    public int col;
    public boolean isArray;
    public NodeType(String name, String type, int row, int col){
        this.name = name;
        this.type = type;
        this.arrSize = null;
        this.list = null;
        this.isArray = false;
        this.row = row;
        this.col = col;
    }
    public NodeType(String name, String type, String arrSize,int row, int col){
        this.name = name;
        this.type = type;
        this.arrSize = arrSize;
        this.list = null;
        this.isArray = true;
        this.row = row;
        this.col = col;
    }
    public NodeType(String name,String type, ParamList list,int row, int col){
        this.name = name;
        this.type = type;
        this.list = list;
        this.arrSize = null;
        this.isArray = false;
        this.row = row;
        this.col = col;
    }
    public NodeType(String name,String type,boolean isArray, int row, int col){
        this.name = name;
        this.type = type;
        this.list = null;
        this.arrSize = null;
        this.isArray = isArray;  
        this.row = row;
        this.col = col;
    }
}
