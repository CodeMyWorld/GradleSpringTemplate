package softlab.rate.service.imp;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import softlab.rate.dao.RateDao;
import softlab.rate.dao.common.IOperations;
import softlab.rate.entity.*;
import softlab.rate.entity.Currency;
import softlab.rate.model.CurrencyModel;
import softlab.rate.model.HistoricalRate;
import softlab.rate.model.RateModel;
import softlab.rate.service.CurrencyService;
import softlab.rate.service.RateService;
import softlab.rate.service.common.AbstractService;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by alex on 17-1-20.
 */




@Service("rateService")
public class RateServiceImpl extends AbstractService<Rate> implements RateService {
    @Resource(name = "rateDao")
    private RateDao rateDao;

    @Resource(name = "currencyService")
    private CurrencyService currencyService;


    @Override
    protected IOperations<Rate> getDao() {
        return rateDao;
    }

    @Transactional
    @Scheduled(cron = "30 * * * * ? ") //30秒的时候更新
    public void updateRate() {
        List<Rate> latestRates = getLatestUpdateRates();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if(latestRates.size() > 0){
            long days = (calendar.getTimeInMillis() - latestRates.get(0).getDate()) / (24 * 60 * 60 * 1000);
            if (days > 1){
                calendar.setTimeInMillis(latestRates.get(0).getDate());
                for (int i = 0; i < days - 1; i++) {
                    calendar.add(Calendar.DATE, 1);
                    Map<String, Double> missingRates = getMissingRateData(calendar);
                    for(Rate rate: latestRates){
                        Rate missingRate = new Rate();
                        missingRate.setCurrency(rate.getCurrency());
                        missingRate.setDate(calendar.getTimeInMillis());
                        if( missingRates != null &&missingRates.get(rate.getCurrency().getCode()) != null){
                            missingRate.setValue(missingRates.get(rate.getCurrency().getCode()));
                        }else {
                            missingRate.setValue(rate.getValue());
                            System.out.println("missing null");
                        }
                        rateDao.create(missingRate);
                    }
                }
            }else{
                Map<String, Double> currentRates = getCurrentRateData();
                for(Rate rate : latestRates){
                    if(currentRates.get(rate.getCurrency().getCode()) != null){
                        rate.setValue(currentRates.get(rate.getCurrency().getCode()));
                        rateDao.update(rate);
                    }
                }
            }
        }
    }

    @Override
    public void updateRateForCurrency(String currencyCode) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeZone(TimeZone.getTimeZone("UTC"));
        cl.set(2017, Calendar.JANUARY, 1);
        cl.set(Calendar.HOUR_OF_DAY, 0);
        cl.set(Calendar.MINUTE, 0);
        cl.set(Calendar.SECOND, 0);
        cl.set(Calendar.MILLISECOND, 0);
        //TODO 要判断不能使USD
        List<String> result =postRateData(cl, currencyCode);
        for(String s:result){
            System.out.println(s);
        }


        if(result.get(0).equals("PACIFIC Exchange Rate Service")){
            List<Currency> currencies = currencyService.queryList("code", currencyCode);
            Currency currency = currencies.get(0);

            long preDate = cl.getTimeInMillis();
            double preRate = 0;

            for (int i = 2; i < result.size() - 1; i++) {
                Rate rate = new Rate();
                rate.setCurrency(currency);
                String[] data = result.get(i).split(" ");

                try {
                    SimpleDateFormat dateSf = new SimpleDateFormat("yyyy/MM/dd");
                    dateSf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date date = dateSf.parse(data[1]);

                    long days = (date.getTime() - preDate) / (24 * 60 * 60 * 1000);
                    if (days > 1) {
                        for (int j = 0; j < days - 1; j++) {
                            //插入空缺的
                            Rate voidRate = new Rate();
                            voidRate.setCurrency(currency);
                            voidRate.setValue(preRate);
                            cl.setTimeInMillis(preDate);
                            cl.add(Calendar.DATE, j + 1);
                            voidRate.setDate(cl.getTimeInMillis());
                            rateDao.create(voidRate);
                        }
                    }
                    rate.setDate(date.getTime());
                    rate.setValue(Double.parseDouble(data[3]));
                    rateDao.create(rate);
                    preDate = rate.getDate();
                    preRate = rate.getValue();
                } catch (Exception ex) {
                    System.out.println("date error " + currencyCode);
                }
            }
        }
    }

    @Override
    public List<Rate> getLatestUpdateRates() {
        return rateDao.getLatestUpdateDate();
    }

    @Override
    public List<RateModel> getCurrentRate(String fromCid) {
        List<Rate> latestRate = getLatestUpdateRates();
        Map<String, Double> rateValue = new HashMap<>();
        List<RateModel> result = new ArrayList<>();

        String usdCid = currencyService.queryList("code", "USD").get(0).getCid();

        for(Rate rate: latestRate){
            rateValue.put(rate.getCurrency().getCid(), rate.getValue());
        }
        if(!fromCid.equals(usdCid)){
            double fromValue = rateValue.get(fromCid);
            for(String cid: rateValue.keySet()){
                RateModel rateModel = new RateModel();
                if (!cid.equals(fromCid)){
                    rateModel.setCid(cid);
                    rateModel.setValue(round(rateValue.get(cid)/fromValue, 3));
                    result.add(rateModel);
                }else {
                    rateModel.setCid(usdCid); // 美元的要补充进去
                    rateModel.setValue(round(1/rateValue.get(cid), 3));
                    result.add(rateModel);
                }
            }

        }else {
            for(String cid: rateValue.keySet()){
                RateModel rateModel = new RateModel();
                rateModel.setCid(cid);
                rateModel.setValue(rateValue.get(cid));
                result.add(rateModel);
            }
        }
        return result;
    }

    @Override
    public HistoricalRate getHistoricalRates(String fromCid, String toCid, long start, long end) {
        Currency fromCurrency = currencyService.findOne(fromCid);
        Currency toCurrency = currencyService.findOne(toCid);
        List<Rate> fromRate = rateDao.getHistoricalRates(fromCurrency, start, end);
        List<Rate> toRate = rateDao.getHistoricalRates(toCurrency, start, end);
        List<Double> values = new ArrayList<>();
        if(fromRate.size() != 0 && toRate.size() != 0 && fromRate.size() == toRate.size()){
            for(int i = 0; i < fromRate.size(); i++){
                double value = round(toRate.get(i).getValue()/fromRate.get(i).getValue(), 3);
                values.add(value);
            }
        }else if (fromRate.size() == 0) {
            for(Rate rate : toRate){
                values.add(round(rate.getValue(), 3));
            }
        }else if (toRate.size() == 0){
            for(Rate rate : fromRate){
                values.add(round(rate.getValue(), 3));
            }
        }
        if(values.size() == 0){
            return null;
        }else {
            HistoricalRate historicalRate = new HistoricalRate();
            historicalRate.setFromCid(fromCid);
            historicalRate.setToCid(toCid);
            historicalRate.setStart(start);
            historicalRate.setValues(values);
            historicalRate.setTitle("Historical Rate");
            return historicalRate;
        }
    }


    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


