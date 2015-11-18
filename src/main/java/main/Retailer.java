package main;

import org.springframework.data.annotation.Id;

/**
 * Created by Adwait on 11/15/2015.
 */
public class Retailer
{
    @Id
    private String id;

    private int storeId;
    private String storeName ;
    private String storeStreet;
    private String storeCity;
    private String storeState;
    private String storeCountry;
    private int storeZip;

    public Retailer() {
    }

    public Retailer(int storeId, String storeName, String storeStreet, String storeCity, String storeState, String storeCountry, int storeZip) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.storeStreet = storeStreet;
        this.storeCity = storeCity;
        this.storeState = storeState;
        this.storeCountry = storeCountry;
        this.storeZip = storeZip;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreStreet() {
        return storeStreet;
    }

    public void setStoreStreet(String storeStreet) {
        this.storeStreet = storeStreet;
    }

    public String getStoreCity() {
        return storeCity;
    }

    public void setStoreCity(String storeCity) {
        this.storeCity = storeCity;
    }

    public String getStoreState() {
        return storeState;
    }

    public void setStoreState(String storeState) {
        this.storeState = storeState;
    }

    public String getStoreCountry() {
        return storeCountry;
    }

    public void setStoreCountry(String storeCountry) {
        this.storeCountry = storeCountry;
    }

    public int getStoreZip() {
        return storeZip;
    }

    public void setStoreZip(int storeZip) {
        this.storeZip = storeZip;
    }

    @Override
    public String toString() {
        return String.format(
                "Retailer[id='%s',storeId='%s',storeName='%s',storeStreet='%s',storeCity='%s',storeState='%s',storeCountry='%s',storeZip='%s']",
                id,storeId,storeName,storeStreet,storeCity,storeState,storeCountry,storeZip);
    }
}
