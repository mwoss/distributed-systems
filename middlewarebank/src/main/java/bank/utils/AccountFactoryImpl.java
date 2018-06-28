package bank.utils;

import com.zeroc.Ice.Current;
import com.zeroc.Ice.Identity;
import exchange_rate.proto.gen.CurrencyType;
import generated.bankClient.*;
import org.apache.log4j.Logger;

import java.util.HashMap;

public class AccountFactoryImpl implements AccountFactory {

    private static final Logger logger = Logger.getLogger(AccountFactoryImpl.class.getName());
    private final double NormalAccountThreshold = 10000.0;

    private HashMap<CurrencyType, Double> exchangeRateValue;

    public AccountFactoryImpl(HashMap<CurrencyType, Double> exchangeRateValue) {
        this.exchangeRateValue = exchangeRateValue;
    }

    //ObjectAdapter map object identity to servant
    //Prx, because we return interface. Pointers are similar to pointers from cpp.
    @Override
    public AccountPrx createAccount(Person person, double monthlyIncome, Current current) throws InvalidPeselStructure {
        logger.info("New user created with id: " + person.pesel);
        if (monthlyIncome >= NormalAccountThreshold) {
            return PremiumAccountPrx.uncheckedCast(current.adapter
                    .add(new PremiumAccountImpl(person, monthlyIncome, exchangeRateValue), new Identity(isPeselValid(person.pesel), "bankClient")));
        }
        return AccountPrx.uncheckedCast(current.adapter
                .add(new AccountImpl(person, monthlyIncome), new Identity(isPeselValid(person.pesel), "bankClient")));
    }

    private String isPeselValid(String pesel) throws InvalidPeselStructure {
        if (!pesel.matches("\\d+"))
            throw new InvalidPeselStructure("PESEL have to contains only digits [0-9]");
        return pesel;
    }
}
