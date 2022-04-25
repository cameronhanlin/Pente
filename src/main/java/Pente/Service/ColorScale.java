package Pente.Service;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ColorScale {

    int NumericValue;
    String HexValue;
    String LongHexValue;
    String Name;


    @JsonProperty("HexValue")
    public String getHexValue() {
        return HexValue;
    }

    @JsonProperty("HexValue")
    public void setHexValue(String hexValue) {
        HexValue = hexValue;
    }

    @JsonProperty("Name")
    public String getName() {
        return Name;
    }

    @JsonProperty("Name")
    public void setName(String name) {
        Name = name;
    }

    @JsonProperty("NumericValue")
    public int getNumericValue() {
        return NumericValue;
    }

    @JsonProperty("NumericValue")
    public void setNumericValue(int numericValue) {
        NumericValue = numericValue;
    }

    @JsonProperty("LongHexValue")
    public String getLongHexValue() {
        return LongHexValue;
    }

    @JsonProperty("LongHexValue")
    public void setLongHexValue(String longHexValue) {
        LongHexValue = longHexValue;
    }
}
