package com.ariana.stockwatch;

import java.util.Comparator;

public class Stocks {

    private String symbol;
    private String name;
    private Double value;
    private Double change;
    private Double percent;

    public Stocks (String symbol, String name, Double value, Double change, Double percent) {
        this.symbol = symbol;
        this.name = name;
        this.value = value;
        this.change = change;
        this.percent = percent;
    }

    public String getSymbol() {return symbol;}
    public String getName() {return name;}
    public Double getValue() {return value;}
    public Double getChange() {return change;}
    public Double getPercent() {return percent;}

    public String toString() {return symbol + "; " + name + "; " + value + "; " + change + "; " + percent;}

    public static Comparator<Stocks> orderStocks = new Comparator<Stocks>() {
        @Override
        public int compare(Stocks stock1, Stocks stock2) {
            String sym1 = stock1.getSymbol();
            String sym2 = stock2.getSymbol();
            return sym1.compareTo(sym2);
        }
    };
}
