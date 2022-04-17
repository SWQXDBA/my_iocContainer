import container.MyIocContainer;

public class Main {
    public static void main(String[] args) {
        MyIocContainer container = new MyIocContainer();
        container.run(Main.class);
    }
}
