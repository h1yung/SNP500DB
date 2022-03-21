import sqlite3
import itertools

# function executed to create the table
# def create_table(con, create_table_sql):
#     try:
#         c = conn.cursor()
#         c.execute(create_table_sql)
#     except Exception as e:
#         print(e)

conn = sqlite3.connect('db/snp500db.db')

# mycursor = conn.cursor()
# sql = "DROP TABLE `EarningsID`;"
# mycursor.execute(sql)

# create table
c = conn.cursor()
c.execute("""CREATE TABLE IF NOT EXISTS EarningsID
		(ID INTEGER PRIMARY KEY AUTOINCREMENT,
		Quarter VARCHAR(2) NOT NULL,
		Year DATE NOT NULL,
		Ticker VARCHAR(4) NOT NULL);
	""")
c1 = conn.cursor()
c1.execute("""CREATE TABLE IF NOT EXISTS IncomeStatement
		(
		ID INT NOT NULL,
		TotalRevenue REAL, 
		GrossProfit REAL, 
		OperatingIncome REAL, 
		NetIncomePreTax REAL, 
		NetIncome REAL,
		DilutedEPS REAL,
		EarningsID INT NOT NULL,
		FOREIGN KEY(ID) REFERENCES EarningsID
		);
	""")
c2 = conn.cursor()
c2.execute("""
		CREATE TABLE IF NOT EXISTS BalanceSheet
		(
		ID INT NOT NULL,
		TotalCurrentAssets REAL, 
		TotalNoncurrentAssets REAL, 
		TotalAssets REAL, 
		TotalCurrentLiabilities REAL, 
		TotalNoncurrentLiabilities REAL,
		TotalLiabilities REAL,
		EquitiesAttrToParentCompany REAL,
		TotalEquities REAL,
		TotalLiabilitiesEquities,
		FOREIGN KEY(ID) REFERENCES EarningsID
		);
	""")
c3 = conn.cursor()
c3.execute("""
		CREATE TABLE IF NOT EXISTS CashFlow
		(
		ID INT NOT NULL,
		NetIncome REAL,
		OperatingActivities REAL, 
		InvestingActivities REAL,
		FinancialActivities REAL,
		EffectOfExchRateChanges REAL,
		NetIncInCashCashEquiv REAL,
		FOREIGN KEY(ID) REFERENCES EarningsID
		);
	""")
c4 = conn.cursor()
c4.execute("""
		CREATE TABLE IF NOT EXISTS PriceTargets
		(
		ID INT NOT NULL,
		NetIncome REAL,
		Price REAL,
		FOREIGN KEY(ID) REFERENCES EarningsID
		);
	""")

c5 = conn.cursor()
quarters = ['Q1', 'Q2', 'Q3', 'Q4']
years = [2021]
tickers = ["AAPL", "MSFT", "AMZN", "GOOGL", "GOOG", "TSLA", "NVDA", "BRKB", "FB", 
"JPM", "UNH", "JNJ", "PG", "V", "HD", "BAC", "XOM", "MA", "DIS", "PFE", 
"CVX", "ABBV", "KO", "CSCO", "AVGO"] # top 25 by cap feb 18, 2022
for quarter, year, ticker in itertools.product(*[quarters, years, tickers]): # combination of q, y, t
    sql = """
    INSERT INTO EarningsID(Quarter, Year, Ticker)
    SELECT '{0}', {1}, '{2}'
    WHERE NOT EXISTS
    	(SELECT Quarter, Year, Ticker
    	FROM EarningsID
    	WHERE Quarter='{0}' AND Year='{1}' AND Ticker='{2}'
    	);
    """.format(quarter, year, ticker)
    c5.execute(sql)

# c6 = conn.cursor()
# c6.execute("""
# 		SELECT * FROM EarningsID;
# 	""")
# print(c6.fetchall())

conn.commit()
conn.close()