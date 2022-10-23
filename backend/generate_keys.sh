#!/bin/bash

JWT_SECRET="`dd if=/dev/urandom bs=66 count=1 2>/dev/null | base64 -w 0`"
SIGNED_URL_SECRET="`dd if=/dev/urandom bs=66 count=1 2>/dev/null | base64 -w 0`"

sed -i "s@^app.jwt-secret=.*\$@app.jwt-secret=$JWT_SECRET@" src/main/resources/application.properties
sed -i "s@^app.signed-url-secret=.*\$@app.signed-url-secret=$SIGNED_URL_SECRET@" src/main/resources/application.properties
