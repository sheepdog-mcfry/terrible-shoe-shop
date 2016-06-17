import requests, json, webbrowser

payload={
'orderId': '2015-03-03-1025', 
'amount': 'NOK 1337.00', 
'cancelRedirect': 'http://example.com/cancel', 
'purchaseDescription': 'Gunnar Inges Shoe', 
'vatRate': '0.00', 
'products': [{'vatRate': '0.10', 'sku': 'CMO-STO-100-M', 'price': 'NOK 1337.00', 'name': 'something', 'timeSpec': 'P1M'}], 
'successRedirect': 'http://example.com/success'}
url='https://staging.payment.telenordigital.com/transactions'

r = requests.post(
    url, 
    auth=('gatling1', 'T5Wo2Ghd5E6av3WTk1LO'), 
    data=json.dumps(payload), 
    headers={'Accept':'application/json', 'Content-Type': 'application/json'})
print r.status_code
print r.content
payResponse = json.loads(r.content)

webbrowser.open(payResponse['links'][0]['href'])