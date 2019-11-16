package com.example.guiprogramming.foreign_currency;

/**
 * Used for retrieving data from
 * SQLite database into a single object
 * */
public class CurrencyMap {

    int id, selectedPosition;
    String baseSymbol, foreignSymbol;


    public CurrencyMap() {
    }
    /**
     * @param id id of a database row
     * @param baseSymbol baseSymbol of a currency
     * @param foreignSymbol foreign symbol of a currency
     * */
    public CurrencyMap(int id, String baseSymbol,
                       String foreignSymbol, int selectedPosition) {
        this.id = id;
        this.baseSymbol = baseSymbol;
        this.foreignSymbol = foreignSymbol;
        this.selectedPosition = selectedPosition;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBaseSymbol() {
        return baseSymbol;
    }

    public void setBaseSymbol(String baseSymbol) {
        this.baseSymbol = baseSymbol;
    }

    public String getForeignSymbol() {
        return foreignSymbol;
    }

    public void setForeignSymbol(String foreignSymbol) {
        this.foreignSymbol = foreignSymbol;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }
}
