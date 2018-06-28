from utils.logger import Logger
import sys
import Ice
import bankClient

Ice.loadSlice('../../resources/slice/bankClient.ice')

LOG = Logger()

currencies = {
    'PLN': bankClient.CurrencyType.PLN,
    'EUR': bankClient.CurrencyType.EUR,
    'JEN': bankClient.CurrencyType.JEN,
    'USD': bankClient.CurrencyType.USD,
    'SZK': bankClient.CurrencyType.SZK,
}


def configure_connection(communicator):
    global bank_name, port, acc_factory, connection
    bank_name = input("Input bank NAME you want connect to\n")
    port = input("Input bank PORT you want connect to\n")
    connection = communicator.stringToProxy(
        "bank/{0}:tcp -h localhost -p {1}:udp -h localhost -p {1}".format(bank_name, port))
    acc_factory = bankClient.AccountFactoryPrx.checkedCast(connection)


def create_account():
    global account
    try:
        name, surname, pesel, income = input(
            "Input name, surname, pesel and your monthly income (Use ; to separate)\n").split(";")
        person = bankClient.Person(name, surname, pesel)
        account = acc_factory.createAccount(person, float(income))
    except ValueError:
        LOG.error_msg("Couldn't unpack inserted string")
        sys.exit(-2)
    except Ice.UnknownLocalException:
        LOG.error_msg("You are already registered. Try to log in using pesel")
        sys.exit(-3)


def login_to_account(communicator):
    global account
    pesel = input("Input your pesel\n")
    obj = communicator.stringToProxy(
        "bankClient/{0}:tcp -h localhost -p {1}:udp -h localhost -p {1}".format(pesel, port))
    account = bankClient.AccountPrx.checkedCast(obj)


with Ice.initialize(sys.argv) as communicator:
    configure_connection(communicator)

    user_task = input("Input 'create' to create account \nInput 'login' to log in to existing account\n")
    if user_task == 'create':
        create_account()
    elif user_task == 'login':
        login_to_account(communicator)
    else:
        LOG.error_msg("Invalid command")
        sys.exit(-1)

    while user_task != "disconnect":
        try:
            user_task = input("Available commands: balance, deposit, credit, disconnect\n")

            if user_task == 'balance':
                LOG.log_msg("Your account balance: " + str(account.accountBalance()))
            elif user_task == 'deposit':
                money = float(input("How much your want to deposit?\n"))
                account.deposit(money)
            elif user_task == 'credit':
                premium_acc = bankClient.PremiumAccountPrx.uncheckedCast(account)

                day_f, month_f, year_f = input("Input start credit date (format: dd-mm-yyyy)\n").split("-")
                day_t, month_t, year_t = input("Input start credit date (format: dd-mm-yyyy)\n").split("-")
                money, currency_type = input("Input credit value and currency (format: value;currency)\n").split(";")

                date_from = bankClient.Date(int(day_f), int(month_f), int(year_f))
                date_to = bankClient.Date(int(day_t), int(month_t), int(year_t))
                credit = bankClient.Credit(float(money), date_from, date_to, currencies[currency_type])

                credit_info = premium_acc.getCredit(credit)
                LOG.log_msg("Credit info: " + str(credit_info))
            else:
                LOG.error_msg("Invalid command\n")
        except Ice.OperationNotExistException:
            LOG.error_msg("Credits are only available for premium accounts\n")
        except KeyError:
            LOG.error_msg("Invalid currency\n")
        except ValueError:
            LOG.error_msg("Couldn't unpack date values\n")
        except Exception:
            LOG.error_msg("Something went wrong. Sorry. Try again.\n")
