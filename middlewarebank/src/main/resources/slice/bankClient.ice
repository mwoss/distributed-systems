module bankClient{
    enum AccountType{
        NORMAL = 0,
        PREMIUM = 1
    };
    enum CurrencyType{
        PLN = 0,
        EUR = 1,
        JEN = 2,
        USD = 3,
        SZK = 4
    };

    exception UnsupportedCurrencyException{
        string reason;
    };
    exception DateFormatError{
        string reason;
    };
    exception InvalidPeselStructure{
        string reason;
    };

    struct Date{
        short day;
        short month;
        short year;
    };

    struct Person{
        string name;
        string surname;
        string pesel;
    };

    struct Credit{
        double cost;
        Date startDate;
        Date endDate;
        CurrencyType currencyType;
    };

    struct CreditInfo{
      CurrencyType baseCurrencyType;
      double baseCreditCost;
      CurrencyType foreignCurrencyType;
      double foreignCreditCost;
    };

    interface Account{
        double accountBalance();
        void deposit(double money);
    };
    interface PremiumAccount extends Account{
        CreditInfo getCredit(Credit credit) throws DateFormatError, UnsupportedCurrencyException;
    };

    interface AccountFactory{
        Account* createAccount(Person person, double monthlyIncome) throws InvalidPeselStructure;
    };
};