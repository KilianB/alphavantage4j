package com.github.kilianB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import org.patriques.ApiConnector;
import org.patriques.BatchStockQuotes;
import org.patriques.DigitalCurrencies;
import org.patriques.ForeignExchange;
import org.patriques.TechnicalIndicators;
import org.patriques.TimeSeries;
import org.patriques.input.ApiParameter;
import org.patriques.input.ApiParameterBuilder;
import org.patriques.output.AlphaVantageException;

/**
 * Connection to Alpha Vantage API.
 *
 * @see ApiConnector
 */
public class AlphaVantageConnector implements ApiConnector {
	private static final String BASE_URL = "https://www.alphavantage.co/query?";
	private final String apiKey;
	private final int timeOut;

	/** Proxy server to connect via */
	private final Proxy proxy;

	private BatchStockQuotes batchEndpoint;
	private DigitalCurrencies cryptoEndpoint;
	private ForeignExchange forexEndpoint;
	private TechnicalIndicators indicatorEndpoint;
	private TimeSeries stockEndpoint;

	/**
	 * Creates an AlphaVantageConnector.
	 *
	 * @param apiKey  the secret key to access the api.
	 * @param timeOut the timeout for when reading the connection should give up.
	 */
	public AlphaVantageConnector(String apiKey, int timeOut) {
		this(apiKey, timeOut, null);
	}

	/**
	 * Creates an AlphaVantageConnector.
	 *
	 * @param apiKey  the secret key to access the api.
	 * @param timeOut the timeout for when reading the connection should give up.
	 * @param proxy   the proxy to connect via. may be null.
	 */
	public AlphaVantageConnector(String apiKey, int timeOut, Proxy proxy) {
		this.apiKey = apiKey;
		this.timeOut = timeOut;
		this.proxy = proxy;
	}

	@Override
	public String getRequest(ApiParameter... apiParameters) {
		String params = getParameters(apiParameters);
		try {
			URL request = new URL(BASE_URL + params);
			URLConnection connection = proxy == null ? request.openConnection() : request.openConnection(proxy);
			connection.setConnectTimeout(timeOut);
			connection.setReadTimeout(timeOut);

			InputStreamReader inputStream = new InputStreamReader(connection.getInputStream(), "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(inputStream);
			StringBuilder responseBuilder = new StringBuilder();

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				responseBuilder.append(line);
			}
			bufferedReader.close();
			return responseBuilder.toString();
		} catch (IOException e) {
			throw new AlphaVantageException("failure sending request", e);
		}
	}

	/**
	 * @return the forex end point
	 */
	public synchronized ForeignExchange forex() {
		if (forexEndpoint == null) {
			forexEndpoint = new ForeignExchange(this);
		}
		return forexEndpoint;
	}

	/**
	 * @return the batch quotes end point
	 */
	public synchronized BatchStockQuotes batchQuotes() {
		if (batchEndpoint == null) {
			batchEndpoint = new BatchStockQuotes(this);
		}
		return batchEndpoint;
	}

	/**
	 * @return the crypto end point
	 */
	public synchronized DigitalCurrencies crypto() {
		if (cryptoEndpoint == null) {
			cryptoEndpoint = new DigitalCurrencies(this);
		}
		return cryptoEndpoint;
	}

	/**
	 * @return the technical indicator end point
	 */
	public synchronized TechnicalIndicators indicators() {
		if (indicatorEndpoint == null) {
			indicatorEndpoint = new TechnicalIndicators(this);
		}
		return indicatorEndpoint;
	}

	/**
	 * @return the technical time series end point
	 */
	public synchronized TimeSeries stocks() {
		if (stockEndpoint == null) {
			stockEndpoint = new TimeSeries(this);
		}
		return stockEndpoint;
	}

	/**
	 * Builds up the url query from the api parameters used to append to the base
	 * url.
	 *
	 * @param apiParameters the api parameters used in the query
	 * @return the query string to use in the url
	 */
	private String getParameters(ApiParameter... apiParameters) {
		ApiParameterBuilder urlBuilder = new ApiParameterBuilder();
		for (ApiParameter parameter : apiParameters) {
			urlBuilder.append(parameter);
		}
		urlBuilder.append("apikey", apiKey);
		return urlBuilder.getUrl();
	}

}
