Details:
compiled using JDK 1.8

can be built using mvn clean install

restAPI can be called by curl -X <METHOD> 'localhost:8085/api/...'
RestAPI:

    GET  /profile?accountNum=123456789&pin=1234
    POST  /maxWithdrawal?accountNum=123456789&pin=1234
    POST  /resetAccounts
    POST  /postWithdraw?accountNum=123456789&pin=1234&withdraw=20
    GET  /transactions?accountNum=123456789
    GET  /atmNoteBalance
HTML can be view by going localhost:8085/ from your browser

DOCKER file is attached in root of application

project can be built and deployed from the commandLine with: 
`mvn clean install && docker build -f Dockerfile -t zinkworksatm . && docker run -p 8085:8085 zinkworksatm`
 
A test report can be view in `target/site/jacoco/index.html`.  This is generated after each build
