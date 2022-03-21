import sqlite3
import itertools

# function executed to create the table
# def create_table(con, create_table_sql):
#     try:
#         c = conn.cursor()
#         c.execute(create_table_sql)
#     except Exception as e:
#         print(e)

conn = sqlite3.connect('../db/snp500db.db')

mycursor = conn.cursor()
sql = "DROP TABLE `IncomeStatement`;"
mycursor.execute(sql)

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
		FOREIGN KEY(ID) REFERENCES EarningsID
		);
	""")

conn.commit()
conn.close()