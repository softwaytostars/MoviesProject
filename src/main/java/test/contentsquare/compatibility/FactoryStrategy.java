package test.contentsquare.compatibility;

public class FactoryStrategy {
    static final String STRATEGY_BASIC = "basic";
    static final String STRATEGY_WEIGHT = "weight";
    public static StrategyCompatibility createStrategy(String ident) {
        switch (ident) {
            case STRATEGY_BASIC:  {
                System.out.println("will use basic strategy");
                return new BasicStrategyCompatibility();
            }
            case STRATEGY_WEIGHT: {
                System.out.println("will use weight strategy");
                return new WeightStrategyCompatibility();
            }
            default: {
                System.out.println("not supported strategy");
                return null;
            }
        }
    }
}
