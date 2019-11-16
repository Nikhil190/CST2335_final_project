package com.example.guiprogramming.foreign_currency;

//For holding data and proving in lists and spinner
public class ForeignSymbols {
    String symbol;
    double rate;

    public ForeignSymbols() {
    }

    public ForeignSymbols(String symbol, double rate) {
        this.symbol = symbol;
        this.rate = rate;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
