
:: Mandatory parameters to be passed (Version and Tag)

set version=%1
cls
set tag=%2
cls

:: Pass the local path of provider to simulate a webhook to verify pacts (https://docs.pact.io/pact_broker/webhooks/)

set path_provider=C:\<<path provider>>
cls

@echo ' --------------------- START BUILD CONSUMER SIDE ------------------------ '


@echo ' ------------------------------------------------------------------------  '
@echo ' ------------------------------------------------------------------------  '
@echo ' ------------------------------------------------------------------------  '
@echo ' ------------------- 1. Consumer Publishing Pacts -----------------------  '
@echo ' ------------------------------------------------------------------------  '
@echo ' ------------------------------------------------------------------------  '
@echo ' ------------------------------------------------------------------------  '

:: Run clean, test and publish to Pact Broker by Maven.

call mvn clean test pact:publish -Dpactversion=%version% -Dpacttag=%tag%

cls

:: Enter in a path directory.

cd %path_provider%

cls

@echo ' ------------------------------------------------------------------------  '
@echo ' ------------------------------------------------------------------------  '
@echo ' ------------------------------------------------------------------------  '
@echo ' --------------- 2. Webhook to Provider Verification  -------------------  '
@echo ' -------------------(Publishing Results to Broker) ----------------------  '
@echo ' ------------------------------------------------------------------------  '
@echo ' ------------------------------------------------------------------------  '

:: Run contract's verification in provider side (with path appointed before).

call mvn pact:verify %vpath_provider%

cls

@echo ' ------------------------------------------------------------------------  '
@echo ' ------------------------------------------------------------------------  '
@echo ' ------------------------------------------------------------------------  '
@echo ' ------------------- 3. Can I Deploy to Prod?  --------------------------  '
@echo ' ------------------------------------------------------------------------  '
@echo ' ------------------------------------------------------------------------  '
@echo ' ------------------------------------------------------------------------  '

:: Return to root dir.

cd /

:: Try to list folder used to generate results of "can i deploy" (C:\out\)

dir /A:D C:\out\ >nul 2>&1

:: If it exists, remove and recreate it. If not, create it.

if ERRORLEVEL 1 (mkdir C:\out\) else (rmdir c:\out /q /s
mkdir c:\out)

:: Call pact-broker with "can-i-deploy" endpoint to check if is possible to deploy a new version of consumer to production. Write results in "c:/out/can-i-deploy.txt".

@echo Status Code
curl -o "c:/out/can-i-deploy.txt" --location --request GET "http://localhost:9292/can-i-deploy?pacticipant=user&version=%version%&environment=production"

:: Access powershell directory in System32 to run some shell comands.

cd C:\Windows\System32\WindowsPowerShell\v1.0

:: Open file, replace double quotes  to "''" (empty) and generate a new output file (C:\out\result-can-i-deploy.txt).

type C:\out\can-i-deploy.txt | powershell -Command "$input | ForEach-Object { $_ -replace \"\"\"\", \"\" }" > C:\out\result-can-i-deploy.txt

:: Search by "deployable:true" in output file to check if is possible deploy in production environment.

powershell.exe C:\Windows\System32\findstr.exe 'deployable:true' c:\out\result-can-i-deploy.txt

:: If not deployable, throw an error and end the execution. If is deployable, call the pact-broker to confirm the deployment and mark environment as "production".

if errorlevel 1 (

    echo You can't deploy it :/

) else (

        @echo ' ------------------------------------------------------------------------  '
        @echo ' ------------------------------------------------------------------------  '
        @echo ' ------------------------------------------------------------------------  '
        @echo ' --------------------- 4. Record Deploy to Prod  ------------------------  '
        @echo ' ------------------------------------------------------------------------  '
        @echo ' ------------------------------------------------------------------------  '
        @echo ' ------------------------------------------------------------------------  '

        @echo Status Code
        curl --location --request POST "http://localhost:9292/pacticipants/user/versions/%version%/deployed-versions/environment/27480ae9-ca59-471b-b82c-fd7e1871c7ca" --header "Accept: application/hal+json, application/json, */*; q=0.01" --header "X-Interface: HAL Browser" --header "Content-Type: application/json" --silent --output /dev/null --write-out "%%{http_code}"

)

@echo ' -------------------- FINISH BUILD CONSUMER SIDE ------------------------- '

:: Return to repo dir (specify your root repo dir)

cd C:\Users\andrevinicius.rodrig\Desktop\docAndre\FF\Repos\user-api
