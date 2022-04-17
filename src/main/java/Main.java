import bao1.Test;
import container.MyIocContainer;

public class Main {
    public static void main(String[] args) {
        Test test = new Test();
        test.hello();
        MyIocContainer container = new MyIocContainer();
        container.run(Main.class);
    }
}
