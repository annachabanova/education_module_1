public class Birds {
    private static String[][] birds = {{"Воробьи","0.2","254","35"}};
    private static final int BIRD_FIELD_COUNT = 4;
        private static final int BIRD_NAME = 0;
        private static final int BIRD_PRICE = 1;
        private static final int BIRD_STOCK = 2;
        private static final int BIRD_SOLD = 3;
    private static final int BIRD_MIN_STOCK = 25;
    private static final String BIRD_PRINT_FORMAT = "%-20.20s %15s %15s %15s %n";
    private static final String BIRD_TABLE_SPACER = new String(new char[70]).replace("\0","-");

    public static void main(String[] args) {
        reportBirds();
        addNewBird("Попугаи",5.5,12);
        addNewBird("Коллибри",14.8,24);
        addNewBird("Орлы",8,5);
        addNewBird("Страусы",12.1,12);
        addNewBird("Голуби",2.5,46);
        reportBirds();
        changeBirdStock("Попугаи",14);
        changeBirdName("Коллибри", "Колибри");
        changeBirdPrice("Колибри",15.3);
        changeBirdSold("Воробьи",48);
        addBirdToStock("Орлы", 13);
        reportBirds();
        reportStock(0,BIRD_MIN_STOCK);
        reportPrice(5,12.5);
        saleBird("Орлы",2);
        saleBird("Попугаи",5);
        reportSold(1,1000);
        reportBirds("Орлы");
        reportBirds("Колибри");
    }

    private static String[] findBird(String name) {
        for (String[] bird : birds) {
            if (bird[BIRD_NAME].equals(name.trim())) {
                return bird;
            }
        }
        return null;
    }
    private static void addNewBird(String name, double price, int stock) {
        if (name.trim().isEmpty() || price<=0 || stock<0) {
            System.err.println("(addNewBird) WRONG INPUT DATA. Name: <" + name + ">, price: " + price + ", stock: " + stock);
            return;
        }

        if (findBird(name)!=null) {
            System.err.println("(addNewBird) bird <" + name + "> already exist!");
            return;
        }

        String[][] newBirds = new String[birds.length+1][BIRD_FIELD_COUNT];

        for (int i = 0; i < birds.length; i++) {
            System.arraycopy(birds[i], 0, newBirds[i], 0, birds[i].length);
        }
        newBirds[birds.length][BIRD_NAME] = name;
        newBirds[birds.length][BIRD_PRICE] = String.valueOf(price);
        newBirds[birds.length][BIRD_STOCK] = String.valueOf(stock);
        newBirds[birds.length][BIRD_SOLD] = String.valueOf(0);
        System.out.println("(addNewBird). Name: <" + name + ">, price: " + price + ", stock: " + stock);
        birds = newBirds;
    }

    private static void changeBirdSold(String name, int count) {
        changeBirdParam(name,BIRD_SOLD,String.valueOf(count));
    }
    private static void changeBirdStock(String name, int count) {
        changeBirdParam(name,BIRD_STOCK,String.valueOf(count));
    }
    private static void changeBirdPrice(String name, double price) {
        changeBirdParam(name,BIRD_PRICE,String.valueOf(price));
    }
    private static void changeBirdName(String oldName, String newName) {
        changeBirdParam(oldName,BIRD_NAME,newName);
    }
    private static void changeBirdParam(String name, int param, String value) {
        String res = "";
        if (value.trim().isEmpty() || param < 0 || param > BIRD_FIELD_COUNT) {
            System.err.println("(changeBirdParam) WRONG INPUT DATA. Name: <" + name + ">, param: " + param + ", value: " + value);
            return;
        }
        String[] bird = findBird(name);
        if (bird == null) {
            System.err.println("(changeBirdParam) bird <" + name + "> not exist!");
            return;
        }
        if (param == BIRD_STOCK || param == BIRD_SOLD) { // Integer value
            bird[param] = String.valueOf(Integer.parseInt(value));
        } else if (param == BIRD_PRICE) { // Double value
            bird[param] = String.valueOf(Double.parseDouble(value));
        } else { // String value
            bird[param] = value;
        }
        System.out.println("(changeBirdParam) " +
                "bird <" + bird[BIRD_NAME] + ">, " +
                "price: " + bird[BIRD_PRICE] + ", " +
                "stock: " + bird[BIRD_STOCK]+ ", " +
                "sold: " + bird[BIRD_SOLD]);
    }

    private static void saleBird(String name, int count) {
        if (name.trim().isEmpty() || count<=0) {
            System.err.println("(saleBird) WRONG INPUT DATA. Name: <" + name + ">, count: " + count);
            return;
        }
        String[] bird = findBird(name);
        if (bird == null) {
            System.err.println("(saleBird) bird <" + name + "> not exist!");
            return;
        }
        if (Integer.parseInt(bird[BIRD_STOCK])<count) {
            System.err.println("(saleBird) bird <" + name + "> stock " + bird[BIRD_STOCK] + " less " + count);
            return;
        }
        bird[BIRD_STOCK]=String.valueOf(Integer.parseInt(bird[BIRD_STOCK])-count);
        bird[BIRD_SOLD]=String.valueOf(Integer.parseInt(bird[BIRD_SOLD])+count);
        System.out.println("(saleBird) bird <" + name + "> stock " + bird[BIRD_STOCK] + ", total sold " + bird[BIRD_SOLD]);
    }
    private static void addBirdToStock(String name, int count) {
        if (name.trim().isEmpty() || count<=0) {
            System.err.println("(addBird) WRONG INPUT DATA. Name: <" + name + ">, count: " + count);
            return;
        }
        String[] bird = findBird(name);
        if (bird == null) {
            System.err.println("(addBird) bird <" + name + "> not exist!");
            return;
        }
        bird[BIRD_STOCK]=String.valueOf(Integer.parseInt(bird[BIRD_STOCK])+count);
        System.out.println("(addBird) bird <" + name + "> add " + count + ", total stock: " + bird[BIRD_STOCK]);
    }

    // REPORT FUNCTONS

    private static void reportBirds() {
        printBirds("all birds info","",-1,-1,-1,-1,-1,-1);
    }
    private static void reportBirds(String name) {
        printBirds(name + " info",name,-1,-1,-1,-1,-1,-1);
    }
    private static void reportStock(int minValue, int maxValue) {
        printBirds("stock from " + minValue + " to " + maxValue,"",-1,-1,minValue,maxValue,-1,-1);
    }
    private static void reportPrice(double minValue, double maxValue) {
        printBirds("price from " + minValue + " to " + maxValue,"",minValue,maxValue,-1,-1,-1,-1);
    }
    private static void reportSold(int minValue, int maxValue) {
        printBirds("sold from " + minValue + " to " + maxValue,"",-1,-1,-1,-1,minValue,maxValue);
    }


    private static void printBird(String[] bird) {
        System.out.printf(BIRD_PRINT_FORMAT, bird[BIRD_NAME], bird[BIRD_PRICE], bird[BIRD_STOCK], bird[BIRD_SOLD]);
    }
    private static void printBirds(String repName, String name,
                                   double minPrice, double maxPrice,
                                   int minStock, int maxStock,
                                   int minSold, int maxSold) {
        int totalStock=0, totalSold=0;
        boolean printInfo = false;
        for (String[] bird : birds) {
            if (!name.trim().isEmpty() && !bird[BIRD_NAME].equals(name.trim())) { continue;}
            if ((minPrice>=0 && Double.parseDouble(bird[BIRD_PRICE])<minPrice) ||
                (maxPrice>=0 && Double.parseDouble(bird[BIRD_PRICE])>maxPrice)) {continue;}
            if ((minStock>=0 && Integer.parseInt(bird[BIRD_STOCK])<minStock) ||
                (maxStock>=0 && Integer.parseInt(bird[BIRD_STOCK])>maxStock)) {continue;}
            if ((minSold>=0 && Integer.parseInt(bird[BIRD_SOLD])<minSold) ||
                (maxSold>=0 && Integer.parseInt(bird[BIRD_SOLD])>maxSold)) {continue;}
            if (!printInfo) {
                System.out.printf("%n%s%n", "#################### REPORT \'" + repName + "\'");
                System.out.printf(BIRD_PRINT_FORMAT,"NAME","PRICE","STOCK","SOLD");
                System.out.println(BIRD_TABLE_SPACER);
            }
            printBird(bird);
            printInfo = true;
            totalStock += Integer.parseInt(bird[BIRD_STOCK]);
            totalSold += Integer.parseInt(bird[BIRD_SOLD]);
        }
        if (printInfo) {
            System.out.println(BIRD_TABLE_SPACER);
            System.out.printf(BIRD_PRINT_FORMAT, "TOTAL:", "", totalStock, totalSold);
        } else {
            System.out.printf("%n%s%n", "#################### REPORT \'" + repName + "\' EMPTY");
        }
    }
}

