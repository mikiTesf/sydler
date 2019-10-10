import view.GeneratorGUI;

class App {
    public static void main(String[] args) {
        System.setProperty("com.j256.ormlite.logger.level","INFO");
        new GeneratorGUI().setupAndDrawUI();
    }
}
