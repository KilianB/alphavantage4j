package co.alphavantage.output.technicalindicators;

import co.alphavantage.output.AlphaVantageException;
import co.alphavantage.output.JsonParser;
import co.alphavantage.output.technicalindicators.data.MACDData;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Representation of moving average converge/divergence (MACD) response from api.
 */
public class MACD {
  private final Map<String, String> metaData;
  private final List<MACDData> indicatorData;

  private MACD(final Map<String, String> metaData, final List<MACDData> indicatorData) {
    this.metaData = metaData;
    this.indicatorData = indicatorData;
  }

  /**
   * Meta data for MACD data
   *
   * @return map of keys and values in json representation of metadata.
   */
  public Map<String, String> getMetaData() {
    return metaData;
  }

  /**
   * List of MACDData
   *
   * @return list of {@link MACDData}.
   */
  public List<MACDData> getData() {
    return indicatorData;
  }

  /**
   * Creates {@code MACD} instance from json
   * @param json string to parse
   * @return MACD instance
   * @throws AlphaVantageException
   */
  public static MACD from(String json) {
    Parser parser = new Parser();
    return parser.parseJson(json);
  }

  /**
   * Helper class for parsing json to {@code MACD}.
   *
   * @see TechnicalIndicatorParser
   * @see JsonParser
   */
  private static class Parser extends TechnicalIndicatorParser<MACD> {

    @Override
    String getIndicatorKey() {
      return "Technical Analysis: MACD";
    }

    @Override
    MACD resolve(Map<String, String> metaData, Map<String, Map<String, String>> indicatorData) {
      List<MACDData> indicators = new ArrayList<>();
      indicatorData.forEach((key, values) -> indicators.add(getMACDData(key, values)));
      return new MACD(metaData, indicators);
    }

    private MACDData getMACDData(String key, Map<String, String> values) {
      try {
        return new MACDData(DateTime.parse(key, DATE_WITH_SIMPLE_TIME_FORMAT), Double.parseDouble(values.get("MACD")));
      } catch (Exception e) {
        throw new AlphaVantageException("MACD adjusted api change", e);
      }
    }
  }
}
