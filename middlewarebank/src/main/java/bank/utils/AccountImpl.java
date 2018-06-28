package bank.utils;

import com.zeroc.Ice.Current;
import generated.bankClient.Account;
import generated.bankClient.Person;
import org.apache.log4j.Logger;
import org.decimal4j.util.DoubleRounder;

import java.util.concurrent.ThreadLocalRandom;

public class AccountImpl implements Account{

    private static final Logger logger = Logger.getLogger(AccountImpl.class.getName());

    private Person person;
    private double income;
    private double balance;

    public AccountImpl(Person person, double income) {
        this.person = person;
        this.income = income;
        this.balance = DoubleRounder.round(ThreadLocalRandom
                .current()
                .nextDouble(100, 5000), 2);
    }

    @Override
    public double accountBalance(Current current) {
        logger.info("Account balance: " + balance + " For user " + current.id);
        return balance;
    }

    @Override
    public void deposit(double money, Current current) {
        logger.info("Deposit "+ money + " money. For user " + current.id);
        balance += money;
    }
}
