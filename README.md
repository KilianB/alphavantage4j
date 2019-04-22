# alphavantage4j

A Java wrapper to get stock data and stock indicators from the Alpha Vantage API.

## Introduction

Alpha Vantage delivers a free API for real time financial data and most used finance indicators. This library implements a wrapper to the free API provided by Alpha
Vantage (http://www.alphavantage.co/). It requires an api key, that can be requested on http://www.alphavantage.co/support/#api-key. You can have a look at all the api 
calls available in their documentation http://www.alphavantage.co/documentation.


## Available via jitpack

https://jitpack.io/#KilianB/alphavantage4j

## Usage

Now that you set up your project and have your api key you can start using the service. Here are a few examples of how you can use the service.


#### Convinience access

````java
public class App {
  private static final String API_KEY = "XXXX";
  private int timeout = 3000;
	
  public static void main(String[] args) {
    AlphaVantageConnector con = new AlphaVantageConnector(API_KEY,3000);	
    Daily ohlc = con.forex().daily("EUR","USD",OutputSize.FULL);
  }
}
````

#### Explicit call
```java
public class App {
  private static final API_KEY = "XXXX";

  public static void main(String[] args) {
    int timeout = 3000;
    AlphaVantageConnector apiConnector = new AlphaVantageConnector(API_KEY, timeout);
    TimeSeries stockTimeSeries = new TimeSeries(apiConnector);
    
    try {
      IntraDay response = stockTimeSeries.intraDay("MSFT", Interval.ONE_MIN, OutputSize.COMPACT);
      Map<String, String> metaData = response.getMetaData();
      System.out.println("Information: " + metaData.get("1. Information"));
      System.out.println("Stock: " + metaData.get("2. Symbol"));
      
      List<StockData> stockData = response.getStockData();
      stockData.forEach(stock -> {
        System.out.println("date:   " + stock.getDateTime());
        System.out.println("open:   " + stock.getOpen());
        System.out.println("high:   " + stock.getHigh());
        System.out.println("low:    " + stock.getLow());
        System.out.println("close:  " + stock.getClose());
        System.out.println("volume: " + stock.getVolume());
      });
    } catch (AlphaVantageException e) {
      System.out.println("something went wrong");
    }
  }
}
```
