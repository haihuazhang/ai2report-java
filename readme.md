# AI Report Generator

## Overview

This project is a web application that allows users to generate reports using AI.

## Features

- Generate reports using AI
- Maintain reports and their related fields
- Maintain parameters
- Maintain knowledges

## Technologies

- SAP BTP
- SAP Fiori Elements
- SAP CAP for Java
- SAP Cloud SDK for Java

## Directory Structure

- `db`: Contains the CDS views and entity definitions.
- `srv`: Contains the service layer implementation.
- `app`: Contains the UI layer implementation.

## Prerequisites

- SAP BTP Account
- SAP BTP Cloud Foundry Environment
- SAP BTP Services
  - SAP HANA Cloud
  - SAP WorkZone
- SAP BTP Destination
  - Destination for SAP AI Core: You need to create a destination(named "AICore") for SAP AI Core service and provide the credentials in SAP BTP Cockpit.

## Local Run
This project must be run with VSCode/Cursor.
- .vscode/tasks.json:
  - cds_bind: Bind the SAP BTP services with application and generate the config files to default-env.json and app/router/default-services.json
- .vscode/launch.json: 
  - Run "Spring Boot-Application<ai-fs>": Run the odata service
  - Run "Launch Approuter": Run the approuter

### `step1` deploy cds artifacts.
run command cds deploy in the root folder.

### `step2` create SAP BTP services and bind to local app.
1. run cf create-service command for service XUSAA and Destination.
   - cf create-service xusaa application aireport-auth -c xs-security.json
   - cf create-service destination lite aireport-destination 
2. run cf create-service-key command for service XSUAA and Destination. 
   - cf create-service-key aireport-auth aireport-auth-key
   - cf create-service-key aireport-destination aireport-destination-key
3. run cds bind command for service HANA Cloud, xusaa and Destination.
   - cds bind -2 aireport-db
   - cds bind -2 aireport-auth
   - cds bind -2 aireport-destination
4. check the content of file '.cdsrc-private.json'.
   - make sure the property apiEndpoint/org/space has the right value.  

### `step3` run/debug srv app using VSCode debug extension

### `step4` run/debug approuter using VSCode debug extension

## Deployment

- Deploy the application to the SAP BTP Cloud Foundry Environment.
  - Run command: mbt build
  - Run command: cf deploy mta_archives/aireport_1.0.0.mtar


