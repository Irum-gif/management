package management.tool;

public class Legimitate {
    //检查小数是否合法
    public Boolean checkDouble(double num){
        boolean status = true;
        if (num<=0){
            status = false;
        }
        return status;
    }
    //检查整数是否合法
    public Boolean checkInt(int num){
        boolean status = true;
        if (num<=0){
            status = false;
        }
        return status;
    }
}
