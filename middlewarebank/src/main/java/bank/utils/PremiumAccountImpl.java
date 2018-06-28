package bank.utils;

import com.zeroc.Ice.Current;
import generated.bankClient.*;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;


public class PremiumAccountImpl extends AccountImpl implements PremiumAccount {

    private static final Logger logger = Logger.getLogger(PremiumAccountImpl.class.getName());
    private Map<exchange_rate.proto.gen.CurrencyType, Double> exchangeRateValue;
    private final double multiplier = 1.15;

    public PremiumAccountImpl(Person person, double income,
                              HashMap<exchange_rate.proto.gen.CurrencyType, Double> exchangeRateValue) {
        super(person, income);
        this.exchangeRateValue = exchangeRateValue;
    }

    @Override
    public CreditInfo getCredit(Credit credit, Current current) throws DateFormatError, UnsupportedCurrencyException {
        isDateValid(credit.startDate, credit.endDate);

        double creditCost = getCreditValueInFreginCurrency(credit.cost * multiplier, credit.currencyType);
        logger.info("User " + current.id + " take credit: " + creditCost + " in " + credit.currencyType);
        return new CreditInfo(CurrencyType.PLN, credit.cost * multiplier,
                credit.currencyType, creditCost);
    }

    private void isDateValid(Date from, Date to) throws DateFormatError {
        if (from.year > to.year || (from.year == to.year && from.month > to.month) ||
                (from.year == to.year && from.month == to.month && from.day > to.day))
            throw new DateFormatError("Invalid data range");
    }

    private double getCreditValueInFreginCurrency(double cost, CurrencyType currencyType) throws UnsupportedCurrencyException {
        Double rate = exchangeRateValue.get(convertTypes(currencyType));
        Double baseRate = exchangeRateValue.get(convertTypes(CurrencyType.PLN));
        if (rate == null)
            throw new UnsupportedCurrencyException("Invalid currency type");
        return cost * (baseRate / rate);
    }

    private exchange_rate.proto.gen.CurrencyType convertTypes(CurrencyType currencyType) throws UnsupportedCurrencyException {
        switch (currencyType) {
            case EUR:
                return exchange_rate.proto.gen.CurrencyType.EUR;
            case JEN:
                return exchange_rate.proto.gen.CurrencyType.JEN;
            case PLN:
                return exchange_rate.proto.gen.CurrencyType.PLN;
            case SZK:
                return exchange_rate.proto.gen.CurrencyType.SZK;
            case USD:
                return exchange_rate.proto.gen.CurrencyType.USD;
            default:
                throw new UnsupportedCurrencyException("Invalid currency type");
        }
    }
}
