package exchange_rate;


import exchange_rate.proto.gen.Currency;
import exchange_rate.proto.gen.CurrencyProviderGrpc;
import exchange_rate.proto.gen.CurrencyType;
import exchange_rate.proto.gen.ExchangeRate;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.Logger;
import org.decimal4j.util.DoubleRounder;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ExchangeServiceImpl extends CurrencyProviderGrpc.CurrencyProviderImplBase {

    private static final Logger logger = Logger.getLogger(ExchangeServiceImpl.class.getName());
    private final int periodTimeCurr = 8;
    private final int periodTimePing = 5;

    private final Map<CurrencyType, Double> exchangeRateValue = new HashMap<>();
    private final Map<StreamObserver<ExchangeRate>, List<CurrencyType>> bankCurrencies = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public ExchangeServiceImpl() {
        exchangeRateValue.put(CurrencyType.EUR, 4.0);
        exchangeRateValue.put(CurrencyType.JEN, 1.2);
        exchangeRateValue.put(CurrencyType.PLN, 1.0);
        exchangeRateValue.put(CurrencyType.SZK, 2.1);
        exchangeRateValue.put(CurrencyType.USD, 3.4);


        scheduler.scheduleAtFixedRate(this::changeCurrenciesValue, 1, periodTimeCurr, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::notifyBanks, 1, periodTimePing, TimeUnit.SECONDS);
    }

    @Override
    public void getExchangeRates(Currency request, StreamObserver<ExchangeRate> responseObserver) {
        bankCurrencies.putIfAbsent(responseObserver, new ArrayList<>());
        bankCurrencies.get(responseObserver).addAll(request.getCurrencyList());
    }

    private void changeCurrenciesValue() {
        for (CurrencyType cType : exchangeRateValue.keySet()) {
            if (ThreadLocalRandom.current().nextInt(2) == 0) {
                double newCurrencyValue = exchangeRateValue.get(cType) * ThreadLocalRandom.current().nextDouble(0.8, 1.2);
                exchangeRateValue.put(cType, DoubleRounder.round(newCurrencyValue, 2));
            }
        }
        logger.info("Currencies values changed");
    }

    private void notifyBanks() {
        bankCurrencies.forEach((respObserver, currencyTypes) -> currencyTypes
                .forEach(currencyType -> currencyNotify(respObserver, currencyType)));
        logger.info("Banks notified");

    }

    private void currencyNotify(StreamObserver<ExchangeRate> bankObserver, CurrencyType currencyType) {
        ExchangeRate exchangeRate = ExchangeRate.newBuilder()
                .setCurrency(currencyType)
                .setRate(exchangeRateValue.get(currencyType))
                .build();
        bankObserver.onNext(exchangeRate);
    }
}