//    private void insertVoidRate(Calendar cal, Currency currency, double preRate)



    private ArrayList<String> postRateData(Calendar cal, String currencyCode){
        final String POST_DATA_URL = "http://fx.sauder.ubc.ca/cgi/fxdata";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(POST_DATA_URL);
        List<org.apache.http.NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("b","USD"));
        nvps.add(new BasicNameValuePair("c",currencyCode));
        nvps.add(new BasicNameValuePair("rd",""));
        nvps.add(new BasicNameValuePair("fd",String.valueOf(cal.get(Calendar.DATE))));
        nvps.add(new BasicNameValuePair("fm",String.valueOf(cal.get(Calendar.MONTH)+1)));
        nvps.add(new BasicNameValuePair("fy",String.valueOf(cal.get(Calendar.YEAR))));
        cal.setTime(new Date());
        nvps.add(new BasicNameValuePair("ld",String.valueOf(cal.get(Calendar.DATE))));
        nvps.add(new BasicNameValuePair("lm",String.valueOf(cal.get(Calendar.MONTH)+1)));
        nvps.add(new BasicNameValuePair("ly",String.valueOf(cal.get(Calendar.YEAR))));
        nvps.add(new BasicNameValuePair("y","daily"));
        nvps.add(new BasicNameValuePair("q","volume"));
        nvps.add(new BasicNameValuePair("f","plain"));
        nvps.add(new BasicNameValuePair("o",""));
        System.out.println(nvps.toString());
        ArrayList<String> result = new ArrayList<>();
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = reader.readLine()) != null){
                result.add(line);
            }
            EntityUtils.consume(entity);
            response.close();

        }catch (Exception ex){
            System.out.println(ex.toString());
        }
        return result;
    }

    private Map<String, Double> getCurrentRateData(){
        final String CURRENT_RATE_URL = "http://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote?format=json";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(CURRENT_RATE_URL);
        Map<String, Double> result = new HashMap<>();
        try{
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                sb.append(line);
            }
            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonArray = jsonObject.getJSONObject("list").getJSONArray("resources");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject fields = ((JSONObject)jsonArray.get(i)).getJSONObject("resource").getJSONObject("fields");
                result.put(fields.getString("name").replace("USD/", ""), fields.getDouble("price"));
            }

            EntityUtils.consume(entity);
            response.close();
        }catch (Exception ex){
            System.out.println(ex.toString());
        }
        return result;
    }

    private Map<String, Double> getMissingRateData(Calendar cal){
        SimpleDateFormat missingDatesf = new SimpleDateFormat("yyyyMMdd");
        missingDatesf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date = missingDatesf.format(cal.getTime());
        final String MISSING_RATE_URL = "http://finance.yahoo.com/connection/currency-converter-cache?bypass=true&date=";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(MISSING_RATE_URL+date);
        Map<String, Double> result = new HashMap<>();
        try{
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            reader.readLine();
            sb.append("{");
            while((line = reader.readLine()) != null){
                sb.append(line);
            }
            sb.delete(sb.length()-1,sb.length());
            JSONObject jsonObject = new JSONObject(sb.toString());
//            JSONObject meta = jsonObject.getJSONObject("list").getJSONObject("meta");
//            if(meta.getInt("count") > 150){
//                return null;
//            }
            JSONArray jsonArray = jsonObject.getJSONObject("list").getJSONArray("resources");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject fields = ((JSONObject)jsonArray.get(i)).getJSONObject("resource").getJSONObject("fields");
                result.put(fields.getString("symbol").replace("=X", ""), fields.getDouble("price"));
            }

            EntityUtils.consume(entity);
            response.close();
        }catch (Exception ex){
            System.out.println(ex.toString());
        }
        return result;
    }
}
